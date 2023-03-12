package app.service;

import app.pojo.Comment;
import app.pojo.CommentRequest;
import pojo.PageData;
import pojo.PageInfo;

import java.util.List;

public interface CommentService {
	
	/**
	 * 获取一本书的根评论
	 *
	 * @param bookId   书的id
	 * @param userId   请求评论的用户。
	 * @param pageInfo 页码、页面大小、排序字段、排序顺序
	 * @return 包含 Comment 对象列表的 PageData 对象。
	 */
	PageData<Comment> getCommentRoots(Long bookId, Long userId, PageInfo pageInfo);
	
	/**
	 * It creates a new comment on a commit
	 *
	 * @param commentRequest The comment request object.
	 * @return The id of the comment that was created.
	 * @serialData 2023-02-12
	 * todo 发送通知
	 */
	Long commitComment(CommentRequest commentRequest);
	
	List<Comment> getCommentChildren(Long id, Long userId);
	
	Boolean commentLikes(Long id, Long userId);
	
	Boolean commentUnLikes(Long id, Long userId);
	
	Boolean commentCancelLikes(Long id, Long userId);
	
	Boolean deleteComment(Long id);
}
