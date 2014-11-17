package utils.client;

import beans.user.UserInfoBean;
import utils.PartFactory;
import utils.json.JSONArray;
import utils.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * author：康乐
 * time：2014/11/13
 * function：用于和服务器进行数据交换的方法集合
 */


public class DataLoadManager {

	/**
	 * 用于从server load status数据，传入服务器所需要的参数，将获得服务器所返回的一定长度
	 * 的数据
	 * @param rs_id 用作标记的资源编号
	 * @param before 如果为真，表示刷新；否则表示加载更多
	 * @return 带有当前请求的数据的数组
	 */
	public static ArrayList<StatusBean> getStatus(int rs_id, boolean before, String sessionID) throws IOException {

		HttpURLConnection httpURLConnection = ConnectionHandler.getConnect(UrlSource.LOAD_STATUS,sessionID);

		JSONObject jsonObject = new JSONObject("{" + "'rs_id':" + rs_id + ";'before':" + before + "}");

		httpURLConnection.getOutputStream().write(jsonObject.toString().getBytes());

		String allData = readData(httpURLConnection.getInputStream());

		String realData = allData.substring(0,allData.length() - 20);

		JSONArray jsonArray = new JSONArray(realData);

		ArrayList<StatusBean> arrayList = new ArrayList<StatusBean>();

		int i = 0;
		while (i < jsonArray.length()) {
			arrayList.add(buildStatusData(jsonArray.getJSONObject(i)));
			i++;
		}
		// 关闭连接
		httpURLConnection.disconnect();
		return arrayList;
	}


	public static ArrayList<MoreSmallPhotoBean> getMoreSmallPhoto (int ID, int rs_id, String sessionID) throws IOException {
		HttpURLConnection httpURLConnection = ConnectionHandler.getConnect(UrlSource.GET_MORE_SMALL_PHOTO,sessionID);

		JSONObject jsonObject = new JSONObject("{" + "'rs_id':" + rs_id + ";'ID':" + ID + "}");

		httpURLConnection.getOutputStream().write(jsonObject.toString().getBytes());

		String allData = readData(httpURLConnection.getInputStream());

		String realData = allData.substring(0,allData.length() - 20);

		JSONArray jsonArray = new JSONArray(realData);

		ArrayList<MoreSmallPhotoBean> arrayList = new ArrayList<MoreSmallPhotoBean>();

		int i = 0;
		while (i < jsonArray.length()) {
			arrayList.add(buildMoreSmallPhoto(jsonArray.getJSONObject(i)));
			i++;
		}
		// 关闭连接
		httpURLConnection.disconnect();
		return arrayList;
	}





	/**
	 * 向服务器上传status
	 * @param descJson status描述数据
	 * @param photos status的BitMap数组
	 * @param sessionID 上传所需要的验证session
	 * @return 返回服务器所返回的json数据
	 * @throws IOException
	 */
	public static JSONObject upLoadPhoto (JSONObject descJson, ArrayList<Object> photos, String sessionID) throws IOException {

		HttpURLConnection httpURLConnection = ConnectionHandler.getConnect(UrlSource.UPLOAD_PHOTO,sessionID,true);

		OutputStream outputStream = httpURLConnection.getOutputStream();
		// 传输json数据
		outputStream.write(PartFactory.PartBuilder("json", "json", "text/plain", descJson.toString().getBytes()));

		// 传输照片数据
		int i = 0;
		while (i < photos.size() - 1) {
			// 构造字节数组
			byte[] image = new byte[10];

			outputStream.write(PartFactory.PartBuilder(String.valueOf(i), String.valueOf(i), "image/jpeg", image));

			i++;
		}

		byte[] lastImage = new byte[10];
		// 最后一个图片，需要进行结尾处理
		outputStream.write(PartFactory.PartBuilder(String.valueOf(i), String.valueOf(i), "image/jpeg", lastImage, true));


		// 由于是上传图片，消耗时间较多，所以必须对相应时限进行控制
		httpURLConnection.setReadTimeout(180000);
		httpURLConnection.setConnectTimeout(10000);

		// 获得服务器返回数据
		String statusCode = readData(httpURLConnection.getInputStream());

		// 释放资源
		httpURLConnection.disconnect();
		return new JSONObject(statusCode);
	}


	/**
	 * 依据所传入的image url，连接服务器并获得image数据，返回字节数组
	 * @param imageUrl 图片url
	 * @return 包含图片数据的字节数组
	 * @throws IOException
	 */
	public static byte[] getImage(String imageUrl) throws IOException {

		HttpURLConnection httpURLConnection = ConnectionHandler.getConnect(imageUrl);

		String imageString = readData(httpURLConnection.getInputStream());
		// 关闭连接
		httpURLConnection.disconnect();
		return imageString.getBytes();
	}


	/**
	 * 获取自己关注的人的信息
	 * @return 自己关注的人的信息的hash表
	 */
	public static HashMap<Integer, UserInfoBean> getFollowersInfo (String sessionID) throws IOException {

		HttpURLConnection httpURLConnection = ConnectionHandler.getConnect(UrlSource.GET_FOLLOWINGS_INFO, sessionID);

		String usersInfo = readData(httpURLConnection.getInputStream());

		JSONArray jsonArray = new JSONArray(usersInfo);

		HashMap<Integer, UserInfoBean> hashMap = new HashMap<Integer, UserInfoBean>();

		int i = 0;
		while (i < jsonArray.length()) {
			UserInfoBean userInfoBean = buildUserInfo(jsonArray.getJSONObject(i));
			hashMap.put(userInfoBean.getID(),userInfoBean);
			i++;
		}
		// 关闭连接
		httpURLConnection.disconnect();
		return hashMap;
	}


