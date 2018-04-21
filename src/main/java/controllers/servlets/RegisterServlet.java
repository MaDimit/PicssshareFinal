package controllers.servlets;

import controllers.managers.LoggingManager;
import model.pojo.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String password = request.getParameter("password1");
            String password2 = request.getParameter("password2");
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            if(!password.equals(password2)) {
                throw new LoggingManager.RegistrationException("Password does not match.");
            }
            User user = LoggingManager.getInstance().register(username, password, email);
            request.getSession().setAttribute("user", user);
            // create feedservlet
            request.getRequestDispatcher("index.html").forward(request, response);
        } catch (LoggingManager.RegistrationException e) {
//            request.setAttribute("error", e.getMessage());
//            request.getRequestDispatcher("error.jsp").forward(request, response);
            System.out.println(e.getMessage());
        }catch(Exception e) {
//            request.setAttribute("error", "Something went totaly wrong. Sorry.");
//            request.getRequestDispatcher("error.jsp").forward(request, response);

            System.out.println(e.getMessage());
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
