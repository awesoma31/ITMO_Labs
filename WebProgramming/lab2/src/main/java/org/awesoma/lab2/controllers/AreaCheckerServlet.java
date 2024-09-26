package org.awesoma.lab2.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.awesoma.lab2.models.Point;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@WebServlet("/areaCheck")
public class AreaCheckerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRequest(request, response);
    }

    @SuppressWarnings("unchecked")
    private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            var session = request.getSession();

            double x = Double.parseDouble(request.getParameter("x"));
            double y = Double.parseDouble(request.getParameter("y"));
            double r = Double.parseDouble(request.getParameter("r"));

            var startTime = Instant.now();
            boolean result = isPointInArea(x, y, r);
            var endTime = Instant.now();
            var execTime = ChronoUnit.NANOS.between(startTime, endTime);

            request.setAttribute("execTime", execTime);
            request.setAttribute("result", result);

            var resultList = (ArrayList<Point>) session.getAttribute("results");
            if (resultList == null) {
                resultList = new ArrayList<>();
            }
            resultList.add(new Point(x, y, r, execTime, result));
            System.out.println(resultList);

            session.setAttribute("results", resultList);

            request.getRequestDispatcher("/result.jsp").forward(request, response);
        } catch (NumberFormatException | ServletException | IOException e) {
            System.err.println(e.getMessage());
            //todo: implement error response
//            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    private boolean isPointInArea(double x, double y, double r) {
        if (y > 0 && x < 0) {
            return false;
        }
        if (y < 0 && x < 0) {
            if ((y*y + x*x) > r*r) {
                return false;
            }
        }
        if (y > 0 && x > 0) {
            if (y > (((double) -1/2)*x) + r/2) {
                return false;
            }
        }
        if (x > 0 && y < 0) {
            return !(x > r) && !(y < -r / 2);
        }
        return true;
    }
}
