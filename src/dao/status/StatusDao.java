package dao.status;

import utils.db.ConnectionFactory;
import utils.db.ReleaseSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * author：康乐
 * time：2014/11/21
 * function：status相关dao
 */
public class StatusDao {


	/**
	 * 检查此状态是否存在
	 *
	 * @param rs_id 状态id
	 * @return 如果存在，返回true；否则返回false
	 * @throws java.sql.SQLException
	 */
	public static boolean isExisted(int rs_id) throws SQLException {

		Connection connection = ConnectionFactory.getMySqlConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		String recordLiker = "SELECT rs_id FROM StatusFeeds WHERE rs_id = ?";

		try {
			preparedStatement = connection.prepareStatement(recordLiker);
			preparedStatement.setInt(1, rs_id);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.first()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			ReleaseSource.releaseSource(resultSet, preparedStatement, connection);
		}
		return false;
	}
}
