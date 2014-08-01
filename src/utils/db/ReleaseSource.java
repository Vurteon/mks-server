package utils.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * author: 康乐
 * time: 记不起了
 * change-time: 2014/7/31
 * function: 释放数据库资源，包括Connection,PreparedStatement和ResultSet
 */
public class ReleaseSource {

	public static boolean releaseSource(Connection connection) {

		if(connection != null){
			try {
				connection.close();
				return true;
			} catch (SQLException e) {
				System.err.println("释放Connection资源出错");
				e.printStackTrace();
			}
		}
		return false;
	}


	public static boolean releaseSource(PreparedStatement preparedStatement) {

		if (preparedStatement != null) {
			try {
				preparedStatement.close();
				return true;
			} catch (SQLException e) {
				System.err.println("释放PreparedStatement资源出错");
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean releaseSource(ResultSet resultSet,PreparedStatement preparedStatement) {

		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				System.err.println("释放ResultSet出错");
				e.printStackTrace();
			}finally {
				if(preparedStatement != null){
					releaseSource(preparedStatement);
				}
			}
		}

		if (preparedStatement != null) {
			releaseSource(preparedStatement);
			return true;
		}
		return false;
	}
}
