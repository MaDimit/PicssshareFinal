package model.dao;

import model.pojo.Album;
import model.pojo.Comment;
import model.pojo.Post;
import model.pojo.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostDao extends Dao {
    //test
//    public static void main(String[] args) {
//        try {
//            PostDao.getInstance().loadAllPosts();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        for(Post p : posts.values()){
//            System.out.println(p);
//        }
//    }

    private HashMap<Integer, Post> posts;
    private static PostDao instance = new PostDao();

    // singleton instance used in commentmanager
    public static PostDao getInstance() {
        return instance;
    }

    private PostDao() {
        super();
        this.posts = new HashMap<>();
    }

    public HashMap<Integer, Post> getPosts() {
        return posts;
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

    //method for fill the collection with posts and all the information about them
    public void loadAllPosts() throws SQLException {
        String sql = "SELECT id,date,poster_id,url FROM posts";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Post p = createPost(rs);
            this.posts.put(p.getId(), p);
        }
        stmt.close();

    }


    public void setCommentLikesCount(int postID) throws SQLException {
        //Fetching users from DB
        String sql = "SELECT liked_comment_id, COUNT(comment_liker_id) FROM picssshare.liker_comment \n" +
                "JOIN comments ON comments.id = liked_comment_id\n" +
                "JOIN posts on posts.id=comments.post_id\n" +
                "WHERE posts.id = ?\n" +
                "GROUP BY liked_comment_id;\n";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, postID);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            while (rs.next()) {
                int likedCommentId = rs.getInt("liked_comment_id");
                int commentsLikesCount = rs.getInt("liked_comment_id");

                posts.get(postID).getComments().get(likedCommentId).setLikes(commentsLikesCount);
            }
        }
        stmt.close();
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

    //------------------ feed creation ------------------//

    public List<Post> getUserFeed(User user) throws SQLException{
        //TODO generating user posts feed
        return null;
    }

    public List<Post> getFriendsFeed(User user) throws SQLException{
        //TODO generating friends feed
        return null;
    }

    public List<Post> getTrendingFeed()throws SQLException{
        //TODO generating trending feed
        return null;
    }

    //------------------ comment manipulations ------------------//

    public void addComment(Comment comment) throws SQLException{
        //TODO comment adding
    }

    public void deleteComment(Comment comment) throws SQLException{
        //TODO comment deleting
    }

    public void likeComment(Comment comment) throws SQLException{
        //TODO comment liking
    }

    public void dislikeComment(Comment comment) throws SQLException{
        //TODO comment disliking
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
        System.out.println(affectedRows);
        stmt.close();
        return affectedRows;

    }

    //================== Post creation ==================//

    // Used in post creation for getPost, and feed. Should be added to while loop or used once with rs.next;
    private Post createPost(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        User user = UserDao.getInstance().getUserByID(rs.getInt("poster_id"));
        String url = rs.getString("url");
        LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
        List<String> tags = getAllTagsForPost(id);
        List<Comment> comments = getAllComments(id);
        List<User> likers = getPostLikers(id);
        List<User> dislikers = getPostDislikers(id);

        //add all the info for the post
        Post post = new Post(id, user, url, date, tags, comments, likers, dislikers);
        return post;
    }

    //get all the tags related to this post
    private List<String> getAllTagsForPost(int postID) throws SQLException {
        List<String> tags = new ArrayList<>();

        //Fetching tags from DB
        String sql = "SELECT tags.tag_name FROM tags \n" +
                "                JOIN post_tag ON post_tag.tag_id=tags.id\n" +
                "                \n" +
                "                JOIN posts ON post_tag.post_id=posts.id\n" +
                "                WHERE posts.id = ? \n" +
                "                ORDER BY tags.tag_name;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, postID);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String tag = rs.getString("tag_name");
            tags.add(tag);
        }
        stmt.close();
        return tags;
    }

    private ArrayList<User> getPostLikers(int postID) throws SQLException {
        return getLikersDislikers(postID, 1);
    }

    private ArrayList<User> getPostDislikers(int postID) throws SQLException {
        return getLikersDislikers(postID, -1);
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

    private List<Comment> getAllComments(int postId) throws SQLException {
        ArrayList<Comment> comments = new ArrayList<>();
        //Fetching users from DB
        String sql = "SELECT id, poster_id, date, content, post_id FROM comments WHERE post_id=? ORDER BY date DESC";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, postId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int commentId = rs.getInt("id");
            int posterId = rs.getInt("poster_id");
            LocalDateTime postDate = rs.getTimestamp("date").toLocalDateTime();
            String content = rs.getString("content");
            Comment c = new Comment(commentId, PostDao.getInstance().posts.get(posterId), UserDao.getInstance().getUserByID(posterId), postDate, content);
            comments.add(c);
        }
        stmt.close();
        return comments;
    }

}
