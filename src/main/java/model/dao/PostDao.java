package model.dao;

import model.pojo.Album;
import model.pojo.Comment;
import model.pojo.Post;
import model.pojo.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostDao extends Dao {
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
    private static PostDao instance = new PostDao();

    // singleton instance used in commentmanager
    public static PostDao getInstance() {
        return instance;
    }

    private PostDao() {
        super();
        this.posts = new HashMap<>();
    }

    public HashMap<Integer, Post> getPosts() {
        return posts;
    }

    public void addAlbumInDB(Album album) throws SQLException {
        String sql = "INSERT INTO albums (name, belonger_id) VALUES (?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1,album.getName());
        stmt.setInt(2,album.getUserID());
        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        int id;
        if (generatedKeys.next()) {
            id = generatedKeys.getInt(1);
            album.setId(id);
        } else {
            throw new SQLException("Creating post failed, no ID obtained.");
        }
        stmt.close();
    }

    //method for fill the collection with posts and all the information about them
    public void loadAllPosts() throws SQLException {
        String sql = "SELECT id,date,poster_id,url FROM posts";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Post p = createPost(rs);
            this.posts.put(p.getId(), p);
        }
        stmt.close();

    }


    public void setCommentLikesCount(int postID) throws SQLException {
        //Fetching users from DB
        String sql = "SELECT liked_comment_id, COUNT(comment_liker_id) FROM picssshare.liker_comment \n" +
                "JOIN comments ON comments.id = liked_comment_id\n" +
                "JOIN posts on posts.id=comments.post_id\n" +
                "WHERE posts.id = ?\n" +
                "GROUP BY liked_comment_id;\n";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, postID);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            while (rs.next()) {
                int likedCommentId = rs.getInt("liked_comment_id");
                int commentsLikesCount = rs.getInt("liked_comment_id");

                posts.get(postID).getComments().get(likedCommentId).setLikes(commentsLikesCount);
            }
        }
        stmt.close();
    }


    //================== Posts Interface ==================//

    public void addPost(Post post) throws SQLException {
        String sql = "INSERT INTO posts (likes, dislikes, date, poster_id, url) VALUES (?,?,?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, post.getLikes());
        stmt.setInt(2, post.getDislikes());
        stmt.setObject(3, Timestamp.valueOf(post.getDate()));
        stmt.setInt(4, post.getPoster().getId());
        stmt.setString(5, post.getUrl());
        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        int id;
        if (generatedKeys.next()) {
            id = generatedKeys.getInt(1);
            post.setId(id);
        } else {
            throw new SQLException("Creating post failed, no ID obtained.");
        }
        stmt.close();
    }

    public Post getPost(int id) throws SQLException {
        String sql = "SELECT id, date, poster_id, url, likes, dislikes FROM posts WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        Post post = rs.next() ? createPost(rs) : null;
        return post;
    }

    public void deletePost(int id) throws SQLException {
        //delete from db
        String sql = "DELETE FROM posts WHERE id=(?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
    }



    public void addLike(Post post, User user) throws SQLException {
        addLikeDislike(post, user, 1);
    }

    public void addDislike(Post post, User user) throws SQLException {
        addLikeDislike(post, user, -1);
    }

    //================== Post likes/dislikes ==================//

    private void addLikeDislike(Post post, User user, int status) throws SQLException{
        synchronized (post) {
            try {
                //Open transaction
                conn.setAutoCommit(false);
                //Inserting or updating liker_post table
                String likerPostSQL = "INSERT INTO liker_post (liker_id, likedpost_id, status) VALUES (?, ?, ?)\n" +
                        "  ON DUPLICATE KEY UPDATE status = ?;";
                PreparedStatement likerPostStmt = conn.prepareStatement(likerPostSQL);
                likerPostStmt.setInt(1, user.getId());
                likerPostStmt.setInt(2, post.getId());
                likerPostStmt.setInt(3, status);
                likerPostStmt.setInt(4, status);
                //if row were inserted returns 1, otherwise(updated) returns 0
                int inserted = likerPostStmt.executeUpdate();
                //if user add like or dislike for first time
                // 1. like case : like + 1, dislike + 0.
                // 2. dislike case : dislike + 1, like + 0;
                // if user add like while he had dislike before and vice versa, old value should be changed in post.
                // 1. like case: like + 1, dislike - 1.
                // 2. dislike case: like - 1, dislike + 1.
                int like = 0;
                int dislike = 0;
                if (inserted == 1) {
                    if (status == 1) {
                        like = 1;
                    } else {
                        dislike = 1;
                    }
                } else {
                    like = status;
                    dislike = status * -1;
                }
                //Updating post table
                String postSQL = "UPDATE posts SET likes = likes + ?, dislikes = dislikes + ? WHERE id = ?";
                PreparedStatement postsStmt = conn.prepareStatement(postSQL);
                postsStmt.setInt(1, like);
                postsStmt.setInt(2, dislike);
                postsStmt.setInt(3, post.getId());
                postsStmt.executeUpdate();

                post.addLike(like);
                post.addDislike(dislike);

                likerPostStmt.close();
                postsStmt.close();


            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }finally {
                conn.setAutoCommit(true);
            }
        }
    }


