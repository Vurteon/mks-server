package web.servlet;


import controllers.UserSever;
import entity.User;

import static controllers.Constants.*;
import static utils.WebUtils.*;
import static web.message.UserMessage.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户登录servlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");


        User user = getUserLoginForm(request);                               //获取登录信息

        String LoginMessage = new UserSever().login(user);                  //获取登录结果消息

        if(LoginMessage.equals(LOGIN_SUCCESS)){
            request.getSession().setAttribute("nickname", user.getNickname());  //登录成功则将当前用户的昵称存入session域
            loginSuccessMessage(request, response);
            return;
        }
        if(LoginMessage.equals(LOGIN_FAIL)){
            loginFailMessage(request, response);
            return;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
