package test.utils;

import utils.CreateJson;
import utils.PartFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by leon on 2014/8/23.
 */
public class PostTest implements Runnable {

	static String testUrl = "http://localhost:8080/UploadPhoto";

	static String BOUNDARY = "---------------------------7de8c1a80910";

	@Override
	public void run() {
		URL url = null;
		try {
			url = new URL(testUrl);

			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setReadTimeout(5000);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0");

			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);

			byte[] image = new byte[8000000];

			FileInputStream fileInputStream = new FileInputStream(new File("E:/asd.jpg"));

			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

			int ll = bufferedInputStream.read(image);

			byte[] newByte = new byte[ll];

			System.arraycopy(image, 0, newByte, 0, ll);

			byte[] asd = CreateJson.getJsonObject("{'a':'我的雪山之旅'}").toString().getBytes();

//		byte[] asd = "{'a':'b}".getBytes();
			httpURLConnection.getOutputStream().write(PartFactory.PartBuilder("test", "test", "text/plain", asd));

			httpURLConnection.getOutputStream().write(PartFactory.PartBuilder("test", "test12.jpg", "image/jpeg", newByte, true));

			httpURLConnection.setReadTimeout(100000);

			System.out.println(httpURLConnection.getResponseCode());


//			StringBuilder sb = new StringBuilder();
//
//			InputStream is = httpURLConnection.getInputStream();
//
//			BufferedReader br = new BufferedReader(
//					new InputStreamReader(is));
//			String line;
//			while ((line = br.readLine()) != null) {
//				sb.append(line);
//			}
//
//			System.out.println(sb);
//
//			httpURLConnection.disconnect();


		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}
