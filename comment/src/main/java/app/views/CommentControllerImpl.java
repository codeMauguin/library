package app.views;

import Message.ResponseCode;
import Message.RestResponse;
import app.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pojo.PageInfo;

@RestController
public class CommentControllerImpl implements CommentController {
	private final CommentService commentService;
	
	public CommentControllerImpl(@Autowired CommentService commentService) {
		this.commentService = commentService;
	}
	
	@Override
	public RestResponse getCommentRoots(Long id, Long userId, PageInfo pageInfo) {
		return RestResponse.response(commentService.getCommentRoots(id, userId, pageInfo),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse getCommentChildren(Long id, Long userId) {
		return RestResponse.response(commentService.getCommentChildren(id, userId),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse commentLikes(Long id, Long userId) {
		return RestResponse.response(commentService.commentLikes(id, userId),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse commentCancelLikes(Long id, Long userId) {
		return RestResponse.response(commentService.commentCancelLikes(id, userId),
				ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse commentUnLikes(Long id, Long userId) {
		return RestResponse.response(commentService.commentUnLikes(id, userId),
				ResponseCode.SUCCESS);
	}
}
