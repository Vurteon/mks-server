package model.uploadpart;

import beans.main.PhotoDesBean;
import utils.EnumUtil.ErrorCodeJson;
import utils.EnumUtil.PhotoType;

import javax.imageio.ImageIO;
import javax.servlet.AsyncContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * author: 康乐
 * time: 2014/8/24
 * function: 存储新上传的照片并将相关的信息存入CacheRowSet
 */


public class SavePart implements Runnable {

	static long asd = 0;


	private long ID;
	private ArrayList<BufferedImage> newImages;
	private ArrayList<BufferedImage> oldImages;
	private PhotoDesBean photoDesBean;

	AsyncContext asyncContext;

	public SavePart(ArrayList<BufferedImage> newImages, ArrayList<BufferedImage> oldImages, PhotoDesBean photoDesBean, long ID, AsyncContext asyncContext) {
		this.newImages = newImages;
		this.oldImages = oldImages;
		this.photoDesBean = photoDesBean;
		this.ID = ID;
		this.asyncContext = asyncContext;
	}

	@Override
	public void run() {

		String newPhotoPath;

		String oldPhotoPath = null;

		File saveParentFile;

		// 如果是专辑，那么就进行存储并将文件夹的名字赋值给相应的位置
		if (photoDesBean.getAlbumName() != null ) {

			// 获得压缩后专辑照片存储的位置
			saveParentFile = getSaveFile(PhotoType.RESIZE_PHOTO);
			if (saveParentFile != null) {

				for (BufferedImage bufferedImage : newImages) {
					File saveFile = new File(saveParentFile, getFileName());
					try {
						ImageIO.write(bufferedImage, "jpg", saveFile);
					} catch (IOException e) {
						sendIoError(asyncContext);
						File[] files = saveParentFile.listFiles();
						if (files != null) {
							for (File file : files) {
								file.delete();
							}
						}
						e.printStackTrace();
					}
				}

				// 将压缩后专辑的文件夹的位置存储
				newPhotoPath = saveParentFile.getPath();
			}else {
				sendMkDirErrorCode(asyncContext);
				return ;
			}


			// 存储原图
			saveParentFile = getSaveFile(PhotoType.ORIGINAL_PHOTO);
			if (saveParentFile != null) {

				for (BufferedImage bufferedImage : oldImages) {
					File saveFile = new File(saveParentFile, getFileName());
					try {
						ImageIO.write(bufferedImage, "jpg", saveFile);
					} catch (IOException e) {
						sendIoError(asyncContext);
						//删除开始存储的新图
						File f = new File(newPhotoPath);
						File[] oldFiles = f.listFiles();
						if (oldFiles != null) {
							for (File oldPhoto : oldFiles) {
								oldPhoto.delete();
							}
						}

						// 删除原图
						File[] originalFiles = saveParentFile.listFiles();
						if (originalFiles != null) {
							for (File file : originalFiles) {
								file.delete();
							}
						}
						e.printStackTrace();
					}
				}
				// 将压缩后专辑的文件夹的位置存储
				oldPhotoPath = saveParentFile.getPath();
			}else {
				// 向客户端发送错误信息
				sendMkDirErrorCode(asyncContext);

				// 原图存储出现问题，删除原来存储的新图并向客户端发送错误代码信息
				File file = new File(newPhotoPath);
				File[] files = file.listFiles();
				if (files != null) {
					for (File f : files) {
						f.delete();
					}
				}

			}
		}else {
			// 如果不是专辑，那么就是单张照片的存储

			// 获得压缩后照片存储的位置
			saveParentFile = getSaveFile(PhotoType.RESIZE_PHOTO);
			if (saveParentFile != null) {
				File saveFile = new File(saveParentFile, getFileName());
				// 照片的位置信息
				newPhotoPath = saveFile.getPath();
				try {
					ImageIO.write(newImages.get(0), "jpg", saveFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				// 向客户端发送错误信息
				sendMkDirErrorCode(asyncContext);
				return ;
			}

			// 存储单张原图
			saveParentFile = getSaveFile(PhotoType.ORIGINAL_PHOTO);
			if (saveParentFile != null) {
				File saveFile = new File(saveParentFile, getFileName());
				// 照片的位置信息
				oldPhotoPath = saveFile.getPath();
				try {
					ImageIO.write(oldImages.get(0), "jpg", saveFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				sendMkDirErrorCode(asyncContext);
				File file = new File(newPhotoPath);
				file.delete();
			}
		}

		photoDesBean.setViewPhotoPath(newPhotoPath);
		photoDesBean.setDetailPhotoPath(oldPhotoPath);

		// 将json中相应的信息和图片的位置信息存储到CacheRowSet
		// 如果出现相应的错误，则返回信息到客户端
		try {
			StatusRowSetManger.insertStatus(photoDesBean);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 存储线程任务完成
		asyncContext.complete();
	}


	/**
	 * 创建保存位置的文件夹，如果是多个文件夹创建失败，将会依次删除创建好后的文件夹
	 * 如果是单个文件夹创建失败，则返回null
	 *
	 * @param photoType 照片的类型
	 * @return 如果成功创建，那么返回创建好的File对象；如果创建失败，返回null
	 * @see utils.EnumUtil.PhotoType
	 */
	private File getSaveFile(PhotoType photoType) {

		File savePlace = null;

		int saveDirectory = (int) (ID / 1000) + 1;

		String idFileParentName = "E:/shunjian_source/photo/";

		File idFileParent = new File(idFileParentName, saveDirectory + "000/" + ID);

		if (!idFileParent.exists()) {
			boolean mkdirs = idFileParent.mkdirs();
			if (!mkdirs) {
				// 循环删除已经创建的文件夹
				// 但是这里是不用删除，因为每一个ID都有自己的专属文件夹，就算创建了也没有什么
				return null;
			}
		}

		if (photoType == PhotoType.ORIGINAL_PHOTO) {

			savePlace = new File(idFileParent, "originalPhoto");
			if (!savePlace.exists()) {
				boolean mkdir = savePlace.mkdir();
				if (!mkdir) {
					return null;
				}
			}
		} else if (photoType == PhotoType.RESIZE_PHOTO) {
			savePlace = new File(idFileParent, "resizePhoto");
			if (!savePlace.exists()) {
				boolean mkdir = savePlace.mkdir();
				if (!mkdir) {
					return null;
				}
			}
		}

		if (photoDesBean.getAlbumName() != null) {
			String albumName = photoDesBean.getAlbumName();
			File albumDirectory = new File(savePlace, albumName);
			if (!albumDirectory.exists()) {
				boolean mkdir = albumDirectory.mkdir();
				if (!mkdir) {
					return null;
				}
			}
			savePlace = albumDirectory;
		}
		return savePlace;
	}


	/**
	 * 创建名字为当前毫秒数的图片jpg文件
	 *
	 * @return 格式为jpg的文件名字
	 */
	private String getFileName() {
		return System.currentTimeMillis() + ".jpg";
	}

	/**
	 * 向客户端发送文件夹创建错误消息并关闭AsyncContext资源
	 *
	 * @param asyncContext servlet异步对象
	 */
	private void sendMkDirErrorCode(AsyncContext asyncContext) {
		try {
			asyncContext.getResponse().getWriter().write(ErrorCodeJson.MKDIRERROR.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭AsyncContext资源
			asyncContext.complete();
		}
	}


	/**
	 * 向客户端发送IO错误的消息并关闭AsyncContext资源
	 * @param asyncContext servlet异步对象
	 */
	private void sendIoError(AsyncContext asyncContext) {
		try {
			asyncContext.getResponse().getWriter().write(ErrorCodeJson.IOERROR.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭AsyncContext资源
			asyncContext.complete();
		}
	}


}
