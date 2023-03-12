package app.views;

import Message.ResponseCode;
import Message.RestResponse;
import app.pojo.CommentRequest;
import app.service.CommentService;
import app.utils.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommitCommentImpl implements CommitComment {
	private final CommentService commentService;
	
	public CommitCommentImpl(@Autowired CommentService commentService) {
		this.commentService =
				commentService;
	}
	
	@Override
	public RestResponse commit(CommentRequest comment) {
		Long aLong = commentService.commitComment(comment);
		if (aLong == null) return RestResponse.response(ResponseCode.FAIL_COMMIT_COMMENT);
		return RestResponse.response(aLong, ResponseCode.SUCCESS);
	}
	
	@Override
	public RestResponse deleteComment(Long id, Long userId) {
		Long currentId = Token.getCurrentId();
		if (!userId.equals(currentId)) {
			return RestResponse.response(ResponseCode.ERROR_ROLE);
		}
		return RestResponse.response(commentService.deleteComment(id),ResponseCode.SUCCESS);
	}
}
