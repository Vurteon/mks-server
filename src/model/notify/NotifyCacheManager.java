package model.notify;

import dao.notify.NotifyDao;
import utils.json.JSONArray;
import utils.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * author：康乐
 * time：2014/11/22
 * function：管理notify缓存，提供对其的操作
 */
public class NotifyCacheManager {

	// 缓存容器初始值
	public static final int CACHEDMESSAGEINITNUMBER = 1000;

	// 容器最大值，暂定10W
	public static final int MAXLENGTH = 100000;

	// 暂定一次写入数据库5K
	public static final int WRITENUMBER = 5000;

	// 缓存容器，使用ConcurrentHashMap实现
	private static ConcurrentHashMap<Integer, List<String>> cacheMessageMap = new ConcurrentHashMap<Integer, List<String>>(CACHEDMESSAGEINITNUMBER);

	// 消息ID队列
	private static ConcurrentLinkedQueue<Integer> IDQueue = new ConcurrentLinkedQueue<Integer>();

	/**
	 * 向缓存中添加一条消息数据，其中用到的ID是整型类型，其所对于的value是
	 * 线程安全的list
	 *
	 * @param ID    需要添加数据的ID
	 * @param value 需要添加数据的ID
	 * @return 是否添加成
	 */
	public static boolean putMessage(int ID, String value) {
		NotifyTask notifyTask;
		if ((notifyTask = WaitingNotifyManager.getWaiter(ID)) != null) {
			notifyTask.setMessageFlag();
		} else {
			// 将ID放入ID队列中
			IDQueue.add(ID);
		}
		// 如果已经存在list，则向list中存放
		if (cacheMessageMap.containsKey(ID)) {
			return cacheMessageMap.get(ID).add(value);
		}

		// 如果不存在，则新建一个list，而且是线程安全型的
		List<String> messageList = Collections.synchronizedList(new ArrayList<String>());
		messageList.add(value);
		return cacheMessageMap.put(ID, messageList) != null;
	}

	/**
	 * 返回当前缓存消息的大小
	 *
	 * @return 放回当前消息的大小
	 */
	public static int getCachedmessagenumber() {
		return cacheMessageMap.size();
	}

	/**
	 * 从缓存中根据ID获得message
	 *
	 * @param ID 需要获取消息的ID
	 * @return 如果有消息，返回一个ArrayList包含所有的当前可获得的消息
	 */
	public static List<String> getMessage(int ID) {
		return cacheMessageMap.get(ID);
	}

	/**
	 * 从缓存中根据ID移除消息
	 *
	 * @param ID 需要被移除消息的ID
	 * @return 如果成功移除，返回true；否则返回false
	 */
	public static boolean removeMessage(int ID) {
		return cacheMessageMap.remove(ID) != null;
	}

	/**
	 * 如果缓存内容超出缓存容量，那么就需要向数据库中写入数据
	 *
	 * @return 写入数据的数量
	 */
	public static int outOfMaxLengthHandler() {

		int i = 0;
		int ID;
		/**
		 * 当写入数小于一次写入数时，且有数可写时，进行数据库写入
		 */
		while (i < WRITENUMBER && IDQueue.peek() != null) {
			ID = IDQueue.poll();

			// 数据库中的消息
			String dbMessage;
			JSONArray jsonArray;
			try {
				/**
				 * 如果在数据库中存在数据，那么就将其取出然后添加
				 */
				if ((dbMessage = NotifyDao.getMessage(ID)) != null) {
					jsonArray = new JSONArray(dbMessage);
				} else {
					jsonArray = new JSONArray();
				}
				List<String> list = cacheMessageMap.get(ID);
				/**
				 * 由于ID队列适用于记录消息进入缓存的先后顺序，从而为向数据库的
				 * 写入提供方便，但是并没有记录当缓存中的数据已经被客户端取走时
				 * 的情况，所以---在队列中的ID，不是每一个在缓存中都有其对应的
				 * 数据
				 */
				if (list != null) {
					for (String cachedMessage : list) {
						// 将缓存中的数据放入已经存在的json数组中
						jsonArray.put(new JSONObject(cachedMessage));
					}

					/**
					 * 如果已经存在数据，则更新数据。由于存在ID主键限制，所以这里
					 * 必须进行检查
 					 */
					if (dbMessage != null) {
						NotifyDao.updateMessage(ID, jsonArray.toString());
					}
					else {
						// 存入数据库
						NotifyDao.addMessage(ID, jsonArray.toString());
					}
					// 从缓存中删除相关ID的缓存数据
					NotifyCacheManager.removeMessage(ID);
					// 计数器加1
					i++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return i;
	}

	/**
	 * 将ID放入ID队列，当向客户端写入而又没有成功时，需要从新将ID数据写入
	 * ID队列
	 * @param ID 需要被放入的ID
	 * @return 写入ID队列是否成功
	 */
	public static boolean addToIDQueue(int ID) {
		return IDQueue.add(ID);
	}
}
