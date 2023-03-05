package app.service;

import app.dao.CommentDao;
import app.injectService.UserService;
import app.pojo.Comment;
import app.pojo.CommentRequest;
import app.utils.SnowFlakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.PageData;
import pojo.PageInfo;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
	
	private final CommentDao commentDao;
	private final SnowFlakeIdWorker snowFlakeIdWorker;
	private final UserService userService;
	
	public CommentServiceImpl(
			@Autowired CommentDao commentDao,
			@Autowired SnowFlakeIdWorker snowFlakeIdWorker,
			@Autowired UserService userService) {
		this.commentDao = commentDao;
		this.snowFlakeIdWorker = snowFlakeIdWorker;
		this.userService = userService;
	}
	
	@Override
	public PageData<Comment> getCommentRoots(
			Long bookId,
			Long userId,
			PageInfo pageInfo
											) {
		return PageData.response(pageInfo, page -> {
					List<Comment> comments = commentDao.queryCommentRoot(bookId, page);
					injectComment(userId,
							comments);
					return comments;
				},
				() -> commentDao.queryRootSize(bookId));
	}
	
	/**
	 * > The function is used to commit a comment
	 *
	 * @param commentRequest The request object that the front end sends to the background.
	 * @return The id of the comment.
	 */
	@Override
	public Long commitComment(CommentRequest commentRequest) {
		Long id = snowFlakeIdWorker.nextId();
		Comment comment = new Comment();
		comment.setId(id);
		comment.setUser_id(commentRequest.getUserId());
		comment.setBookId(commentRequest.getBookId());
		comment.setContent(commentRequest.getContent());
		comment.setParent_id(commentRequest.getParentId());
		Boolean commit = commentDao.commit(comment);
		if (Boolean.TRUE.equals(commit)) return id;
		return null;
	}
	
	@Override
	public List<Comment> getCommentChildren(Long id, Long userId) {
		List<Comment> comments = commentDao.queryCommentChildren(id);
		injectComment(userId, comments);
		return comments;
	}
	
	@Override
	public Boolean commentLikes(Long id, Long userId) {
		return commentDao.insertLike(id, userId) || commentDao.like(id, userId);
		
	}
	
	@Override
	public Boolean commentUnLikes(Long id, Long userId) {
		return commentDao.insertUnLike(id, userId) || commentDao.unLike(id, userId);
	}
	
	@Override
	public Boolean commentCancelLikes(Long id, Long userId) {
		return commentDao.cancelLike(id, userId);
	}
	
	private void injectComment(Long userId, List<Comment> comments) {
		for (Comment comment : comments) {
			Comment queryComment = commentDao.queryComment(comment.getId(), userId);
			comment.setLiked(queryComment.getLiked());
			comment.setUnLike(queryComment.getUnLike());
			comment.setIsLiked(queryComment.getIsLiked());
			comment.setIsUnLiked(queryComment.getIsUnLiked());
			// 查询图书拥有者账户
			comment.setUser(userService.query(queryComment.getUser_id()));
		}
	}
}
