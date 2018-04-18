package model.dao;

import model.pojo.Comment;

import java.sql.SQLException;

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
}
