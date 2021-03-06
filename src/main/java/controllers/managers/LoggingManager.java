package controllers.managers;

import model.dao.UserDao;
import model.pojo.User;

import java.sql.SQLException;

public class LoggingManager {
    public static class RegistrationException extends Exception {
        public RegistrationException(String msg) {
            super(msg);
        }
    }

    public static class LoggingException extends Exception {
        public LoggingException(String msg) {
            super(msg);
        }
    }


    private final static LoggingManager instance = new LoggingManager();

    private LoggingManager() {

    }

    public static LoggingManager getInstance() {
        return instance;
    }

    //========================REGISTER PART===================================//

    public User register(String username, String password, String email) throws RegistrationException, SQLException {

        //Username validating
        validateUsername(username);

        if (!validatePassword(password)) {
            throw new RegistrationException("weakPass");
        }

        if (!validateEmailAddress(email)) {
            throw new RegistrationException("emailNotValid");
        }

        //if data is valid user obj is created
        User user = new User(username, password, email);

        //adding to DB and collections
        try {
            UserDao.getInstance().registerUser(user);
        } catch (SQLException e) {
            System.out.println("Registering to DB problem: " + e.getMessage());
            e.printStackTrace();
            throw new RegistrationException("Data base connection problem!");
        }

        System.out.println("Registration of " + user.getUsername() + " is successfull!");
        return user;
    }

    //========================VALIDATIONS===================================//
    // validate username
    public boolean validateUsername(String username) throws RegistrationException, SQLException {
        if (username == null || username.isEmpty()) {
            throw new RegistrationException("emptyName");
        }
        if (!username.matches("^[a-zA-Z0-9]+([_ -]?[a-zA-Z0-9])*$")) {
            throw new RegistrationException("nonValidChars");
        }
        if (UserDao.getInstance().checkIfUsernameIsTaken(username)) {
            throw new RegistrationException("nameExists");
        }
        return true;
    }

    /*
     * This regex will enforce these rules for password:
     *
     * At least one upper case English letter, (?=.*?[A-Z])
     * At least one lower case English letter, (?=.*?[a-z])
     * At least one digit, (?=.*?[0-9])
     * Minimum eight in length .{8,} (with the anchors)
     */
    public boolean validatePassword(String password) {
        return (password != null && !password.isEmpty()
                && password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$"));
    }

    // validate first name
    public boolean validateFirstName(String firstName) {
        return firstName.matches("[a-zA-z]+([ '-][a-zA-Z]+)*");
    }

    // validate last name
    public boolean validateLastName(String lastName) {
        return lastName.matches("[a-zA-z]+([ '-][a-zA-Z]+)*");
    }

    // validate email address
    public boolean validateEmailAddress(String email) throws RegistrationException, SQLException {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);

        if (UserDao.getInstance().checkIfEmailIsTaken(email)) {
            throw new RegistrationException("emailExists");
        }

        return m.matches();
    }



    //=========================LOGGING=======================================//

    //Logging by username and password ----> //TODO: CHECK FOR RIGHT LOGIN IN THE SESSION
    public User login(String username, String password) throws LoggingException, SQLException {
        User user = UserDao.getInstance().login(username);
        if (user == null) {
            throw new LoggingException("username");
        }
        if (!user.getPassword().equals(password)) {
            throw new LoggingException("password");
        }
        return user;
    }
}
