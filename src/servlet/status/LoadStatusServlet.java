package servlet.status;

import listener.DealPartThreadsListener;
import model.status.LoadStatus;
import utils.EnumUtil.ErrorCode;
import utils.JsonUtils;
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
 * author: 康乐
 * time: 2014/9/3
 * function: 更新最多10条的状态信息到客户端
 */
@WebServlet(name = "LoadStatusServlet",urlPatterns = "/load_status",asyncSupported = true)
public class LoadStatusServlet extends HttpServlet {

	/**
	 * 更新算法为,由客户端发来当前最新状态的rs_id资源标志,服务器将
	 * 返回rs_id以前的最多10条数据
	 *
	 * @param request 请求对象
	 * @param response 回应对象
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession httpSession = request.getSession(false);

		String content = RequestInfoUtils.getPostContent(request);

		if (content == null) {
			response.sendError(404);
			return ;
		}

		JSONObject jsonObject = new JSONObject(content);

		/**
		 * rs_id:客户端传来的资源标记信息
		 * before:标记是更新还是加载更多
		 */
		int rs_id;
		boolean before;

		if (jsonObject.has("rs_id") && jsonObject.has("before")) {
			rs_id = jsonObject.getInt("rs_id");
			before = jsonObject.getBoolean("before");
		}else {
			StatusResponseHandler.sendStatus("status",ErrorCode.JSONFORMATERROR,response);
			return ;
		}

		// 当前用户的所有关注的人
		HashSet<Integer> followings = (HashSet<Integer>)httpSession.getAttribute("followings");
		AsyncContext asyncContext = request.startAsync();

		// 设置异步线程执行最长消耗时间20秒
		asyncContext.setTimeout(20000);

		// 给此异步线程加入监听器
		asyncContext.addListener(new DealPartThreadsListener());
		// 将任务提交到IO型线程池中进行处理
		ThreadPoolUtils.getIoThreadPoolExecutor().submit(new LoadStatus(asyncContext,followings,rs_id,before));
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("hello_world!");
		response.getWriter().close();
	}
}
