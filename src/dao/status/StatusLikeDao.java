package dao.status;

import utils.db.ConnectionFactory;
import utils.db.ReleaseSource;
import utils.db.TimeBuilder;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * author：康乐
 * time：2014/11/19
 * function：提供点赞相关操作的ado层
 */

public class StatusLikeDao {

	/**
	 * 向数据库插入点赞的人的信息
	 * @param rs_id 被点赞的资源
	 * @param liker 点赞的人的ID
	 * @return 是否是否成功记录
	 * @throws SQLException
	 */
	public static boolean recordLiker (int rs_id, int liker) throws SQLException {

		Connection connection = ConnectionFactory.getMySqlConnection();

		PreparedStatement preparedStatement = null;

		String recordLiker = "insert into Likers (rs_id, liker, time) values (?, ?, ?)";

		try {
			preparedStatement = connection.prepareStatement(recordLiker);
			preparedStatement.setInt(1,rs_id);
			preparedStatement.setInt(2,liker);
			preparedStatement.setTimestamp(3, TimeBuilder.getLocalTime());

			preparedStatement.execute();
			return true;
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}finally {
			ReleaseSource.releaseSource(preparedStatement,connection);
		}
	}


	/**
	 * 从数据库删除点赞的人的信息
	 * @param rs_id 被点赞的资源
	 * @param liker 点赞的人的ID
	 * @return 是否是否成功删除
	 * @throws SQLException
	 */
	public static boolean removeLiker (int rs_id, int liker) throws SQLException {

		Connection connection = ConnectionFactory.getMySqlConnection();

		PreparedStatement preparedStatement = null;

		String recordLiker = "delete from  Likers where rs_id = ? and liker = ?";

		try {
			preparedStatement = connection.prepareStatement(recordLiker);
			preparedStatement.setInt(1,rs_id);
			preparedStatement.setInt(2,liker);

			preparedStatement.execute();
			return true;
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}finally {
			ReleaseSource.releaseSource(preparedStatement,connection);
		}
	}

	/**
	 * 检查是否已经对某状态赞过了
	 * @param rs_id 被赞的状态
	 * @param liker 赞的人
	 * @return 是否已经赞过
	 */
	public static boolean isLiked (int rs_id, int liker) throws SQLException {

		Connection connection = ConnectionFactory.getMySqlConnection();

		PreparedStatement preparedStatement = null;

		ResultSet resultSet = null;

		String recordLiker = "select liker from Likers where rs_id = ? and liker = ?";

		try {
			preparedStatement = connection.prepareStatement(recordLiker);
			preparedStatement.setInt(1,rs_id);
			preparedStatement.setInt(2,liker);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.first()) {
				return true;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}finally {
			ReleaseSource.releaseSource(resultSet, preparedStatement,connection);
		}
		return false;
	}

	/**
	 * 将点赞的信息记录到status中，并记录点赞人的信息
	 * @param rs_id 被点赞的资源
	 * @param liker 点赞的人
	 * @return 如果全部记录成功，返回true；如果失败，必然会出现sql异常
	 */
	public static boolean likeIt (int rs_id, int liker) throws SQLException {

		Connection connection = ConnectionFactory.getMySqlConnection();
		PreparedStatement preparedStatement = null;

		try {
			// 首先检查是否存在此状态
			String selectStatus = "select rs_id from StatusFeeds where rs_id = ?";
			preparedStatement = connection.prepareStatement(selectStatus);
			preparedStatement.setInt(1,rs_id);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.first()) {
				ReleaseSource.releaseSource(resultSet, preparedStatement, connection);
				return false;
			}else {
				// 如果存在相关状态，那么就释放查询资源，然后进行记录
				ReleaseSource.releaseSource(resultSet,preparedStatement);
			}

			connection.setAutoCommit(false);
			String addLikeNum = "update StatusFeeds set likes_number =+  1 where rs_id = ?";
			preparedStatement = connection.prepareStatement(addLikeNum);
			preparedStatement.setInt(1,rs_id);
			preparedStatement.executeUpdate();
			ReleaseSource.releaseSource(preparedStatement);

			String recordLiker = "insert into Likers (rs_id, liker,time) values (?, ?, ?)";
			preparedStatement = connection.prepareStatement(recordLiker);
			preparedStatement.setInt(1, rs_id);
			preparedStatement.setInt(2, liker);
			preparedStatement.setTimestamp(3, TimeBuilder.getLocalTime());
			preparedStatement.execute();
			connection.commit();
			return true;

		}catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		}finally {
			ReleaseSource.releaseSource(preparedStatement, connection);
		}
		return false;
	}


}
