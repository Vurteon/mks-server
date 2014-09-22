package test.utils;

import utils.CreateJson;
import utils.PartFactory;
import utils.json.JSONArray;
import utils.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;

/**
 * Created by leon on 14-9-10.
 */
public class LoadTest implements Runnable {
	static String testUrl = "http://localhost:8080/LoadStatus";


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




			byte[] postInfo = CreateJson.getJsonObject("{'rs_id':3;'before':true}").toString().getBytes();


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

			JSONArray jsonArray = new JSONArray(sb.toString());

			int i = 0;

			while (i < jsonArray.length()) {
				JSONObject jsonObject = (JSONObject)jsonArray.get(i);
				System.out.println("ID:" + jsonObject.get("ID") + "   rs_id:" + jsonObject.get("rs_id") +
				"   时间:" + jsonObject.get("time"));
				i++;
			}



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

