package com.aurionpro.test;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username").trim();
        String password = req.getParameter("password").trim();
        String email = req.getParameter("email").trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            resp.sendRedirect("register.html?error=Please+fill+all+fields");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)");
            ps.setString(1, username);
            ps.setString(2, password); 
            ps.setString(3, email);
            ps.executeUpdate();
            resp.sendRedirect("login.html?success=registered");
        } catch (SQLException e) {
            resp.sendRedirect("register.html?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("register.html");
    }
}
