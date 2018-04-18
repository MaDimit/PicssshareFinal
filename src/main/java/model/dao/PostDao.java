package model.dao;

import model.pojo.Album;
import model.pojo.Comment;
import model.pojo.Post;
import model.pojo.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class PostDao extends Dao {

    private static final int TRENDING_DAYS = 1;

//    private HashMap<Integer, Post> posts;
    private static PostDao instance = new PostDao();

    // singleton instance used in commentmanager
    public static PostDao getInstance() {
        return instance;
    }

    private PostDao() {
        super();
    }



    //================== Posts Interface ==================//

    //------------------ post manipulations ------------------//

    public void addPost(Post post) throws SQLException {
        String sql = "INSERT INTO posts (date, poster_id, url) VALUES (?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setObject(1, Timestamp.valueOf(post.getDate()));
        stmt.setInt(2, post.getPoster().getId());
        stmt.setString(3, post.getUrl());
        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        int id;
        if (generatedKeys.next()) {
            id = generatedKeys.getInt(1);
            post.setId(id);
        } else {
            throw new SQLException("Creating post failed, no ID obtained.");
        }
        stmt.close();
    }

    public Post getPost(int id) throws SQLException {
        String sql = "SELECT id, date, poster_id, url FROM posts WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        Post post = rs.next() ? createPost(rs) : null;
        stmt.close();
        return post;
    }

    public void deletePost(int id) throws SQLException {
        //delete from db
        String sql = "DELETE FROM posts WHERE id=(?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
    }

    //------------------ liking/disliking ------------------//

    public void addLike(Post post, User user) throws SQLException {
        synchronized (post) {
            int affectedRows = addLikeDislike(post, user, 1);
            if (affectedRows == 1) {
                post.addLiker(user);
            } else if (affectedRows == 2) {
                post.addLiker(user);
                post.removeDisliker(user);
            }
        }
    }

    public void addDislike(Post post, User user) throws SQLException {
        synchronized (post) {
            int affectedRows = addLikeDislike(post, user, -1);
            if (affectedRows == 1) {
                post.addDisliker(user);
            } else if (affectedRows == 2) {
                post.addDisliker(user);
                post.removeLiker(user);
            }
        }
    }

    public HashSet<String> getAllLikers() throws SQLException{
        return getAllLikesDislikes(1);
    }

    public HashSet<String> getAllDislikers() throws SQLException{
        return getAllLikesDislikes(-1);
    }

    private HashSet<String> getAllLikesDislikes(int status) throws SQLException{
        HashSet<String> users = new HashSet<>();
        String sql = "SELECT liker_id, likedpost_id FROM liker_post WHERE status = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, status);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()){
            users.add(rs.getInt("liker_id") + "" + rs.getInt("likedpost_id"));
        }
        return users;
    }

    //------------------ feed creation ------------------//

    public List<Post> getUserFeed(User user) throws SQLException{
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT id, date, poster_id, url FROM posts WHERE poster_id = ? ORDER BY date";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1,user.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()){
            posts.add(createPost(rs));
        }
        stmt.close();
        return posts;
    }

    public List<Post> getFriendsFeed(User user) throws SQLException{
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT id, date, poster_id, url FROM posts p\n" +
                "JOIN subscriber_subscribed s ON  s.subscriber_id = ?\n" +
                "WHERE poster_id = subscribedto_id ORDER BY date";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1,user.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()){
            posts.add(createPost(rs));
        }
        stmt.close();
        return posts;
    }

    public List<Post> getTrendingFeed()throws SQLException{
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT id, date, poster_id, url FROM posts WHERE date >= NOW() - INTERVAL ? DAY;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, TRENDING_DAYS);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            posts.add(createPost(rs));
        }
        stmt.close();
        return posts;
    }

    //================== Post likes/dislikes ==================//

    private int addLikeDislike(Post post, User user, int status) throws SQLException {
        //Inserting or updating liker_post table
        String sql = "INSERT INTO liker_post (liker_id, likedpost_id, status) VALUES (?, ?, ?)\n" +
                "  ON DUPLICATE KEY UPDATE status = ?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, user.getId());
        stmt.setInt(2, post.getId());
        stmt.setInt(3, status);
        stmt.setInt(4, status);
        //Update, row already existed with a status - 1 row affected
        //Insert, newly created row - 1 row affected
        //Update, status changed to opposite - 2 rows affected
        int affectedRows = stmt.executeUpdate();
        stmt.close();
        return affectedRows;

    }

    //================== Post creation ==================//

    // Used in post creation for getPost, and feed. Should be added to while loop or used once with rs.next;
    Post createPost(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        User user = UserDao.getInstance().getUserByID(rs.getInt("poster_id"));
        String url = rs.getString("url");
        LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();

        //add all the info for the post
        Post post = new Post(id, user, url, date);
        post.setTags(getAllTagsForPost(post));
        post.setComments(CommentDao.getInstance().getAllComments(post));
        post.setLikers(getPostLikers(post));
        post.setDislikers(getPostDislikers(post));
        return post;
    }

    //get all the tags related to this post
    private List<String> getAllTagsForPost(Post post) throws SQLException {
        List<String> tags = new ArrayList<>();

        //Fetching tags from DB
        String sql = "SELECT tags.tag_name FROM tags \n" +
                "                JOIN post_tag ON post_tag.tag_id=tags.id\n" +
                "                \n" +
                "                JOIN posts ON post_tag.post_id=posts.id\n" +
                "                WHERE posts.id = ? \n" +
                "                ORDER BY tags.tag_name;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, post.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String tag = rs.getString("tag_name");
            tags.add(tag);
        }
        stmt.close();
        return tags;
    }

    private ArrayList<User> getPostLikers(Post post) throws SQLException {
        return getLikersDislikers(post.getId(), 1);
    }

    private ArrayList<User> getPostDislikers(Post post) throws SQLException {
        return getLikersDislikers(post.getId(), -1);
    }

    private ArrayList<User> getLikersDislikers(int postID, int status) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        //Fetching likers from DB
        String sql = "SELECT users.id FROM users\n" +
                "JOIN liker_post ON liker_post.liker_id=users.id\n" +
                "JOIN posts ON liker_post.likedpost_id=posts.id\n" +
                "WHERE posts.id= ? AND status = ?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, postID);
        stmt.setInt(2, status);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int likerId = rs.getInt(1);
            users.add(UserDao.getInstance().getUserByID(likerId));
        }
        stmt.close();
        return users;
    }

}
