package model.photo;

import dao.photo.SmallPhotoDao;
import model.status.StatusRowSetManager;
import utils.EnumUtil.ErrorCode;
import utils.StatusResponseHandler;
import utils.db.ReleaseSource;
import utils.json.JSONArray;
import utils.json.JSONObject;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;

/**
 * author：康乐
 * time：2014/11/13
 * function：用于从服务器获取缩略图信息并以json数组的方式放松给客户端
 */
public class LoadMoreSmallPhoto implements Runnable{

	// 异步线程上下文
	private AsyncContext asyncContext;
	// 所需要被获取缩略图的用户的ID
	private int ID;
	// 用作比较的rs_id
	private int rs_id;

	// 一次服务器返回给客户端的最多缩略图信息的条数
	public static final int LOAD_NUMBER = 12;

	public LoadMoreSmallPhoto (AsyncContext asyncContext, int ID, int rs_id) {
		this.asyncContext = asyncContext;
		this.ID = ID;
		this.rs_id = rs_id;
	}


	/**
	 * 查找算法为：现在缓存中进行查找，如果找到的数量不大于12条，那么就在数据库继续进行查找
	 */
	@Override
	public void run() {

		CachedRowSet cachedRowSet = null;

		JSONArray jsonArray;

		try {
			// 获得json数组
			jsonArray = StatusRowSetManager.getMoreSmallPhoto(ID, rs_id);

			// 如果是在缓存中没有获得数据，那就在数据库中取得相应的条数
			if (jsonArray.length() == 0) {
				cachedRowSet = SmallPhotoDao.getSmallPhotoPath(ID, rs_id, LOAD_NUMBER);
				if (cachedRowSet.first()) {
					do {
						buildMoreSmallPhoto(cachedRowSet, jsonArray, ID);
					}while (cachedRowSet.next());
				}
			}else if (jsonArray.length() < LOAD_NUMBER) {
				// 如果小于12，就在数据库中取数据，填充到12
				cachedRowSet = SmallPhotoDao.getSmallPhotoPath(ID, LOAD_NUMBER - jsonArray.length());
				if (cachedRowSet.first()) {
					do {
						buildMoreSmallPhoto(cachedRowSet, jsonArray, ID);
					}while (cachedRowSet.next());
				}
			}

			// 向客户端发送取得的数据
			StatusResponseHandler.sendStatus(jsonArray, (HttpServletResponse)asyncContext.getResponse(), false);
			asyncContext.complete();
		} catch (SQLException e) {
			StatusResponseHandler.sendStatus("status", ErrorCode.SQLERROR,(HttpServletResponse)asyncContext.getResponse());
			e.printStackTrace();
		}finally {
			ReleaseSource.releaseSource(cachedRowSet);
		}
	}

	/**
	 * 从CachedRowSet中获取数据并填充在json对象中
	 * @param cachedRowSet 数据源
	 * @param jsonArray 需要被json对象填充的json数组
	 * @throws SQLException
	 */
	private static void buildMoreSmallPhoto (CachedRowSet cachedRowSet, JSONArray jsonArray, int ID) throws SQLException {
		int rs_id = cachedRowSet.getInt("rs_id");
		String photoPath = cachedRowSet.getString("more_small_photo");
		String album = cachedRowSet.getString("album");

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("rs_id", rs_id);
		jsonObject.put("ID",ID);
		jsonObject.put("more_small_photo", photoPath);
		jsonObject.put("album", album);
		jsonArray.put(jsonObject);
	}
}
























