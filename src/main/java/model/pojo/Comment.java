package model.pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Comment implements Comparable<Comment> {

    private int id;
    private Post post;
    private User user;
    private LocalDateTime date;
    private String content;
    private List<User> likers;

    //Creating comment from DB
    public Comment(int id, Post post, User user, LocalDateTime date, String content) {
        this.id = id;
        this.post = post;
        this.user = user;
        this.date = date;
        this.content = content;
    }

    //Creating new comment
    public Comment(Post post, User user, String content) {
        this.post = post;
        this.user = user;
        this.content = content;
        this.date = LocalDateTime.now();
        this.likers = new ArrayList<>();
    }

    public void addLiker(User user) {
        likers.add(user);
    }

    public void removeLiker(User user) {
        likers.add(user);
    }

    //========================== Setters ==========================//


    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLikers(List<User> likers) {
        this.likers = likers;
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
        return likers.size();
    }

    private JSONArray getLikersAsJSON(){
        JSONArray array = new JSONArray();
        for (int i = 0; i < likers.size(); i++) {
            array.put(likers.get(i).getJSONObject());
        }
        return array;
    }




    @Override
    public int compareTo(Comment comment) {
        return this.date.compareTo(comment.date) > 0 ? -1 : 1;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("commentId", id);
            obj.put("commentPoster", user.getJSONObject());
            obj.put("commentDate", date);
            obj.put("commentContent", content);
            obj.put("commentLikers", getLikersAsJSON());

//            obj.put("Category", _category);
        } catch (JSONException e) {

        }
        return obj;
    }
    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", post=" + post.getId() +
                ", user=" + user +
                ", date=" + date +
                ", content='" + content + '\'' +
                ", likers=" + likers +
                '}';
    }
}