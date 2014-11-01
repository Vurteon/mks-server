package beans.account;


/**
 * author: 康乐
 * time: 2014/7/28
 * function: 用户注册时参数存储对象
 */

public class SignUpInfoBean {

	private String name;

	private String userAccount;

	private String password;

	public String getUserAccount() {
		return userAccount;
	}

	public String getPassword() {
		return password;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
