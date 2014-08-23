package model.uploadpart;

import beans.main.PhotoDesBean;
import utils.EnumUtil.PhotoType;
import utils.json.JSONObject;

import javax.imageio.ImageIO;
import javax.servlet.AsyncContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by leon on 2014/8/21.
 */
public class SavePart implements Runnable{

	private long ID;
	private ArrayList<BufferedImage> newImages;
	private ArrayList<BufferedImage> oldImages;
	private PhotoDesBean photoDesBean;

	private ArrayList<String> newImagesPaths;
	private ArrayList<String> oldImagesPaths;

	AsyncContext asyncContext;

	public SavePart(ArrayList<BufferedImage> newImages, ArrayList<BufferedImage> oldImages, PhotoDesBean photoDesBean, long ID,AsyncContext asyncContext) {
		this.newImages = newImages;
		this.oldImages = oldImages;
		this.photoDesBean = photoDesBean;
		this.ID = ID;
		oldImagesPaths = new ArrayList<String>();
		newImagesPaths = new ArrayList<String>();
		this.asyncContext = asyncContext;
	}

	@Override
	public void run() {


		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		//  存储调整尺寸后的图片，并将位置信息存入newImagesPaths
		for(BufferedImage bufferedImage : newImages) {
			try {

				File saveParentFile = getSaveFile(PhotoType.RESIZE_PHOTO);
				File saveFile = new File(saveParentFile,getFileName());
				ImageIO.write(bufferedImage, "jpg",saveFile);

				// 如果是专辑，那么就存储储存专辑文件的文件夹的名字
				if (photoDesBean.getAlbumName() != null) {
					newImagesPaths.add(saveParentFile.getPath());
				}else {
					newImagesPaths.add(saveFile.getPath());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		//  存储调整尺寸后的图片，并将位置信息存入oldImagesPaths
		for (BufferedImage bufferedImage : oldImages) {
			try {

				File saveParentFile = getSaveFile(PhotoType.ORIGINAL_PHOTO);
				File saveFile = new File(saveParentFile,getFileName());
				ImageIO.write(bufferedImage, "jpg",saveFile);

				// 如果是专辑，那么就存储储存专辑文件的文件夹的名字
				if (photoDesBean.getAlbumName() != null) {
					oldImagesPaths.add(saveParentFile.getPath());
				}else {
					oldImagesPaths.add(saveFile.getPath());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 解析出照片位置信息，因为如果是专辑，那么存储的文件夹的名字，所以list中
		// 所有的名字都是一样的；如果是单张照片，那么照片位置势必只有单一的一个位置
		// 所以其大小是1，位置信息储存在0单元中，所以无论是照片还是专辑，都可以通过
		// 0号单元中的信息确定其位置信息
		String newPhotoPath;
		String oldPhotoPath;

		newPhotoPath = newImagesPaths.get(0);
		oldPhotoPath = oldImagesPaths.get(0);

		photoDesBean.setViewPhotoPath(newPhotoPath);
		photoDesBean.setDetailPhotoPath(oldPhotoPath);

		// 将json中相应的信息和图片的位置信息存储到CacheRowSet
		StatusRowSetManger.insertStatus(photoDesBean);

		// 存储线程任务完成
		asyncContext.complete();
	}


	private File getSaveFile(PhotoType photoType) {

		File savePlace = null;

		int saveDirectory = (int)(ID / 1000) + 1;

		String idFileParentName = "E:/shunjian_source/photo/" + saveDirectory + "000/" + ID;

		File idFileParent = new File(idFileParentName);

		if (!idFileParent.exists()) {
			boolean mkdirs = idFileParent.mkdirs();
		}

		if (photoType == PhotoType.ORIGINAL_PHOTO) {

			savePlace = new File(idFileParent,"originalPhoto");
			if (!savePlace.exists()) {
				savePlace.mkdir();
			}
		}else if (photoType == PhotoType.RESIZE_PHOTO) {
			savePlace = new File(idFileParent,"resizePhoto");
			if (!savePlace.exists()) {
				savePlace.mkdir();
			}
		}

		if (photoDesBean.getAlbumName() != null){
			String albumName = photoDesBean.getAlbumName();
			File albumDirectory = new File(savePlace,albumName);
			if (!albumDirectory.exists()) {
				albumDirectory.mkdir();
			}
			savePlace = albumDirectory;
		}
		return savePlace;
	}

	private String getFileName() {
		return System.currentTimeMillis() + ".jpg";
	}
}
