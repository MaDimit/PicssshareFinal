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

    // User collections, fetching from DB by id during runtime
    private List<Integer> subscribedTo;
    private List<Integer> subscribers;
    private List<Integer> posts;
    private List<Integer> notifications;
    private List<Integer> albums;
    private List<Integer> likes;
    private List<Integer> dislikes;
    private List<Integer> commentLikes;

    //Empty collections for newly registered user
    private User(){
        this.subscribedTo = new ArrayList<>();
        this.subscribers = new ArrayList<>();
        this.posts = new ArrayList<>();
        this.notifications = new ArrayList<>();
        this.albums = new ArrayList<>();
        this.likes = new ArrayList<>();
        this.dislikes = new ArrayList<>();
        this.commentLikes = new ArrayList<>();
    }

    // Used during registration
    public User(String username,String password,String email){
        this();
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

    //========================== Adding to collcetions ==========================//

    public void addSubscribedTo(int userId){
        this.subscribedTo.add(userId);
    }

    public void addSubscriber(int userId){
        this.subscribers.add(userId);
    }

    public void addPost(int postId){
        this.posts.add(postId);
    }

    public void addNotification(int notificationId){
        this.notifications.add(notificationId);
    }

    public void addAlbum(int albumId){
        this.albums.add(albumId);
    }

    public void addLike(int postId){
        this.likes.add(postId);
    }

    public void addDislike(int postId){
        this.dislikes.add(postId);
    }

    public void addCommentLike(int commentLikeId){
        this.commentLikes.add(commentLikeId);
    }

    //========================== Setters ==========================//

    //Setters for user data that can be changed
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

    //Setters for user collection. Used during creation of user from DB
    public void setSubscribedTo(List<Integer> subscribedTo) {
        this.subscribedTo = subscribedTo;
    }

    public void setSubscribers(List<Integer> subscribers) {
        this.subscribers = subscribers;
    }

    public void setPosts(List<Integer> posts) {
        this.posts = posts;
    }

    public void setNotifications(List<Integer> notifications) {
        this.notifications = notifications;
    }

    public void setAlbums(List<Integer> albums) {
        this.albums = albums;
    }

    public void setLikes(List<Integer> likes) {
        this.likes = likes;
    }

    public void setDislikes(List<Integer> dislikes) {
        this.dislikes = dislikes;
    }

    public void setCommentLikes(List<Integer> commentLikes){
        this.commentLikes = commentLikes;
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

    public List<Integer> getSubscribedTo() {
        return subscribedTo;
    }

    public List<Integer> getSubscribers() {
        return subscribers;
    }

    public List<Integer> getPosts() {
        return posts;
    }

    public List<Integer> getNotifications() {
        return notifications;
    }

    public List<Integer> getAlbums() {
        return albums;
    }

    public List<Integer> getLikes() {
        return likes;
    }

    public List<Integer> getDislikes() {
        return dislikes;
    }

    public List<Integer> getCommentLikes(){
        return commentLikes;
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
