package app.service;

import app.dao.CommentDao;
import app.injectService.UserService;
import app.pojo.Comment;
import app.pojo.CommentRequest;
import app.utils.SnowFlakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import pojo.PageData;
import pojo.PageInfo;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CommentServiceImpl implements CommentService {
	
	private final CommentDao commentDao;
	private final SnowFlakeIdWorker snowFlakeIdWorker;
	private final UserService userService;
	
	private final TransactionTemplate transactionTemplate;
	
	public CommentServiceImpl(
			@Autowired CommentDao commentDao,
			@Autowired SnowFlakeIdWorker snowFlakeIdWorker,
			@Autowired UserService userService,
			@Autowired TransactionTemplate transactionTemplate) {
		this.commentDao = commentDao;
		this.snowFlakeIdWorker = snowFlakeIdWorker;
		this.userService = userService;
		this.transactionTemplate = transactionTemplate;
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
		comment.setParentId(commentRequest.getParentId());
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
	
	@Override
	public Boolean deleteComment(Long id) {
		List<Long> query = query(id);
		return transactionTemplate.execute(status -> {
			try {
				for (Long ids : query) {
					commentDao.deleteComment(ids);
					commentDao.deleteCommentInfo(ids);
				}
				return true;
			} catch (Exception e) {
				status.setRollbackOnly();
			}
			return false;
		});
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
	
	private List<Long> query(Long id) {
		// 查出当前的子节点id
		List<Long> longs = commentDao.queryChild(id);
		
		return Stream.concat(Stream.of(id),Stream.concat(longs.stream().flatMap(
							 i -> query(i).stream()
																			   ), longs.stream()))
					 .toList();
	}
}
