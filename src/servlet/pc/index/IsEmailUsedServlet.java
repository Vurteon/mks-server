package servlet.pc.index;

import dao.index.RegisteDao;
import utils.GetPostContent;
import utils.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * author: 康乐
 * time: 2014/7/15
 * change-time：2014/7/28
 * change-time: 2014/7/30
 * function: 检测用户名(邮箱)是否存在，并返回json数据
 */
@WebServlet(name = "IsEmailUsedServlet",urlPatterns = "/isEmailUsed")
public class IsEmailUsedServlet extends HttpServlet {
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

		// 如果是get请求，直接404
		response.sendError(404);
	}
}
