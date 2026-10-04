package com.petcare.filter;

import com.petcare.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final String[] PUBLIC_PREFIXES = {
        "/login",
        "/register",
        "/forgot-password",
        "/auth",
        "/assets/",
        "/index.jsp",
        "/favicon.ico"
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Force UTF-8 encoding
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");

        String contextPath = req.getContextPath();
        String path = req.getRequestURI().substring(contextPath.length());

        // Allow root path redirection
        if (path.equals("/") || path.isEmpty()) {
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("currentUser") != null) {
                res.sendRedirect(contextPath + "/dashboard");
            } else {
                res.sendRedirect(contextPath + "/login");
            }
            return;
        }

        // Check if path is public
        boolean isPublic = false;
        for (String prefix : PUBLIC_PREFIXES) {
            if (path.startsWith(prefix)) {
                isPublic = true;
                break;
            }
        }

        if (isPublic) {
            chain.doFilter(request, response);
            return;
        }

        // Verify session for protected routes
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            res.sendRedirect(contextPath + "/login?message=Please+log+in+to+continue&redirect=" + path);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
