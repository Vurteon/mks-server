package listener;



import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.io.IOException;

/**
 * author: 康乐
 * time: 2014/8/21
 * function: 当异步线程达到相应状态时，相应的方法将被执行，并通知客户端
 */

@WebListener()
public class DealPartThreadListener implements AsyncListener{


	@Override
	public void onComplete(AsyncEvent asyncEvent) throws IOException {
		asyncEvent.getSuppliedResponse().getWriter().write("OK");
//		System.out.println("存储线程完成----->Complete");
	}

	@Override
	public void onTimeout(AsyncEvent asyncEvent) throws IOException {
		System.out.println("TimeOut");
	}

	@Override
	public void onError(AsyncEvent asyncEvent) throws IOException {
		System.out.println("ERROR！！");
	}

	@Override
	public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
		System.out.println("Start");
	}
}
