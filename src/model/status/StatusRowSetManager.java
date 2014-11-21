package model.status;

import beans.status.PhotoDesBean;
import dao.cache.CachedRowSetDao;
import utils.db.TimeBuilder;
import utils.json.JSONArray;
import utils.json.JSONObject;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * author: 康乐
 * time: 2014/8/22
 * function: 提供对RowSet的相关操作(增、删、查),将RowSetDao所需要的信息进行封装
 * 并在内存数据库存储数量大于额定数值后向mysql数据库写入一部分数据
 */

public class StatusRowSetManager {

	/**
	 * statusSet的大小，目前为1000，可能会占用2M到6M内存
	 */
	public static final int statusSetMaxSize = 10;

	/**
	 * statusSet当前数量
	 */
	public static int statusCurrentSize;

	/**
	 * 当达到数据数量容量上限时，一次性需要向数据库写入的数据数量
	 */
	public static final int writeOnceNumber = 5;

	// 存储status的RowSet对象
	public static CachedRowSet statusRowSet;

	/**
	 * 临时rs_id。每一个动态都会有一个id，为了保证id的唯一性性，数据库中每次
	 * 新增数据其id号都会自增1，且是主键，目前只能在内存中暂时使用自增的整形
	 * 来获得临时id，向其中存储的时候再由数据库生成rs_id号
	 */
	public static int temp_rs_id = 100000000;

	// 同步锁控制对象
	private static final Object object = new Object();

	/**
	 * 一次向客户端推送状态的最大数量,当是更新的时候,每次推送的数量至多为N条
	 * 每次加载数据时,每次推送均为10条
	 */
	public static final int statusSendNumber = 10;

	/**
	 * 同步控制将信息插入CachedRowSet,向传入的photoDesBean中插入时间和临时rs_id
	 *
	 * @param photoDesBean 状态描述对象
	 * @return 是否插入成功
	 * @throws SQLException
	 */
	public static boolean insertStatus(PhotoDesBean photoDesBean) throws SQLException {

		boolean b;

		// 插入数据时需要保持同步性
		synchronized (object) {
			photoDesBean.setTime(TimeBuilder.getLocalTime());
			photoDesBean.setRs_id(getRs_id());
			// 如果数据数量已超出最大容量，那么就向数据库写入数据，此时也需要
			// 保持同步性，所以需要在synchronized块中进行
			if (statusCurrentSize >= statusSetMaxSize) {
				System.out.println("这里是单线程的！");
				statusOverSizeHandler();
				statusCurrentSize -= writeOnceNumber;
			}

			b = CachedRowSetDao.insertData(photoDesBean);

			// 当前数量大小增1，由于是写完后才进行数量增加，所以每一次
			// 需要数据同步的时候都是下一个请求引起，而最后一个请求刚好
			// 达到上限的值
			if (b) {
				statusCurrentSize++;
			}
		}
		// 增加用户照片数量，修改数据库中相应的数据
		CachedRowSetDao.updatePhotoNumber(photoDesBean.getID(),photoDesBean.getPhotoNumber(),true);
		return b;
	}


