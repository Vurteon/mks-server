package dao.status;

import utils.db.ConnectionFactory;
import utils.db.ReleaseSource;
import utils.db.TimeBuilder;

import java.sql.*;

/**
 * author：康乐
 * time：2014/11/19
 * function：管理评论相关的dao
 */

public class StatusCommentDao {

	/**
	 * 记录评论道数据库
	 * @param rs_id 与评论相关的资源
	 * @param commenter 评论者的ID
	 * @param commented 被评论的人的ID
	 * @param content 评论内容
	 * @throws SQLException
	 */
	public static boolean recordComment(int rs_id, int commenter, int commented, String content) throws SQLException {
		Connection connection = ConnectionFactory.getMySqlConnection();
		PreparedStatement preparedStatement = null;
		String recordComment = "INSERT INTO Comments (rs_id, commenter, commented, content, time) VALUES (?, ?, ?, ?, ?);";
		try {
			preparedStatement = connection.prepareStatement(recordComment);
			preparedStatement.setInt(1,rs_id);
			preparedStatement.setInt(2,commenter);
			preparedStatement.setInt(3,commented);
			preparedStatement.setString(4,content);
			preparedStatement.setTimestamp(5, TimeBuilder.getLocalTime());
			preparedStatement.execute();
			return true;
		}catch (SQLException e) {
			e.printStackTrace();
			ReleaseSource.releaseSource(preparedStatement, connection);
			throw e;
		}finally {
			ReleaseSource.releaseSource(preparedStatement, connection);
		}
	}


	/**
	 * 评论，包括增加status的评论条数和将评论的内容方式comments表
	 * @param rs_id 被评论资源的ID
	 * @param commenter 评论者的ID
	 * @param commented 被评论者的ID
	 * @param content 评论的内容
	 * @return 如果评论操作成功，返回true；否则便是抛出sql异常
	 * @throws SQLException
	 */
	public static boolean comment(int rs_id, int commenter, int commented, String content) throws SQLException {

		Connection connection = ConnectionFactory.getMySqlConnection();

		PreparedStatement preparedStatement = null;

		try {
			connection.setAutoCommit(false);
			String addCommentNumber = "UPDATE StatusFeeds SET comments_number = comments_number + 1 WHERE rs_id = ?";
			preparedStatement = connection.prepareStatement(addCommentNumber);
			preparedStatement.setInt(1,rs_id);
			preparedStatement.execute();
			ReleaseSource.releaseSource(preparedStatement);

			String recordComment = "insert into Comments (rs_id, commenter, commented, content, time) values (?,?,?,?,?)";
			preparedStatement = connection.prepareStatement(recordComment);
			preparedStatement.setInt(1,rs_id);
			preparedStatement.setInt(2,commenter);
			preparedStatement.setInt(3,commented);
			preparedStatement.setString(4,content);
			preparedStatement.setTimestamp(5, TimeBuilder.getLocalTime());
			preparedStatement.execute();
			connection.commit();
			return true;
		}catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
			ReleaseSource.releaseSource(preparedStatement, connection);
			throw e;
		}finally {
			ReleaseSource.releaseSource(preparedStatement, connection);
		}
	}
}
