package dao.dao.impl;

import utils.UserSql;
import dao.UserDao;
import domain.user.User;


public class UserDaoimpl implements UserDao {
    public boolean registerUser(User user){
        boolean registerSuccess = false;


        if(!findUser(user.getEmail())) {
            String sql = "insert into usermessage(nickname,email,password)values(?,?,?)";
            registerSuccess = new UserSql().registerUser(sql, user);

        }
        return registerSuccess;
    }
    public boolean findUser(String email){
        boolean findUser = false;
        String sql = "select * from usermessage where email=?";
        findUser = new UserSql().findUser(sql,email);
        return findUser;
    }
    public boolean checkUser(String email, String password){
        boolean checkedUser = false;
        String sql = "select * from usermessage where email=? and password=?";
        checkedUser = new UserSql().checkUser(sql, email, password);
        return checkedUser;
    }
}