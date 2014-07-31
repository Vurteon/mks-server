package servlet.phone.index;

import dao.index.RegisteDao;
import utils.GetPostContent;
import utils.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by leon on 2014/7/30.
 */
public class PhoneIsEmailUsedServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String emailContent = GetPostContent.getPostContent(request);

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
			String dbEmail = RegisteDao.getUser(email);

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