//    public void updateLikes(Post post) throws SQLException {
//        String sql = "UPDATE posts SET likes = ? WHERE id = ?";
//        PreparedStatement stmt = conn.prepareStatement(sql);
//        stmt.setInt(1, post.getLikes());
//        stmt.setInt(2, post.getId());
//        stmt.executeUpdate();
//        stmt.close();
//    }
//
//    public void addInLikerPostTable(User liker, Post post) throws SQLException {
//        String sql = "INSERT INTO liker_post (`liker_id`, `likedpost_id`, `status`) VALUES (?,?,1)";
//        PreparedStatement stmt = conn.prepareStatement(sql);
//        stmt.setInt(1, liker.getId());
//        stmt.setInt(2, post.getId());
//        stmt.executeUpdate();
//        stmt.close();
//    }

    //================== Post creation ==================//

    // Used in post creation for getPost, and feed. Should be added to while loop or used once with rs.next;
    private Post createPost(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        User user = UserDao.getInstance().getUserByID(rs.getInt("poster_id"));
        String url = rs.getString("url");
        int likes = rs.getInt("likes");
        int dislikes = rs.getInt("dislikes");
        LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
        List<String> tags = getAllTagsForPost(id);
        List<Comment> comments = getAllComments(id);
        List<User> likers = getPostLikers(id);
        List<User> dislikers = getPostDislikers(id);

        //add all the info for the post
        Post post = new Post(id, user, url, likes, dislikes, date, tags, comments, likers, dislikers);
        return post;
    }

    //get all the tags related to this post
    private List<String> getAllTagsForPost(int postID) throws SQLException {
        List<String> tags = new ArrayList<>();

        //Fetching tags from DB
        String sql = "SELECT tags.tag_name FROM tags \n" +
                "                JOIN post_tag ON post_tag.tag_id=tags.id\n" +
                "                \n" +
                "                JOIN posts ON post_tag.post_id=posts.id\n" +
                "                WHERE posts.id = ? \n" +
                "                ORDER BY tags.tag_name;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, postID);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String tag = rs.getString("tag_name");
            tags.add(tag);
        }
        stmt.close();
        return tags;
    }

    private ArrayList<User> getPostLikers(int postID) throws SQLException {
        return getLikersDislikers(postID, 1);
    }

    private ArrayList<User> getPostDislikers(int postID) throws SQLException {
        return getLikersDislikers(postID, -1);
    }

    private ArrayList<User> getLikersDislikers(int postID, int status) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        //Fetching likers from DB
        String sql = "SELECT users.id FROM users\n" +
                "JOIN liker_post ON liker_post.liker_id=users.id\n" +
                "JOIN posts ON liker_post.likedpost_id=posts.id\n" +
                "WHERE posts.id= ? AND status = ?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, postID);
        stmt.setInt(2, status);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int likerId = rs.getInt(1);
            users.add(UserDao.getInstance().getUserByID(likerId));
        }
        stmt.close();
        return users;
    }

    private List<Comment> getAllComments(int postId) throws SQLException {
        ArrayList<Comment> comments = new ArrayList<>();
        //Fetching users from DB
        String sql = "SELECT id, poster_id, date, content, post_id FROM comments WHERE post_id=? ORDER BY date DESC";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, postId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int commentId = rs.getInt("id");
            int posterId = rs.getInt("poster_id");
            LocalDateTime postDate = rs.getTimestamp("date").toLocalDateTime();
            String content = rs.getString("content");
            Comment c = new Comment(commentId, PostDao.getInstance().posts.get(posterId), UserDao.getInstance().getUserByID(posterId), postDate, content);
            comments.add(c);
        }
        stmt.close();
        return comments;
    }

}
