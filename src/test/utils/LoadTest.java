package test.utils;

import utils.JsonUtils;
import utils.json.JSONArray;
import utils.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.Date;

/**
 * Created by leon on 14-9-10.
 */
public class LoadTest implements Runnable {
//	static String testUrl = "http://120.24.68.64:8080/mks/get_user_info";

//	static String testUrl = "http://127.0.0.1:8080/get_more_small_photo";
//	static String testUrl = "http://127.0.0.1:8080/login";
	static String testUrl = "http://127.0.0.1:8080/follow_by_id";


	@Override
	public void run() {
		URL url;

		try {
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
			httpURLConnection.setRequestProperty("Cookie","JSESSIONID=91A730BA4ED90E214CE7D4051C616608");



//			byte[] postInfo = JsonUtils.getJsonObject("{'ID':39;'rs_id':1000000}").toString().getBytes();

//			byte[] postInfo = JsonUtils.getJsonObject("{'account':'2012051051';'password':'520520'}").toString().getBytes();
			byte[] postInfo = JsonUtils.getJsonObject("{'his_id':23}").toString().getBytes();


			System.out.println("发出请求时间：" + new Date());

			httpURLConnection.getOutputStream().write(postInfo);


			StringBuilder sb = new StringBuilder();

			InputStream is = httpURLConnection.getInputStream();

			BufferedReader br = new BufferedReader(
					new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			System.out.println("完成时间：" + new Date());


			System.out.println(sb);



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

