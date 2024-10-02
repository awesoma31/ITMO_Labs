package org.awesoma.lab2.controllers;

import java.io.*;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/controller")
public class ControllerServlet extends HttpServlet {
    private static final String ERROR_MSG = "Incorrect data provided: %s";
    private static final Set<Double> ALLOWED_X = DoubleStream
            .iterate(-2, i -> i <= 2, i -> i + .5)
            .boxed().collect(Collectors.toSet());

    private static final Set<Double> ALLOWED_R = DoubleStream
            .iterate(1, i -> i <= 3, i -> i + .5)
            .boxed().collect(Collectors.toSet());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        handleRequest(request, response);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var x = request.getParameter("x");
        var y = request.getParameter("y");
        var r = request.getParameter("r");

        if (invalidArgs(response, x, y, r)) return;

        try {
            var dx = Double.parseDouble(x);
            var dy = Double.parseDouble(y);
            var dr = Double.parseDouble(r);

            if (notInside(response, dx, dr, dy)) return;

            request.getRequestDispatcher("./areaCheck").forward(request, response);
        } catch (NumberFormatException | NullPointerException | ServletException e) {
            errorResponse(response, e.toString());
        }
    }

    private boolean invalidArgs(HttpServletResponse response, String x, String y, String r) throws IOException {
        if (x == null || y == null || r == null) {
            errorResponse(response, String.format(ERROR_MSG, "x, y, and r are required"));
            return true;
        }

        if (x.isEmpty() || y.isEmpty() || r.isEmpty()) {
            errorResponse(response, String.format(ERROR_MSG, "x, y, and r must not be empty"));
            return true;
        }
        return false;
    }

    private boolean notInside(HttpServletResponse response, double dx, double dr, double dy) throws IOException {
        if (dx < -2 || dx > 2) {
            errorResponse(response, String.format(ERROR_MSG, "x must be in " + ALLOWED_X));
            return true;
        }

        if (!ALLOWED_R.contains(dr)) {
            errorResponse(response, String.format(ERROR_MSG, "r must be in " + ALLOWED_R));
            return true;
        }

        if (dy < -5 || dy > 5) {
            errorResponse(response, String.format(ERROR_MSG, "y must be in [-5, 5]"));
            return true;
        }
        return false;
    }

    private void errorResponse(HttpServletResponse response, String error) throws IOException {
//        var errorObj = new ControllerError(error);
//        var jsonb = JsonbBuilder.create();

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
//        response.getWriter().write(jsonb.toJson(errorObj));
        // TODO: implement error response
    }
}