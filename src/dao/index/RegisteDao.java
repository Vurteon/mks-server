package dao.index;

import beans.index.RegisterInfoBean;
import utils.db.GetConnection;
import utils.db.ReleaseSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * author: 康乐
 * time: 2014/7/30
 * function: 查询邮箱是否存在、注册用户并填写相关数据到各个表中
 */
public class RegisteDao {

	/**
	 *通过userEmail 查询邮箱是否存在，如果存在，返回邮箱；如果不存在，返回null
	 * @param userEmail 用户邮箱
	 * @return 用户邮箱或者null
	 */
	public static String getUser(String userEmail) {

		Connection con = GetConnection.getMySqlConnection();

		String getUserSql = "select email from UserEmail where email = ?";

		PreparedStatement ps = null;

		ResultSet resultSet = null;

		try {
			ps = con.prepareStatement(getUserSql);
			ps.setString(1,userEmail);
			resultSet = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String result = null;
		try {
			if(resultSet.next()){
				result = resultSet.getString("email");
			}else  {
				//System.out.println("这个表里没有要查询的邮箱");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (!ReleaseSource.releaseSource(resultSet,ps))
				System.err.println("释放资源出错，紧急情况！");
		}
		return result;
	}


	/**
	 * 记录用户信息到数据库，初始化后台对用户的标记
	 * @param registerInfoBean 用户信息bean
	 * @return 向数据库记录用户是否成功，成功true；失败 false
	 */
	public static boolean recordUser(RegisterInfoBean registerInfoBean) {


		Connection con = GetConnection.getMySqlConnection();

		String insertAccountSql = "insert into AccountInfo(email,password) values (?,?)";

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement(insertAccountSql);

			ps.setString(1, registerInfoBean.getEmail());
			ps.setString(2, registerInfoBean.getPassword());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String insertEmailSql = "insert into UserEmail (email) values(?)";

		try {
			ps = con.prepareStatement(insertEmailSql);
			ps.setString(1, registerInfoBean.getEmail());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		long ID = 0;    //用户ID，由插入email时自动生成，是后台处理用户使用的唯一标记

		String getID = "select ID from UserEmail where email = ?";

		try {
			ps = con.prepareStatement(getID);
			ps.setString(1, registerInfoBean.getEmail());
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()){
				ID = resultSet.getLong("ID");
			}else {
				System.err.println("ID获取出现问题，紧急情况！");
				resultSet.close();
				return false;
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String insertUserInfoSql = "insert into UserInfo (email,name,ID) value (?,?,?)";

		try {
			ps = con.prepareStatement(insertUserInfoSql);
			ps.setString(1, registerInfoBean.getEmail());
			ps.setString(2, registerInfoBean.getName());
			ps.setLong(3, ID);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (!ReleaseSource.releaseSource(ps))
				System.err.println("释放资源出错，紧急情况！");
		}
		return true;
	}
}
