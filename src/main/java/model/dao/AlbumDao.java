package model.dao;

import model.pojo.Album;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

}
