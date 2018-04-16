package model.pojo;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private long id;
    private User user;
    private String name;
    private List<Post> posts;

    //newly created Album
    public Album(User user, String name){
        this.user = user;
        this.name = name;
        this.posts = new ArrayList<>();
    }

    //Creating album from DB
    public Album(long id, User user, String name, List<Post> posts) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.posts = posts;
    }

    public void addPost(Post post){
        this.posts.add(post);
    }

    //========================== Setters ==========================//

    public void setId(long id) {
        this.id = id;
    }

    //========================== Getters ==========================//


    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
