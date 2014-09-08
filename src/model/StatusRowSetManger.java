package model;

import beans.main.PhotoDesBean;
import dao.main.CachedRowSetDao;
import utils.db.TimeBuilder;
import utils.json.JSONArray;
import utils.json.JSONObject;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;

/**
 * author: 康乐
 * time: 2014/8/22
 * function: 提供对RowSet的相关操作(增、删、查),将RowSetDao所需要的信息进行封装
 * 并在内存数据库存储数量大于额定数值后向mysql数据库写入一部分数据
 */

public class StatusRowSetManger {

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

		// 插入数据时需要保持同步性
		synchronized (object) {
			photoDesBean.setTime(TimeBuilder.getLocalTime());
			photoDesBean.setRs_id(getRs_id());
			// 如果数据数量已超出最大容量，那么就向数据库写入数据，此时也需要
			// 保持同步性，所以需要在synchronized块中进行
			if (statusCurrentSize >= statusSetMaxSize) {
				statusOverSizeHandler();
				statusCurrentSize -= writeOnceNumber;
			}

			boolean b = CachedRowSetDao.insertData(photoDesBean);

			// 当前数量大小增1，由于是写完后才进行数量增加，所以每一次
			// 需要数据同步的时候都是下一个请求引起，而最后一个请求刚好
			// 达到上限的值
			if (b) {
				statusCurrentSize++;
			}
			return b;
		}
	}


	/**
	 * 使用同步控制,依据参数从缓存或者数据库中获取一定数量的数据.如果当前
	 * rs_id小于缓存中最小的rs_id,那么就在数据库中进行查找操作;否则便在
	 * 缓存中进行查找. 随后将查找到的数据封装为json对象返回
	 *
	 * 需要说明的是:此算法存在漏洞-->当数据库中的id大于临时id时会出现许多
	 * 意外的bug,但是这个将会是数据库存储数量达到10亿时候的事情,那个时候
	 * 这个bug我想早就已经修复了.
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

		statusRowSet = CachedRowSetDao.buildNewCacheRowSet();


//		statusRowSet.first();
//
//		do {
//
//			System.out.println(statusRowSet.getInt(1) + " : " + statusRowSet.getTimestamp(3));
//
//		}while (statusRowSet.next());




		// 如果不在数据库中,则在缓存中查找
		synchronized (object) {

			JSONArray jsonArray = new JSONArray();
			/**
			 * 如果是更新,那么就返回至多最新10条数据,如果更新数据没有
			 * 10条,则有几条发几条
			 */

			if (before) {
//				System.out.println("更新");
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
								buildStatus(jsonArray);
								// 检查json数组大小,如果大于10个则不再继续添加
								if (jsonArray.length() >= statusSendNumber) {
									break;
								}
							} else {
//								System.out.println("已经是最新内容");
								// 如果当前rs_id不小于缓存中的rs_id,则表明内容已经
								// 不需要再更新了
								break;
							}
						}
					} while (statusRowSet.previous());
				}
				return jsonArray;
			}

			/**
			 * 上面的代码负责更新,将传送至多10条最新数据到客户端
			 * 如果before为false,将执行下面为加载更多数据的代码
			 */

			int firstNumber;

			synchronized (object) {
				// 将游标调整到第一行
				statusRowSet.first();
				firstNumber = statusRowSet.getInt(1);
			}

			/**
			 * 如果发来的rs_id小于缓存中最小的rs_id,表明其已经存于数据库,所以
			 * 下面直接进行数据库操作
			 */
			if (firstNumber >= rs_id) {
				CachedRowSet loadMoreSet = CachedRowSetDao.deleteData(rs_id);
//				System.out.println("直接淘汰,查询数据库");
				return jsonArray;
			}

//			System.out.println("加载更多");
			statusRowSet.last();
			// 移动到rs_id的旧一行
			do {
				if (statusRowSet.getInt(1) < rs_id) {
					break;
				}
			}while (statusRowSet.previous());

			// 一次获取10条信息
			do {
				if (followings.contains(statusRowSet.getInt(2))) {
					buildStatus(jsonArray);
					if (jsonArray.length() >= statusSendNumber) {
						break;
					}
				}
			}while (statusRowSet.previous());

			// 返回数据
			return jsonArray;
		}
	}


	/**
	 * 根据当前游标获取数据,并生成相应的json对象且将其放入传入的json数组中
	 *
	 * @param jsonArray 将会被添加新json对象的json数组
	 * @throws SQLException 当且仅当取出数据出错时
	 */
	private static void buildStatus(JSONArray jsonArray) throws SQLException {

		// 获得rs_id
		int rs_id = statusRowSet.getInt(1);

		int ID = statusRowSet.getInt(2);

		// 获取时间
		Timestamp time = statusRowSet.getTimestamp(3);

		// 获取照片分类
		String photoClass = statusRowSet.getString(4);

		// 获取at的人
		String photoAt = statusRowSet.getString(5);

		// 获取话题
		String photoTopic = statusRowSet.getString(6);

		// 获取评论数量
		int commentsNumber = statusRowSet.getInt(7);

		// 获取喜欢数量
		int likesNumber = statusRowSet.getInt(8);

		// 获取分享数量
		int sharesNumber = statusRowSet.getInt(9);

		// 获取是否有位置信息
		String isLocated = statusRowSet.getString(10);

		// 获取是否有详细信息
		String hasDetail = statusRowSet.getString(11);

		// 获取原信息
		String olderWords = statusRowSet.getString(12);

		// 获取自己的描述
		String myWords = statusRowSet.getString(13);

		// 获取照片位置信息
		String location = statusRowSet.getString(14);

		// 获取照片view信息
		String viewPhoto = statusRowSet.getString(15);

		// 获取殴照片detail信息
		String detailPhoto = statusRowSet.getString(16);

		JSONObject jsonObject = new JSONObject();

		// 将相关的信息放入新创建的json对象
		jsonObject.put("rs_id", rs_id);
		jsonObject.put("ID", ID);
		jsonObject.put("time", time);
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
		// 如果一切都顺利,那么就将返回true
	}


	public static void deleteStatus() {

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
		StatusRowSetManger.statusRowSet = statusRowSet;

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
