package servlet.main;

import listener.DealPartThreadListener;
import model.uploadpart.DealPart;
import utils.ThreadPoolUtils;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.Collection;

/**
 * author: 康乐
 * time: 2014/8/17
 * function: 接受手机端发来的上传照片请求，并将照片存储并生成相应的记录
 */

@WebServlet(urlPatterns = "/UploadPhoto", asyncSupported = true)
@MultipartConfig()
public class UploadPhotoServlet extends HttpServlet {


	/**
	 * @param request  请求对象
	 * @param response 回应对象
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession httpSession = request.getSession(false);

		int ID = (Integer) httpSession.getAttribute("ID");

		// 获得相关的上传数据
		Collection<Part> parts = request.getParts();

		// 获得异步线程执行所需要的上下文
		AsyncContext asyncContext = request.startAsync();

		// 设置异步线程执行最长消耗时间20秒
		asyncContext.setTimeout(20000);

		// 给此异步线程加入监听器
		asyncContext.addListener(new DealPartThreadListener());

		// 将异步线程放入线程池中执行
		ThreadPoolUtils.getCpuThreadPoolExecutor().submit(new DealPart(asyncContext, parts, ID));

	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 直接返回404错误代码
		response.sendError(404);
	}
}


