package model.account;

import beans.index.SignUpInfoBean;
import dao.account.RegisteDao;
import utils.CreateJson;
import utils.json.JSONObject;

/**
 * author: 康乐
 * time: 2014/7/28
 * function: 注册用户
 */
public class SignUpUserHandler {

	public static JSONObject registeUser(SignUpInfoBean signUpInfoBean) {
		if(isUserExist(signUpInfoBean.getEmail())){
			// 构造json对象
			String jsonString = "{'isExist':'yes'}";
			return CreateJson.getJsonObject(jsonString);
		}else {
			if (RegisteDao.recordUser(signUpInfoBean)){
				String jsonString = "{'isRecorded':'yes'}";
				return CreateJson.getJsonObject(jsonString);
			}else {
				String jsonString = "{'isRecorded':'no'}";
				return CreateJson.getJsonObject(jsonString);
			}
		}
	}


	public static boolean isUserExist(String userName) {
		String isExisted = RegisteDao.getUserEmail(userName);
		return isExisted != null;
	}
}
