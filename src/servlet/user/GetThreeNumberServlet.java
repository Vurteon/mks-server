package servlet.user;

import model.user.UserInfoManger;
import utils.EnumUtil.ErrorCode;
import utils.RequestInfoUtils;
import utils.StatusResponseHandler;
import utils.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * author：康乐
 * time：2014/11/13
 * function：返回给由客户端指定的ID的三个数量指标
 */

@WebServlet(name = "GetThreeNumberServlet",urlPatterns = "/get_three_number")
public class GetThreeNumberServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userIDInfo = RequestInfoUtils.getPostContent(request);
		JSONObject jsonObject = new JSONObject(userIDInfo);

		if (jsonObject.has("ID")) {
			int ID = jsonObject.getInt("ID");
			try {
				JSONObject jsonObject1 = UserInfoManger.getThreeNum(ID);
				StatusResponseHandler.sendStatus(jsonObject1, response, true);
			} catch (SQLException e) {
				StatusResponseHandler.sendStatus("status", ErrorCode.SQLERROR,response);
				e.printStackTrace();
			}
		}else {
			StatusResponseHandler.sendStatus("status", ErrorCode.JSONFORMATERROR,response);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("get_three_number!");
		response.getWriter().close();
	}
}
