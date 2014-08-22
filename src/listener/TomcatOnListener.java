package listener;

import utils.ThreadPoolUtil;

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

		ThreadPoolExecutor cpuThreadPoolExecutor = new ThreadPoolExecutor(ThreadPoolUtil.CPUCORETHREADSNUMBER,ThreadPoolUtil.CPUMAXTHREADSNUMBER,
				ThreadPoolUtil.CPUTHREADSKEEPALIVETIME,ThreadPoolUtil.CPUTHREADSTIMEUNIT,ThreadPoolUtil.CPUTHREADSQUEUE,ThreadPoolUtil.HANDLER);

		ThreadPoolUtil.setCpuThreadPoolExecutor(cpuThreadPoolExecutor);

		ThreadPoolExecutor ioThreadPoolExecutor = new ThreadPoolExecutor(ThreadPoolUtil.IOCORETHREADSNUMBER,ThreadPoolUtil.IOMAXTHREADSNUMBER,
				ThreadPoolUtil.IOTHREADSKEEPALIVETIME,ThreadPoolUtil.IOTHREADSTIMEUNIT,ThreadPoolUtil.IOTHREADSQUEUE,ThreadPoolUtil.HANDLER);

		ThreadPoolUtil.setIoThreadPoolExecutor(ioThreadPoolExecutor);
	}


	/**
	 * 在结束tomcat的时候，做下面的事情，网上说的是可以防止一个警告、
	 * @param servletContextEvent 现在还没有多少用处
	 */

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {

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
