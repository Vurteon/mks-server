package dao;



import entity.User;

import java.sql.*;

/**
 * Created by dahy on 2014/7/13.
 */
public class UserSql {
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;

    String url = "jdbc:mysql://127.0.0.1:3306/momentdata?useUnicode=true&characterEncoding=utf-8";
    String user = "root";
    String password = "moment";

    public UserSql(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("驱动加载错误");
        }
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库连接错误");
        }
    }

    public  boolean register(String sql, User user){        //用户注册
        boolean registerSuccess = false;                              //注册成功或失败判断

        try{
            ps = con.prepareStatement(sql);
            ps.setString(1, user.getNickname());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.executeUpdate();

            registerSuccess = true;                                  //注册成功

        }catch(Exception e){
            e.printStackTrace();
        }
        return registerSuccess;                                     //返回注册消息
    }

    public  boolean checkEmail(String sql, String email){        //检测数据库中是否存在该用户
        boolean findSuccess = false;                                   //检测结果标记

        try{
            ps = con.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();
            while(rs.next()){
                findSuccess = true;                                  //数据库中含有该用户
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return findSuccess;
    }

    public  boolean LoginCheck(String sql, User user){     //用户登录检测
        boolean loginSuccess = false;                                //登录成功与否标志

        try{
            ps = con.prepareStatement(sql);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            rs = ps.executeQuery();
            while(rs.next()){
                loginSuccess = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return loginSuccess;
    }
}
