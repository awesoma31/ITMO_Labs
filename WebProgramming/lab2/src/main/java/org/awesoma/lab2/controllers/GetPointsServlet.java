package org.awesoma.lab2.controllers;

import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.awesoma.lab2.util.LocalDateTimeAdapter;
import org.awesoma.lab2.models.Point;
import java.time.*;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet("/getPoints")
public class GetPointsServlet extends HttpServlet {
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        // Register the LocalDateTime adapter
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        ArrayList<Point> points = null;
        if (session != null) {
            points = (ArrayList<Point>) session.getAttribute("results");
        }
        if (points == null) {
            points = new ArrayList<>();
        }

//        gson = new Gson();
        String jsonPoints = gson.toJson(points);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonPoints);
        out.flush();
    }
}
