package model.status.uploadpart;

import beans.main.PhotoDesBean;
import model.status.StatusRowSetManager;
import utils.EnumUtil.ErrorCode;
import utils.EnumUtil.PhotoType;
import utils.StatusResponseHandler;

import javax.imageio.ImageIO;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
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

    public static final String saveHome = "/alidata/server/tomcat7/webapps/mks/moment/photo";

//	public static final String saveHome = "F:/shunjian/photo";

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

		String oldPhotoPath;

		File saveParentFile;

		ArrayList<File> savedPhoto = new ArrayList<File>();

		// 如果是专辑，那么就进行存储并将文件夹的名字赋值给相应的位置
		if (photoDesBean.getAlbumName() != null) {

			// 获得压缩后专辑照片存储的位置
			saveParentFile = getSaveFile(PhotoType.RESIZE_PHOTO);
			if (saveParentFile != null) {
				for (BufferedImage bufferedImage : newImages) {
					File saveFile = new File(saveParentFile, getFileName());
					try {
						// 将压缩后的照片以jpg格式写入相应的位置
						ImageIO.write(bufferedImage, "jpg", saveFile);
						// 存储位置记录
						savedPhoto.add(saveFile);
					} catch (IOException e) {
						// 向客户端发送io错误信息
						sendIoError(asyncContext);
						e.printStackTrace();

						// 服务器文件回滚操作
						rollBackPhoto(savedPhoto);
						// 线程结束
						return ;
					}
				}
				// 将压缩后专辑的文件夹的位置存储
				newPhotoPath = saveParentFile.getPath();
			} else {
				// 向客户端发送目录创建错误的信息
				sendMkDirErrorCode(asyncContext);
				return;
			}

			// 存储原图
			saveParentFile = getSaveFile(PhotoType.ORIGINAL_PHOTO);
			if (saveParentFile != null) {
				for (BufferedImage bufferedImage : oldImages) {
					File saveFile = new File(saveParentFile, getFileName());
					try {
						// 将原图以jpg格式写入相应的位置
						ImageIO.write(bufferedImage, "jpg", saveFile);
						savedPhoto.add(saveFile);
					} catch (IOException e) {
						sendIoError(asyncContext);
						e.printStackTrace();
						// 回滚操作
						rollBackPhoto(savedPhoto);
						// 线程结束
						return ;
					}
				}
				// 将压缩后专辑的文件夹的位置存储
				oldPhotoPath = saveParentFile.getPath();
			} else {
				// 向客户端发送错误信息
				sendMkDirErrorCode(asyncContext);
				// 原图存储出现问题，删除原来存储的压缩后的图并向客户端发送错误代码信息
				rollBackPhoto(savedPhoto);
				return ;
			}
		} else {
			// 如果不是专辑，那么就是单张照片的存储
			File savedNewFile;
			File savedOriginFile;

			// 获得压缩后照片存储的位置
			saveParentFile = getSaveFile(PhotoType.RESIZE_PHOTO);
			if (saveParentFile != null) {
				savedNewFile = new File(saveParentFile, getFileName());
				try {
					ImageIO.write(newImages.get(0), "jpg", savedNewFile);
				} catch (IOException e) {
					sendIoError(asyncContext);
					rollBackPhoto(savedNewFile);
					e.printStackTrace();
					return ;
				}
				// 照片的位置信息
				newPhotoPath = savedNewFile.getPath();
			} else {
				// 向客户端发送目录创建出错的错误信息
				sendMkDirErrorCode(asyncContext);
				return ;
			}

			// 存储单张原图
			saveParentFile = getSaveFile(PhotoType.ORIGINAL_PHOTO);
			if (saveParentFile != null) {
				savedOriginFile = new File(saveParentFile, getFileName());
				try {
					ImageIO.write(oldImages.get(0), "jpg", savedOriginFile);
				} catch (IOException e) {
					sendIoError(asyncContext);
					rollBackPhoto(savedOriginFile);
					e.printStackTrace();
					return ;
				}
				// 照片的位置信息
				oldPhotoPath = savedOriginFile.getPath();
			} else {
				// 原图存储 创建目录出错
				sendMkDirErrorCode(asyncContext);
				rollBackPhoto(savedNewFile);
				return ;
			}
		}

		// 将存储照片位置信息放入 照片描述对象
		photoDesBean.setViewPhotoPath(newPhotoPath);
		photoDesBean.setDetailPhotoPath(oldPhotoPath);

		// 将json中相应的信息和图片的位置信息存储到CacheRowSet
		// 如果出现相应的错误，则返回信息到客户端
		try {
			StatusRowSetManager.insertStatus(photoDesBean);
		} catch (SQLException e) {

			// 这里需要做些许处理，目前暂时不处理
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

		String idFileParentName = saveHome;

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
	 * 需要特别注意的是：当单个用户高并发的请求upload时，由于此处并未
	 * 考虑此种情况，所以当此种情况发生时，此处将产生一个bug----使用高
	 * 并发请求的用户所上传的照片有可能会存在被覆盖的情况，原因是高并发
	 * 造成获取的FileName一样，而同一个用户又是在一个文件夹下，所以照片
	 * 存在被覆盖的情况。实际测试中，产生这种bug的概率十分大。
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
		StatusResponseHandler.sendStatus("status", ErrorCode.MKDIRERROR,
				(HttpServletResponse) asyncContext.getResponse());
	}


	/**
	 * 向客户端发送IO错误的消息并关闭AsyncContext资源
	 *
	 * @param asyncContext servlet异步对象
	 */
	private static void sendIoError(AsyncContext asyncContext) {
		StatusResponseHandler.sendStatus("status", ErrorCode.IOERROR,
				(HttpServletResponse) asyncContext.getResponse());
	}

	private static void rollBackPhoto (ArrayList<File> files) {
		for (File file : files) {
			file.delete();
		}
	}

	private static void rollBackPhoto (File file) {
		file.delete();
	}


}
