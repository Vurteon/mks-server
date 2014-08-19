package servlet.phone;

import utils.GetPostContent;
import utils.ShowRequestInfo;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * author: 康乐
 * time: 2014/8/17
 * function: 接受手机端发来的上传照片请求，并将照片存储并生成相应的记录
 */

@WebServlet(urlPatterns = "/UploadPhoto")
@MultipartConfig()
public class UploadPhotoServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		ShowRequestInfo.showHeaderContent(request);

//		Collection<Part> part = request.getParts();
//
//		Iterator<Part> iterator = part.iterator();
//
//		while (iterator.hasNext()) {
//
//			Part part1 = iterator.next();
//
//			InputStream inputStream = part1.getInputStream();
//
//			InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"utf-8");
//
//			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//			String content = bufferedReader.readLine();
//			System.out.println(content);
//		}

		Part part = request.getPart("test");

		part.write("E:/asd.jpg");


//		BufferedImage bi = ImageIO.read(part.getInputStream());
//
//		int w = 400;
//
//		int h = 300;
//
//
//		// 下面的代码是可以将图片压缩并缩放到指定的大小
//
//		BufferedImage image = new BufferedImage(w, h, BufferedImage.SCALE_SMOOTH);
//		Graphics graphics = image.getGraphics();
//		graphics.drawImage(bi, 0, 0, w, h, null);
//
//
//		File ff = new File("E:/test2.jpg");
//		ImageIO.write(image,"jpg",ff);

////


//		Collection<Part> part = request.getParts();
//		System.out.println(part.size());
////
////		//String content = GetPostContent.getPostContent(request);
////		//System.out.println(content);
//
//		BufferedReader br;
//
//		StringBuilder sb = null;
//		br = new BufferedReader(new InputStreamReader(
//				request.getInputStream(), "utf-8"));
//		String temp;
//
//
//		while ((temp = br.readLine()) != null) {
//			System.out.println(temp);
//		}
//		br.close();


		System.out.println("连接成功！！");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
