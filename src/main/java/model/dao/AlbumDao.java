package model.dao;

import model.pojo.Album;
import model.pojo.Post;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AlbumDao extends Dao {

    private static AlbumDao instance = new AlbumDao();

    public static AlbumDao getInstance() {
        return instance;
    }

    private AlbumDao() {
        super();
    }


    public static void addPostInAlbumInDB(int postID, int albumID) throws SQLException {
        String sql = "INSERT INTO albums_has_posts (album_id, post_id) VALUES (?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1,albumID);
        stmt.setInt(2,postID);
        stmt.executeUpdate();
        stmt.close();
    }

    public static void removePostFromAlbumInDB(int postID, int albumID) throws SQLException {
        String sql = "DELETE FROM albums_has_posts WHERE album_id = ? AND post_id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1,albumID);
        stmt.setInt(2,postID);
        stmt.executeUpdate();
        stmt.close();
    }


    public void addAlbumInDB(Album album) throws SQLException {
        String sql = "INSERT INTO albums (name, belonger_id) VALUES (?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1,album.getName());
        stmt.setInt(2,album.getUserID());
        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        int id;
        if (generatedKeys.next()) {
            id = generatedKeys.getInt(1);
            album.setId(id);
        } else {
            throw new SQLException("Creating post failed, no ID obtained.");
        }
        stmt.close();
    }

    public static void deleteAlbum(int albumID) throws SQLException {
        String sql = "DELETE FROM albums WHERE id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1,albumID);
        stmt.executeUpdate();
        stmt.close();
    }

    public List<Album> getAllAlbumsForUser(int userID) throws SQLException {
        List<Album> albums = new ArrayList<>();
        String sql = "SELECT id, name, belonger_id FROM albums WHERE belonger_id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1,userID);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            int albumID=rs.getInt("id");
            String albumName = rs.getString("name");
            int belongerID = rs.getInt("belonger_id");
            //create album
            Album a = new Album(UserDao.getInstance().getUserByID(belongerID), albumName, albumID);
            //fill posts for this album
            ArrayList<Integer> posts = getAllPostsForAlbum(albumID);
            for (int i = 0; i < posts.size(); i++) {
                Post p = PostDao.getInstance().getPost(posts.get(i));
                a.addPost(p);
            }
            //add the ready album to the collections
            albums.add(a);
        }
        return albums;
    }

    private ArrayList<Integer> getAllPostsForAlbum(int albumID) throws SQLException {
        ArrayList<Integer> postsID = new ArrayList<>();
        String query = "SELECT post_id FROM albums_has_posts WHERE album_id=?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setInt(1,albumID);
        ResultSet results = statement.executeQuery();
        while(results.next()){
            int postID = results.getInt(1);
            postsID.add(postID);
        }
        return postsID;
    }
}
