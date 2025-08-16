package com.aurionpro.test;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT id, password FROM users WHERE BINARY username = ?")) { 

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                if (dbPassword.equals(password)) {
                    HttpSession session = req.getSession();
                    session.setAttribute("userId", rs.getInt("id"));
                    session.setAttribute("username", username);
                    resp.sendRedirect("question");
                } else {
                    resp.sendRedirect("login.html?error=Invalid+username+or+password");
                }

            } else {
                resp.sendRedirect("login.html?error=Invalid+username+or+password");
            }

        } catch (SQLException e) {
            resp.getWriter().println("DB Error: " + e.getMessage());
        }
    }
}
