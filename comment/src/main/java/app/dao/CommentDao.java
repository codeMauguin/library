package app.dao;


import app.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.PageInfo;

import java.util.List;

@Mapper
public interface CommentDao {
	/**
	 * > 查询一本书的根评论
	 *
	 * @param bookId   图书编号
	 * @param pageInfo PageInfo 是一个封装分页信息的类。
	 * @return 评论列表。
	 */
	List<Comment> queryCommentRoot(@Param("bookId") Long bookId,
								   @Param("pageInfo") PageInfo pageInfo);
	
	/**
	 * 通过 id 和 userId 查询评论。
	 *
	 * @param id     需要查询的评论id
	 * @param userId 正在查询评论的用户的用户ID。
	 * @return 评论对象。
	 */
	Comment queryComment(@Param("id") Long id, @Param("userId") Long userId);
	
	@Select("SELECT COUNT(id) FROM lb_comments WHERE bookId = #{bookId} AND root_id IS NULL ")
	Integer queryRootSize(@Param("bookId") Long bookId);
	
	Boolean commit(@Param("comment") Comment comment);
	
	List<Comment> queryCommentChildren(@Param("id") Long id);
	
	Boolean like(@Param("id") Long id, @Param("cardId") Long cardId);
	
	Boolean insertLike(@Param("id") Long id, @Param("userId") Long userId);
	
	Boolean insertUnLike(@Param("id") Long id, @Param("userId") Long userId);
	
	boolean unLike(@Param("id") Long id, @Param("userId") Long userId);
	
	Boolean cancelLike(@Param("id") Long id, @Param("userId") Long userId);
	
	List<Long> queryChild(@Param("id") Long id);
	
	Boolean deleteComment(@Param("ids") Long ids);
	Boolean deleteCommentInfo(@Param("ids") Long ids);
}
