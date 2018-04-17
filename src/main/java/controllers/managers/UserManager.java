package controllers.managers;

import model.dao.UserDao;
import model.pojo.Notification;
import model.pojo.Post;
import model.pojo.User;

import java.sql.SQLException;
import java.util.List;

public class UserManager {

    public static class UserManagerException extends Exception{
        private UserManagerException(String msg) {
            super(msg);
        }
    }


    private static final UserManager instance = new UserManager();
    private UserDao userDao;

    private UserManager() {
        this.userDao = UserDao.getInstance();
    }

    public static synchronized UserManager getInstance() {
        return instance;
    }

    public void subscribe(User subscriber, User subscribedTo) throws SQLException, UserManagerException{
        if(subscribedTo == null || subscriber == null){
            throw new UserManagerException("User you trying to subscribe to does not exist");
        }
        userDao.addSubscription(subscriber,subscribedTo);
    }

    public void updateProfileInfo(User user, String password, String first_name, String last_name, String email, String profilePicURL) throws SQLException, LoggingManager.RegistrationException {
        //TODO add not null validation here instead of UserDao
       userDao.executeProfileUpdate(user,password,first_name,last_name,email, profilePicURL);
    }

    public void deleteUser(User user) throws SQLException{
        userDao.deleteUser(user);
    }


}