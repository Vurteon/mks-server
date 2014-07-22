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
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 用户登录servlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        UserSever userSever = new UserSever();

        User user = getUserLoginForm(request);                               //获取登录信息

        String LoginMessage = userSever.login(user);                  //获取登录结果消息

        if(LoginMessage.equals(LOGIN_SUCCESS)){
            String nickname = userSever.findUsernickname(user.getEmail()); //将昵称存入当前用户对象
            user.setNickname(nickname);
            System.out.print(user.getNickname());
            HttpSession session = request.getSession();  //登录成功则将当前用户的昵称存入session域
            session.setAttribute("nickname", user.getNickname());
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
