package model.pojo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Post implements Comparable<Post>{

    private int id;
    private int likes;
    private int dislikes;
    private LocalDateTime date;
    private User poster;
    private String url;
    private List<String> tags;
    private List<Comment> comments;
    private List<User> likers;
    private List<User> dislikers;

    //Post creation from DB
    public Post(int id, User poster, String url, int likes, int dislikes, LocalDateTime date, List<String> tags, List<Comment> comments, List<User> likers, List<User> dislikers){
        this.id = id;
        this.poster = poster;
        this.url = url;
        this.likes = likes;
        this.dislikes = dislikes;
        this.date = date;
        this.tags = tags;
        this.comments = comments;
        this.likers = likers;
        this.dislikers = dislikers;
    }

    //New Post creation
    public Post(User user, String url ){
        this.poster = user;
        this.url = url;
        this.date = LocalDateTime.now();
        this.likes = 0;
        this.dislikes = 0;
        this.tags = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.likers = new ArrayList<>();
        this.dislikers = new ArrayList<>();
    }

    public void addLike(){
        synchronized (this) {
            likes++;
        }
    }

    public void addDislike(){
        synchronized (this) {
            likes--;
        }
    }

    public void addTag(String tag){
        synchronized (this) {
            this.tags.add(tag);
        }
    }

    public void removeTag(String tag) {
        synchronized (this) {
            this.tags.remove(tag);
        }
    }

    public void addComment(Comment c){
        synchronized (this) {
            this.comments.add(c);
        }
    }

    //========================== Setters ==========================//

    public void setId(int id) {
        this.id = id;
    }

    //========================== Getters ==========================//

    public int getId() {
        return id;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public User getPoster() {
        return poster;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<User> getLikers() {
        return likers;
    }

    public List<User> getDislikers() {
        return dislikers;
    }

    @Override
    public int compareTo(Post post) {
        return this.date.compareTo(post.date) > 0 ? -1 : 1;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", likes=" + likes +
                ", date=" + date +
                ", poster=" + poster +
                ", url='" + url + '\'' +
                ", tags=" + tags +
                ", comments=" + comments +
                ", likers=" + likers +
                '}';
    }
}
