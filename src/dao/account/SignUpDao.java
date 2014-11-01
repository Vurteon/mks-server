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
	public static String getUserEmail(String userEmail) {

		Connection con = ConnectionFactory.getMySqlConnection();

		String getUserSql = "SELECT email FROM UserEmail WHERE email = ?";

		PreparedStatement ps = null;

		ResultSet resultSet = null;

		try {
			ps = con.prepareStatement(getUserSql);
			ps.setString(1, userEmail);
			resultSet = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String result = null;
		try {
			if (resultSet.next()) {
				result = resultSet.getString("email");
			} else {
				//System.out.println("这个表里没有要查询的邮箱");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (!ReleaseSource.releaseSource(resultSet, ps))
				System.err.println("释放资源出错，紧急情况！");
		}
		return result;
	}


	/**
	 * 记录用户信息到数据库，初始化后台对用户的标记
	 *
	 * @param signUpInfoBean 用户信息bean
	 * @return 向数据库记录用户是否成功，成功true；失败 false
	 */
	public static boolean recordUser(SignUpInfoBean signUpInfoBean) {


		Connection con = ConnectionFactory.getMySqlConnection();

		PreparedStatement ps = null;

		String insertEmailSql = "INSERT INTO UserEmail (email) VALUES(?)";

		try {
			ps = con.prepareStatement(insertEmailSql);
			ps.setString(1, signUpInfoBean.getUserAccount());
			ps.execute();
		} catch (SQLException e) {
			System.err.println("插入ID出现异常");
			e.printStackTrace();
			return false;
		} finally {
			ReleaseSource.releaseSource(ps);
		}


		/**
		 * 如果上面的Email和ID成功记录，下面为开启一段事务处理
		 * 将释放资源的代码放在里面，如果出现任何异常，最后也会被捕获
		 */
		long ID = 0;    //用户ID，由插入email时自动生成，是后台处理用户使用的唯一标记

		try {
			con.setAutoCommit(false);

			// 首先获得ID，这是全局性的标记
			String getID = "SELECT ID FROM UserEmail WHERE email = ?";

			ps = con.prepareStatement(getID);
			ps.setString(1, signUpInfoBean.getUserAccount());
			ResultSet resultSet = ps.executeQuery();

			if (resultSet.next()) {
				ID = resultSet.getLong("ID");
			}
			// 释放rs资源
			resultSet.close();
			ps.close();


			// 插入信息到AccountInfo
			String insertAccountSql = "INSERT INTO AccountInfo(email,password) VALUES (?,?)";
			ps = con.prepareStatement(insertAccountSql);

			ps.setString(1, signUpInfoBean.getUserAccount());
			ps.setString(2, signUpInfoBean.getPassword());
			ps.execute();
			ps.close();


			// 插入数据到UserInfo
			String insertUserInfoSql = "INSERT INTO UserInfo (email,name,ID) VALUE (?,?,?)";

			ps = con.prepareStatement(insertUserInfoSql);
			ps.setString(1, signUpInfoBean.getUserAccount());
			ps.setString(2, signUpInfoBean.getName());
			ps.setLong(3, ID);
			ps.execute();
			ps.close();

			// 插入ID 到UserProfile
			String insertUserProfileSql = "INSERT INTO UserProfile (ID) VALUE (?)";
			ps = con.prepareStatement(insertUserProfileSql);
			setID(ID, ps);

			con.commit();

		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.err.println("事务回滚出错，严重错误！");
				e1.printStackTrace();
			}

			// 删除已经在UserEmail中记录的数据

			String deleteEmail = "DELETE FROM UserEmail WHERE ID = ?";

			try {
				ps = con.prepareStatement(deleteEmail);
				ps.setLong(1,ID);
				ps.execute();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				ReleaseSource.releaseSource(ps);
			}
			System.err.println("注册事务出错，严重错误！");
			e.printStackTrace();
		} finally {
			// 不论是哪里抛出的异常，其有且仅有一个ps没有被释放，所以ps一定会被释放
			ReleaseSource.releaseSource(ps);
		}
		return true;
	}

	private static void setID(long ID, PreparedStatement ps) throws SQLException {
		ps.setLong(1, ID);
		ps.execute();
		ps.close();
	}
}
