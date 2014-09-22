package listener;

import dao.main.CachedRowSetDao;
import model.StatusRowSetManger;
import utils.ThreadPoolUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
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

		System.out.println("创建线程池");

		ThreadPoolExecutor cpuThreadPoolExecutor = new ThreadPoolExecutor(ThreadPoolUtils.CPUCORETHREADSNUMBER, ThreadPoolUtils.CPUMAXTHREADSNUMBER,
				ThreadPoolUtils.CPUTHREADSKEEPALIVETIME, ThreadPoolUtils.CPUTHREADSTIMEUNIT, ThreadPoolUtils.CPUTHREADSQUEUE, ThreadPoolUtils.HANDLER);

		ThreadPoolUtils.setCpuThreadPoolExecutor(cpuThreadPoolExecutor);

		ThreadPoolExecutor ioThreadPoolExecutor = new ThreadPoolExecutor(ThreadPoolUtils.IOCORETHREADSNUMBER, ThreadPoolUtils.IOMAXTHREADSNUMBER,
				ThreadPoolUtils.IOTHREADSKEEPALIVETIME, ThreadPoolUtils.IOTHREADSTIMEUNIT, ThreadPoolUtils.IOTHREADSQUEUE, ThreadPoolUtils.HANDLER);

		ThreadPoolUtils.setIoThreadPoolExecutor(ioThreadPoolExecutor);


		System.out.println("创建CacheRowSet，实现内存数据库");

		try {
			StatusRowSetManger.setStatusRowSet(CachedRowSetDao.buildNewCacheRowSet());

		} catch (SQLException e) {
			System.err.println("创建Cache出错，从新启动tomcat");
			e.printStackTrace();
		}


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

//		try {
//			StatusRowSetManger.statusRowSet.first();
//			do {
//				CachedRowSetDao.statusSynchronized(StatusRowSetManger.statusRowSet);
//			}while (StatusRowSetManger.statusRowSet.next());
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}

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
