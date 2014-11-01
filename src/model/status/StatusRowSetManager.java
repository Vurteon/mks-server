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
	 * <p/>
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

		// 缓存中的最小的rs_id
		int firstNumber;

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
		if (!before && firstNumber >= rs_id) {
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
				// 移动到rs_id的旧一行
				do {
					if (statusRowSet.getInt(1) < rs_id) {
						break;
					}
				} while (statusRowSet.previous());

				// 一次获取10条信息,如果是缓存中的最后一条,那么也会将此条做比较
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
		 * 倘若操作在缓存中没有获得一条数据,则表明更新操作或者加载更多操作需要在数据库进行
		 *
		 * 此处进行此检查有两种防备:
		 * 1.如果在获取数据过程中没有出现缓存向数据库写数据的情况,而且确实在缓存中没有获取到数据的情况
		 * 2.如果是在获取过程中出现了缓存向数据库写数据,而刚好将需要获取的数据全部写入了数据库,那么此
		 *   时也会获取不到数据,那么此时也需要从数据库中获取数据
		 */

		// 获取rs_id中最小的rs_id号
		synchronized (object) {
			// 将游标调整到第一行
			statusRowSet.first();
			firstNumber = statusRowSet.getInt(1);
		}
		// 1.如果是加载更多获得的数据为0,那么进行数据库查找,并获取相关数据
		// 2.如果是更新到最后一行后,没有数据且更新所给的rs_id小于缓存中最小的rs_id,就表明数据更新需要到数据库中继续进行
		if ((!before && jsonArray.length() == 0) || (before && jsonArray.length() == 0 && firstNumber >= rs_id)) {
			CachedRowSet cachedRowSet = CachedRowSetDao.selectData(new ArrayList<Integer>(followings), rs_id, before);
			if (cachedRowSet.first()) {
				do {
					buildStatus(cachedRowSet, jsonArray);
				} while (cachedRowSet.next());
			}
		}
		return jsonArray;
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

		// 获取原信息
		String olderWords = cachedRowSet.getString(12);

		// 获取自己的描述
		String myWords = cachedRowSet.getString(13);

		// 获取照片位置信息
		String location = cachedRowSet.getString(14);

		// 获取照片view信息
		String viewPhoto = cachedRowSet.getString(15);

		// 获取殴照片detail信息
		String detailPhoto = cachedRowSet.getString(16);

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
