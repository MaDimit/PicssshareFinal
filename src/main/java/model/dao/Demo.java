package model.dao;

import controllers.managers.LoggingManager;
import model.pojo.Post;
import model.pojo.User;

import java.sql.SQLException;

public class Demo {
    public static void main(String[] args) {
        try {
            User u = LoggingManager.getInstance().login("Maxim", "Qwerty12345");
            Post p = PostDao.getInstance().getPost(5);
            System.out.println(u.getAlbums());
        } catch (LoggingManager.LoggingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
