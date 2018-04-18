package controllers.managers;

import model.dao.CommentDao;
import model.dao.UserDao;
import model.pojo.Comment;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class CommentManager {
    private final static CommentManager instance = new CommentManager();

    private CommentManager() {

    }

    public static CommentManager getInstance() {
        return instance;
    }

    public void editComment(int oldCommentID, String editContent) throws PostManager.PostException, SQLException {
        Comment comment = CommentDao.getInstance().getCommentByID(oldCommentID);
        comment.setContent(editContent);
        comment.setDate(LocalDateTime.now());
        CommentDao.getInstance().editComment(comment);
    }

    public void likeComment(int commentID, int likerID) throws Exception {
        if(commentID>-1 && likerID>-1) {
            CommentDao.getInstance().addLike(CommentDao.getInstance().getCommentByID(commentID), UserDao.getInstance().getUserByID(likerID));
        }
        else {
            throw new Exception("Error during user liking a comment.");
        }
    }

    public void addComment(Comment c) throws SQLException {
        if(c!=null) {
            CommentDao.getInstance().addComment(c);
        }
    }

    public void deleteComment(int commentID) throws SQLException {
        if(commentID>-1) {
            CommentDao.getInstance().deleteComment(commentID);
        }
    }


}
