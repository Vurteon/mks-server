package dao.user;

import utils.db.CachedRowSetFactory;
import utils.db.ConnectionFactory;
import utils.db.ReleaseSource;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * author:康乐
 * time:2014/11/4
 * function:管理用户个人信息相关
 */

public class UserInfoDao {

	/**
	 * 传入用户的ID号，从数据库·获得用户的name、头像（两种格式）、个性化签名
	 * @param IDs 需要获得信息的IDs
	 * @return 拥有用户个人信息的CachedRowSet
	 */
	public static CachedRowSet getUserInfo (Iterator<Integer> IDs) throws SQLException {

		Connection con = ConnectionFactory.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;
		CachedRowSet cachedRowSet = null;

		StringBuilder stringBuilder = new StringBuilder();

		Integer id;

		while (IDs.hasNext()) {
			id = IDs.next();
			stringBuilder.append(id);
			stringBuilder.append(",");
		}

		stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());

		String getUsersInfo = "select ID, email,name,brief_intro,bg_photo,main_head_photo,home_head_photo from UserInfo natural join UserProfile where ID in ( " + stringBuilder + " )";

		try {
			ps = con.prepareStatement(getUsersInfo);
			resultSet = ps.executeQuery();

			cachedRowSet = CachedRowSetFactory.getRowSetFactory().createCachedRowSet();
			cachedRowSet.populate(resultSet);
		} catch (SQLException e) {
			ReleaseSource.releaseSource(cachedRowSet);
			e.printStackTrace();
			throw e;
		}finally {
			ReleaseSource.releaseSource(resultSet,ps,con);
		}
		return cachedRowSet;
	}


	/**
	 * 传入用户的ID号，从数据库·获得用户的name、头像（两种格式）、个性化签名
	 * @param ID 需要获得信息的IDs
	 * @return 拥有用户个人信息的CachedRowSet
	 */
	public static CachedRowSet getUserInfo (int ID) throws SQLException {

		Connection con = ConnectionFactory.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;
		CachedRowSet cachedRowSet = null;

		String getUsersInfo = "select ID, email,name,brief_intro,bg_photo,main_head_photo,home_head_photo from UserInfo natural join UserProfile where ID = ?";
		try {
			ps = con.prepareStatement(getUsersInfo);
			ps.setInt(1,ID);
			resultSet = ps.executeQuery();

			cachedRowSet = CachedRowSetFactory.getRowSetFactory().createCachedRowSet();
			cachedRowSet.populate(resultSet);
		} catch (SQLException e) {
			ReleaseSource.releaseSource(cachedRowSet);
			e.printStackTrace();
			throw e;
		}finally {
			ReleaseSource.releaseSource(resultSet,ps,con);
		}
		return cachedRowSet;
	}


	/**
	 * 返回指定的ID的照片的数量
	 * @param ID 指定的ID
	 * @return 返回指定的照片的数量
	 * @throws SQLException
	 */
	public static int getUserPhotoNum (int ID) throws SQLException {

		Connection con = ConnectionFactory.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;

		int photoNum = 0;

		String getUserPhotoNum = "select photo_sum_number from PhotoSum where ID = ?";
		try {
			ps = con.prepareCall(getUserPhotoNum);
			ps.setInt(1,ID);
			resultSet = ps.executeQuery();
			if (resultSet.first()) {
				photoNum = resultSet.getInt("photo_sum_number");
			}
		}catch (SQLException e) {
			ReleaseSource.releaseSource(resultSet,ps);
			e.printStackTrace();
			throw e;
		}finally {
			ReleaseSource.releaseSource(resultSet,ps,con);
		}
		return photoNum;
	}


	/**
	 * 返回指定的ID的粉丝的数量
	 * @param ID 指定的ID
	 * @return 返回指定的ID的粉丝的数量
	 * @throws SQLException
	 */
	public static int getUserFansNum (int ID) throws SQLException {

		Connection con = ConnectionFactory.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;

		int fanNum = 0;

		String getUserFansNum = "select COUNT(*) from Follow where ID = ?";
		try {
			ps = con.prepareCall(getUserFansNum);
			ps.setInt(1,ID);
			resultSet = ps.executeQuery();
			if (resultSet.first()) {
				fanNum = resultSet.getInt("COUNT(*)");
			}
		}catch (SQLException e) {
			ReleaseSource.releaseSource(resultSet,ps);
			e.printStackTrace();
			throw e;
		}finally {
			ReleaseSource.releaseSource(resultSet,ps,con);
		}
		return fanNum;
	}



	/**
	 * 返回指定的ID的关注的人的数量
	 * @param ID 指定的ID
	 * @return 返回指定的ID的关注的人数量
	 * @throws SQLException
	 */
	public static int getUserFollowingNum (int ID) throws SQLException {

		Connection con = ConnectionFactory.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;

		int followingNum = 0;

		String getUserFollowingNum = "select COUNT(*) from Followings where ID = ?";
		try {
			ps = con.prepareCall(getUserFollowingNum);
			ps.setInt(1,ID);
			resultSet = ps.executeQuery();
			if (resultSet.first()) {
				followingNum = resultSet.getInt("COUNT(*)");
			}
		}catch (SQLException e) {
			ReleaseSource.releaseSource(resultSet,ps);
			e.printStackTrace();
			throw e;
		}finally {
			ReleaseSource.releaseSource(resultSet,ps,con);
		}
		return followingNum;
	}

	/**
	 * 获得用户昵称
	 * @param ID 需要获取昵称的ID
	 * @return 昵称
	 * @throws SQLException
	 */
	public static String getUserName(int ID) throws SQLException {
		Connection con = ConnectionFactory.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;
		String getUsersInfo = "select name from UserInfo where ID = ?";
		try {
			ps = con.prepareStatement(getUsersInfo);
			ps.setInt(1,ID);
			resultSet = ps.executeQuery();
			if (resultSet.first()) {
				return resultSet.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}finally {
			ReleaseSource.releaseSource(resultSet,ps,con);
		}
		return null;
	}
}
