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
 * Created by dahy on 2014/7/13.
 */
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        User user = getUserRegisterForm(request);
        String registerMessage = new UserSever().register(user);
        if(registerMessage.equals(USER_EXIST)){
            userExistMessage(request, response);
            return;
        }
        if(registerMessage.equals(REGISTER_SUCCESS)){
            registerSuccessMessage(request, response);
            return;
        }
        if(registerMessage.equals(REGISTER_FAIL)){
            registerFailMessage(request, response);
            return;
        }
        errorMessage(response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            doPost(request, response);
    }
}
