package model.notify;

import java.util.concurrent.TimeUnit;

/**
 * author：康乐
 * time：2014/11/22
 * function：用于检测消息缓存的数量是否超出一定范围，并在
 * 超出的情况下向数据库写入一定量的数据
 */
public class OutOfMaxLengthHandler implements Runnable{

	/**
	 * 每隔10秒检测一次缓存消息的数量，查看其是否超出最大范围，如果
	 * 超出，调用超出处理方法进行数据库写入
	 */
	@Override
	public void run() {
		while (true) {
			if (NotifyCacheManager.getCachedmessagenumber() > NotifyCacheManager.MAXLENGTH) {
				int number = NotifyCacheManager.outOfMaxLengthHandler();
				System.out.println("此次写入数据库消息数量:" + number);
				continue;
			}
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
