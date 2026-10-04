<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password - Smart Pet Care &amp; Vaccination Tracker</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .auth-wrapper {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: #f8fafc;
            padding: 24px;
        }
        .auth-card {
            width: 100%;
            max-width: 420px;
            background: #ffffff;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 32px;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
        }
        .auth-brand {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 24px;
        }
    </style>
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-card">
        <div class="auth-brand">
            <div class="brand-icon">P</div>
            <div>
                <h2 style="font-size: 18px; font-weight: 700; color: #0f172a;">Smart Pet Care</h2>
                <p style="font-size: 12px; color: #64748b;">Reset Account Password</p>
            </div>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/forgot-password" method="post">
            <div class="form-group">
                <label class="form-label">Registered Email</label>
                <input type="email" name="email" class="form-control" placeholder="name@example.com" required autofocus>
            </div>
            <div class="form-group">
                <label class="form-label">New Password</label>
                <input type="password" name="newPassword" class="form-control" placeholder="••••••••" required>
            </div>
            <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 8px;">Reset Password</button>
        </form>

        <div style="text-align: center; margin-top: 24px; font-size: 13px; color: #64748b;">
            Remembered your password? <a href="${pageContext.request.contextPath}/login" style="color: var(--primary); font-weight: 600; text-decoration: none;">Back to Sign In</a>
        </div>
    </div>
</div>
</body>
</html>
