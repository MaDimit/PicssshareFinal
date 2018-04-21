package controllers.servlets;

import org.apache.catalina.Session;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "LoginFilter", urlPatterns = "/jhlkjh*") //TODO "/*" was used in url pattern, but it caused bugs in jquery for some reason
public class LoginFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession(false);

        String path = request.getRequestURI();
        boolean loggedIn = session != null && session.getAttribute("user") != null;
        boolean loginRequest = path.startsWith("/login") || path.startsWith("/register");

        if (loggedIn || loginRequest) {
            chain.doFilter(req, resp);
        } else {
            response.sendRedirect("login.html");
        }

    }

}
