package controllers.managers;

import model.dao.AlbumDao;
import model.dao.PostDao;
import model.dao.UserDao;
import model.pojo.Album;
import model.pojo.Post;
import model.pojo.User;

import java.sql.SQLException;
import java.util.List;

public class AlbumManager {
    private final static AlbumManager instance = new AlbumManager();

    private AlbumManager() {

    }

    public static AlbumManager getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        try {
            User u = LoggingManager.getInstance().login("Phillip", "Pass112233");
//            AlbumDao.getInstance().createAlbum(u, "Friends");
        } catch (LoggingManager.LoggingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Album> getAllAlbumsForUser(int userID) throws SQLException {
        return AlbumDao.getInstance().getAllAlbumsForUser(userID);
    }

    public void createAlbum(User u, String name) throws SQLException {
        //create album object
        Album album = new Album(u, name);
        //add in db
        AlbumDao.getInstance().addAlbumInDB(album);
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