	/**
	 * 返回给定的id的人的信息
	 * @param ID 给定的人的id
	 * @param sessionID 自己登陆的session
	 * @return 给定的人的id的个人信息
	 * @throws IOException
	 */
	public static UserInfoBean getUserInfo (int ID, String sessionID) throws IOException {

		HttpURLConnection httpURLConnection = ConnectionHandler.getConnect(UrlSource.GET_USER_INFO, sessionID);
		JSONObject jsonObject = new JSONObject("{'ID':" + ID + "}");

		httpURLConnection.getOutputStream().write(jsonObject.toString().getBytes());

		String userInfo = readData(httpURLConnection.getInputStream());
		httpURLConnection.disconnect();
		return buildUserInfo(new JSONObject(userInfo));
	}

	/**
	 * 从服务器获得指定ID用户的三个数量指标
	 * @param ID 用户ID
	 * @param sessionID 自己登陆的session
	 * @return 包含三个数量的json对象
	 * @throws IOException
	 */
	public static JSONObject getThreeNum (int ID, String sessionID) throws IOException {
		HttpURLConnection httpURLConnection = ConnectionHandler.getConnect(UrlSource.GET_THREE_NUMBER, sessionID);
		JSONObject jsonObject = new JSONObject("{'ID':" + ID + "}");

		httpURLConnection.getOutputStream().write(jsonObject.toString().getBytes());

		String userInfo = readData(httpURLConnection.getInputStream());
		httpURLConnection.disconnect();
		return new JSONObject(userInfo);
	}

	/**
	 * 通过输入流获取其中的数据
	 * @param inputStream 作为数据源的输入流
	 * @return 输入流中所有的数据
	 * @throws IOException
	 */
	private static String readData (InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(
				new InputStreamReader(inputStream));
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}


	/**
	 * 依据json对象，创建一个Status对象
	 * @param jsonObject 数据源json
	 * @return 依据数据源所创建的status对象
	 */
	private static StatusBean buildStatusData (JSONObject jsonObject) {

		StatusBean statusBean = new StatusBean();

		statusBean.setID(jsonObject.getInt("ID"));
		statusBean.setRs_id(jsonObject.getInt("rs_id"));
		statusBean.setTime(jsonObject.getString("time"));

		if (jsonObject.has("albumName")) {
			statusBean.setAlbumName(jsonObject.getString("albumName"));
		}

		if (jsonObject.has("olderWords")) {
			statusBean.setOlderWords(jsonObject.getString("olderWords"));
		}

		if (jsonObject.has("myWords")) {
			statusBean.setMyWords(jsonObject.getString("myWords"));
		}

		if (jsonObject.has("photoLocation")) {
			statusBean.setPhotoLocation(jsonObject.getString("photoLocation"));
		}

		if (jsonObject.has("moreSmallPhotoPath")) {
			statusBean.setMoreSmallPhotoPath(jsonObject.getString("moreSmallPhotoPath"));
		}

		if (jsonObject.has("viewPhotoPath")) {
			statusBean.setViewPhotoPath(jsonObject.getString("viewPhotoPath"));
		}

		if (jsonObject.has("detailPhotoPath")) {
			statusBean.setDetailPhotoPath(jsonObject.getString("detailPhotoPath"));
		}

		if (jsonObject.has("photoClass")) {
			statusBean.setPhotoClass(jsonObject.getString("photoClass"));
		}

		if (jsonObject.has("photoAt")) {
			statusBean.setPhotoAt(jsonObject.getString("photoAt"));
		}

		if (jsonObject.has("photoTopic")) {
			statusBean.setPhotoTopic(jsonObject.getString("photoTopic"));
		}
		return statusBean;
	}


	/**
	 * 根据json对象，创建一个用户信息对象
	 * @param jsonObject 数据输入源
	 * @return 根据输入数据所创建的用户信息对象
	 */
	private static UserInfoBean buildUserInfo (JSONObject jsonObject) {

		UserInfoBean userInfoBean = new UserInfoBean();

		userInfoBean.setID(jsonObject.getInt("ID"));

		if (jsonObject.has("name")) {
			userInfoBean.setName(jsonObject.getString("name"));
		}

		if (jsonObject.has("brief_intro")) {
			userInfoBean.setBrief_intro(jsonObject.getString("brief_intro"));
		}

		if (jsonObject.has("bg_photo")) {
			userInfoBean.setBg_photo(jsonObject.getString("bg_photo"));
		}

		if (jsonObject.has("main_head_photo")) {
			userInfoBean.setMain_head_photo(jsonObject.getString("main_head_photo"));
		}

		if (jsonObject.has("home_head_photo")) {
			userInfoBean.setHome_head_photo(jsonObject.getString("home_head_photo"));
		}
		return userInfoBean;
	}


	/**
	 *
	 * @param jsonObject
	 * @return
	 */
	private static MoreSmallPhotoBean buildMoreSmallPhoto (JSONObject jsonObject) {

		MoreSmallPhotoBean moreSmallPhotoBeanc = new MoreSmallPhotoBean();

		moreSmallPhotoBeanc.setID(jsonObject.getInt("ID"));
		moreSmallPhotoBeanc.setRs_id(jsonObject.getInt("rs_id"));
		moreSmallPhotoBeanc.setPath(jsonObject.getString("more_small_photo"));
		if (jsonObject.has("album")) {
			moreSmallPhotoBeanc.setAlbum(jsonObject.getString("album"));
		}
		return moreSmallPhotoBeanc;
	}

}
