package model.dao;

import model.pojo.Comment;
import model.pojo.Post;
import model.pojo.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostDao {
    //test
//    public static void main(String[] args) {
//        try {
//            PostDao.getInstance().loadAllPosts();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        for(Post p : posts.values()){
//            System.out.println(p);
//        }
//    }

    private HashMap<Integer, Post> posts;
    private static PostDao instance;

    // singleton instance used in commentmanager
    public static PostDao getInstance() {
        if (instance == null) {
            instance = new PostDao();
        }
        return instance;
    }

    private PostDao() {
        super();
        this.posts = new HashMap<>();
    }

    public HashMap<Integer, Post> getPosts() {
        return posts;
    }
    //method for fill the collection with posts and all the information about them
    public void loadAllPosts() throws SQLException {
        Connection conn = DbManager.getInstance().getConnection();
        String sql = "SELECT id,date,poster_id,url FROM posts";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int id = rs.getInt("id");
            LocalDateTime postDate = rs.getTimestamp("date").toLocalDateTime();
            int poster_id = rs.getInt("poster_id");
            String url = rs.getString("url");
            ArrayList<User> likers =  getAllPostLikers(id);

            //add all the info for the post
            Post p = new Post(id,  UserDao.getInstance().getUserByID(poster_id), url, getAllPostLikers(id).size(),
                    postDate,  getAllTagsForPost(id),getAllComments(id), getAllPostLikers(id) );
            //put in collection
            this.posts.put(id, p);
        }


    }
    //get all the tags related to this post
    public List<String> getAllTagsForPost(int postID) throws SQLException {
        Connection conn =  DbManager.getInstance().getConnection();
        PreparedStatement stmt;
        List<String> tags = new ArrayList<>();

        //Fetching tags from DB
        String sql = "\tSELECT tags.tag_name FROM tags \n" +
                "                JOIN post_tag ON post_tag.tag_id=tags.id\n" +
                "                \n" +
                "                JOIN posts ON post_tag.post_id=posts.id\n" +
                "                WHERE posts.id = ? \n" +
                "                ORDER BY tags.tag_name;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, postID);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            String tag = rs.getString("tag_name");
            tags.add(tag);
        }
        return tags;
    }

    public ArrayList<User> getAllPostLikers(int postID) throws SQLException {
        Connection conn = DbManager.getInstance().getConnection();
        PreparedStatement stmt;

        ArrayList<Integer> likersID = new ArrayList<>();
        //Fetching likers from DB
        String sql = "SELECT users.id FROM users\n" +
                "JOIN liker_post ON liker_post.liker_id=users.id\n" +
                "JOIN posts ON liker_post.likedpost_id=posts.id\n" +
                "WHERE posts.id=? AND status = 1\n" +
                "";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, postID);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int likerId = rs.getInt(1);
            likersID.add(likerId);
        }
        ArrayList<User> likers = new ArrayList<>();
        for (int i = 0; i < likersID.size(); i++) {
            likers.add(UserDao.getInstance().getUserByID(likersID.get(i)));
        }
        return likers;
    }

    public List<Comment> getAllComments(int postId) throws SQLException {
        Connection conn =  DbManager.getInstance().getConnection();
        PreparedStatement stmt;
        ArrayList<Comment> comments = new ArrayList<>();
        //Fetching users from DB
        String sql = "SELECT id, poster_id, date, content, post_id FROM comments WHERE post_id=? ORDER BY date DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, postId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int commentId = rs.getInt("id");
            int posterId = rs.getInt("poster_id");
            LocalDateTime postDate =   rs.getTimestamp("date").toLocalDateTime();
            String content = rs.getString("content");
            Comment c = new Comment(commentId, PostDao.getInstance().posts.get(posterId), UserDao.getInstance().getUserByID(posterId), postDate, content);
            comments.add(c);
        }
        return comments;
    }



    public void setCommentLikesCount(int postID) throws SQLException {
        Connection conn =  DbManager.getInstance().getConnection();
        PreparedStatement stmt;

        //Fetching users from DB
        String sql = "SELECT liked_comment_id, COUNT(comment_liker_id) FROM picssshare.liker_comment \n" +
                "JOIN comments ON comments.id = liked_comment_id\n" +
                "JOIN posts on posts.id=comments.post_id\n" +
                "WHERE posts.id = ?\n" +
                "GROUP BY liked_comment_id;\n";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, postID);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            while (rs.next()) {
                int likedCommentId = rs.getInt("liked_comment_id");
                int commentsLikesCount = rs.getInt("liked_comment_id");

                posts.get(postID).getComments().get(likedCommentId).setLikes(commentsLikesCount);
            }
        }
    }

    public void addPost(Post post) throws SQLException{
        Connection conn =  DbManager.getInstance().getConnection();
        String sql = "INSERT INTO posts (likes,  date, poster_id, url) VALUES (?,?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, post.getLikes());
        stmt.setObject(2, Timestamp.valueOf(post.getDate()));
        stmt.setInt(3, post.getPoster().getId());
        stmt.setString(4, post.getUrl());
        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        int id;
        if (generatedKeys.next()) {
            id=generatedKeys.getInt(1);
            post.setId(id);
        }else {
            throw new SQLException("Creating user failed, no ID obtained.");
        }
        //add to collection
        this.posts.put(id, post);
    }

    public void deletePost(int id) throws SQLException{
        //delete from db
        Connection conn =  DbManager.getInstance().getConnection();
        String sql = "DELETE FROM posts WHERE id=(?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        //delete from collection
        this.posts.remove(id);
    }

    public void updateLikes(Post post) throws SQLException {
        Connection conn =  DbManager.getInstance().getConnection();
        String sql = "UPDATE posts SET likes = ? WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, post.getLikes());
        stmt.setInt(2, post.getId());
        stmt.executeUpdate();
    }

    public void addInLikerPostTable(User liker, Post post) throws SQLException {
        Connection conn =  DbManager.getInstance().getConnection();
        PreparedStatement stmt = null;
        String sql;
        sql = "INSERT INTO liker_post (`liker_id`, `likedpost_id`) VALUES (?,?)";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, liker.getId());
        stmt.setInt(2, post.getId());
        stmt.executeUpdate();
    }

}
