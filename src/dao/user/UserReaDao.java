package dao.user;

import utils.db.ConnectionFactory;
import utils.db.ReleaseSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * author：康乐
 * time：2014/11/17
 * function：给用户关系建立、删除、查询提供相关功能
 */


public class UserReaDao {

	/**
	 * 关注某人
	 * @param hisID 被关注的人的ID
	 * @param myID 我的ID
	 */
	public static boolean followTheID (int myID, int hisID) throws SQLException {
		Connection connection = ConnectionFactory.getMySqlConnection();

		String followTheID = "insert into Followings (ID, following) values (?, ?)";
		String addFollower = "insert into Follow (ID, follower) values (?, ?)";
		PreparedStatement preparedStatement = null;


		try {
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(followTheID);
			preparedStatement.setInt(1, myID);
			preparedStatement.setInt(2, hisID);

			preparedStatement.execute();
		}catch (SQLException e) {
			connection.rollback();
			ReleaseSource.releaseSource(preparedStatement, connection);
			e.printStackTrace();
			throw e;
		}


		try {
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(addFollower);
			preparedStatement.setInt(1, hisID);
			preparedStatement.setInt(2, myID);

			preparedStatement.execute();
			connection.commit();
		}catch (SQLException e) {
			connection.rollback();
			ReleaseSource.releaseSource(preparedStatement, connection);
			e.printStackTrace();
			throw e;
		}finally {
			ReleaseSource.releaseSource(preparedStatement, connection);
		}
		return true;
	}








}
