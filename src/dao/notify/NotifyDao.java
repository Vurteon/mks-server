package dao.notify;

import utils.db.CachedRowSetFactory;
import utils.db.ConnectionFactory;
import utils.db.ReleaseSource;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * author：康乐
 * time：2014/11/22
 * function：用于推送消息缓存的数据库相关操作
 */
public class NotifyDao {

	/**
	 * 向数据库中添加消息
	 *
	 * @param ID      添加者
	 * @param message 消息
	 * @return 如果成功，返回true；否则返回false
	 */
	public static boolean addMessage(int ID, String message) throws SQLException {

		Connection connection = ConnectionFactory.getMySqlConnection();
		PreparedStatement preparedStatement = null;
		String addMessage = "INSERT INTO Message(ID, message) VALUES (?,?)";
		try {
			preparedStatement = connection.prepareStatement(addMessage);
			preparedStatement.setInt(1, ID);
			preparedStatement.setString(2, message);
			preparedStatement.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			ReleaseSource.releaseSource(preparedStatement, connection);
		}
	}


	/**
	 * 根据ID获得其在数据库中存储的消息
	 *
	 * @param ID 需要获取消息的ID
	 * @return 如果有消息，返回一个带有格式的string；否则返回null
	 * @throws java.sql.SQLException
	 */
	public static String getMessage(int ID) throws SQLException {
		Connection connection = ConnectionFactory.getMySqlConnection();

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String getMessage = "SELECT message FROM Message WHERE ID = ?";
		try {
			preparedStatement = connection.prepareStatement(getMessage);
			preparedStatement.setInt(1, ID);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.first()) {
				return resultSet.getString("message");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			ReleaseSource.releaseSource(resultSet, preparedStatement, connection);
		}
		return null;
	}

	/**
	 * 从数据库中根据ID移除相关的消息数据
	 *
	 * @param ID 需要移除消息的数据
	 * @return 如果移除成功，返回true；否则返回false
	 */
	public static boolean removeMessage(int ID) throws SQLException {
		Connection connection = ConnectionFactory.getMySqlConnection();

		PreparedStatement preparedStatement = null;
		String removeMessage = "DELETE FROM Message WHERE ID = ?";
		try {
			preparedStatement = connection.prepareStatement(removeMessage);
			preparedStatement.setInt(1, ID);
			preparedStatement.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			ReleaseSource.releaseSource(preparedStatement, connection);
		}
	}


	/**
	 * 更新数据库中的消息
	 * @param ID 需要被更新的ID
	 * @param message 更新的消息
	 * @return 是否更新成功
	 * @throws SQLException
	 */
	public static boolean updateMessage(int ID, String message) throws SQLException {
		Connection connection = ConnectionFactory.getMySqlConnection();
		PreparedStatement preparedStatement = null;
		String updateMessage = "UPDATE Message set message = ? where ID = ?";
		try {
			preparedStatement = connection.prepareStatement(updateMessage);
			preparedStatement.setString(1, message);
			preparedStatement.setInt(2, ID);
			preparedStatement.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			ReleaseSource.releaseSource(preparedStatement, connection);
		}
	}
}
