package utils;

import domain.user.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by dahy on 2014/7/13.
 */
public class WebUtils {
    public WebUtils(){

    }
    public static User getUserRegisterForm(HttpServletRequest request){   //获得用户注册表单
        String nickname = (String) request.getParameter("nickname");
        String email = (String) request.getParameter("email");
        String password = (String) request.getParameter("password");
        User user = new User(nickname, email, password);
        return user;
    }
    public static User getUserLoginForm(HttpServletRequest request){       //获得用户登录表单
        String email = (String) request.getParameter("email");
        String password = (String) request.getParameter("password");
        User user = new User(email, password);
        return user;
    }
}
