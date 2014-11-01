package listener;



import utils.EnumUtil.ErrorCode;
import utils.StatusResponseHandler;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * author: 康乐
 * time: 2014/8/21
 * function: 当异步线程达到相应状态时，相应的方法将被执行，并通知客户端
 */

@WebListener()
public class DealPartThreadsListener implements AsyncListener{

	/**
	 * 当upload照片所有操作全部成功后，会调用此方法
	 * @param asyncEvent 异步事件对象
	 * @throws IOException
	 */
	@Override
	public void onComplete(AsyncEvent asyncEvent) throws IOException {
		StatusResponseHandler.sendStatus("status", ErrorCode.UPLOADSUCCESS,
				(HttpServletResponse) asyncEvent.getSuppliedResponse());
	}

	/**
	 * 服务器处理超时；有两种可能，一是确实请求太多忙不过来，二是由于请求太多，请求被
	 * 服务器拒绝
	 * @param asyncEvent 异步事件对象
	 * @throws IOException
	 */
	@Override
	public void onTimeout(AsyncEvent asyncEvent) throws IOException {
		StatusResponseHandler.sendStatus("status", ErrorCode.UPLOADTIMEOUT,
				(HttpServletResponse) asyncEvent.getSuppliedResponse());
	}

	/**
	 * 服务器出现突发性错误的时候调用
	 * @param asyncEvent 异步事件对象
	 * @throws IOException
	 */
	@Override
	public void onError(AsyncEvent asyncEvent) throws IOException {
		System.err.println("紧急事态--------------》》》》》线程出现ERROR！！");
		System.err.println("紧急事态--------------》》》》》线程出现ERROR！！");
		System.err.println("紧急事态--------------》》》》》线程出现ERROR！！");
		System.err.println("紧急事态--------------》》》》》线程出现ERROR！！");
	}

	/**
	 * 异步线程开始时所触发的函数
	 * @param asyncEvent 异步事件对象
	 * @throws IOException
	 */
	@Override
	public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
		System.out.println("Start");
	}
}
