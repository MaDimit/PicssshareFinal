import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "NameServlet", urlPatterns = "/name")
public class NameServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //PHILIP TESTING HERE
        String name = NameManager.appendHello(request.getParameter("name"));
        request.setAttribute("name",name);
        request.getRequestDispatcher("hello.jsp").forward(request,response);
    }
}
