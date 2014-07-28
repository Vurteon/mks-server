package listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingEvent;


/**
 * author: 康乐
 * time: 2014/7/28
 * function: 用于tomcat启动的时候加载数据库等全局初始化任务
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

	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		System.out.println("See you,Tom猫");
	}
}
