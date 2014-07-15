package dao;



import entity.User;

import java.sql.*;

/**
 * Created by dahy on 2014/7/13.
 */
public class UserSql {
    static private Connection con;
    static private PreparedStatement ps;
    static private ResultSet rs;

    static String url = "jdbc:mysql://127.0.0.1:3306/momentdata?useUnicode=true&characterEncoding=utf-8";
    static String user = "root";
    static String password = "moment";

    public UserSql(){

    }
    public static void addSqlDriver(){                    //加载数据库驱动
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
    public static boolean register(User user){        //用户注册
        boolean registerSuccess = false;                              //注册成功或失败判断
        String sql = "insert into usermessage(nickname,email,password)values(?,?,?)";
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

    public static boolean checkEmail(String email){        //检测数据库中是否存在该用户
        boolean findSuccess = false;                                   //检测结果标记
        String sql = "select * from usermessage where email=?";
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

    public static boolean LoginCheck(User user){     //用户登录检测
        boolean loginSuccess = false;                                //登录成功与否标志
        String sql = "select * from usermessage where email=? and password=?";
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