	/**
	 * 使用同步控制,依据参数从缓存或者数据库中获取一定数量的数据.如果当前
	 * rs_id小于缓存中最小的rs_id,那么就在数据库中进行查找操作;否则便在
	 * 缓存中进行查找. 随后将查找到的数据封装为json对象返回
	 * <p/>
	 * 需要说明的是:此算法存在漏洞-->当数据库中的最大id大于临时id时会出现
	 * 许多意外的bug,但是这个将会是数据库存储数量达到10亿时候的事情,那个时
	 * 候这个bug我想早就已经修复了.
	 *
	 * @param followings 当前用户所关注的所有人,使用hash表便于检查是否
	 *                   相关id号存在于联系人或者
	 * @param rs_id      标记资源rs_id,用于和需要获取的数据的rs_id做比较
	 * @param before     标记是获取标记rs_id以前的数据还是标记时间以后的
	 *                   数据. 如果为真,则获取标记rs_id以前的数据;如果为
	 *                   假,则获取标记rs_id以后的数据
	 * @return 返回查找到的有效的数据的json对象
	 * @throws java.sql.SQLException
	 */
	public static JSONArray selectStatus(HashSet<Integer> followings, int rs_id, boolean before) throws SQLException {

		// 缓存中的最小的rs_id
		int firstNumber;

		// 此算法第二次检查最小rs_id
		int firstNumberLater;

		// 获取rs_id中最小的rs_id号
		synchronized (object) {
			// 将游标调整到第一行
			statusRowSet.first();
			firstNumber = statusRowSet.getInt(1);
		}

		/**
		 * 如果是请求加载更多且发来的rs_id小于缓存中最小的rs_id,表明其已经存于数据库,所以
		 * 下面直接进行数据库操作
		 */
		if (!before && firstNumber > rs_id) {
			// 直接在数据库进行查找
			CachedRowSet loadMoreSet = CachedRowSetDao.selectData(new ArrayList<Integer>(followings), rs_id, false);
			JSONArray jsonArray = new JSONArray();
			if (loadMoreSet.first()) {
				do {
					buildStatus(loadMoreSet, jsonArray);
				} while (loadMoreSet.next());
			}
			return jsonArray;
		}

		JSONArray jsonArray = new JSONArray();

		// 如果不在数据库中,则在缓存中查找
		synchronized (object) {
			/**
			 * 如果是更新,那么就返回至多最新10条数据,如果更新数据没有
			 * 10条,则有几条发几条
			 */

			if (before) {
				// 移动到最后一条,同时也是最新的一条
				if (statusRowSet.last()) {
					int cached_rs_id;
					do {
						// 获得当前行的标记ID
						int ID = statusRowSet.getInt(2);

						// 检查是否存在于当前用户的关注列表中
						if (followings.contains(ID)) {
							cached_rs_id = statusRowSet.getInt(1);
							// 判断当前id是否小于缓存中的rs_id
							if (cached_rs_id > rs_id) {
								/**
								 * 如果是缓存中的rs_id大于传来的rs_id,则
								 * 表明内容比当前传来的rs_id新,便可以将其
								 * 传送给客户端
								 */
								buildStatus(statusRowSet, jsonArray);
								// 检查json数组大小,如果大于10个则不再继续添加
								if (jsonArray.length() >= statusSendNumber) {
									break;
								}
							} else {
								// 如果当前rs_id不小于缓存中的rs_id,则表明内容已经
								// 不需要再更新了
								break;
							}
						}
					} while (statusRowSet.previous());
				}
			} else {
				/**
				 * 上面的代码负责更新,将传送至多10条最新数据到客户端
				 * 如果before为false,将执行下面为加载更多数据的代码
				 */
				statusRowSet.last();
				// 移动到rs_id的那一行
				do {
					if (statusRowSet.getInt(1) < rs_id) {
						break;
					}
				} while (statusRowSet.previous());

				// 一次获取10条信息,如果是缓存中的最后一条,那么也会将此条作比较
				do {
					if (followings.contains(statusRowSet.getInt(2))) {
						buildStatus(statusRowSet, jsonArray);
						if (jsonArray.length() >= statusSendNumber) {
							break;
						}
					}
				} while (statusRowSet.previous());
			}
		}

		/**
		 * 倘若操作在缓存中没有获得一条数据,存在以下几种情况
		 *
		 * 1.对于更新操作而言，确实已经没有任何数据可以更新
		 * 2.对于更新操作而言，其rs_id值小于firstNumber的值，更新虽然在缓存中没有，但是在数据库中存在
		 *
		 * 3.对于加载更多而言，存在确实已经没有任何数据可以加载，无论如何都会返回数据，除非数据库中都没
		 * 有数据了
		 * 写入的情况
		 *
		 */

		// 获取rs_id中最小的rs_id号
		synchronized (object) {
			// 将游标调整到第一行
			statusRowSet.first();
			firstNumberLater = statusRowSet.getInt(1);
		}

		if (jsonArray.length() == 0) {
			// 存放下面所获得的数据
			CachedRowSet cachedRowSetLater;

			if (before) {
				/**
				 * 对于更新操作，首先判断两次获取到的rs_id是否相等，如果相等
				 * 表明缓存在此算法执行中没有进行数据库写入。此时，表明确实已
				 * 经没有数据可以更新；如果不相等，则一定进行了数据库写入，那
				 * 需要进行数据库查询，直到到达预定rs_id值。
				 */
				if (firstNumber == firstNumberLater) {
					return jsonArray;
				}else {
					cachedRowSetLater = CachedRowSetDao.selectData(new ArrayList<Integer>(followings), rs_id, true);
				}
			}else {
				// 获取数据（因为加载更多，无论如何都会返回数据，如果缓存中不存在，那么就去数据库查询）
				cachedRowSetLater = CachedRowSetDao.selectData(new ArrayList<Integer>(followings), rs_id, false);
			}

			// 构建jsonArray
			if (cachedRowSetLater.first()) {
				do {
					buildStatus(cachedRowSetLater, jsonArray);
				} while (cachedRowSetLater.next());
			}

		}
		// 返回json数组，可能长度为0
		return jsonArray;
	}


