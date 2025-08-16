package com.aurionpro.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/question")
public class QuestionServlet extends HttpServlet {

    private static final long QUIZ_DURATION_MS = 5 * 60 * 1000L;
    
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("userId") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        long startTime = session.getAttribute("startTime") == null
                ? System.currentTimeMillis()
                : (long) session.getAttribute("startTime");
        session.setAttribute("startTime", startTime);

        List<Integer> questionIds = (List<Integer>) session.getAttribute("questionIds");
        Map<Integer, String> answers = (Map<Integer, String>) session.getAttribute("answers");
        Integer currentIndex = (Integer) session.getAttribute("currentIndex");

        if (questionIds == null) {
            questionIds = new ArrayList<>();
            answers = new HashMap<>();
            currentIndex = 0;

            try (Connection conn = DBConnection.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id FROM questions")) {

                List<Integer> allIds = new ArrayList<>();
                while (rs.next()) {
                    allIds.add(rs.getInt("id"));
                }

                Collections.shuffle(allIds);
                for (int i = 0; i < 5 && i < allIds.size(); i++) {
                    questionIds.add(allIds.get(i));
                }

            } catch (SQLException e) {
                resp.getWriter().println("DB Error: " + e.getMessage());
                return;
            }

            session.setAttribute("questionIds", questionIds);
            session.setAttribute("answers", answers);
            session.setAttribute("currentIndex", currentIndex);
        }


        if (currentIndex == null) currentIndex = 0;
        if (currentIndex >= questionIds.size()) {
            resp.sendRedirect("submitQuiz");
            return;
        }

        int qId = questionIds.get(currentIndex);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM questions WHERE id = ?")) {
            ps.setInt(1, qId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                resp.getWriter().println("Question not found.");
                return;
            }

            resp.setContentType("text/html");
            PrintWriter out = resp.getWriter();
            out.println("<!doctype html><html><head><meta charset='utf-8'>");
            out.println("<meta name='viewport' content='width=device-width,initial-scale=1'>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("<link rel='stylesheet' href='css/styles.css'>"); 
            out.println("<title>Quiz</title></head><body>");

            out.println("<div class='container mt-5'>");
            out.println("<div class='d-flex justify-content-between align-items-center mb-3'>");
            out.println("<h4 class='fw-bold text-primary'>Welcome, " + session.getAttribute("username") + "</h4>");
            out.println("<div>");
            out.println("<a class='btn btn-outline-secondary btn-sm me-2' href='viewResults'>View Results</a>");
            out.println("<a class='btn btn-danger btn-sm' href='logout'>Logout</a>");
            out.println("</div></div>");

            long endTime = startTime + QUIZ_DURATION_MS;
            out.println("<div class='mb-3'><span class='badge bg-warning text-dark p-2'>Time Remaining: <span id='timer'></span></span></div>");

            out.println("<div class='card shadow-sm border-0'>");
            out.println("<div class='card-body'>");
            out.println("<form method='post' action='question'>");
            out.println("<p class='fs-5 fw-semibold mb-4'>Question " + (currentIndex + 1) + ":</p>");
            out.println("<p class='mb-4'>" + rs.getString("question_text") + "</p>");

            char[] opts = new char[]{'A','B','C','D'};
            for (char opt : opts) {
                String optLower = String.valueOf(opt).toLowerCase();
                String label = rs.getString("option_" + optLower);
                String checked = answers.containsKey(qId) && answers.get(qId).equals(String.valueOf(opt)) ? "checked" : "";
                out.println("<div class='form-check mb-2'>");
                out.println("<input class='form-check-input' type='radio' name='answer' id='opt" + opt + "' value='" + opt + "' " + checked + ">");
                out.println("<label class='form-check-label' for='opt" + opt + "'><strong>" + opt + ".</strong> " + label + "</label>");
                out.println("</div>");
            }

            out.println("<div class='mt-4 d-flex'>");
            if (currentIndex > 0) {
                out.println("<button type='submit' name='action' value='back' class='btn btn-outline-secondary'>Back</button>");
            }
            if (currentIndex < questionIds.size() - 1) {
                out.println("<button type='submit' name='action' value='next' class='btn btn-primary ms-2'>Next</button>");
            } else {
                out.println("<button type='submit' name='action' value='finish' class='btn btn-success ms-2'>Finish</button>");
            }
            out.println("</div>");

            out.println("</form>");
            out.println("</div>");
            out.println("</div>"); 
            out.println("</div>"); 

            out.println("<script>");
            out.println("const endTime=" + endTime + ";");
            out.println("function updateTimer(){");
            out.println("let rem=endTime-Date.now();");
            out.println("if(rem<=0){document.getElementById('timer').innerText='00:00'; window.location='submitQuiz'; return;}");
            out.println("let m=Math.floor(rem/60000);");
            out.println("let s=Math.floor((rem%60000)/1000);");
            out.println("document.getElementById('timer').innerText=(m<10?'0':'')+m+':' + (s<10?'0':'')+s;");
            out.println("}");
            out.println("updateTimer(); setInterval(updateTimer,1000);");
            out.println("</script>");

            out.println("</body></html>");


        } catch (SQLException e) {
            resp.getWriter().println("DB Error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("userId") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        List<Integer> questionIds = (List<Integer>) session.getAttribute("questionIds");
        Map<Integer, String> answers = (Map<Integer, String>) session.getAttribute("answers");
        Integer currentIndex = (Integer) session.getAttribute("currentIndex");

        if (questionIds == null || currentIndex == null) {
            resp.sendRedirect("question");
            return;
        }

        String action = req.getParameter("action");
        String answer = req.getParameter("answer");

        if ("back".equals(action)) {
            if (answer != null) answers.put(questionIds.get(currentIndex), answer);
            session.setAttribute("answers", answers);
            session.setAttribute("currentIndex", Math.max(0, currentIndex - 1));
            resp.sendRedirect("question");
            return;
        }

        if (answer == null || answer.isEmpty()) {
            session.setAttribute("error", "Please choose an option before proceeding.");
            resp.sendRedirect("question");
            return;
        }

        answers.put(questionIds.get(currentIndex), answer);
        session.setAttribute("answers", answers);

        if ("next".equals(action)) {
            session.setAttribute("currentIndex", Math.min(questionIds.size() - 1, currentIndex + 1));
            resp.sendRedirect("question");
        } else if ("finish".equals(action)) {
            session.setAttribute("currentIndex", questionIds.size());
            resp.sendRedirect("submitQuiz");
        } else {
            resp.sendRedirect("question");
        }
    }
}
