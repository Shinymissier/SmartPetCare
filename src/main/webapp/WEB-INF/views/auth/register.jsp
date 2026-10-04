<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register – Smart Pet Care</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Inter', sans-serif;
            background: #0d1117;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 32px 24px;
        }

        .auth-card {
            width: 100%;
            max-width: 480px;
            background: #161b27;
            border: 1px solid rgba(255,255,255,0.08);
            border-radius: 16px;
            padding: 36px 32px;
            box-shadow: 0 24px 64px rgba(0,0,0,0.5);
        }

        .brand {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 28px;
        }
        .brand-icon {
            width: 42px; height: 42px;
            background: linear-gradient(135deg, #3b82f6, #6366f1);
            border-radius: 10px;
            display: flex; align-items: center; justify-content: center;
            font-size: 18px; font-weight: 800; color: #fff;
        }
        .brand-name { font-size: 17px; font-weight: 700; color: #f1f5f9; }
        .brand-sub  { font-size: 11px; color: #64748b; margin-top: 2px; }

        .alert-danger {
            background: rgba(239,68,68,0.12);
            border: 1px solid rgba(239,68,68,0.3);
            color: #fca5a5;
            padding: 10px 14px;
            border-radius: 8px;
            font-size: 13px;
            margin-bottom: 16px;
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 14px;
        }

        .form-group { margin-bottom: 16px; }
        .form-label {
            display: block;
            font-size: 12px;
            font-weight: 600;
            color: #94a3b8;
            margin-bottom: 6px;
            letter-spacing: 0.3px;
        }
        .form-control {
            width: 100%;
            background: #0d1117;
            border: 1px solid rgba(255,255,255,0.1);
            border-radius: 8px;
            padding: 10px 14px;
            font-size: 13px;
            color: #e2e8f0;
            font-family: 'Inter', sans-serif;
            outline: none;
            transition: border-color 0.2s;
        }
        .form-control:focus { border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59,130,246,0.12); }
        .form-control::placeholder { color: #475569; }

        .security-note {
            font-size: 11px;
            color: #475569;
            margin-top: 4px;
            margin-bottom: 20px;
            line-height: 1.6;
        }

        .btn-submit {
            width: 100%;
            background: linear-gradient(135deg, #3b82f6, #6366f1);
            color: #fff;
            border: none;
            border-radius: 8px;
            padding: 11px;
            font-size: 14px;
            font-weight: 700;
            font-family: 'Inter', sans-serif;
            cursor: pointer;
            transition: opacity 0.2s, transform 0.1s;
        }
        .btn-submit:hover { opacity: 0.92; transform: translateY(-1px); }
        .btn-submit:active { transform: translateY(0); }

        .login-link { text-align: center; margin-top: 20px; font-size: 13px; color: #64748b; }
        .login-link a { color: #3b82f6; font-weight: 600; text-decoration: none; }
        .login-link a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="auth-card">
    <div class="brand">
        <div class="brand-icon">P</div>
        <div>
            <div class="brand-name">Smart Pet Care</div>
            <div class="brand-sub">Create Your Pet Owner Account</div>
        </div>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert-danger">${errorMessage}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/register" method="post">
        <div class="form-group">
            <label class="form-label">Full Name</label>
            <input type="text" name="fullName" class="form-control"
                   placeholder="Your full name" required>
        </div>
        <div class="form-group">
            <label class="form-label">Email Address</label>
            <input type="email" name="email" class="form-control"
                   placeholder="name@example.com" required>
        </div>
        <div class="form-group">
            <label class="form-label">Phone Number <span style="color:#475569;font-weight:400;">(optional)</span></label>
            <input type="text" name="phone" class="form-control"
                   placeholder="+1 (555) 000-0000">
        </div>
        <div class="form-row">
            <div class="form-group" style="margin-bottom:0;">
                <label class="form-label">Password</label>
                <input type="password" name="password" class="form-control"
                       placeholder="At least 8 characters" required>
            </div>
            <div class="form-group" style="margin-bottom:0;">
                <label class="form-label">Confirm Password</label>
                <input type="password" name="confirmPassword" class="form-control"
                       placeholder="Confirm your password" required>
            </div>
        </div>
        <p class="security-note">
            🔒 Your name, email, and phone are saved to the database. Passwords are stored securely as hashed values.
        </p>
        <button type="submit" class="btn-submit">Create Account</button>
    </form>

    <div class="login-link">
        Already have an account? <a href="${pageContext.request.contextPath}/login">Sign In</a>
    </div>
</div>
</body>
</html>
