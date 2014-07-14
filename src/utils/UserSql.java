package utils;

import domain.user.User;

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

    public boolean registerUser(String sql, User user){
        boolean registerSuccess = false;
        try{
            ps = con.prepareStatement(sql);
            ps.setString(1, user.getNickname());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.executeUpdate();

            registerSuccess = true;

        }catch(Exception e){
            e.printStackTrace();
        }
        return registerSuccess;
    }

    public boolean findUser(String sql, String email){
        boolean findSuccess = false;
        try{
            ps = con.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();
            while(rs.next()){
                findSuccess = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return findSuccess;
    }

    public boolean checkUser(String sql, String email, String password){
        boolean checkedUser = false;
        try{
            ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            rs = ps.executeQuery();
            while(rs.next()){
                checkedUser = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return checkedUser;
    }
}
