package model.index;

import beans.index.RegisterInfoBean;
import dao.index.RegisteDao;
import utils.CreateJson;
import utils.json.JSONObject;

/**
 * author: 康乐
 * time: 2014/7/28
 * function: 注册用户
 */
public class RegisteUser {

	public static JSONObject registeUser(RegisterInfoBean registerInfoBean) {
		if(isUserExist(registerInfoBean.getEmail())){
			// 构造json对象
			String jsonString = "{'isExist':'yes'}";
			return CreateJson.getJsonObject(jsonString);
		}else {
			if (RegisteDao.recordUser(registerInfoBean)){
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
