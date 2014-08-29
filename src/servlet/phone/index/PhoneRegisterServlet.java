package servlet.phone.index;

import beans.index.RegisterInfoBean;
import model.index.RegisteUser;
import utils.RequestInfoUtils;
import utils.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * author: 康乐
 * time: 2014/7/28
 * function: 提供手机客户端注册功能，基于http连接
 */

@WebServlet(name = "PhoneRegisterServlet",urlPatterns = "/phone_register")
public class PhoneRegisterServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String registerInfo = RequestInfoUtils.getPostContent(request);

		if (registerInfo == null) {
			// 对于这种蓄意请求直接404
			response.sendError(404);
			return ;
		}

		JSONObject registerInfoJson = new JSONObject(registerInfo);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),"utf-8"));

		response.setHeader("Content-Type","application/json");


		// 向数据库添加注册信息
		RegisterInfoBean registerInfoBean = new RegisterInfoBean();


		if (registerInfoJson.has("name") && registerInfoJson.has("email") && registerInfoJson.has("password")) {
			registerInfoBean.setName(registerInfoJson.getString("name"));
			registerInfoBean.setEmail(registerInfoJson.getString("email"));
			registerInfoBean.setPassword(registerInfoJson.getString("password"));
		}else {
			response.sendError(404);
			return ;
		}

		JSONObject jsonObject = RegisteUser.registeUser(registerInfoBean);

		// 由于在注册前就已经完全检查了数据的正确性，所以这里返回的一定是record的信息
		// 如果是返回了email已经存在的信息，必然是有人构建了一次非正常请求，

		// 如果返回值是{"isExist":"yes"}表示这是一个非正常请求，需要记录
		if (jsonObject.has("isExist")) {
			System.err.println("发生了一次非正常请求,ip地址为:" + request.getRemoteAddr());
			response.sendError(404);
			return ;
		}


		HttpSession httpSession = request.getSession(true);

		// 获取session发送到Android客户端
		String sessionID = httpSession.getId();

		// 这里构建json的字符串比较短，就直接+了
		if (jsonObject.has("isRecorded") && jsonObject.getString("isRecorded").equals("yes")) {
			JSONObject recordInfo = new JSONObject("{'isRecorded':'yes','JSESSIONID':'" + sessionID + "'}");
			System.out.println(recordInfo);
			bw.write(recordInfo.toString());
			bw.close();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(404);
	}
}
