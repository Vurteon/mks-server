package model.user;

import dao.user.UserInfoDao;
import utils.db.ReleaseSource;
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
		ReleaseSource.releaseSource(cachedRowSet);
		return jsonArray;
	}


	/**
	 * 从DAO接口获取CachedRowSet对象，转化为json数组并返回给上一层
	 * @param ID 用户所关注的人的ID号
	 * @return 带有用户个人信息的json数组
	 * @throws SQLException
	 */
	public static JSONObject getUsersInfo (int ID) throws SQLException {

		CachedRowSet cachedRowSet = UserInfoDao.getUserInfo(ID);

		JSONArray jsonArray = new JSONArray();
		if (cachedRowSet.first()) {
			buildUserInfo(cachedRowSet,jsonArray);
		}
		ReleaseSource.releaseSource(cachedRowSet);
		return jsonArray.getJSONObject(0);
	}




	/**
	 * 依据CachedRowSet构建json对象，并将其放入json数组
	 * @param cachedRowSet json对象的数据来源
	 * @param jsonArray 装用户信息json对象的json数组
	 * @throws SQLException
	 */
	private static void buildUserInfo (CachedRowSet cachedRowSet, JSONArray jsonArray) throws SQLException {

		int ID = cachedRowSet.getInt("ID");
		String name = cachedRowSet.getString("name");
		String brief_intro = cachedRowSet.getString("brief_intro");
		String bg_photo = cachedRowSet.getString("bg_photo");
		String main_head_photo = cachedRowSet.getString("main_head_photo");
		String home_head_photo = cachedRowSet.getString("home_head_photo");

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ID", ID);
		jsonObject.put("name",name);
		jsonObject.put("brief_intro",brief_intro);
		jsonObject.put("bg_photo",bg_photo);
		jsonObject.put("main_head_photo",main_head_photo);
		jsonObject.put("home_head_photo",home_head_photo);

		jsonArray.put(jsonObject);
	}

	/**
	 * 获取指定的ID的三个数量指标
	 * @param ID ID
	 * @return 返回指定的ID的三个数量指标的json对象
	 */
	public static JSONObject getThreeNum (int ID) throws SQLException {

		int photosNumber = UserInfoDao.getUserPhotoNum(ID);
		int fansNum = UserInfoDao.getUserFansNum(ID);
		int followingsNum = UserInfoDao.getUserFollowingNum(ID);

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("photosNumber", photosNumber);
		jsonObject.put("fansNum", fansNum);
		jsonObject.put("followingsNum", followingsNum);

		return jsonObject;
	}
}
