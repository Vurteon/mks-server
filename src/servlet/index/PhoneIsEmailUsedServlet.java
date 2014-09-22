package servlet.index;

import dao.index.RegisteDao;
import utils.RequestInfoUtils;
import utils.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * author: 康乐
 * time: 2014/7/28
 * function: 为手机客户端提供检查油箱是否已经被注册接口，基于http连接
 *
 */

@WebServlet(name = "PhoneIsEmailUsedFilter",urlPatterns = "/phone_isEmailUsed")
public class PhoneIsEmailUsedServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String emailContent = RequestInfoUtils.getPostContent(request);

		if (emailContent == null) {
			// 对于这种蓄意请求直接404
			response.sendError(404);
			return ;
		}

		JSONObject jsonObject = new JSONObject(emailContent);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),"utf-8"));

		response.setHeader("Content-Type","application/json");

		if (jsonObject.has("email")) {

			String email = jsonObject.getString("email");

			//直接使用数据库查询服务
			String dbEmail = RegisteDao.getUserEmail(email);

			if (dbEmail == null) {
				// 不存在当前邮箱
				bw.write(new JSONObject("{'isUsed':'no'}").toString());
			}else {
				bw.write(new JSONObject("{'isUsed':'yes'}").toString());
			}
		}
		bw.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(404);
	}
}
