package dao;

/**
 * 用户信息的增删查
 */


import entity.User;
import static dao.UserSql.*;

public class UserDao{

    public static boolean addDriverManager(){                 //加载数据库驱动
        addSqlDriver();
        return true;
    }

    public static boolean registerUser(User user){             //用户注册
        boolean registerSuccess = false;
        registerSuccess = register(user);
        return registerSuccess;
    }
    public static boolean findUser(String email){               //查看用户Email是否存在
        boolean findUser = false;
        findUser =  checkEmail(email);
        return findUser;
    }
    public static boolean userLoginCheck(User user){            //用户登录信息检验
        boolean LoginSuccess = false;
        LoginSuccess = LoginCheck(user);
        return LoginSuccess;
    }
}