	/**
	 * 从缓存中获得指定ID的缩略图资源，且资源的rs_id要小于传入的rs_id的值
	 * @param ID 所需要被获取的人的ID
	 * @param rs_id 资源标记
	 * @return 所获得的资源的json数组
	 * @throws SQLException
	 */
	public static JSONArray getMoreSmallPhoto (int ID, int rs_id) throws SQLException {

		JSONArray jsonArray = new JSONArray();

		int firstNumber;

		synchronized (object) {
			statusRowSet.first();
			firstNumber = statusRowSet.getInt("rs_id");
			if (firstNumber > rs_id) {
				return jsonArray;
			}else {
				// 从最新的一条查起走，这里我表示我很担心这个效率会不会
				// 在流量稍大的时候引起整个服务出现严重问题啊！！
				statusRowSet.last();
				do {
					if (statusRowSet.getInt("ID") == ID) {
						// 如果ID相等，再检查缓存中的rs_id是否大于传入标记rs_id，如果大于，放弃；如果小于
						// 便检查下json数组的长度是否是小于13的，如果大于13，放弃；否则将数据存入json数组
						if (statusRowSet.getInt("rs_id") > rs_id
								&& jsonArray.length() < 13 ) {
							buildMoreSmallPhoto(statusRowSet, jsonArray);
						}
					}
				}while (statusRowSet.previous());
			}

		}
		return jsonArray;
	}


	/**
	 * 给某个状态点赞！
	 * @param rs_id 被点赞的状态的rs_id
	 * @return 是否点赞成功；如果返回false，表示该缓存中不存在该状态
	 * @throws SQLException
	 */
	public static boolean likeIt (int rs_id) throws SQLException {

		synchronized (object) {
			if (statusRowSet.last()) {
				do {
					if (statusRowSet.getInt("rs_id") == rs_id) {
						// 更新喜欢数量
						statusRowSet.updateInt("likes_number",statusRowSet.getInt("likes_number") + 1);
						statusRowSet.updateRow();
						return true;
					}
				}while (statusRowSet.previous());
			}
		}
		return false;
	}

	/**
	 * 取消给某个状态已经点的赞
	 * @param rs_id 被点赞的状态的rs_id
	 * @return 是否点赞成功；如果返回false，表示该缓存中不存在该状态
	 * @throws SQLException
	 */
	public static boolean unLikeIt (int rs_id) throws SQLException {

		synchronized (object) {
			if (statusRowSet.last()) {
				do {
					if (statusRowSet.getInt("rs_id") == rs_id) {
						// 更新喜欢数量
						statusRowSet.updateInt("likes_number",statusRowSet.getInt("likes_number") - 1);
						statusRowSet.updateRow();
						return true;
					}
				}while (statusRowSet.previous());
			}
		}
		return false;
	}


	/**
	 * 更新缓存中状态的评论数量
	 * @param rs_id 需要被更新的状态
	 * @return 是否更新成功；如果返回false表明该缓存中不存在该状态
	 * @throws SQLException
	 */
	public static boolean addCommentNumber (int rs_id) throws SQLException {
		synchronized (object) {
			if (statusRowSet.last()) {
				do {
					if (statusRowSet.getInt("rs_id") == rs_id) {
						// 更新喜欢数量
						statusRowSet.updateInt("comments_number",statusRowSet.getInt("comments_number") + 1);
						statusRowSet.updateRow();
						return true;
					}
				}while (statusRowSet.previous());
			}
		}
		return false;
	}

