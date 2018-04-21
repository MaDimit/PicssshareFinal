package controllers.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import controllers.managers.LoggingManager;
import model.pojo.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            JsonObject object = gson.fromJson(reader,JsonObject.class);
            String username = object.get("username").getAsString();
            String password1 = object.get("password1").getAsString();
            String password2 = object.get("password2").getAsString();
            String email = object.get("email").getAsString();
            if(!password1.equals(password2)) {
                response.getWriter().write("passNotMatch");
                return;
            }
            User user = LoggingManager.getInstance().register(username, password1, email);
            request.getSession().setAttribute("user", user);
            response.getWriter().write("success");
        } catch (LoggingManager.RegistrationException e) {
            response.getWriter().write(e.getMessage());
            System.out.println(e.getMessage());
        }catch(Exception e) {
            response.setStatus(500);
            response.getWriter().write(e.getMessage());
        }

    }
}
