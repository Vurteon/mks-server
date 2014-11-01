package utils.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 */
public class ConnectionFactory {

	private static Connection con;
	/**
	 *
	 * @return 数据库mysql的连接
	 */
	public static Connection getMySqlConnection(){

		if (con == null) {
			try {
				String url = "jdbc:mysql://120.24.68.64:3306/moment";
				String user = "moment";
				String password = "520520";
				return DriverManager.getConnection(url, user, password);
			} catch (SQLException e) {
				System.err.println("获取Mysql数据库连接出错----GetConnection");
				e.printStackTrace();
			}
		}else {
			return con;
		}
		return null;
	}

	public static void setCon(Connection con) {
		ConnectionFactory.con = con;
	}
}
