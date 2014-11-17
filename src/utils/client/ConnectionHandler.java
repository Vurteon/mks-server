package utils.client;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Administrator on 2014/11/2.
 * @author 谭林
 * @version 1.0
 */
public class ConnectionHandler {

	public static final String URL = "http://localhost:8080";

	public static final int CONNECTION_TIME_OUT = 5000;


	/**
	 * 返回一个和指定url相连的连接对象
	 * @param url 需要连接的url
	 * @return 一个url连接对象，采用post连接方式
	 */
	public static HttpURLConnection getConnect(String url) {
		URL urlObject;
		HttpURLConnection connection = null;
		try {
			urlObject = new URL(URL + url);
			connection = (HttpURLConnection) urlObject.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setConnectTimeout(CONNECTION_TIME_OUT);
			//设置请求头字段
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("user-agent", "Android 4.0.1");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	}


	/**
	 * 返回一个url连接对象
	 * @param url 需要连接的url
	 * @param sessionID 连接所需要的session
	 * @return 一个url连接对象，使用post连接;如果session错误或者过期，那么这个连接和普通连接没有区别
	 */
	public static HttpURLConnection getConnect(String url, String sessionID) {
		URL urlObject;
		HttpURLConnection connection = null;
		try {
			urlObject = new URL(URL + url);
			connection = (HttpURLConnection) urlObject.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setConnectTimeout(CONNECTION_TIME_OUT);
			//设置请求头字段
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("user-agent", "Android 4.0.1");
			connection.setRequestProperty("Cookie","JSESSIONID=" + sessionID);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	}


	/**
	 * 返回一个使用multi-part传送数据的连接
	 * @param url 指定的支持multi-part数据传送的url
	 * @param sessionID 传输数据中可能需要的session
	 * @param isMultiData 是否为multi-part数据标记
	 * @return 一个url连接对象;如果isMultiData为假，则使用普通数据传输方式的连接
	 */
	public static HttpURLConnection getConnect(String url, String sessionID, boolean isMultiData) {
		URL urlObject;
		HttpURLConnection connection = null;
		try {
			urlObject = new URL(URL + url);
			connection = (HttpURLConnection) urlObject.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setConnectTimeout(CONNECTION_TIME_OUT);
			//设置请求头字段
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//这个属性将被用于大文件传输，有效的提高效率
			if (isMultiData) {
				connection.setRequestProperty("Content-Type","multipart/form-data");
			}
			//有相同的属性则覆盖
			connection.setRequestProperty("user-agent", "Android 4.0.1");
			connection.setRequestProperty("Cookie","JSESSIONID=" + sessionID);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	}

}
