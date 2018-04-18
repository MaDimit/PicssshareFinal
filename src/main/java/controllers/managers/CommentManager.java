package controllers.managers;

import model.dao.CommentDao;
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
}
