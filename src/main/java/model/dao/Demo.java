package model.dao;

import controllers.managers.AlbumManager;
import controllers.managers.LoggingManager;
import model.pojo.Album;
import model.pojo.Post;
import model.pojo.User;

import java.sql.SQLException;

public class Demo {
    public static void main(String[] args) {
        try {
            User u = LoggingManager.getInstance().login("Phillip", "Pass112233");
            Album a =  u.getAlbumByID(3);
//            for (int i = 0; i <a.getPosts().size(); i++) {
//                System.out.println(a.getPosts().get(i));
//            }
//            System.out.println(u.getAlbums());
//            AlbumManager.getInstance().removePostFromAlbum(9,3);
            AlbumManager.deleteAlbum(99);
        } catch (LoggingManager.LoggingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
