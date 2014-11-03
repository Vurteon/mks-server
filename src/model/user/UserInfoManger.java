package model.user;

import dao.user.UserInfoDao;
import utils.json.JSONArray;
import utils.json.JSONObject;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * author:康乐
 * time:2014/11/3
 * function:管理用户个人信息
 */
public class UserInfoManger {

	/**
	 * 从DAO接口获取CachedRowSet对象，转化为json数组并返回给上一层
	 * @param IDs 用户所关注的人的ID号
	 * @return 带有用户个人信息的json数组
	 * @throws SQLException
	 */
	public static JSONArray getUsersInfo (Iterator<Integer> IDs) throws SQLException {

		CachedRowSet cachedRowSet = UserInfoDao.getUserInfo(IDs);

		JSONArray jsonArray = new JSONArray();

		if (cachedRowSet.first()) {
			do {
				// 构建json数组
				buildUserInfo(cachedRowSet,jsonArray);
			}while (cachedRowSet.next());
		}
		return jsonArray;
	}

	/**
	 * 依据CachedRowSet构建json对象，并将其放入json数组
	 * @param cachedRowSet json对象的数据来源
	 * @param jsonArray 装用户信息json对象的json数组
	 * @throws SQLException
	 */
	private static void buildUserInfo (CachedRowSet cachedRowSet, JSONArray jsonArray) throws SQLException {

		String name = cachedRowSet.getString("name");
		String brief_intro = cachedRowSet.getString("brief_intro");
		String main_head_photo = cachedRowSet.getString("main_head_photo");
		String home_head_photo = cachedRowSet.getString("home_head_photo");

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name",name);
		jsonObject.put("brief_intro",brief_intro);
		jsonObject.put("main_head_photo",main_head_photo);
		jsonObject.put("home_head_photo",home_head_photo);

		jsonArray.put(jsonObject);
	}


}
