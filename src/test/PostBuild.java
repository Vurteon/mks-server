package test;

import utils.PartFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by leon on 2014/8/19.
 */
public class PostBuild {

	static String testUrl = "http://localhost:8080/UploadPhoto";

	static String BOUNDARY = "---------------------------7de8c1a80910";

	public static void main(String[] args) throws IOException {

		URL url = new URL(testUrl);

		HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setUseCaches(false);
		httpURLConnection.setReadTimeout(5000);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
		httpURLConnection.setRequestProperty("Charset", "UTF-8");
		httpURLConnection.setRequestProperty("User-Agent","Mozilla/4.0");

		httpURLConnection.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + BOUNDARY);

		byte[] image = new byte[10000000];

		FileInputStream fileInputStream = new FileInputStream(new File("E:/test.jpg"));

		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

		int ll = bufferedInputStream.read(image);

		byte[] newByte = new byte[ll];

		System.arraycopy(image,0,newByte,0,ll);

		httpURLConnection.getOutputStream().write(PartFactory.PartBuilder("test","test12.jpg","image/jpeg",newByte,true));

		System.out.println(httpURLConnection.getResponseCode());


//		StringBuilder sb = new StringBuilder();

//		InputStream is = httpURLConnection.getInputStream();
//		if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//			System.out.println("连接成功");
//			httpURLConnection.disconnect();
//			return;
//		}
//		sb.setLength(0);
//		BufferedReader br = new BufferedReader(
//				new InputStreamReader(is));
//		String line;
//		while ((line = br.readLine()) != null) {
//			sb.append(line);
//		}

		httpURLConnection.disconnect();
	}
}
