package servlet.photo;

import listener.DealPartThreadsListener;
import model.photo.LoadMoreSmallPhoto;
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
 * time：2014/11/14
 * function：返回给定id的最多15个缩略图的地址信息，这些缩略图的rs_id紧跟着客户端所给的
 *           rs_id后
 */
@WebServlet(name = "GetMoreSmallPhotoServlet",urlPatterns = "/get_more_small_photo",asyncSupported = true)
public class GetMoreSmallPhotoServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String requestInfo = RequestInfoUtils.getPostContent(request);

		JSONObject jsonObject = new JSONObject(requestInfo);

		System.out.println(jsonObject);

		int ID;
		int rs_id;

		if (jsonObject.has("ID") && jsonObject.has("rs_id")) {
			ID = jsonObject.getInt("ID");
			rs_id = jsonObject.getInt("rs_id");
		}else {
			StatusResponseHandler.sendStatus("status", ErrorCode.JSONFORMATERROR,response);
			return ;
		}

		AsyncContext asyncContext = request.startAsync();
		// 设置最多10秒必须得到相应
		asyncContext.setTimeout(10000);

		asyncContext.addListener(new DealPartThreadsListener());

		ThreadPoolUtils.getIoThreadPoolExecutor().submit(new LoadMoreSmallPhoto(asyncContext, ID, rs_id));

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("get_more_small_photo!");
		response.getWriter().close();
	}
}
