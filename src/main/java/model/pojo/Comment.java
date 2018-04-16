package model.pojo;

import java.time.LocalDateTime;

public class Comment implements Comparable<Comment>{

    private long id;
    private Post post;
    private User user;
    private LocalDateTime date;
    private String content;
    private int likes;

    //Creating comment from DB
    public Comment(long id, Post post, User user, LocalDateTime date, String content, int likes){
        this.id = id;
        this.post = post;
        this.user = user;
        this.date = date;
        this.content = content;
        this.likes = likes;
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

    public void setId(long id) {
        this.id = id;
    }

    //========================== Getters ==========================//

    public long getId() {
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
