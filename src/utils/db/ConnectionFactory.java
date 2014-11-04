package utils.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 */
public class ConnectionFactory {

	/**
	 * 数据库连接池的配置文件位置
	 */
	public static final String configFile = "F:/mks-server/web/WEB-INF/c3p0_con_pool.xml";

	/**
	 * 数据库连接池对象，用于初始化连接池和获得与数据库的连接
	 */
	public static final ComboPooledDataSource dataSource = new ComboPooledDataSource();


	/**
	 * 返回一个mysql的数据库连接
	 * @return mysql数据库连接
	 * @throws SQLException
	 */
	public static Connection getMySqlConnection() throws SQLException {
		return dataSource.getConnection();
	}


	/**
	 * 初始化数据库连接池
	 * @throws DocumentException
	 * @throws PropertyVetoException
	 */
	public static void initConPool () throws DocumentException, PropertyVetoException {

		File file = new File(configFile);

		SAXReader saxReader = new SAXReader();

		// 获得默认此次需要的配置
		Element config = saxReader.read(file).getRootElement().element("default-config");

		Iterator iterator = config.elementIterator();

		HashMap<String,String> property = new HashMap<String, String>();

		while (iterator.hasNext()) {
			Element element = (Element)iterator.next();
			String propertyName = element.getName();
			String propertyValue = element.getStringValue();
			property.put(propertyName, propertyValue);
		}

		// 设置数据库驱动程序
		dataSource.setDriverClass(property.get("driverClass"));
		// 设置数据库url
		dataSource.setJdbcUrl(property.get("jdbcUrl"));
		// 设置用户名
		dataSource.setUser(property.get("user"));
		// 设置用户密码
		dataSource.setPassword(property.get("password"));

	}
}
