
package serverapp;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

public class ValidationFilter implements Filter {

    private static final Set<Integer> VALID_R_VALUES = Set.of(1, 2, 3, 4, 5);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String xParam = httpRequest.getParameter("x");
        String yParam = httpRequest.getParameter("y");
        String rParam = httpRequest.getParameter("r");

        if (xParam == null || yParam == null || rParam == null ||
                xParam.trim().isEmpty() || yParam.trim().isEmpty() || rParam.trim().isEmpty()) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "All parameters (x, y, r) must be provided and non-empty.");
            return;
        }

        try {
            double x = Double.parseDouble(xParam);
            double y = Double.parseDouble(yParam);

            double rDouble = Double.parseDouble(rParam);

            if (rDouble != Math.floor(rDouble)) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Parameter 'r' must be an integer (e.g., 1, 2, 3, 4, or 5).");
                return;
            }

            int rInt = (int) rDouble;
            if (!VALID_R_VALUES.contains(rInt)) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Parameter 'r' must be one of: 1, 2, 3, 4, 5.");
                return;
            }
            // передаем запрос controller сервлету
            chain.doFilter(request, response);

        } catch (NumberFormatException e) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid number format: x, y must be valid decimals; r must be an integer.");
        }
    }
}