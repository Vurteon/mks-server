package servlet.user;

import listener.DealPartThreadsListener;
import model.status.LoadStatus;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;

/**
 * author：康乐
 * time:2014/11/18
 * function：依据ID号返回该ID号的最多10条status
 */

@WebServlet(name = "LoadStatusByIDServlet",urlPatterns = "/load_status_by_id",asyncSupported = true)
public class LoadStatusByIDServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String content = RequestInfoUtils.getPostContent(request);

		if (content == null) {
			response.sendError(404);
			return ;
		}

		JSONObject jsonObject = new JSONObject(content);

		/**
		 * rs_id:客户端传来的资源标记信息
		 * before:标记是更新还是加载更多
		 * ID:需要被获取资源的ID号
		 */
		int rs_id;
		boolean before;
		int ID;

		if (jsonObject.has("rs_id") && jsonObject.has("before") && jsonObject.has("ID")) {
			rs_id = jsonObject.getInt("rs_id");
			before = jsonObject.getBoolean("before");
			ID = jsonObject.getInt("ID");
		}else {
			StatusResponseHandler.sendStatus("status", ErrorCode.JSONFORMATERROR, response);
			return ;
		}

		// 当前用户的所有关注的人
		HashSet<Integer> followings = new HashSet<Integer>();
		followings.add(ID);
		AsyncContext asyncContext = request.startAsync();

		// 设置异步线程执行最长消耗时间20秒
		asyncContext.setTimeout(20000);

		// 给此异步线程加入监听器
		asyncContext.addListener(new DealPartThreadsListener());
		// 将任务提交到IO型线程池中进行处理
		ThreadPoolUtils.getIoThreadPoolExecutor().submit(new LoadStatus(asyncContext,followings,rs_id,before));
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("load_status_by_id!");
		response.getWriter().close();
	}
}
