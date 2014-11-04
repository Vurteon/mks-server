package dao.account;

import beans.account.UserAccountBean;
import dao.exception.NoSuchIDException;
import utils.db.CachedRowSetFactory;
import utils.db.ConnectionFactory;
import utils.db.ReleaseSource;

import javax.sql.rowset.CachedRowSet;
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
	public static int getID(String email) throws NoSuchIDException, SQLException {

		Connection con = ConnectionFactory.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String getIDSql = "SELECT ID FROM UserEmail WHERE email = ?";

		int ID = 0;

		try {
			ps = con.prepareStatement(getIDSql);
			ps.setString(1, email);
			resultSet = ps.executeQuery();

			if (resultSet.next()) {
				ID = resultSet.getInt("ID");
			} else {
				throw new NoSuchIDException();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			ReleaseSource.releaseSource(resultSet, ps, con);
		}
		return ID;
	}

	/**
	 * 通过用户ID ，查询包括pic，UserInfo，UserProfile中的全部信息，并结合成一个结果集
	 *
	 * @param ID 用户后台标记ID
	 * @return 包含用户设置的结果集；或者是null当获取时出现异常时
	 */

	public static CachedRowSet getUserSettings(int ID) throws SQLException {
		Connection con = ConnectionFactory.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;

		// 获取用户初始化数据
		String sql = "SELECT email,name,brief_intro,home_location,bg_photo,main_head_photo,home_head_photo from UserProfile NATURAL JOIN UserInfo WHERE ID = ?";

		CachedRowSet cachedRowSet = null;

		try {
			ps = con.prepareStatement(sql);
			ps.setInt(1, ID);
			resultSet = ps.executeQuery();

			// 获取cacheRowSet并用ResultSet填充，下面这行代码只要发生异常，CacheRowSet一定是null
			// 这个信息是可以作为DB是否异常的信息传递到上一层
			cachedRowSet = buildCacheRawSet(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			ReleaseSource.releaseSource(resultSet, ps, con);
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

	public static CachedRowSet getFollowingPeopleID(int ID) throws SQLException {

		Connection con = ConnectionFactory.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;

		String getFollowingSql = "SELECT following FROM Followings WHERE ID = ?";
		CachedRowSet cachedRowSet = null;

		try {
			ps = con.prepareStatement(getFollowingSql);
			ps.setInt(1, ID);
			resultSet = ps.executeQuery();
			cachedRowSet = buildCacheRawSet(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			ReleaseSource.releaseSource(resultSet, ps, con);
		}

		return cachedRowSet;
	}

	/**
	 * 检查用户登录时账号、密码是否正确
	 *
	 * @param userAccountBean 用户信息Bean
	 * @return 正确，true;错误，false
	 */
	public static boolean accountCheck(UserAccountBean userAccountBean) throws SQLException {

		String email = userAccountBean.getAccount();
		String password = userAccountBean.getPassword();

		String accountCheckSql = "SELECT email,password FROM AccountInfo WHERE email = ?";

		Connection con = ConnectionFactory.getMySqlConnection();
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
			throw e;
		} finally {
			ReleaseSource.releaseSource(resultSet, ps, con);
		}
	}

	/**
	 * 创建CachedRowSet
	 * @param resultSet CachedRowSet数据源
	 * @return 创建好的CachedRowSet
	 * @throws SQLException
	 */
	private static CachedRowSet buildCacheRawSet(ResultSet resultSet) throws SQLException {
		CachedRowSet cachedRowSet;
		cachedRowSet = CachedRowSetFactory.getRowSetFactory().createCachedRowSet();
		cachedRowSet.populate(resultSet);
		return cachedRowSet;
	}


}
