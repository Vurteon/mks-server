package model.status.uploadpart;

import beans.main.PhotoDesBean;
import utils.CreateJson;
import utils.EnumUtil.ErrorCodeJson;
import utils.ThreadPoolUtils;
import utils.json.JSONObject;

import javax.imageio.ImageIO;
import javax.servlet.AsyncContext;
import javax.servlet.http.Part;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * author: 康乐
 * time: 2014/8/21
 * function: 用于处理、解析parts，线程被cpuThreadPoolExecutor管理
 * @see utils.ThreadPoolUtils
 */
public class DealPart implements Runnable{

	// 压缩照片后的照片宽度
	public static final float WIDTH = 800.0f;

	// 当前用户的ID
	private int ID;
	private AsyncContext asyncContext = null;
	// part的集合
	private Collection<Part> parts = null;

	public DealPart(AsyncContext asyncContext,Collection<Part> parts, int ID){
		this.parts = parts;
		this.asyncContext = asyncContext;
		this.ID = ID;
	}


	@Override
	public void run() {

		// 照片描述json对象
		JSONObject jsonObject;

		// 照片描述信息存储Bean
		PhotoDesBean photoDesBean = null;

		// 获取描述信息
		Iterator<Part> iterator = parts.iterator();

		if (iterator.hasNext()) {

			Part jsonPart = iterator.next();
			BufferedReader bufferedReader = null;

			try {
				bufferedReader = new BufferedReader(new InputStreamReader(jsonPart.getInputStream(),"utf-8"));

				String temp;

				StringBuilder sb = new StringBuilder();

				while ((temp = bufferedReader.readLine()) != null) {
					sb.append(temp);
				}

				// 描述照片的json对象
				jsonObject = CreateJson.getJsonObject(sb.toString());

				photoDesBean = buildJsonInfo(jsonObject);

			} catch (IOException e) {
				System.err.println("IO出错！------------------->>>需要返回给客户端信息");

				// 返回IO读取错误信息
				try {
					asyncContext.getResponse().getWriter().write(ErrorCodeJson.IOERROR.toString());
				} catch (IOException e1) {
					System.err.println("----------------------->>>向客户端发送信息错误");
					e1.printStackTrace();
				}finally {
					asyncContext.complete();
				}
				e.printStackTrace();
			}finally {

				// 关闭bufferedReader资源
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						System.err.println("释放资源出错！------------------>>>紧急错误");
						asyncContext.complete();
						e.printStackTrace();
					}
				}
			}

		}

		// 存储原图、压缩后的图片的两个容器
		ArrayList<BufferedImage> newArrayList = new ArrayList<BufferedImage>();
		ArrayList<BufferedImage> oldArrayList = new ArrayList<BufferedImage>();

		// 处理（缩小照片尺寸）照片
		while (iterator.hasNext()) {

			Part photoPart = iterator.next();

			InputStream is = null;

			try {
				 is = photoPart.getInputStream();
				// 所上传图像的内容
				BufferedImage bi = ImageIO.read(is);

				// 将宽度设置为800
				float minification = bi.getWidth()/WIDTH;

				float height = bi.getHeight() / minification;

				// 将新的图片绘制
				BufferedImage image = new BufferedImage((int)WIDTH, (int)height, BufferedImage.SCALE_SMOOTH);
				Graphics graphics = image.getGraphics();
				graphics.drawImage(bi, 0, 0, (int)WIDTH, (int)height, null);

				// 将图片资源添加到list中，以便下一个线程使用
				newArrayList.add(image);
				oldArrayList.add(bi);

			} catch (IOException e) {
				try {
					asyncContext.getResponse().getWriter().write(ErrorCodeJson.IOERROR.toString());
				} catch (IOException e1) {
					System.err.println("----------------------->>>向客户端发送信息错误");
					e1.printStackTrace();
				}finally {
					asyncContext.complete();
				}
				e.printStackTrace();
			}finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						System.err.println("释放资源出错！------------------>>>紧急错误");
						asyncContext.complete();
						e.printStackTrace();
					}
				}

			}
		}

		// 将现在的事务提交给存储线程池解决
		ThreadPoolUtils.getIoThreadPoolExecutor().submit(new SavePart(newArrayList,oldArrayList,photoDesBean,ID,asyncContext));
	}

	// 将json中的信息解析到PhotoDesBean中
	private PhotoDesBean buildJsonInfo(JSONObject jsonObject) {

		PhotoDesBean photoDesBean = new PhotoDesBean();

		photoDesBean.setID(ID);

		// 设置专辑名字
		if (jsonObject.has("albumName")) {
			photoDesBean.setAlbumName(jsonObject.getString("albumName"));
		}

		// 设置原文话语
		if (jsonObject.has("olderWords")) {
			photoDesBean.setOlderWords(jsonObject.getString("olderWords"));
		}

		// 设置自己的描述
		if (jsonObject.has("myWords")) {
			photoDesBean.setMyWords(jsonObject.getString("myWords"));
		}

		// 设置位置信息
		if (jsonObject.has("photoLocation")) {
			photoDesBean.setPhotoLocation(jsonObject.getString("photoLocation"));
		}

		// 设置类别信息
		if (jsonObject.has("photoClass")) {
			photoDesBean.setPhotoClass(jsonObject.getString("photoClass"));
		}

		// 设置at信息
		if (jsonObject.has("photoAt")) {
			photoDesBean.setPhotoAt(jsonObject.getString("photoAt"));
		}

		// 设置话题
		if (jsonObject.has("photoTopic")) {
			photoDesBean.setPhotoTopic(jsonObject.getString("photoTopic"));
		}

		return photoDesBean;
	}
}
