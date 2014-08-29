package utils.db;

import javax.sql.rowset.CachedRowSet;
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

	/**
	 * 释放数据库连接Connection资源
	 * @param connection 数据库连接对象
	 * @return 是否成功释放资源
	 */
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


	/**
	 * 释放数据库PreparedStatement资源
	 * @param preparedStatement PreparedStatement对象
	 * @return 释放释放资源成功
	 */
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

	/**
	 * 释放ResultSet、PreparedStatement对象
	 * @param resultSet 需要释放资源的结果集对象
	 * @param preparedStatement PreparedStatement对象
	 * @return 均释放成功返回true，否则返回false
	 */
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


	/**
	 * 释放CachedRowSet、ResultSet、PreparedStatement对象
	 * @param cachedRowSet CachedRowSet 对象
	 * @param resultSet ResultSet结果集对象
	 * @param preparedStatement  PreparedStatement对象
	 * @return 均释放成功返回true，否则返回false
	 */
	public static boolean releaseSource(CachedRowSet cachedRowSet,ResultSet resultSet,PreparedStatement preparedStatement) {
		if(cachedRowSet != null) {
			try {
				cachedRowSet.close();
				return releaseSource(resultSet,preparedStatement);
			} catch (SQLException e) {
				System.err.println("释放RawSet资源出错");
				e.printStackTrace();
			}
		}
		return false;
	}


	/**
	 * 释放CacheRowSet对象
	 * @param cachedRowSet 需要释放的对象
	 * @return 如果释放成功返回true，否则返回false
	 */
	public static boolean releaseSource (CachedRowSet cachedRowSet) {
		if (cachedRowSet != null) {
			try {
				cachedRowSet.close();
			} catch (SQLException e) {
				System.err.println("释放RawSet资源出粗");
				e.printStackTrace();
			}
		}
		return false;
	}
}
