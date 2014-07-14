package test;

import dao.dao.impl.UserDaoimpl;
import domain.user.User;
import sever.UserSever;

/**
 * Created by dahy on 2014/7/13.
 */
public class utilTest {
    public static void main(String args[]){
        User user = new User();
        user.setNickname("jack");
        user.setEmail("123456");
        user.setPassword("never");

        System.out.print(new UserSever().register(user));
    }
}
