package listener;

import utils.EnumUtil.ErrorCode;
import utils.StatusResponseHandler;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author：康乐
 * time：2014/11/24
 * function：用于检测消息推送线程
 */
public class NotifyThreadsListener implements AsyncListener {
	@Override
	public void onComplete(AsyncEvent asyncEvent) throws IOException {
		System.out.println("done");
	}

	@Override
	public void onTimeout(AsyncEvent asyncEvent) throws IOException {
		StatusResponseHandler.sendStatus("status", ErrorCode.TIMEOUT,
				(HttpServletResponse) asyncEvent.getSuppliedResponse());
	}

	@Override
	public void onError(AsyncEvent asyncEvent) throws IOException {

	}

	@Override
	public void onStartAsync(AsyncEvent asyncEvent) throws IOException {

	}
}
