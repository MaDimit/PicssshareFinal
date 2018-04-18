package controllers.managers;

import model.dao.UserDao;
import model.pojo.Notification;
import model.pojo.Post;
import model.pojo.User;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

public class UserManager {

    public static class UserManagerException extends Exception{
        private UserManagerException(String msg) {
            super(msg);
        }
    }

    private HashSet<String> cashedSubscriptions;


    private static final UserManager instance = new UserManager();
    private UserDao userDao;

    private UserManager() {
        this.userDao = UserDao.getInstance();
        try {
            this.cashedSubscriptions = userDao.getSubscriptions();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static synchronized UserManager getInstance() {
        return instance;
    }

    public boolean subscribe(User subscriber, User subscribedTo)throws  UserManagerException{
        if(subscribedTo == null || subscriber == null){
            throw new UserManagerException("User you trying to subscribe to does not exist");
        }
        if(cashedSubscriptions.contains(subscriber.getId() + "" + subscribedTo.getId())){
            return false;
        }
        try {
            userDao.addSubscription(subscriber, subscribedTo);
            cashedSubscriptions.add(subscriber.getId() + "" + subscribedTo.getId());
        }catch (SQLException e){
            throw new UserManagerException("Problem during subscription.");
        }
        return true;
    }

    public boolean removeSubscription(User subscriber, User subscribedTo) throws UserManagerException{
        if(subscribedTo == null || subscriber == null){
            throw new UserManagerException("User you trying to subscribe to does not exist");
        }
        if(!cashedSubscriptions.contains(subscriber.getId() + "" + subscribedTo.getId())){
            return false;
        }
        try {
            userDao.removeSubscription(subscriber, subscribedTo);
            cashedSubscriptions.remove(subscriber.getId() + "" + subscribedTo.getId());
        }catch (SQLException e){
            throw new UserManagerException("Problem during unsubscribing");
        }
        return true;
    }

    public void updateProfileInfo(User user, String password, String first_name, String last_name, String email, String profilePicURL) throws SQLException, LoggingManager.RegistrationException {
        //TODO add not null validation here instead of UserDao
       userDao.executeProfileUpdate(user,password,first_name,last_name,email, profilePicURL);
    }

    public void deleteUser(User user) throws UserManagerException{
        try {
            userDao.deleteUser(user);
        }catch (SQLException e){
            throw new UserManagerException("Problem during account deletion.");
        }
    }


}