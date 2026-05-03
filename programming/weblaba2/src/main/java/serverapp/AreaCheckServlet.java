package serverapp;
import serverapp.PointResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AreaCheckServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            double x = Double.parseDouble(request.getParameter("x"));
            double y = Double.parseDouble(request.getParameter("y"));
            double r = Double.parseDouble(request.getParameter("r"));

            // Создаем новую точку
            PointResult newResult = new PointResult(x, y, r, checkHit(x, y, r), System.currentTimeMillis());

            HttpSession session = request.getSession();
            List<PointResult> results = (List<PointResult>) session.getAttribute("results");

            if (results == null) {
                results = new ArrayList<>();
            }

            results.add(newResult);

            session.setAttribute("results", results);
            session.setAttribute("selectedR", r);

            request.getRequestDispatcher("/result.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters format");
        }
    }

    private boolean checkHit(double x, double y, double r) {
        if (x >= 0 && y >= 0 && x <= r && y <= r) {
            return true;
        }
        if (x >= 0 && y <= 0 && (x * x + y * y) <= (r / 2) * (r / 2)) {
            return true;
        }
        if (x <= 0 && y <= 0 && x >= -r && y >= -r / 2 && (-y * 2) <= (r + 2 * x)) {
            return true;
        }
        return false;
    }
}