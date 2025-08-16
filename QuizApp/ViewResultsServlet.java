package com.aurionpro.test;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/viewResults")
public class ViewResultsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        resp.setContentType("text/html");
        try (PrintWriter out = resp.getWriter();
             Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT score, taken_on FROM results WHERE user_id = ? ORDER BY taken_on DESC")) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            out.println("<!doctype html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='utf-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("<style>");
            out.println("body { background: linear-gradient(to right, #e3f2fd, #ffffff); font-family: 'Segoe UI', sans-serif; }");
            out.println(".card { border-radius: 15px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }");
            out.println("table { border-radius: 10px; overflow: hidden; }");
            out.println("thead { background-color: #0d6efd; color: white; }");
            out.println("tbody tr:hover { background-color: #f1f1f1; transition: 0.3s; }");
            out.println(".btn-custom { border-radius: 30px; padding: 10px 20px; font-weight: 500; }");
            out.println("</style>");
            out.println("<title>Your Results</title>");
            out.println("</head>");
            out.println("<body>");

            out.println("<div class='container mt-5'>");
            out.println("<div class='card p-4'>");
            out.println("<h3 class='text-center mb-4'>Past Results for <span class='text-primary'>" 
                        + session.getAttribute("username") + "</span></h3>");

            out.println("<div class='table-responsive'>");
            out.println("<table class='table table-striped text-center align-middle'>");
            out.println("<thead><tr><th>#</th><th>Score</th><th>Taken On</th></tr></thead><tbody>");

            int i = 1;
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + i++ + "</td>");
                out.println("<td><span class='badge bg-success fs-6'>" + rs.getInt("score") + " / 5</span></td>");
                out.println("<td>" + rs.getTimestamp("taken_on") + "</td>");
                out.println("</tr>");
            }

            out.println("</tbody></table>");
            out.println("</div>"); // table responsive

            out.println("<div class='d-flex justify-content-center gap-3 mt-4'>");
            out.println("<a class='btn btn-primary btn-custom' href='question'>Take New Quiz</a>");
            out.println("<a class='btn btn-danger btn-custom' href='logout'>Logout</a>");
            out.println("</div>");

            out.println("</div>"); // card
            out.println("</div>"); // container

            out.println("</body></html>");
        } catch (SQLException e) {
            resp.getWriter().println("DB Error: " + e.getMessage());
        }
    }
}
