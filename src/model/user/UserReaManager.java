package model.user;


import dao.user.UserReaDao;

import java.sql.SQLException;

/**
 * author：康乐
 * time：2014/11/16
 * function：管理用户关系
 */


public class UserReaManager {

	/**
	 * 关注某一个人，返回关注信息
	 * @param myID 我的ID
	 * @param hisID 被我关注的人的ID
	 * @return 如果成功关注，返回true；如果失败，只有可能是sql异常
	 * @throws SQLException
	 */
	public static boolean followByID (int myID, int hisID) throws SQLException {
		return UserReaDao.followTheID(myID, hisID);
	}







}
