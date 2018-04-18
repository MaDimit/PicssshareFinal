package controllers.managers;

import model.dao.PostDao;
import model.pojo.Album;
import model.pojo.Post;
import model.pojo.User;

import java.sql.SQLException;

public class AlbumManager {

    public static void main(String[] args) {
        try {
            User u = LoggingManager.getInstance().login("Phillip", "Pass112233");
            createAlbum(u, "Friends");
        } catch (LoggingManager.LoggingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void createAlbum(User u, String name) throws SQLException {
        //create album object
        Album album = new Album(u, name);
        //add in db
        PostDao.getInstance().addAlbumInDB(album);
        //add in user's collection
        u.addAlbum(album);
    }

    public void addPostInAlbum(Post post, Album album){

    }
}
