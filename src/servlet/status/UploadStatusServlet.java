package servlet.status;

import listener.DealPartThreadsListener;
import model.status.uploadpart.DealPart;
import utils.EnumUtil.ErrorCode;
import utils.StatusResponseHandler;
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

@WebServlet(name = "UploadPhotoServlet", urlPatterns = "/UploadPhoto", asyncSupported = true)
@MultipartConfig()
public class UploadStatusServlet extends HttpServlet {


	/**
	 * @param request  请求对象
	 * @param response 回应对象
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 由于filter已经做过session检查，所以这里一定能得到session
		HttpSession httpSession = request.getSession(false);

		int ID = (Integer) httpSession.getAttribute("ID");

		Collection<Part> parts;

		try {
			// 获得相关的上传数据
			parts = request.getParts();
		}catch (Exception e) {
			// 如果headers中的没有标明是multi-part数据类型，则报错，返回客户端信息
			e.printStackTrace();
			StatusResponseHandler.sendStatus("status", ErrorCode.MULTIPARTERROR, response);
			return;
		}

		// 获得异步线程执行所需要的上下文
		AsyncContext asyncContext = request.startAsync();

		// 设置异步线程执行最长消耗时间200秒
		asyncContext.setTimeout(200000);

		// 给此异步线程加入监听器
		asyncContext.addListener(new DealPartThreadsListener());

		// 将异步线程放入线程池中执行
		ThreadPoolUtils.getCpuThreadPoolExecutor().submit(new DealPart(asyncContext, parts, ID));
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("hello_world!");
		response.getWriter().close();
	}
}


