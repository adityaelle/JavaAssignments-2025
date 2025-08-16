package com.aurionpro.test;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

@WebServlet("/submitQuiz")
public class SubmitQuizServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("userId") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        List<Integer> questionIds = (List<Integer>) session.getAttribute("questionIds");
        Map<Integer, String> answers = (Map<Integer, String>) session.getAttribute("answers");

        if (questionIds == null || answers == null) {
            resp.sendRedirect("question");
            return;
        }

        int score = 0;
        List<String> resultDetails = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM questions WHERE id=?");
            for (Integer qId : questionIds) {
                ps.setInt(1, qId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String correct = rs.getString("correct_option");
                    String userAns = answers.get(qId);
                    if (correct.equalsIgnoreCase(userAns)) score++;

                    // Add styled result item
                    resultDetails.add(
                        "<div class='p-3 mb-3 border rounded shadow-sm bg-light'>" +
                        "<p class='mb-1'><strong>Q:</strong> " + rs.getString("question_text") + "</p>" +
                        "<p class='mb-0'><span class='fw-bold text-secondary'>Your Answer:</span> " + userAns + "</p>" +
                        "<p class='mb-0'><span class='fw-bold text-success'>Correct Answer:</span> " + correct + "</p>" +
                        "</div>"
                    );
                }
                rs.close();
            }

            int userId = (Integer) session.getAttribute("userId");
            PreparedStatement ins = conn.prepareStatement("INSERT INTO results(user_id,score) VALUES(?,?)");
            ins.setInt(1, userId);
            ins.setInt(2, score);
            ins.executeUpdate();

        } catch (SQLException e) {
            resp.getWriter().println("DB Error: " + e.getMessage());
            return;
        }

        // Clear quiz session data
        session.removeAttribute("questionIds");
        session.removeAttribute("answers");
        session.removeAttribute("currentIndex");
        session.removeAttribute("startTime");

        // Output HTML
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<!doctype html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='utf-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
        out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        out.println("<style>");
        out.println("body { background: linear-gradient(to right, #f0f8ff, #ffffff); font-family: 'Segoe UI', sans-serif; }");
        out.println(".card { border-radius: 15px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }");
        out.println(".score-badge { font-size: 1.2rem; padding: 10px 20px; border-radius: 30px; }");
        out.println("</style>");
        out.println("<title>Quiz Results</title>");
        out.println("</head>");
        out.println("<body>");

        out.println("<div class='container mt-5'>");
        out.println("<div class='card p-4'>");
        out.println("<h3 class='text-center mb-4'>Quiz Completed!</h3>");
        out.println("<div class='text-center mb-4'>");
        out.println("<span class='badge bg-primary score-badge'>Your Score: " + score + " / " + questionIds.size() + "</span>");
        out.println("</div>");

        // Show all question results
        for (String detail : resultDetails) {
            out.println(detail);
        }

        // Buttons
        out.println("<div class='d-flex justify-content-center gap-3 mt-4'>");
        out.println("<a class='btn btn-primary btn-lg rounded-pill' href='question'>Attempt New Quiz</a>");
        out.println("<a class='btn btn-secondary btn-lg rounded-pill' href='viewResults'>View All Results</a>");
        out.println("<a class='btn btn-danger btn-lg rounded-pill' href='logout'>Logout</a>");
        out.println("</div>");

        out.println("</div>"); // card
        out.println("</div>"); // container

        out.println("</body>");
        out.println("</html>");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
