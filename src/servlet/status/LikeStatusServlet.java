package servlet.status;

import listener.DealPartThreadsListener;
import model.photo.LoadMoreSmallPhoto;
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
 * time：2014/11/19
 * function：用户点赞或者取消赞的接口
 */

@WebServlet(name = "LikeStatusServlet",urlPatterns = "/like_status",asyncSupported = true)
public class LikeStatusServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String likerInfo = RequestInfoUtils.getPostContent(request);

		JSONObject likerInfoJson = new JSONObject(likerInfo);

		int liker;
		int rs_id;
		boolean isLike;
		if (likerInfoJson.has("rs_id") && likerInfoJson.has("isLike")) {
			rs_id = likerInfoJson.getInt("rs_id");
			liker = (Integer)request.getSession(false).getAttribute("ID");
			isLike = likerInfoJson.getBoolean("isLike");
		}else {
			StatusResponseHandler.sendStatus("status", ErrorCode.JSONFORMATERROR,response);
			return ;
		}

		AsyncContext asyncContext = request.startAsync();
		// 设置最多30秒必须得到相应
		asyncContext.setTimeout(30000);

		asyncContext.addListener(new DealPartThreadsListener());

		ThreadPoolUtils.getIoThreadPoolExecutor().submit(new StatusLikeHandler(liker, rs_id, isLike, asyncContext));
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
}
