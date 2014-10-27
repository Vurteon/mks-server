package test.utils;

import utils.JsonUtils;
import utils.PartFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by leon on 2014/8/23.
 */
public class PostTest implements Runnable {


	public static ArrayList<String> asds = new ArrayList<String>();


	static String testUrl = "http://localhost:8080/UploadPhoto";

	static String BOUNDARY = "---------------------------7de8c1a80910";

	@Override
	public void run() {
		URL url = null;
		try {


//			HttpClient


			url = new URL(testUrl);

			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setReadTimeout(100000);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0");

			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);

			byte[] image = new byte[8000000];

			FileInputStream fileInputStream = new FileInputStream(new File("/home/leon/asd.jpg"));

			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

			int ll = bufferedInputStream.read(image);

			byte[] newByte = new byte[ll];

			System.arraycopy(image, 0, newByte, 0, ll);

			byte[] asd = JsonUtils.getJsonObject("{'myWords':'test'}").toString().getBytes();

			System.out.println("发出请求时间：" + new Date());

			httpURLConnection.getOutputStream().write(PartFactory.PartBuilder("test", "test", "text/plain", asd));

			httpURLConnection.getOutputStream().write(PartFactory.PartBuilder("test", "test12.jpg", "image/jpeg", newByte, true));

			httpURLConnection.setReadTimeout(100000);

			httpURLConnection.setConnectTimeout(10000);

			StringBuilder sb = new StringBuilder();

			InputStream is = httpURLConnection.getInputStream();

			BufferedReader br = new BufferedReader(
					new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}


			asds.add(sb.toString());

			System.out.println("完成时间：" + new Date());
			httpURLConnection.disconnect();


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

