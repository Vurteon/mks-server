package utils.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by leon on 2014/7/28.
 */
public class GetConnection {

	private static String url = "jdbc:mysql://127.0.0.1:3306/moment";
	private static String user = "root";
	private static String password = "520520";

	public static Connection getMySqlConnection(){
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.err.println("获取Mysql数据库连接出错----GetConnection");
			e.printStackTrace();
		}
		return null;
	}
}
