package servlet.account;

import beans.index.SignUpInfoBean;
import model.account.SignUpUserHandler;
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
import javax.servlet.http.HttpSession;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * author: 康乐
 * time: 2014/7/28
 * function: 提供手机客户端注册功能，基于http连接
 */

@WebServlet(name = "SignUpServlet",urlPatterns = "/sign_up")
public class SignUpServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String signUpInfo = RequestInfoUtils.getPostContent(request);

		if (signUpInfo == null) {
			// 对于这种蓄意请求直接404
			response.sendError(404);
			return ;
		}

		JSONObject signUpInfoJson = new JSONObject(signUpInfo);

		/**
		 * 向数据库添加注册信息
 		 */

		SignUpInfoBean signUpInfoBean = new SignUpInfoBean();

		if (signUpInfoJson.has("name") && signUpInfoJson.has("userAccount") && signUpInfoJson.has("password")) {

			// 进行格式检查
			if (FormatCheckManager.checkName() && FormatCheckManager.checkAccount() && FormatCheckManager.checkPassword()) {
				signUpInfoBean.setName(signUpInfoJson.getString("name"));
				signUpInfoBean.setUserAccount(signUpInfoJson.getString("userAccount"));
				signUpInfoBean.setPassword(signUpInfoJson.getString("password"));
			}else {
				StatusResponseHandler.sendStatus("accountResult", ErrorCode.JSONFORMATERROR,response,true);
				return ;
			}
		}else {
			StatusResponseHandler.sendStatus("accountResult", ErrorCode.JSONFORMATERROR,response,true);
			return ;
		}

		if (SignUpUserHandler.isUserExist(signUpInfoBean.getUserAccount())) {
			// 因为客户端在注册前已经检验了账号是否可用，可用才能发送信息。所以，如果有此情况，那么一定是一次非正常请求
			System.err.println("发生了一次非正常请求,ip地址为:" + request.getRemoteAddr());
			StatusResponseHandler.sendStatus("accountResult", "exist",response);
			return ;
		}

		boolean recordInfo = SignUpUserHandler.registeUser(signUpInfoBean);

		if (recordInfo) {
			StatusResponseHandler.sendStatus("accountResult", "success",response,true);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(404);
	}
}
