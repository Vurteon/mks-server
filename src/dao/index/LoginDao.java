package dao.index;

import beans.index.UserAccountBean;
import dao.exception.NoSuchIDException;
import sun.misc.Cache;
import utils.db.GetConnection;
import utils.db.GetRowSetFactory;
import utils.db.ReleaseSource;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * author: 康乐
 * time: 2014/7/30
 * function: 检测用户登录、获取相关登录信息
 */
public class LoginDao {


	/**
	 * 获得用户在后台对于的标记ID
	 *
	 * @param email 用户账号
	 * @return 用户在后台所对应的的ID
	 */
	public static long getID(String email) throws NoSuchIDException {

		Connection con = GetConnection.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String getIDSql = "SELECT ID FROM UserEmail WHERE email = ?";

		long ID = 0;

		try {
			ps = con.prepareStatement(getIDSql);

			ps.setString(1, email);

			resultSet = ps.executeQuery();

			if (resultSet.next()) {
				ID = resultSet.getLong("ID");
			} else {
				throw new NoSuchIDException();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ReleaseSource.releaseSource(resultSet, ps);
		}

		return ID;
	}

	/**
	 * 通过用户ID ，查询包括pic，HomePageName，UserInfo，UserProfile中的部分或者全部信息，并结合成一个结果集
	 *
	 * @param ID 用户后台标记ID
	 * @return 包含用户设置的结果集；或者是null当获取时出现异常时
	 */

	public static CachedRowSet getUserSettings(long ID) {


		Connection con = GetConnection.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String sql = "SELECT bg_pic,hot_pic_area,home_page_name," +
				"phone_bg_pic,pc_main_bg_pic,phone_head_pic,pc_main_pic " +
				"FROM UserProfile AS UI, HomePageName AS HP,HeadBgPic AS HB," +
				"HeadPic AS HH WHERE UI.ID = ? AND HP.ID = ? AND HB.ID = ? AND HH.ID = ?";

		CachedRowSet cachedRowSet = null;

		try {

			ps = con.prepareStatement(sql);

			ps.setLong(1, ID);
			ps.setLong(2, ID);
			ps.setLong(3, ID);
			ps.setLong(4, ID);

			resultSet = ps.executeQuery();

			// 获取cacheRowSet并用ResultSet填充，下面这行代码只要发生异常，CacheRowSet一定是null
			// 这个信息是可以作为DB是否异常的信息传递到上一层
			cachedRowSet = buildCacheRawSet(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ReleaseSource.releaseSource(resultSet, ps);
		}

		// 这里返回要么是cacheRawSet，要么是null
		return cachedRowSet;
	}

	/**
	 * 返回用户关注的人的ID或者是null，当SQL查询出现异常
	 *
	 * @param ID 用户后台唯一标记
	 * @return 可能包含信息的cacheRowSet或者null，当SQL查询出现异常时
	 */

	public static CachedRowSet getFollowingPeopleID(long ID) {

		Connection con = GetConnection.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String getFollowingSql = "SELECT following FROM following WHERE ID = ?";

		CachedRowSet cachedRowSet = null;

		try {
			ps = con.prepareStatement(getFollowingSql);
			ps.setLong(1, ID);
			resultSet = ps.executeQuery();

			cachedRowSet = buildCacheRawSet(resultSet);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ReleaseSource.releaseSource(resultSet, ps);
		}

		return cachedRowSet;
	}

	/**
	 * 返回用户好友的ID或者是null，当SQL查询出现异常
	 *
	 * @param ID 用户后台唯一标记
	 * @return 可能包含信息的cacheRowSet或者null，当SQL查询出现异常时
	 */

	public static CachedRowSet getFriendsID(long ID) {

		Connection con = GetConnection.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String getFollowingSql = "SELECT friend FROM friends WHERE ID = ?";

		CachedRowSet cachedRowSet = null;

		try {
			ps = con.prepareStatement(getFollowingSql);
			ps.setLong(1, ID);
			resultSet = ps.executeQuery();

			cachedRowSet = buildCacheRawSet(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ReleaseSource.releaseSource(resultSet, ps);
		}

		return cachedRowSet;
	}

	/**
	 * 检查用户登录时账号、密码是否正确
	 *
	 * @param userAccountBean 用户信息Bean
	 * @return 正确，true;错误，false
	 */
	public static boolean accountCheck(UserAccountBean userAccountBean) {

		String email = userAccountBean.getEmail();
		String password = userAccountBean.getPassword();

		String accountCheckSql = "SELECT email,password FROM AccountInfo WHERE email = ?";

		Connection con = GetConnection.getMySqlConnection();

		PreparedStatement ps = null;

		ResultSet resultSet = null;

		try {
			ps = con.prepareStatement(accountCheckSql);

			ps.setString(1, email);

			resultSet = ps.executeQuery();

			if (resultSet.next()) {

				String dbEmail = resultSet.getString("email");
				String dbPassword = resultSet.getString("password");
				return dbEmail.equals(email) && dbPassword.equals(password);
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ReleaseSource.releaseSource(resultSet, ps);
		}
		return false;
	}


	private static CachedRowSet buildCacheRawSet(ResultSet resultSet) throws SQLException {
		CachedRowSet cachedRowSet;
		cachedRowSet = GetRowSetFactory.getRowSetFactory().createCachedRowSet();
		cachedRowSet.populate(resultSet);
		return cachedRowSet;
	}


}
