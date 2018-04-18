package controllers.managers;

import model.dao.AlbumDao;
import model.dao.PostDao;
import model.pojo.Album;
import model.pojo.Post;
import model.pojo.User;

import java.sql.SQLException;

public class AlbumManager {
    private final static AlbumManager instance = new AlbumManager();

    private AlbumManager() {

    }

    public static AlbumManager getInstance() {
        return instance;
    }

//    public static void main(String[] args) {
//        try {
//            User u = LoggingManager.getInstance().login("Phillip", "Pass112233");
//            createAlbum(u, "Friends");
//        } catch (LoggingManager.LoggingException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


    public void createAlbum(User u, String name) throws SQLException {
        //create album object
        Album album = new Album(u, name);
        //add in db
        AlbumDao.getInstance().addAlbumInDB(album);
        //add in user's collection
        u.addAlbum(album);
    }

    public static void addPostInAlbum(int postID, int albumID) throws SQLException {
        AlbumDao.addPostInAlbumInDB(postID, albumID);
    }
    public static void removePostFromAlbum(int postID, int albumID) throws SQLException {
        AlbumDao.removePostFromAlbumInDB(postID, albumID);
    }

    public static void deleteAlbum(int albumID) throws SQLException {
        AlbumDao.getInstance().deleteAlbum(albumID);
    }

}