	/**
	 * 更新缓存中状态的评论数量
	 * @param rs_id 需要被更新的状态
	 * @return 是否更新成功；如果返回false表明该缓存中不存在该状态
	 * @throws SQLException
	 */
	public static boolean subCommentNumber (int rs_id) throws SQLException {
		synchronized (object) {
			if (statusRowSet.last()) {
				do {
					if (statusRowSet.getInt("rs_id") == rs_id) {
						// 更新喜欢数量
						statusRowSet.updateInt("comments_number",statusRowSet.getInt("comments_number") - 1);
						statusRowSet.updateRow();
						return true;
					}
				}while (statusRowSet.previous());
			}
		}
		return false;
	}





	/**
	 * 从CachedRowSet中获取数据并填充在json对象中
	 * @param cachedRowSet 数据源
	 * @param jsonArray 需要被json对象填充的json数组
	 * @throws SQLException
	 */
	private static void buildMoreSmallPhoto (CachedRowSet cachedRowSet, JSONArray jsonArray) throws SQLException {
		int rs_id = cachedRowSet.getInt("rs_id");
		int ID = cachedRowSet.getInt("ID");
		String photoPath = cachedRowSet.getString("more_small_photo");
		String album = cachedRowSet.getString("album");

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("rs_id", rs_id);
		jsonObject.put("ID",ID);
		jsonObject.put("more_small_photo", photoPath);
		jsonObject.put("album", album);
		jsonArray.put(jsonObject);
	}




	/**
	 * 根据当前游标获取数据,并生成相应的json对象且将其放入传入的json数组中
	 *
	 * @param jsonArray 将会被添加新json对象的json数组
	 * @throws SQLException 当且仅当取出数据出错时
	 */
	private static void buildStatus(CachedRowSet cachedRowSet, JSONArray jsonArray) throws SQLException {

		// 获得rs_id
		int rs_id = cachedRowSet.getInt(1);

		int ID = cachedRowSet.getInt(2);

		// 获取时间
		Timestamp time = cachedRowSet.getTimestamp(3);

		// 获取照片分类
		String photoClass = cachedRowSet.getString(4);

		// 获取at的人
		String photoAt = cachedRowSet.getString(5);

		// 获取话题
		String photoTopic = cachedRowSet.getString(6);

		// 获取评论数量
		int commentsNumber = cachedRowSet.getInt(7);

		// 获取喜欢数量
		int likesNumber = cachedRowSet.getInt(8);

		// 获取分享数量
		int sharesNumber = cachedRowSet.getInt(9);

		// 获取是否有位置信息
		String isLocated = cachedRowSet.getString(10);

		// 获取是否有详细信息
		String hasDetail = cachedRowSet.getString(11);

		String album = cachedRowSet.getString(12);

		// 获取原信息
		String olderWords = cachedRowSet.getString(13);

		// 获取自己的描述
		String myWords = cachedRowSet.getString(14);

		// 获取照片位置信息
		String location = cachedRowSet.getString(15);

		// 获取照片view信息
		String viewPhoto = cachedRowSet.getString(16);

		// 获取殴照片detail信息
		String detailPhoto = cachedRowSet.getString(17);

		JSONObject jsonObject = new JSONObject();

		// 将相关的信息放入新创建的json对象
		jsonObject.put("rs_id", rs_id);
		jsonObject.put("ID", ID);
		jsonObject.put("time", time);
		jsonObject.put("album",album);
		jsonObject.put("photoClass", photoClass);
		jsonObject.put("photoAt", photoAt);
		jsonObject.put("photoTopic", photoTopic);
		jsonObject.put("commentsNumber", commentsNumber);
		jsonObject.put("likesNumber", likesNumber);
		jsonObject.put("sharesNumber", sharesNumber);
		jsonObject.put("isLocated", isLocated);
		jsonObject.put("hasDetail", hasDetail);
		jsonObject.put("olderWords", olderWords);
		jsonObject.put("myWords", myWords);
		jsonObject.put("location", location);
		jsonObject.put("viewPhoto", viewPhoto);
		jsonObject.put("detailPhoto", detailPhoto);

		// 将json对象放入json数组
		jsonArray.put(jsonObject);
	}


