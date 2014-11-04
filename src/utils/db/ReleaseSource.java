package utils.db;

import javax.sql.rowset.CachedRowSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * author: 康乐
 * time: 记不起了
 * change-time: 2014/7/31
 * function: 释放数据库资源，包括Connection、PreparedStatement、ResultSet、CachedRowSet、
 *           Statement
 */
public class ReleaseSource {

	/**
	 * 释放数据库连接Connection资源
	 * @param connection 数据库连接对象
	 * @return 是否成功释放资源,如果connection为null,返回false
	 */
	public static boolean releaseSource(Connection connection) {

		if(connection != null){
			try {
				connection.close();
				return true;
			} catch (SQLException e) {
				System.err.println("释放Connection资源出错");
				e.printStackTrace();
				return false;
			}
		}else {
			return true;
		}

	}


	/**
	 * 释放数据库PreparedStatement资源
	 * @param preparedStatement PreparedStatement对象
	 * @return 释放释放资源成功,如果preparedStatement为null,返回false
	 */
	public static boolean releaseSource(PreparedStatement preparedStatement) {

		if (preparedStatement != null) {
			try {
				preparedStatement.close();
				return true;
			} catch (SQLException e) {
				System.err.println("释放PreparedStatement资源出错");
				e.printStackTrace();
				return false;
			}
		}else {
			return true;
		}
	}


	/**
	 * 释放statement对象
	 * @param statement 需要释放的资源对象
	 * @return 如果释放成功返回true，如果statement为null,返回false
	 */
	public static boolean releaseSource(Statement statement) {

		if (statement != null) {
			try {
				statement.close();
				return true;
			} catch (SQLException e) {
				System.err.println("释放Statement资源出错");
				e.printStackTrace();
				return false;
			}
		}else {
			return true;
		}
	}

	/**
	 * 释放ResultSet对象
	 * @param resultSet 需要释放的资源对象
	 * @return 如果释放成功返回true，如果resultSet为null,返回false
	 */
	public static boolean releaseSource(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}else {
			return true;
		}
	}

	/**
	 * 释放CacheRowSet对象
	 * @param cachedRowSet 需要释放的对象
	 * @return 如果释放成功返回true，如果cachedRowSet为null,返回false
	 */
	public static boolean releaseSource (CachedRowSet cachedRowSet) {
		if (cachedRowSet != null) {
			try {
				cachedRowSet.close();
				return true;
			} catch (SQLException e) {
				System.err.println("释放RawSet资源出错");
				e.printStackTrace();
				return false;
			}
		}else {
			return true;
		}
	}


	/**
	 * 释放ResultSet、PreparedStatement对象
	 * @param resultSet 需要释放资源的结果集对象
	 * @param preparedStatement PreparedStatement对象
	 * @return 均释放成功返回true；否则为false
	 */
	public static boolean releaseSource(ResultSet resultSet,PreparedStatement preparedStatement) {
		return releaseSource(resultSet) &&
				releaseSource(preparedStatement);
	}

	/**
	 * 释放ResultSet、PreparedStatement、Connection三种资源
	 * @param resultSet 结果集
	 * @param preparedStatement 临时表
	 * @param connection 数据库连接
	 * @return 如果全部释放成功，返回true；否则返回false
	 */
	public static boolean releaseSource(ResultSet resultSet,PreparedStatement preparedStatement,Connection connection) {

		return releaseSource(resultSet) &&
				releaseSource(preparedStatement) &&
				releaseSource(connection);
	}


	/**
	 * 释放CachedRowSet、ResultSet、PreparedStatement对象
	 * @param cachedRowSet CachedRowSet 对象
	 * @param resultSet ResultSet结果集对象
	 * @param preparedStatement  PreparedStatement对象
	 * @return 均释放成功返回true；否则为false
	 */
	public static boolean releaseSource(CachedRowSet cachedRowSet,ResultSet resultSet,PreparedStatement preparedStatement) {
		return releaseSource(cachedRowSet) && releaseSource(resultSet)
				&& releaseSource(preparedStatement);
	}

	/**
	 * 释放PreparedStatement、con对象
	 * @param preparedStatement 临时表
	 * @param connection con
	 * @return 均释放成功返回true；否则为false
	 */
	public static boolean releaseSource(PreparedStatement preparedStatement, Connection connection) {
		return releaseSource(preparedStatement) &&
				releaseSource(connection);
	}
}
