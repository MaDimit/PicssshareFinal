package model.dao;

import model.pojo.Comment;
import model.pojo.Post;
import model.pojo.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDao extends Dao {

    private static CommentDao instance = new CommentDao();

    public static CommentDao getInstance() {
        return instance;
    }

    private CommentDao() {
        super();
    }

    //================== Comments Interface ==================//

    public void addComment(Comment comment) throws SQLException {
        String sql ="INSERT INTO comments (poster_id, date, content, post_id) VALUES (?,?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1,comment.getUser().getId());
        stmt.setObject(2, Timestamp.valueOf(comment.getDate()));
        stmt.setString(3,comment.getContent());
        stmt.setInt(4, comment.getPost().getId());
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            comment.setId(generatedKeys.getInt(1));
        } else {
            //throw new SQLException("Creating comment failed, no ID obtained.");
        }
        stmt.close();
    }

    public void deleteComment(Comment comment) throws SQLException{

    }

    public void addLike(Comment comment) throws SQLException{
        //TODO comment liking
    }

    public void removeLike(Comment comment) throws SQLException{
        //TODO comment disliking
    }

    public List<Comment> getAllComments(Post post) throws SQLException {
        ArrayList<Comment> comments = new ArrayList<>();
        //Fetching users from DB
        String sql = "SELECT id, poster_id, date, content, post_id FROM comments WHERE post_id=? ORDER BY date DESC";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, post.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            comments.add(createComment(rs,post));
        }
        stmt.close();
        return comments;
    }

    Comment createComment(ResultSet rs, Post post) throws SQLException{
        int commentId = rs.getInt("id");
        int posterId = rs.getInt("poster_id");
        LocalDateTime postDate = rs.getTimestamp("date").toLocalDateTime();
        String content = rs.getString("content");
        Comment c = new Comment(commentId, post, UserDao.getInstance().getUserByID(posterId), postDate, content);
        c.setLikers(this.getCommentLikers(c));
        return c;
    }

    private List<User> getCommentLikers(Comment comment)throws SQLException{
        List<User> likers = new ArrayList<>();
        String sql = "SELECT id, username, password, first_name, last_name,email, profile_picture_url FROM users\n" +
                "JOIN liker_comment ON id = comment_liker_id\n" +
                "WHERE liked_comment_id = ?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, comment.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()){
            likers.add(UserDao.getInstance().createUser(rs));
        }
        return likers;
    }
}
