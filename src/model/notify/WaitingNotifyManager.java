package model.notify;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author：康乐
 * time：2014/11/23
 * function：用于存放和管理客户端请求（消息推送）对象
 */
public class WaitingNotifyManager {

	// map最大值
	public static final int MAXNOTIFYWAITERNUMBER = 1000;

	// 初始化大小
	public static final int WAITENOTIFYINITNUMBER = 100;

	// 在线等待容器
	private static ConcurrentHashMap<Integer, NotifyTask> waiteMessageIdMap =
			new ConcurrentHashMap<Integer, NotifyTask>(WAITENOTIFYINITNUMBER);


	/**
	 * 获取等待者对象
	 * @param ID 需要获取的等待者的ID
	 * @return 等待者对象
	 */
	public static NotifyTask getWaiter(int ID) {
		return waiteMessageIdMap.get(ID);
	}



	/**
	 * 将等待者放入map
	 * @param ID 等待者ID
	 * @param waiter 等待者执行对象
	 * @return 放入成功，返回true；否则返回false
	 */
	public static boolean putWaiter(int ID, NotifyTask waiter) {
		return waiteMessageIdMap.put(ID, waiter) != null;
	}

	/**
	 * 将等待者移除
	 * @param ID 需要被移除的等待者
	 * @return 是否移除成功
	 */
	public static boolean removeWaiter(int ID) {
		return waiteMessageIdMap.remove(ID) != null;
	}

	/**
	 * 获取等待者的个数
	 * @return 等待者的个数
	 */
	public static int getWaiterNumber() {
		return waiteMessageIdMap.size();
	}


}
