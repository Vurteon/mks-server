package sever;

import dao.UserDao;
import dao.dao.impl.UserDaoimpl;
import domain.user.User;

import static domain.user.Constants.*;
/**
 * Created by dahy on 2014/7/13.
 */
public class UserSever {
    private UserDao dao = new UserDaoimpl();

    public String register(User user){
        if(dao.findUser(user.getEmail())){
            return USER_EXIST;
        }

        if(dao.registerUser(user)){
            return REGISTER_SUCCESS;
        }else{
           return REGISTER_FAIL;
        }

    }

    public String login(User user){
        if(dao.checkUser(user)){
            return LOGIN_SUCCESS;
        }else{
            return LOGIN_FAIL;
        }
    }
}
