package dao.account;

import beans.account.SignUpInfoBean;
import utils.db.ConnectionFactory;
import utils.db.ReleaseSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * author: 康乐
 * time: 2014/7/30
 * change-time: 2014/7/31
 * function: 查询邮箱是否存在、注册用户并填写相关数据到各个表中
 */
public class SignUpDao {

	/**
	 * 通过userEmail 查询邮箱是否存在，如果存在，返回邮箱；如果不存在，返回null
	 *
	 * @param userEmail 用户邮箱
	 * @return 用户邮箱或者null
	 */
	public static String getUserAccount(String userEmail) throws SQLException {

		Connection con = ConnectionFactory.getMySqlConnection();

		String getUserSql = "SELECT email FROM UserEmail WHERE email = ?";

		PreparedStatement ps = null;

		ResultSet resultSet = null;

		String result = null;

		try {
			ps = con.prepareStatement(getUserSql);
			ps.setString(1, userEmail);
			resultSet = ps.executeQuery();
			if (resultSet.next()) {
				result = resultSet.getString("email");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}finally {
			ReleaseSource.releaseSource(resultSet, ps, con);
		}
		return result;
	}


	/**
	 * 记录用户信息到数据库，初始化后台对用户的标记
	 *
	 * @param signUpInfoBean 用户信息bean
	 * @return 向数据库记录用户是否成功，成功true；失败 false
	 */
	public static boolean recordUser(SignUpInfoBean signUpInfoBean) throws SQLException {

		Connection con = ConnectionFactory.getMySqlConnection();
		PreparedStatement ps = null;

		String insertEmailSql = "INSERT INTO UserEmail (email) VALUES(?)";

		try {
			// 设置数据库事务
			con.setAutoCommit(false);
			ps = con.prepareStatement(insertEmailSql);
			ps.setString(1, signUpInfoBean.getUserAccount());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			ReleaseSource.releaseSource(ps, con);
		}

		/**
		 * 将ID插入到需要的表中
		 */
		int ID = 0;    //用户ID，由插入email时自动生成，是后台处理用户使用的唯一标记
		try {
			// 首先获得ID，这是全局性的标记
			String getID = "SELECT ID FROM UserEmail WHERE email = ?";
			ps = con.prepareStatement(getID);
			ps.setString(1, signUpInfoBean.getUserAccount());
			ResultSet resultSet = ps.executeQuery();

			if (resultSet.next()) {
				ID = resultSet.getInt("ID");
			}
			// 释放资源
			ReleaseSource.releaseSource(resultSet,ps);

			// 插入信息到AccountInfo
			String insertAccountSql = "INSERT INTO AccountInfo(email,password) VALUES (?,?)";
			ps = con.prepareStatement(insertAccountSql);
			ps.setString(1, signUpInfoBean.getUserAccount());
			ps.setString(2, signUpInfoBean.getPassword());
			ps.execute();
			// 释放资源
			ReleaseSource.releaseSource(ps);

			// 插入数据到UserInfo
			String insertUserInfoSql = "INSERT INTO UserInfo (email,name,ID) VALUE (?,?,?)";
			ps = con.prepareStatement(insertUserInfoSql);
			ps.setString(1, signUpInfoBean.getUserAccount());
			ps.setString(2, signUpInfoBean.getName());
			ps.setInt(3, ID);
			ps.execute();
			// 释放资源
			ReleaseSource.releaseSource(ps);

			// 插入ID 到UserProfile
			String insertUserProfileSql = "INSERT INTO UserProfile (ID) VALUE (?)";
			ps = con.prepareStatement(insertUserProfileSql);
			ps.setInt(1, ID);
			ps.execute();
			// 释放资源
			ReleaseSource.releaseSource(ps);

			// 插入ID 到PhotoSum，初始化照片数量
			String insertIntoPhotoSum = "INSERT INTO PhotoSum (ID) VALUE (?)";
			ps = con.prepareStatement(insertIntoPhotoSum);
			ps.setInt(1, ID);
			ps.execute();
			// 释放资源
			ReleaseSource.releaseSource(ps);

			// 以上操作均成功，提交事务
			con.commit();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw e;
			}
		} finally {
			// 不论是哪里抛出的异常，其有且仅有一个ps没有被释放，所以ps一定会被释放
			ReleaseSource.releaseSource(ps, con);
		}
		return true;
	}
}
