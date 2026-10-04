package com.petcare.servlet;

import com.petcare.dao.UserDAO;
import com.petcare.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/register", "/logout", "/forgot-password", "/change-password"})
public class AuthServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/login?message=You+have+been+logged+out+successfully");
            return;
        }

        // If already logged in and visiting login or register, redirect to dashboard
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null && ("/login".equals(path) || "/register".equals(path))) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        switch (path) {
            case "/register":
                req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
                break;
            case "/forgot-password":
                req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
                break;
            case "/login":
            default:
                req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/register".equals(path)) {
            handleRegister(req, resp);
        } else if ("/forgot-password".equals(path)) {
            handleForgotPassword(req, resp);
        } else if ("/change-password".equals(path)) {
            handleChangePassword(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String redirect = req.getParameter("redirect");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Please provide both email and password.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        User user = userDAO.authenticate(email, password);
        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getFullName());

            if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("login") && !redirect.contains("logout")) {
                resp.sendRedirect(req.getContextPath() + redirect);
            } else {
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            }
        } else {
            req.setAttribute("errorMessage", "Invalid email or password. For demo, use: demo@petcare.com / admin123");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        if (fullName == null || fullName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Full Name, Email, and Password are required.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("errorMessage", "Passwords do not match.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        if (userDAO.findByEmail(email) != null) {
            req.setAttribute("errorMessage", "An account with this email already exists.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        User newUser = new User();
        newUser.setFullName(fullName.trim());
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setPhone(phone != null ? phone.trim() : "");
        newUser.setAvatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80");

        boolean created = userDAO.register(newUser, password);
        if (created) {
            User registered = userDAO.findByEmail(email);
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", registered);
            session.setAttribute("userId", registered.getId());
            session.setAttribute("userName", registered.getFullName());
            resp.sendRedirect(req.getContextPath() + "/dashboard?welcome=true");
        } else {
            req.setAttribute("errorMessage", "Registration failed. Please check database connectivity.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }

    private void handleForgotPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String newPassword = req.getParameter("newPassword");

        if (email == null || email.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Please provide your email and new password.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        User user = userDAO.findByEmail(email);
        if (user == null) {
            req.setAttribute("errorMessage", "No account found with this email.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        boolean updated = userDAO.updatePassword(user.getId(), newPassword);
        if (updated) {
            resp.sendRedirect(req.getContextPath() + "/login?message=Password+has+been+reset+successfully.+Please+log+in.");
        } else {
            req.setAttribute("errorMessage", "Failed to reset password. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
        }
    }

    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String currentPass = req.getParameter("currentPassword");
        String newPass = req.getParameter("newPassword");

        if (userDAO.authenticate(currentUser.getEmail(), currentPass) == null) {
            resp.sendRedirect(req.getContextPath() + "/settings?error=Current+password+is+incorrect");
            return;
        }

        boolean ok = userDAO.updatePassword(currentUser.getId(), newPass);
        if (ok) {
            resp.sendRedirect(req.getContextPath() + "/settings?message=Password+changed+successfully");
        } else {
            resp.sendRedirect(req.getContextPath() + "/settings?error=Failed+to+update+password");
        }
    }
}
