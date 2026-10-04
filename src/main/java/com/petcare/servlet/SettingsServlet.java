package com.petcare.servlet;

import com.petcare.config.AppConfig;
import com.petcare.config.DatabaseConnection;
import com.petcare.config.SupabaseRestClient;
import com.petcare.dao.UserDAO;
import com.petcare.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/settings")
public class SettingsServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        AppConfig config = AppConfig.getInstance();
        req.setAttribute("config", config);
        req.setAttribute("user", user);

        // Check live connection status
        boolean dbConnected = false;
        String dbInfo = "";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                dbConnected = true;
                dbInfo = conn.getMetaData().getDatabaseProductName() + " " + conn.getMetaData().getDatabaseProductVersion();
            }
        } catch (Exception e) {
            dbInfo = "Connection error: " + e.getMessage();
        }

        req.setAttribute("dbConnected", dbConnected);
        req.setAttribute("dbInfo", dbInfo);
        req.setAttribute("isSupabaseConfigured", config.isSupabaseConfigured());

        req.getRequestDispatcher("/WEB-INF/views/settings/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");

        if ("test-supabase".equalsIgnoreCase(action)) {
            boolean ok = SupabaseRestClient.testConnection();
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/settings?message=Supabase+REST+API+connection+successful!");
            } else {
                resp.sendRedirect(req.getContextPath() + "/settings?error=Supabase+REST+connection+failed.+Verify+URL+and+Anon+Key.");
            }
            return;
        }

        if ("update-profile".equalsIgnoreCase(action)) {
            String fullName = req.getParameter("fullName");
            String phone = req.getParameter("phone");
            String avatarUrl = req.getParameter("avatarUrl");

            user.setFullName(fullName);
            user.setPhone(phone);
            if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                user.setAvatarUrl(avatarUrl.trim());
            }

            if (userDAO.updateProfile(user)) {
                session.setAttribute("currentUser", user);
                session.setAttribute("userName", user.getFullName());
                resp.sendRedirect(req.getContextPath() + "/settings?message=Profile+updated+successfully");
            } else {
                resp.sendRedirect(req.getContextPath() + "/settings?error=Failed+to+update+profile");
            }
            return;
        }

        if ("update-supabase".equalsIgnoreCase(action)) {
            String dbMode = req.getParameter("dbMode");
            String supabaseUrl = req.getParameter("supabaseUrl");
            String anonKey = req.getParameter("anonKey");
            String jdbcUrl = req.getParameter("jdbcUrl");
            String jdbcUser = req.getParameter("jdbcUser");
            String jdbcPass = req.getParameter("jdbcPassword");

            AppConfig config = AppConfig.getInstance();
            config.updateRuntimeConfig(dbMode, supabaseUrl, anonKey, jdbcUrl, jdbcUser, jdbcPass);

            resp.sendRedirect(req.getContextPath() + "/settings?message=Database+settings+updated+successfully");
        }
    }
}
