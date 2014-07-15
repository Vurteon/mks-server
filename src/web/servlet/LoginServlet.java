package web.servlet;

import domain.user.User;
import sever.UserSever;

import static domain.user.Constants.*;
import static utils.WebUtils.*;
import static web.messages.UserMessage.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dahy on 2014/7/14.
 */
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        User user = getUserLoginForm(request);
        String LoginMessage = new UserSever().login(user);

        if(LoginMessage.equals(LOGIN_SUCCESS)){
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
