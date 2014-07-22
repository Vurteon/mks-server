package dao;

/**
 * 用户信息的增删查
 */


import entity.User;
import static dao.UserSql.*;

public class UserDao{

    private UserSql userSql = new UserSql();                          //jdbc接口，可用于多种数据库类型的链接

    public  boolean registerUser(User user){             //用户注册
        String sql = "insert into usermessage(nickname,email,password) values (?,?,?)";
        boolean registerSuccess =  userSql.register(sql, user);
        return registerSuccess;
    }
    public boolean findUser(String email){               //查看用户Email是否存在
        String sql = "select * from usermessage where email=?";
        boolean findUser = userSql.checkEmail(sql, email);
        return findUser;
    }
    public  boolean userLoginCheck(User user){            //用户登录信息检验
        String sql = "select * from usermessage where email=? and password=?";
        boolean LoginSuccess =  userSql.LoginCheck(sql, user);
        return LoginSuccess;
    }
}