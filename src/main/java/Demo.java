import controllers.managers.AlbumManager;
import controllers.managers.LoggingManager;
import controllers.managers.PostManager;
import controllers.managers.UserManager;
import model.dao.CommentDao;
import model.dao.PostDao;
import model.dao.UserDao;
import model.pojo.Comment;
import model.pojo.Post;
import model.pojo.User;

import java.sql.SQLException;


public class Demo {

    public static void main(String[] args) throws SQLException, LoggingManager.LoggingException, LoggingManager.RegistrationException {



        LoggingManager lm = LoggingManager.getInstance();
        PostDao pd = PostDao.getInstance();
        User user = lm.login("Maxim","Qwerty12345");
        User user1 = lm.login("Philip", "Qwerty123456");
        CommentDao cd = CommentDao.getInstance();
        AlbumManager am = AlbumManager.getInstance();
        UserManager um = UserManager.getInstance();
        PostManager pm = PostManager.getInstance();
        long time = System.currentTimeMillis();
        try {
            System.out.println(pm.getTrendingFeed());
        } catch (PostManager.PostException e) {
            e.printStackTrace();
        }


        System.out.println(System.currentTimeMillis() - time);
    }
}
