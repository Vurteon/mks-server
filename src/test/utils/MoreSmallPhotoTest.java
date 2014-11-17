package test.utils;

import utils.client.DataLoadManager;
import utils.client.MoreSmallPhotoBean;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by leon on 2014/11/16.
 */
public class MoreSmallPhotoTest implements Runnable{
	@Override
	public void run() {
		ArrayList<MoreSmallPhotoBean> arrayList = null;
		try {
			arrayList = DataLoadManager.getMoreSmallPhoto(39, 3, "2302863B75287372F69E0A881AD0A8FE");
			System.out.println("长度是：" + arrayList.size());


			for (MoreSmallPhotoBean moreSmallPhotoBean : arrayList) {
				System.out.println("ID:" + moreSmallPhotoBean.getID());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}
