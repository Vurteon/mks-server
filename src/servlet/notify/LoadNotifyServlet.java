package servlet.notify;

import listener.DealPartThreadsListener;
import listener.NotifyThreadsListener;
import model.notify.NotifyTask;
import model.notify.WaitingNotifyManager;
import utils.EnumUtil.ErrorCode;
import utils.StatusResponseHandler;
import utils.ThreadPoolUtils;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author：康乐
 * time：2014/11/23
 * function：用于客户端获取推送消息
 */

@WebServlet(name = "LoadNotifyServlet",urlPatterns = "/load_notify",asyncSupported = true)
public class LoadNotifyServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 首先检查是否还存在线程资源，如果存在，继续；否则拒绝服务
		if (WaitingNotifyManager.getWaiterNumber() >= WaitingNotifyManager.MAXNOTIFYWAITERNUMBER) {
			StatusResponseHandler.sendStatus("status", ErrorCode.NOMORETHREAD, response);
			return ;
		}

		int ID = (Integer)request.getSession(false).getAttribute("ID");

		AsyncContext asyncContext = request.startAsync();
		// 设置最多120秒必须得到相应
		asyncContext.setTimeout(120000);

		asyncContext.addListener(new NotifyThreadsListener());

		NotifyTask notifyTask = new NotifyTask(ID, asyncContext);

		// 将等待者放入等待map
		WaitingNotifyManager.putWaiter(ID,notifyTask);

		ThreadPoolUtils.getIoThreadPoolExecutor().submit(notifyTask);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
}
