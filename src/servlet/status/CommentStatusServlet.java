package servlet.status;

import listener.DealPartThreadsListener;
import model.status.StatusCommentHandler;
import model.status.StatusLikeHandler;
import utils.EnumUtil.ErrorCode;
import utils.RequestInfoUtils;
import utils.StatusResponseHandler;
import utils.ThreadPoolUtils;
import utils.json.JSONObject;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author：康乐
 * time：2014/11/21
 * function：向某条状态添加评论
 */

@WebServlet(name = "CommentStatusServlet", urlPatterns = "/comment_status", asyncSupported = true)
public class CommentStatusServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String commentInfo = RequestInfoUtils.getPostContent(request);

		JSONObject commentInfoJson = new JSONObject(commentInfo);

		int commenter;
		int commented;
		int rs_id;
		String content;
		if (commentInfoJson.has("commented") && commentInfoJson.has("rs_id") && commentInfoJson.has("content")) {
			rs_id = commentInfoJson.getInt("rs_id");
			commenter = (Integer) request.getSession(false).getAttribute("ID");
			commented = commentInfoJson.getInt("commented");
			content = commentInfoJson.getString("content");
		} else {
			StatusResponseHandler.sendStatus("status", ErrorCode.JSONFORMATERROR, response);
			return;
		}

		AsyncContext asyncContext = request.startAsync();
		// 设置最多30秒必须得到相应
		asyncContext.setTimeout(30000);

		asyncContext.addListener(new DealPartThreadsListener());

		ThreadPoolUtils.getIoThreadPoolExecutor().submit(new StatusCommentHandler(rs_id, commenter, commented, content, asyncContext));
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
}
