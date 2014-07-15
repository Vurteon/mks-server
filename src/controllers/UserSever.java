package controllers;

import static dao.UserDao.*;
import entity.User;


import static controllers.Constants.*;
/**
 * 用户注册与登录控制类
 */
public class UserSever {

    public UserSever(){
        addDriverManager();
    }

    public String register(User user){                    //用户注册接口
        if(findUser(user.getEmail())){                    //如果Email存在则返回存在消息
            return USER_EXIST;
        }

        if(registerUser(user)){                           //不存在则注册
            return REGISTER_SUCCESS;
        }else{
           return REGISTER_FAIL;
        }

    }

    public String login(User user){                   //用户登录接口
        if(userLoginCheck(user)){                     //检测登录信息是否正确
            return LOGIN_SUCCESS;
        }else{
            return LOGIN_FAIL;
        }
    }
}
