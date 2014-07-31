package dao.index;

import beans.index.UserAccountBean;

/**
 * author: 康乐
 * time: 2014/7/30
 * function: 检测用户登录、获取相关登录信息
 */
public class LoginDao {

	public static void logIn() {

	}

	/**
	 * 检查用户登录时账号、密码是否正确
	 * @param userAccountBean 用户信息Bean
	 * @return 正确，true;错误，false
	 */
	public static boolean accountCheck(UserAccountBean userAccountBean) {

		String email = userAccountBean.getEmail();
		String password = userAccountBean.getPassword();



		return false;
	}



}
