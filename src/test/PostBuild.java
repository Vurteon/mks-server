package test;

import beans.user.UserInfoBean;
import model.photo.LoadMoreSmallPhoto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import test.utils.LoadTest;
import test.utils.MoreSmallPhotoTest;
import test.utils.PostPhotoTest;
import utils.client.DataLoadManager;
import utils.client.MoreSmallPhotoBean;
import utils.json.JSONObject;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by leon on 2014/8/19.
 */
public class PostBuild {

//	private static final Logger logger = LogManager.getLogger(PostBuild.class);

	public static void main(String[] args) throws IOException, InterruptedException {

//		for (int i = 0; i < 500;i++)
			new Thread(new LoadTest()).start();

//		DataLoadManager.getStatus(10,false,"CB805ACD4C84D8268379FB5961352B10");

//		HashMap<Integer,UserInfoBean> hashMap = DataLoadManager.getFollowersInfo("5BF4108171B745E6B126CADF963F103C");
//
//		System.out.println(hashMap.keySet());

//		UserInfoBean userInfoBean = DataLoadManager.getUserInfo(38, "5BF4108171B745E6B126CADF963F103C");
//
//		System.out.println(userInfoBean.getName());


//		JSONObject jsonObject = DataLoadManager.getThreeNum(39,"40DC7DE0A0C2DA7AF221E6C86D5C370C");
//
//		System.out.println(jsonObject);


//		ArrayList<MoreSmallPhotoBean> arrayList = DataLoadManager.getMoreSmallPhoto(39, 3, "2302863B75287372F69E0A881AD0A8FE");
//
//		System.out.println("长度是：" + arrayList.size());
//
//
//		for (MoreSmallPhotoBean moreSmallPhotoBean : arrayList) {
//			System.out.println("ID:" + moreSmallPhotoBean.getID());
//		}

	}
}
