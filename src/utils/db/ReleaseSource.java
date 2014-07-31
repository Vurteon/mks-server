package utils.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by leon on 2014/7/28.
 */
public class ReleaseSource {

	public static boolean releaseSource(Connection connection) {
		try {
			connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	public static boolean releaseSource(PreparedStatement preparedStatement) {
		try {
			preparedStatement.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean releaseSource(ResultSet resultSet,PreparedStatement preparedStatement) {
		try {
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			preparedStatement.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
