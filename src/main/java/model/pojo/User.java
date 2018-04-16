package model.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {

    // User data
    private long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicUrl;

    // Used during registration
    public User(String username,String password,String email){
        this.username = username;
        this.password = password;
        this.email = email;
       // this.profilePicUrl = TODO Default profile picture;
    }

    //Used during creation from db
    public User(long id, String username, String password, String firstName, String lastName, String email, String profilePicUrl) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
    }

    //========================== Setters ==========================//


    public void setId(long id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //========================== Getters ==========================//


    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    //========================== Object methods ==========================//

    // equality by username
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
