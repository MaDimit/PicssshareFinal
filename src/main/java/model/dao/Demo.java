package model.dao;

import controllers.managers.AlbumManager;
import controllers.managers.CommentManager;
import controllers.managers.LoggingManager;
import controllers.managers.PostManager;
import model.pojo.Album;
import model.pojo.Comment;
import model.pojo.Post;
import model.pojo.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Demo {
    public static void main(String[] args) {
//        try {
//            User u = LoggingManager.getInstance().login("Phillip", "Pass112233");
//            ArrayList<Album> albums = (ArrayList<Album>) AlbumManager.getInstance().getAllAlbumsForUser(u.getId());
//            for (int i = 0; i < albums.size(); i++) {
//                for (int j = 0; j < albums.get(i).getPosts().size(); j++) {
//                    System.out.println(albums.get(i).getPosts().get(j));
//                }
//            }
//            Album a =  AlbumDao.getInstance().
//            for (int i = 0; i <a.getPosts().size(); i++) {
//                System.out.println(a.getPosts().get(i));
//            }
           // System.out.println(u.getAlbums());
//            AlbumManager.getInstance().removePostFromAlbum(9,3);
          //  AlbumManager.deleteAlbum(99);
        try{
            User u = LoggingManager.getInstance().login("Phillip", "Pass112233");
           CommentManager.getInstance().editComment(12, "new edit");
        } catch (LoggingManager.LoggingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (PostManager.PostException e) {
            e.printStackTrace();
        }
    }
}
