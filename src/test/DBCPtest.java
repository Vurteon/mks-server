package test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by leon on 2014/11/4.
 */
public class DBCPtest {

	public static void main(String[] args) throws SQLException, PropertyVetoException, DocumentException {
		//创建C3P0连接池
		ComboPooledDataSource dataSource = new ComboPooledDataSource();

		dataSource.setDriverClass("com.mysql.jdbc.Driver");

		dataSource.setJdbcUrl("jdbc:mysql://120.24.68.64:3306/moment");

		dataSource.setUser("moment");

		dataSource.setPassword("520520");

		File file = new File("F:/mks-server/web/WEB-INF/c3p0_con_pool.xml");

		SAXReader saxReader = new SAXReader();

		Document document = saxReader.read(file);

		Element root = document.getRootElement();

		Element element = root.element("default-config");

		String name = element.elementText("user");

		System.out.println(name);


	}

}
