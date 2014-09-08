package servlet.phone.main;

import listener.DealPartThreadListener;
import model.main.LoadStatus;
import utils.CreateJson;
import utils.EnumUtil.ErrorCodeJson;
import utils.RequestInfoUtils;
import utils.ThreadPoolUtils;
import utils.json.JSONObject;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
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
			response.getWriter().write(ErrorCodeJson.JSONERROR.toString());
			return ;
		}

		JSONObject jsonObject = CreateJson.getJsonObject(content);

		int rs_id;
		boolean before;
		if (jsonObject.has("rs_id") && jsonObject.has("before")) {
			rs_id = jsonObject.getInt("rs_id");
			before = jsonObject.getBoolean("before");
		}else{
			// 向客户端返回json格式不满足要求
			response.getWriter().write(ErrorCodeJson.JSONERROR.toString());
			return ;
		}
		// 当前用户的所有关注的人
		HashSet<Integer> followings = (HashSet<Integer>)httpSession.getAttribute("followings");
		AsyncContext asyncContext = request.startAsync();

		// 设置异步线程执行最长消耗时间20秒
		asyncContext.setTimeout(20000);

		// 给此异步线程加入监听器
		asyncContext.addListener(new DealPartThreadListener());
		// 将任务提交到IO型线程池中进行处理
		ThreadPoolUtils.getIoThreadPoolExecutor().submit(new LoadStatus(asyncContext,followings,rs_id,before));
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//
		response.sendError(404);
	}
}
