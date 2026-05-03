package serverapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;

public class ControllerServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Получаем параметры
        String x = request.getParameter("x");
        String y = request.getParameter("y");
        String r = request.getParameter("r");

        if (x != null && y != null && r != null) {
            // Перенаправляем в AreaCheckServlet для обработки
            request.getRequestDispatcher("/area-check").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
}