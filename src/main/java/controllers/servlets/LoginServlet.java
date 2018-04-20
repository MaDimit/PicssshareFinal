package controllers.servlets;

import controllers.managers.LoggingManager;
import controllers.managers.LoggingManager.*;
import model.pojo.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String name = request.getParameter("username");
            String password = request.getParameter("password");
            User user = LoggingManager.getInstance().login(name, password);
            request.getSession().setAttribute("user", user);
            request.getRequestDispatcher("index.html").forward(request, response);
        } catch (LoggingException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }catch(Exception e) {
            request.setAttribute("error", "Something went totaly wrong. Sorry.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}
