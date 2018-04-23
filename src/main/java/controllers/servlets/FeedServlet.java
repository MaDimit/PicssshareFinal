package controllers.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import model.dao.PostDao;
import model.dao.UserDao;
import model.pojo.Post;
import model.pojo.User;
import org.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "FeedServlet", urlPatterns = "/friendsfeed")
public class FeedServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(request.getParameter("username"));
        try {
            User u = UserDao.getInstance().getUserByID(Integer.parseInt(request.getParameter("username")));
            ArrayList<Post> userPosts = (ArrayList<Post>) PostDao.getInstance().getFriendsFeed(u);
            JSONArray array = new JSONArray();
            for (int i = 0; i < userPosts.size(); i++) {
                array.put(userPosts.get(i).getJSONObject());
            }
            System.out.println(array);
            response.getWriter().print(array.toString());
        } catch (SQLException e) {
            e.printStackTrace();

            response.getWriter().print("success");
        }
    }
}
