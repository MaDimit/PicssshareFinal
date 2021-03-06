package model.dao;

import controllers.managers.LoggingManager;
import model.pojo.Album;
import model.pojo.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class UserDao extends Dao {


    private static UserDao instance = new UserDao();

    private UserDao() {
        super();
    }

    public static UserDao getInstance() {
        return instance;
    }

    //================== User Interface ==================//

    //used for search
    public ArrayList<User> getAllUsersByPattern(String pattern) throws SQLException {
        ArrayList<User> matchingUsers = new ArrayList<>();
        String sql = "SELECT id FROM users WHERE UPPER(username) LIKE UPPER('%"+pattern+"%') ORDER BY username;";
        PreparedStatement stmt = conn.prepareStatement(sql);
      //  stmt.setString(1,pattern);
        ResultSet resultSet = stmt.executeQuery();
        while(resultSet.next()) {
            int userID = resultSet.getInt("id");
            matchingUsers.add(getUserByID(userID));
        }
        return matchingUsers;
    }

    public User getUserByID(int id) throws SQLException {
        String sql = "SELECT id, username, password, first_name, last_name, email, profile_picture_url FROM users WHERE users.id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1,id);
        ResultSet resultSet = stmt.executeQuery();
        if(resultSet.next()) {
            return createUser(resultSet);
        }
        return null;
    }

//    public void loadUsersAlbums(User u) throws SQLException {
//        String sql = "SELECT albums.id as album_id,albums.name, posts.id as post_id\n" +
//                "FROM albums JOIN albums_has_posts ON albums_has_posts.album_id=albums.id\n" +
//                "JOIN posts ON albums_has_posts.post_id=posts.id WHERE albums.belonger_id = ?";
//        PreparedStatement stmt = conn.prepareStatement(sql);
//        stmt.setInt(1,u.getId());
//        ResultSet resultSet = stmt.executeQuery();
//        while(resultSet.next()){
//            int albumID = resultSet.getInt(1);
//            String albumName = resultSet.getString(2);
//            int postID = resultSet.getInt(3);
//            if(u.getAlbumByID(albumID)==null){
//                u.addAlbum(new Album(u, albumName, albumID));
//            }
//            u.getAlbums().get(albumID).addPost(PostDao.getInstance().getPost(postID));
//        }
//    }

    public void addSubscription(User subscriber, User subscribedTo) throws SQLException {
        String sql = "INSERT INTO subscriber_subscribed (subscriber_id, subscribedto_id) VALUES (?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, subscriber.getId());
        stmt.setInt(2, subscribedTo.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    public void removeSubscription(User subscriber, User subscribedTo) throws SQLException{
        String sql = "DELETE FROM subscriber_subscribed WHERE subscriber_id = ? AND subscribedto_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, subscriber.getId());
        stmt.setInt(2, subscribedTo.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    public void deleteUser(User user) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1,user.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    public HashSet<String> getAllSubscriptions() throws SQLException{
        HashSet<String> subscriptions = new HashSet<>();
        String sql = "SELECT subscriber_id, subscribedto_id FROM subscriber_subscribed";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            subscriptions.add(rs.getInt("subscriber_id") + "" + rs.getInt("subscribedto_id"));
        }
        return subscriptions;
    }

    // username and id should not be modified
    public void executeProfileUpdate(User u, String password, String first_name, String last_name, String email, String profilePicURL)
            throws SQLException, LoggingManager.RegistrationException {
        // TODO Exctract notnull validation to UserManager
        // Store in collection not null values, because the user could choose to change
        // different number of
        // information for his profile

        //for each field, check if it has been changed
        HashMap<String, String> notNullValues = new HashMap<>();


        if (!password.equalsIgnoreCase(u.getPassword())) {
            if(LoggingManager.getInstance().validatePassword(password)) {
                notNullValues.put("password", password);
                u.setPassword(password);
            }
        }
        if (!first_name.equalsIgnoreCase(u.getFirstName())) {
            if(LoggingManager.getInstance().validateFirstName(first_name)) {
                notNullValues.put("first_name", first_name);
                u.setFirstName(first_name);
            }
        }
        if (!last_name.equalsIgnoreCase(u.getLastName())) {
            if(LoggingManager.getInstance().validateLastName(last_name)) {
                notNullValues.put("last_name", last_name);
                u.setLastName(last_name);
            }
        }
        if (!email.equalsIgnoreCase(u.getEmail())) {
            // check the existing ones and if there is not such an email - set it
            if(LoggingManager.getInstance().validateEmailAddress(email)) {
                if (!UserDao.getInstance().checkIfEmailIsTaken(email))
                    notNullValues.put("email", email);
                u.setEmail(email);
            }
        }
        if (!profilePicURL.equalsIgnoreCase(u.getProfilePicUrl())) {
            notNullValues.put("profilePicURL", profilePicURL);
            u.setProfilePicUrl(profilePicURL);
        }

        StringBuilder sb = new StringBuilder();
        // comma count is used for placing commas between set statements
        int commaCounter = 0;
        for (Map.Entry<String, String> entry : notNullValues.entrySet()) {
            commaCounter++;
            sb.append(entry.getKey());
            sb.append("='");
            sb.append(entry.getValue());
            sb.append("'");
            if (commaCounter > 0 && commaCounter < notNullValues.size()) {
                sb.append(", ");
            }

        }
        String sql = "UPDATE users SET "+sb.toString()+" WHERE id = ?";
        System.out.println(sql);

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, u.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    //================== Logging and Registration ==================//

    public boolean checkIfUsernameIsTaken(String username) throws SQLException {
        String sql = "SELECT users.username FROM users WHERE users.username = ?";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1,username);
        ResultSet resultSet = stmt.executeQuery();
        //return true if exists and false if not
        return resultSet.next();
    }

    public boolean checkIfEmailIsTaken(String email) throws SQLException {
        String sql = "SELECT users.username FROM users WHERE users.email = ?";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1,email);
        ResultSet resultSet = stmt.executeQuery();
        //return true if exists and false if not
        return resultSet.next();
    }

    public User login(String username) throws SQLException{
        String sql = "SELECT id, username, password,first_name,last_name,email,profile_picture_url FROM users WHERE username = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1,username);
        ResultSet rs = stmt.executeQuery();
        if(!rs.next()){
            return null;
        }
        User user = createUser(rs);
        stmt.close();
        return user;
    }


    public void registerUser(User user) throws SQLException {

        // Inserting into DB
        String sql = "INSERT INTO users (username, password, email) VALUES (?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword());
        stmt.setString(3, user.getEmail());
        stmt.executeUpdate();

        // Getting id for registered user
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            user.setId(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        stmt.close();
    }

    //================== User Creation ==================//

    User createUser(ResultSet rs) throws SQLException{
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String firstname = rs.getString("first_name");
        String lastname = rs.getString("last_name");
        String email = rs.getString("email");
        String profilePicUrl = rs.getString("profile_picture_url");
        return new User (id, username, password, firstname, lastname, email, profilePicUrl);
    }

}