	/**
	 * 删除rs_id所标记的资源;首先会进行id对比检查,如果是ID不符合,那么删除
	 * 操作将失败,如果正确,那么进行删除操作;
	 * <p/>
	 * 首先会查询需要删除的rs_id是否在缓存中,如果在,那么就进行删除,如果不在,
	 * 则在数据库中进行删除操作
	 *
	 * @param ID    发起删除请求的id
	 * @param rs_id 需要删除的资源
	 */
	public static JSONObject deleteStatus(int ID, int rs_id) throws SQLException {

		synchronized (object) {
			statusRowSet.first();
			if (rs_id > statusRowSet.getInt(1)) {

				statusRowSet.last();
				do {
					// 如果遇到rs_id相等的,那么就检查ID值是否吻合,如果
					// 吻合,那么就进行删除,删除成功后返回true
					if (rs_id == statusRowSet.getInt(1)) {
						if (ID == statusRowSet.getInt(2)) {
							JSONObject jsonObject = buildPathJson();
							statusRowSet.deleteRow();
							return jsonObject;
						}
					}
				} while (statusRowSet.previous());
			} else if (rs_id == statusRowSet.getInt(1)) {
				// 如果碰巧最后一行就是需要删除的行,那么就进行
				// 身份验证
				if (ID == statusRowSet.getInt(2)) {
					JSONObject jsonObject = buildPathJson();
					statusRowSet.deleteRow();
					return jsonObject;
				}
			}
		}

		/**
		 * 如果上述均未返回,就意味着,需要删除的数据存在于数据库中
		 */







		return null;

	}


	private static JSONObject buildPathJson() throws SQLException {
		String viewPath = statusRowSet.getString(15);
		String detailPath = statusRowSet.getString(16);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("viewPath",viewPath);
		jsonObject.put("detailPath",detailPath);
		return jsonObject;
	}





	/**
	 * 当缓存数量达到上限后，则调用此方法从而将缓存数据存入数据库
	 *
	 * @throws SQLException
	 */

	private static void statusOverSizeHandler() throws SQLException {

		// 将删除对CacheRowSet立即执行
		statusRowSet.setShowDeleted(false);
		statusRowSet.first();
		int temp = writeOnceNumber;
		while (temp > 0) {
			CachedRowSetDao.statusSynchronized(statusRowSet);
			// 删除当前行
			statusRowSet.deleteRow();

			//将指针移动到第一行
			statusRowSet.first();
			temp--;
		}

	}


	/**
	 * CachedRowSet的getter
	 *
	 * @return status的CachedRowSet对象
	 */
	public static CachedRowSet getStatusRowSet() {

		return statusRowSet;
	}

	/**
	 * CachedRowSet的setter，并初始化statusCurrentSize
	 *
	 * @param statusRowSet 用于初始化status CachedRowSet的对象
	 */
	public static void setStatusRowSet(CachedRowSet statusRowSet) {
		StatusRowSetManager.statusRowSet = statusRowSet;

		try {
			statusRowSet.first();

			while (statusRowSet.next()) {
				statusCurrentSize++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	/**
	 * 由于rs_id是由数据库生成，且是唯一的，而缓存数据库是为了降低数据库的开销，所以必须
	 * 使用临时的id，待到向数据库写入数据时，才生成真正的id
	 * <p/>
	 * 在CachedRowSet缓存中可能存在相同rs_id号的情况，但是这并没有影响，在数据库中的操作
	 * 是依据真实的id进行，而在缓存中根据rs_id进行操作时，可能遇到相同ID和rs_id几率十分小
	 * 可以忽略不计，但是仍然有可能出现问题
	 * <p/>
	 * 由于基础数值是从1亿开始的，所以此问题暂时解决
	 *
	 * @return 临时生成的rs_id
	 */
	public static int getRs_id() {
		// 返回rs_id后自增1
		return temp_rs_id++;
	}

}
