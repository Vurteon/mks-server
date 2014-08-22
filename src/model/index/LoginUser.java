package model.index;

import beans.index.UserAccountBean;
import dao.exception.NoSuchIDException;
import dao.index.LoginDao;
import utils.CreateJson;
import utils.db.ReleaseSource;
import utils.json.JSONObject;

import javax.servlet.http.HttpSession;
import javax.sql.rowset.CachedRowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * author: 康乐
 * time: 2014/8/1
 * function: 负责用户登录时后台所需要进行的相关设置
 */
public class LoginUser {

	/**
	 * 检测用户名和密码是否正确
	 * @param accountBean 用户数据Bean
	 * @return 如果正确，返回true；错误返回false
	 */
	public static JSONObject isAccountPassed(UserAccountBean accountBean) {

		JSONObject jsonObject;

		if (LoginDao.accountCheck(accountBean)) {
			jsonObject = CreateJson.getJsonObject("{'isPassed':'yes'}");
		}else {
			jsonObject = CreateJson.getJsonObject("{'isPassed':'no'}");
		}
		return jsonObject;
	}


	/**
	 * 设置相关数据到session，相关数据包括用户设置，用户following的人，用户的朋友
	 *
	 * @param userAccountBean 用户账户对象
	 * @param httpSession session
	 * @return session设置是否成功
	 * @throws NoSuchIDException
	 */

	public static boolean setSession(UserAccountBean userAccountBean, HttpSession httpSession) throws NoSuchIDException {

		long userID = LoginDao.getID(userAccountBean.getEmail());

		CachedRowSet settings = LoginDao.getUserSettings(userID);
		CachedRowSet followings = LoginDao.getFollowingPeopleID(userID);
		CachedRowSet friends = LoginDao.getFriendsID(userID);

		if (settings == null || followings == null || friends == null) {
			System.err.println("数据库查询出错");
			return false;
		} else {

			// 由于个人登录时需要的相关设置一般不会更新地十分频繁，所以不使用RowSet作为存储介质
			try {

				// 设置settings
				if (settings.next()) {

					ResultSetMetaData resultSetMetaData = settings.getMetaData();
					int columnCounts = resultSetMetaData.getColumnCount();
					int temp = 1;

					while (temp <= columnCounts) {
						httpSession.setAttribute(resultSetMetaData.getColumnName(temp), settings.getString(temp));
						temp++;
					}
				}

				httpSession.setAttribute("ID",userID);

				// 设置followings
				ArrayList<Long> followingAl = new ArrayList<Long>();
				while (followings.next()) {
					followingAl.add(followings.getLong("following"));
				}

				httpSession.setAttribute("followings", followingAl);

				// 设置friends
				ArrayList<Long> friendAl = new ArrayList<Long>();
				while (friends.next()) {
					friendAl.add(friends.getLong("friend"));
				}

				httpSession.setAttribute("followings", followingAl);

				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// 释放cacheRowSet资源
				ReleaseSource.releaseSource(settings);
				ReleaseSource.releaseSource(followings);
				ReleaseSource.releaseSource(friends);
			}
			return false;

		}
	}


}
