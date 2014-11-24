package model.notify;

import dao.notify.NotifyDao;
import utils.EnumUtil.ErrorCode;
import utils.StatusResponseHandler;
import utils.json.JSONArray;
import utils.json.JSONObject;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * author：康乐
 * time：2014/11/22
 * function：执行异步消息推送任务
 */
public class NotifyTask implements Runnable {

	// 标记位，用于标记是否在缓存中存在数据，用于查询后没有数据，在等待时检测该
	// 标记位，从而确定是否存在数据
	private int flag = 0;

	// 等待最大时长
	public static final int WAITEMAXTIME = 10;

	private int ID;

	private AsyncContext asyncContext;

	public NotifyTask(int ID, AsyncContext asyncContext) {
		this.ID = ID;
		this.asyncContext = asyncContext;
	}

	/**
	 * 首先检查是否有消息，如果没有，则循环延时20秒
	 * 每延时1秒，检查缓存数据是否存在，最后返回
	 */
	@Override
	public void run() {
		List<String> messageList;
		JSONArray message;
		String dbMessage;
		// 首先检查缓存
		messageList = NotifyCacheManager.getMessage(ID);
		// 然后检查数据库
		try {
			dbMessage = NotifyDao.getMessage(ID);
			// 如果数据存在，则格式化后写向客户端
			if (dbMessage != null || messageList != null) {
				/**
				 * 当数据库中存在信息，则将数据库中的信息写入json数组，然后检查
				 * 缓存中是否存在数据；当数据库中没有存在消息，那么缓存中一定存
				 * 在消息
				 */
				if (dbMessage != null) {
					message = new JSONArray(dbMessage);
					//如果缓存中存在数据
					if (messageList != null) {
						for (String messageString: messageList) {
							JSONObject jsonObject = new JSONObject(messageString);
							message.put(jsonObject);
						}
					}
				} else {
					message = new JSONArray();
					for (String messageString: messageList) {
						JSONObject jsonObject = new JSONObject(messageString);
						message.put(jsonObject);
					}
				}
				/**
				 * 写入客户端，如果成功，则从缓存和数据库中删除消息；
				 * 否则不做操作
				 */

				asyncContext.getResponse().getWriter().write(message.toString());

				// 执行数据删除操作
				if (messageList != null) {
					if (!NotifyCacheManager.removeMessage(ID))
						System.err.println("----------缓存删除推送消息失败--------------");

				}
				if (dbMessage != null) {
					if (!NotifyDao.removeMessage(ID))
						System.err.println("----------数据库删除推送消息失败--------------");
				}
				asyncContext.complete();
			} else {
				int i = 0;
				// 如果数据不存在，进入等待循环
				while (i < WAITEMAXTIME) {
					if (flag == 1) {
						/**
						 * 如果flag为1，表明在缓存中存在数据，则执行相关操作
						 */
						List<String> list;
						JSONArray jsonArray = new JSONArray();
						if ((list = NotifyCacheManager.getMessage(ID)) != null) {
							// 向客户端发送数据
							for (String messageObject:list) {
								JSONObject jsonObject = new JSONObject(messageObject);
								jsonArray.put(jsonObject);
							}
							asyncContext.getResponse().getWriter().write(jsonArray.toString());
							// 从缓存中删除数据
							if (!NotifyCacheManager.removeMessage(ID))
								System.err.println("----------缓存删除推送消息失败--------------");
							asyncContext.complete();
							// 从等待者中移除消息
							WaitingNotifyManager.removeWaiter(ID);
							return;
						}
					}
					i++;
					TimeUnit.SECONDS.sleep(2);
				}
				StatusResponseHandler.sendStatus("status", ErrorCode.NODATA,
						(HttpServletResponse) asyncContext.getResponse());
			}
		} catch (SQLException e) {
			StatusResponseHandler.sendStatus("status", ErrorCode.SQLERROR,
					(HttpServletResponse) asyncContext.getResponse());
			e.printStackTrace();
			System.err.println("----------数据库推送表出错--------------");
		} catch (IOException e) {
			/**
			 * 如果是flag为0时，IO出错，则删除操作并不会被执行；
			 * 当flag不为0时的IO出错，则需要将ID放入缓存队列，
			 * 以便可以写入数据库
			 */
			if (flag == 1) {
				NotifyCacheManager.addToIDQueue(ID);
			}
			e.printStackTrace();
			StatusResponseHandler.sendStatus("status", ErrorCode.IOERROR,
					(HttpServletResponse) asyncContext.getResponse());
			System.err.println("----------IO推送出错--------------");
		} catch (InterruptedException e) {
			StatusResponseHandler.sendStatus("status", ErrorCode.TIMEOUT,
					(HttpServletResponse) asyncContext.getResponse());
			e.printStackTrace();
			System.err.println("---------- 线程出错--------------");
		}
	}


	/**
	 * 用于设置消息标记位
	 */
	public void setMessageFlag() {
		flag = 1;
	}

}
