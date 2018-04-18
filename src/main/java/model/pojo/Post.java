package model.pojo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Post implements Comparable<Post> {

    private int id;
    private LocalDateTime date;
    private User poster;
    private String url;
    private List<String> tags;
    private List<Comment> comments;
    private List<User> likers;
    private List<User> dislikers;

    //Post creation from DB
    public Post(int id, User poster, String url, LocalDateTime date) {
        this.id = id;
        this.poster = poster;
        this.url = url;
        this.date = date;
    }

    //New Post creation
    public Post(User user, String url) {
        this.poster = user;
        this.url = url;
        this.date = LocalDateTime.now();
        this.tags = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.likers = new ArrayList<>();
        this.dislikers = new ArrayList<>();
    }

    public void addLiker(User liker) {
        if(!likers.contains(liker)) {
            this.likers.add(liker);
        }
    }

    public void removeLiker(User user) {
        likers.remove(user);
    }

    public void addDisliker(User disliker) {
        if(!dislikers.contains(disliker)) {
            this.dislikers.add(disliker);
        }
    }

    public void removeDisliker(User user) {
        dislikers.remove(user);
    }

    public void addTag(String tag) {
        this.tags.add(tag);

    }

    public void removeTag(String tag) {
        synchronized (this) {
            this.tags.remove(tag);
        }
    }

    public void addComment(Comment c) {
        synchronized (this) {
            this.comments.add(c);
        }
    }

    //========================== Setters ==========================//

    public void setId(int id) {
        this.id = id;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setLikers(List<User> likers) {
        this.likers = likers;
    }

    public void setDislikers(List<User> dislikers) {
        this.dislikers = dislikers;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    //========================== Getters ==========================//

    public int getId() {
        return id;
    }

    public int getLikes() {
        return likers.size();
    }

    public int getDislikes() {
        return dislikers.size();
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
                ", likes=" + likers.size() +
                ", dislikes=" + dislikers.size() +
                ", date=" + date +
                ", poster=" + poster +
                ", url='" + url + '\'' +
                ", tags=" + tags +
                ", comments=" + comments +
                ", likers=" + likers +
                ", dislikers=" + dislikers +
                '}';
    }
}
