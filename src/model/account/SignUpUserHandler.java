package model.account;

import beans.index.SignUpInfoBean;
import dao.account.SignUpDao;

/**
 * author: 康乐
 * time: 2014/7/28
 * function: 注册用户
 */
public class SignUpUserHandler {

	public static boolean registeUser(SignUpInfoBean signUpInfoBean) {

		return SignUpDao.recordUser(signUpInfoBean);
	}


	public static boolean isUserExist(String userName) {
		String isExisted = SignUpDao.getUserEmail(userName);
		return isExisted != null;
	}
}
