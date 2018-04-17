package model.pojo;

import java.time.LocalDateTime;
import java.util.List;

public class Comment implements Comparable<Comment>{

    private int id;
    private Post post;
    private User user;
    private LocalDateTime date;
    private String content;
    private int likes;

    //Creating comment from DB
    public Comment(int id, Post post, User user, LocalDateTime date, String content){
        this.id = id;
        this.post = post;
        this.user = user;
        this.date = date;
        this.content = content;
    }

    //Creating new comment
    public Comment(Post post, User user, String content){
        this.post = post;
        this.user = user;
        this.content = content;
        this.date = LocalDateTime.now();
        this.likes = 0;
    }

    public void addLike(){
        likes++;
    }

    public void removeLike(){
        likes--;
    }

    //========================== Setters ==========================//

    public void setId(int id) {
        this.id = id;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    //========================== Getters ==========================//

    public int getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public int getLikes() {
        return likes;
    }


    @Override
    public int compareTo(Comment comment) {
        return this.date.compareTo(comment.date) > 0 ? -1 : 1;
    }
}
