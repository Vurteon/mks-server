package servlet.account;

import dao.account.SignUpDao;
import utils.EnumUtil.ErrorCode;
import utils.FormatCheckManager;
import utils.RequestInfoUtils;
import utils.StatusResponseHandler;
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

@WebServlet(name = "CheckEmailServlet",urlPatterns = "/checkAccount")
public class CheckAccountServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String emailContent = RequestInfoUtils.getPostContent(request);

		if (emailContent == null) {
			// 对于这种蓄意请求直接404
			response.sendError(404);
			return ;
		}

		JSONObject jsonObject = new JSONObject(emailContent);

		if (jsonObject.has("account")) {

			String email = jsonObject.getString("account");

			if (FormatCheckManager.checkAccount()) {
				//直接使用数据库查询服务
				String dbEmail = SignUpDao.getUserEmail(email);
				if (dbEmail == null) {
					StatusResponseHandler.sendStatus("accountResult","not_exist",response);
				}else {
					StatusResponseHandler.sendStatus("accountResult","exist",response);
				}
			}else {
				// 格式错误
				StatusResponseHandler.sendStatus("accountResult", ErrorCode.JSONFORMATERROR, response);
			}
		}else {
			StatusResponseHandler.sendStatus("accountResult", ErrorCode.JSONFORMATERROR, response);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("hello_world!");
		response.getWriter().close();
	}
}
