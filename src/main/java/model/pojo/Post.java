package model.pojo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Post implements Comparable<Post>{

    private long id;
    private int likes;
    private int dislikes;
    private LocalDateTime date;
    private User user;
    private String url;
    private List<String> tags;

    //Post creation from DB
    public Post(long id, User user, String url, int likes, int dislikes, LocalDateTime date, List<String> tags){
        this.id = id;
        this.user = user;
        this.url = url;
        this.likes = likes;
        this.dislikes = dislikes;
        this.date = date;
        this.tags = tags;
    }

    //New Post creation
    public Post(User user, String url ){
        this.user = user;
        this.url = url;
        this.date = LocalDateTime.now();
        this.likes = 0;
        this.dislikes = 0;
        this.tags = new ArrayList<>();
    }

    public void addLike(){
        likes++;
    }

    public void addDislike(){
        dislikes++;
    }

    public void addTag(String tag){
        this.tags.add(tag);
    }

    //========================== Setters ==========================//

    public void setId(long id) {
        this.id = id;
    }

    //========================== Getters ==========================//

    public long getId() {
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

    public User getUser() {
        return user;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public int compareTo(Post post) {
        return this.date.compareTo(post.date) > 0 ? -1 : 1;
    }
}
