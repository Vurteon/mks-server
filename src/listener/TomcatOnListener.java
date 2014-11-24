package listener;

import dao.cache.CachedRowSetDao;
import model.notify.NotifyCacheManager;
import model.notify.OutOfMaxLengthHandler;
import model.notify.WaitingNotifyManager;
import model.status.StatusRowSetManager;
import org.dom4j.DocumentException;
import utils.ThreadPoolUtils;
import utils.db.ConnectionFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.beans.PropertyVetoException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.*;


/**
 * author: 康乐
 * time: 2014/7/28
 * function: 用于tomcat启动的时候加载数据库、创建线程池等全局初始化任务
 */


@WebListener()
public class TomcatOnListener implements ServletContextListener{


	// Public constructor is required by servlet spec
	public TomcatOnListener() {

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		System.out.println("我是一个监听器，监听tom猫");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("MySql数据库驱动程序加载成功----TomcatOnListener");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		ThreadPoolExecutor cpuThreadPoolExecutor = new ThreadPoolExecutor(ThreadPoolUtils.CPUCORETHREADSNUMBER, ThreadPoolUtils.CPUMAXTHREADSNUMBER,
				ThreadPoolUtils.CPUTHREADSKEEPALIVETIME, ThreadPoolUtils.CPUTHREADSTIMEUNIT, ThreadPoolUtils.CPUTHREADSQUEUE, ThreadPoolUtils.HANDLER);

		ThreadPoolUtils.setCpuThreadPoolExecutor(cpuThreadPoolExecutor);

		ThreadPoolExecutor ioThreadPoolExecutor = new ThreadPoolExecutor(ThreadPoolUtils.IOCORETHREADSNUMBER, ThreadPoolUtils.IOMAXTHREADSNUMBER,
				ThreadPoolUtils.IOTHREADSKEEPALIVETIME, ThreadPoolUtils.IOTHREADSTIMEUNIT, ThreadPoolUtils.IOTHREADSQUEUE, ThreadPoolUtils.HANDLER);

		ThreadPoolUtils.setIoThreadPoolExecutor(ioThreadPoolExecutor);

		System.out.println("创建线程池成功");



		try {
			ConnectionFactory.initConPool();
		} catch (DocumentException e) {
			e.printStackTrace();
			System.out.println("初始化数据库连接池失败---------");
			System.exit(-1);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			System.out.println("初始化数据库连接池失败---------");
			System.exit(-1);
		}


		try {
			StatusRowSetManager.setStatusRowSet(CachedRowSetDao.buildNewCacheRowSet());
		} catch (SQLException e) {
			System.err.println(new Date() + "创建Cache出错，退出tomcat");
			System.err.println(new Date() + "创建Cache出错，退出tomcat");
			System.err.println(new Date() + "创建Cache出错，退出tomcat");
			System.err.println(new Date() + "创建Cache出错，退出tomcat");
			e.printStackTrace();
			// 直接退出JVM
			System.exit(-1);
		}
		System.out.println("创建CacheRowSet，构造缓存成功");

		// 启动推送消息缓存监视线程成功
		new Thread(new OutOfMaxLengthHandler()).start();
		System.out.println("启动消息推送缓存监视线程成功");
	}


	/**
	 * 在结束tomcat的时候，做下面的事情，网上说的是可以防止一个警告、
	 * @param servletContextEvent 现在还没有多少用处
	 */

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {

		/**
		 * 将缓存中的所有数据同步到数据库
		 */
		try {
			StatusRowSetManager.statusRowSet.first();
			do {
				CachedRowSetDao.statusSynchronized(StatusRowSetManager.statusRowSet);
			}while (StatusRowSetManager.statusRowSet.next());
		} catch (SQLException e) {
			// 暂时无法处理
			e.printStackTrace();
			System.err.println(new Date() + "----------------->>>>重大错误，重大错误，缓存批量写入DB出错，数据丢失！！！");
			System.err.println(new Date() + "----------------->>>>重大错误，重大错误，缓存批量写入DB出错，数据丢失！！！");
			System.err.println(new Date() + "----------------->>>>重大错误，重大错误，缓存批量写入DB出错，数据丢失！！！");
			System.err.println(new Date() + "----------------->>>>重大错误，重大错误，缓存批量写入DB出错，数据丢失！！！");
			System.err.println(new Date() + "----------------->>>>重大错误，重大错误，缓存批量写入DB出错，数据丢失！！！");
			System.err.println(new Date() + "----------------->>>>重大错误，重大错误，缓存批量写入DB出错，数据丢失！！！");
			System.err.println(new Date() + "----------------->>>>重大错误，重大错误，缓存批量写入DB出错，数据丢失！！！");
			System.err.println(new Date() + "----------------->>>>重大错误，重大错误，缓存批量写入DB出错，数据丢失！！！");
		}

		/**
		 * 将推送消息写入数据库
		 */

		while (NotifyCacheManager.getCachedmessagenumber() > 0) {
			NotifyCacheManager.outOfMaxLengthHandler();
		}
		System.out.println("推送消息写入数据库成功");


		/**
		 * 移除所有相关组件
		 */
		Enumeration<Driver> driverEnumeration = DriverManager.getDrivers();
		while (driverEnumeration.hasMoreElements()) {
			Driver driver = driverEnumeration.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("See you,Tom猫");
	}
}
