package model.account;

import beans.account.SignUpInfoBean;
import dao.account.SignUpDao;

import java.sql.SQLException;

/**
 * author: 康乐
 * time: 2014/7/28
 * function: 注册用户
 */
public class SignUpUserHandler {

	/**
	 * 向DAO层请求注册用户
	 * @param signUpInfoBean 用户注册信息bean
	 * @return 是否注册成功
	 * @throws SQLException
	 */
	public static boolean registeUser(SignUpInfoBean signUpInfoBean) throws SQLException {
		return SignUpDao.recordUser(signUpInfoBean);
	}

	/**
	 * 检查用户账号是否已经存在
	 * @param userAccount 用户账号
	 * @return 是否存在参数所代表的用户账号
	 * @throws SQLException
	 */
	public static boolean isAccountExist(String userAccount) throws SQLException {
		String isExisted = SignUpDao.getUserAccount(userAccount);
		return isExisted != null;
	}
}
