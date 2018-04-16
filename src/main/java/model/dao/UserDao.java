package model.dao;

//import controllers.managers.LoggingManager;
import controllers.managers.LoggingManager;
import model.pojo.User;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class UserDao extends Dao {

    private static UserDao instance = new UserDao();

    // lazy singleton
    private UserDao() {
        super();
    }

    // singleton instance used in usermanager
    public static synchronized UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }

    public boolean checkIfUsernameIsFree(String username) throws SQLException {
        Connection conn = dbManager.getConnection();
        String sql = "SELECT users.username FROM users WHERE users.username = ?";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1,username);
        ResultSet resultSet = stmt.executeQuery();
        //return true if exists and false if not
        return resultSet.next();
    }

    public boolean checkIfEmailIsFree(String email) throws SQLException {
        Connection conn = dbManager.getConnection();
        String sql = "SELECT users.username FROM users WHERE users.email = ?";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1,email);
        ResultSet resultSet = stmt.executeQuery();
        //return true if exists and false if not
        return resultSet.next();
    }



    public void registerUser(User user) throws SQLException {
        Connection conn = dbManager.getConnection();

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
    }

    public void addSubscription(User subscriber, User subscribed) throws SQLException {
        Connection conn = dbManager.getConnection();
        String sql = "INSERT INTO subscriber_subscribed (subscriber_id, subscribedto_id) VALUES (?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, subscriber.getId());
        stmt.setInt(2, subscribed.getId());
        stmt.executeUpdate();
    }

    // username and id should not be modified
    public void executeProfileUpdate(User u, String password, String first_name, String last_name, String email)
            throws SQLException, LoggingManager.RegistrationException {
        Connection conn = dbManager.getConnection();
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
                if (!UserDao.getInstance().checkIfEmailIsFree(email))
                    notNullValues.put("email", email);
                u.setEmail(email);
            }
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

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, u.getId());
        stmt.executeUpdate();

        // for next usages
        commaCounter = 0;

    }


}