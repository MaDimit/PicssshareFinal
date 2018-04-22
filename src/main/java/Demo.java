import com.google.gson.Gson;
import com.google.gson.JsonArray;
import controllers.managers.AlbumManager;
import controllers.managers.LoggingManager;
import controllers.managers.PostManager;
import controllers.managers.UserManager;
import model.dao.CommentDao;
import model.dao.PostDao;
import model.dao.UserDao;
import model.pojo.Comment;
import model.pojo.Post;
import model.pojo.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;


public class Demo {

    public static void main(String[] args) throws SQLException, LoggingManager.LoggingException, LoggingManager.RegistrationException {

    class Student{
        String name;
        int age;
        String familyname;
        Student(String name, int age, String familyname){
            this.name = name;
            this.age = age;
            this.familyname = familyname;

        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getFamilyname() {
            return familyname;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", familyname='" + familyname + '\'' +
                    '}';
        }
    }

//        LoggingManager lm = LoggingManager.getInstance();
//        PostDao pd = PostDao.getInstance();
//        User user = lm.login("Maxim","Qwerty12345");
//        User user1 = lm.login("Philip", "Qwerty123456");
//        CommentDao cd = CommentDao.getInstance();
//        AlbumManager am = AlbumManager.getInstance();
//        UserManager um = UserManager.getInstance();
//        PostManager pm = PostManager.getInstance();
//        long time = System.currentTimeMillis();
//        try {
//            System.out.println(pm.getTrendingFeed());
//        } catch (PostManager.PostException e) {
//            e.printStackTrace();
//        }
//
//
//        System.out.println(System.currentTimeMillis() - time);


//        User u = UserDao.getInstance().getUserByID(2);
//        ArrayList<Post> userPosts = (ArrayList<Post>) PostDao.getInstance().getFriendsFeed(u);
//        JSONArray json = new JSONArray();
//        for (int i = 0; i < userPosts.size(); i++) {
//            json.put(userPosts.get(i).getJSONObject());
//        }
//
//        System.out.println(json);


        User u = UserDao.getInstance().getUserByID(2);
        ArrayList<Post> userPosts = (ArrayList<Post>) PostDao.getInstance().getFriendsFeed(u);
        JSONArray array = new JSONArray();
        for (int i = 0; i < userPosts.size(); i++) {
            array.put(userPosts.get(i).getJSONObject());
        }
        System.out.println(array);
    }
}
