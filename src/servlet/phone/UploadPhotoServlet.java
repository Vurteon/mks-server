package servlet.phone;

import listener.DealPartThreadListener;
import model.uploadpart.DealPart;
import utils.ThreadPoolUtil;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * author: 康乐
 * time: 2014/8/17
 * function: 接受手机端发来的上传照片请求，并将照片存储并生成相应的记录
 */

@WebServlet(urlPatterns = "/UploadPhoto",asyncSupported = true)
@MultipartConfig()
public class UploadPhotoServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


//		HttpSession httpSession = request.getSession(false);
//
//		long ID = (Long) httpSession.getAttribute("ID");

		Collection<Part> parts = request.getParts();

		AsyncContext asyncContext = request.startAsync();

		asyncContext.addListener(new DealPartThreadListener());

		ThreadPoolUtil.getCpuThreadPoolExecutor().submit(new DealPart(asyncContext,parts,18320l));

		try {
			TimeUnit.SECONDS.sleep(1);

			System.out.println("cpu型任务等待队列长度：" + ThreadPoolUtil.CPUTHREADSQUEUE.size());
			System.out.println("io型任务登对队列长度：" + ThreadPoolUtil.IOTHREADSQUEUE.size());

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("servlet 线程完成。");
	}



	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}


