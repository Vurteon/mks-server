package dao;
import domain.user.User;

public interface UserDao {
    boolean registerUser(User user);                  //注册用户
    boolean findUser(String email);               //查看注册用户在数据库中是否存在
    boolean checkUser(String email, String password);         //登录检查用户密码是否正确
}
