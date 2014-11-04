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

	static String testUrl = "http://127.0.0.1:8080/login";


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
			httpURLConnection.setRequestProperty("Cookie","JSESSIONID=527DFF236FA9A723A80657382A8DD7EF");



			byte[] postInfo = JsonUtils.getJsonObject("{'account':'2012051051';'password':'520520'}").toString().getBytes();


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
//			System.out.println(URLDecoder.decode(sb.toString(),"utf8"));
//			System.out.println(new JSONObject(sb.toString()));

//			JSONArray jsonArray = new JSONArray(sb.toString());
//
//			int i = 0;
//
//			while (i < jsonArray.length()) {
//				JSONObject jsonObject = (JSONObject)jsonArray.get(i);
//				System.out.println("ID:" + jsonObject.get("ID") + "   rs_id:" + jsonObject.get("rs_id") +
//				"   时间:" + jsonObject.get("time"));
//				i++;
//			}



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

