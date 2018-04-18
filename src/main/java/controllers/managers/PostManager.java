package controllers.managers;

import model.dao.PostDao;
import model.pojo.Post;
import model.pojo.User;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

public class PostManager {

    public static class PostException extends Exception{
        public PostException(String msg) {
            super(msg);
        }
    }
    private final PostDao postDao;
    private HashSet<String> cachedlikes;
    private HashSet<String> cachedDislikes;

    private final static PostManager instance = new PostManager();

    private PostManager() {
        this.postDao = PostDao.getInstance();
        try{
            this.cachedlikes = postDao.getAllLikers();
            this.cachedlikes = postDao.getAllDislikers();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static PostManager getInstance() {
        return instance;
    }

    //================= Post manipulation =================//
    public void addPost(Post post)throws PostException{
        validate(post);
        try {
            postDao.addPost(post);
        }catch (SQLException e){
            throw new PostException("Problem during adding post to DB");
        }
    }

    public void deletePost(Post post) throws PostException{
        validate(post);
        try{
            postDao.deletePost(post.getId());
        }catch (SQLException e){
            throw new PostException("Problem during removing post from DB");
        }
    }

    public Post getPost(int postID) throws PostException{
        try{
            return postDao.getPost(postID);
        }catch (SQLException e){
            throw new PostException("Post with this ID can't be found");
        }
    }

    //================= liking/disliking =================//

    public boolean likePost(Post post, User user) throws PostException{
        if(cachedlikes.contains(user.getId() + "" + post.getId())){
            return false;
        }
        try{
            postDao.addLike(post, user);
            cachedlikes.add(user.getId() + "" + post.getId());
        }catch (SQLException e){
            throw new PostException("Problem during like adding");
        }
        return true;
    }

    public boolean dislikePost(Post post, User user) throws PostException{
        if(cachedDislikes.contains(user.getId() + "" + post.getId())){
            return false;
        }
        try{
            postDao.addDislike(post, user);
            cachedDislikes.add(user.getId() + "" + post.getId());
        }catch (SQLException e){
            throw new PostException("Problem during dislike adding");
        }
        return true;
    }

    //================= Feed =================//

    public List<Post> getUserFeed(User user) throws PostException{
        try {
            List<Post> posts = postDao.getUserFeed(user);
            return posts;
        }catch (SQLException e){
            throw new PostException("Problem during user feed creation");
        }
    }

    public List<Post> getFriendsFeed(User user) throws PostException{
        try {
            List<Post> posts = postDao.getFriendsFeed(user);
            return posts;
        }catch (SQLException e){
            throw new PostException("Problem during friends feed creation");
        }
    }

    public List<Post> getTrendingFeed() throws PostException{
        try {
            List<Post> posts = postDao.getTrendingFeed();
            posts.sort((p1, p2)->p1.getLikes() + p1.getDislikes() > p2.getLikes() + p2.getDislikes() ? -1 : 1);
            return posts;
        }catch (SQLException e){
            throw new PostException("Problem during trending feed creation");
        }
    }

    private void validate(Post post, User user) throws PostException{
        if(post == null){
            throw new PostException("Post does not exist");
        }
        if(user == null){
            throw new PostException("User does not exist");
        }

    }

    private void validate(Post post) throws PostException{
        validate(post, post.getPoster());
    }

}