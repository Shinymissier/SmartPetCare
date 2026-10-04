<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In – Smart Pet Care</title>
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
            padding: 24px;
        }

        .auth-card {
            width: 100%;
            max-width: 420px;
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

        .alert {
            padding: 10px 14px;
            border-radius: 8px;
            font-size: 13px;
            margin-bottom: 16px;
        }
        .alert-danger  { background: rgba(239,68,68,0.12); border: 1px solid rgba(239,68,68,0.3); color: #fca5a5; }
        .alert-success { background: rgba(52,211,153,0.10); border: 1px solid rgba(52,211,153,0.25); color: #6ee7b7; }

        .demo-box {
            background: rgba(59,130,246,0.08);
            border: 1px solid rgba(59,130,246,0.2);
            border-radius: 8px;
            padding: 12px 14px;
            margin-bottom: 20px;
            font-size: 12px;
            color: #93c5fd;
            line-height: 1.7;
        }
        .demo-box strong { color: #bfdbfe; }
        .demo-box code { background: rgba(255,255,255,0.08); border-radius: 4px; padding: 1px 5px; font-family: monospace; }

        .form-group { margin-bottom: 16px; }
        .form-label { display: block; font-size: 12px; font-weight: 600; color: #94a3b8; margin-bottom: 6px; letter-spacing: 0.3px; }
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

        .label-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
        .forgot-link { font-size: 12px; color: #3b82f6; text-decoration: none; }
        .forgot-link:hover { text-decoration: underline; }

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
            margin-top: 6px;
            transition: opacity 0.2s, transform 0.1s;
        }
        .btn-submit:hover { opacity: 0.92; transform: translateY(-1px); }
        .btn-submit:active { transform: translateY(0); }

        .register-link { text-align: center; margin-top: 20px; font-size: 13px; color: #64748b; }
        .register-link a { color: #3b82f6; font-weight: 600; text-decoration: none; }
        .register-link a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="auth-card">
    <div class="brand">
        <div class="brand-icon">P</div>
        <div>
            <div class="brand-name">Smart Pet Care</div>
            <div class="brand-sub">Vaccination &amp; XML Tracker</div>
        </div>
    </div>

    <c:if test="${not empty param.message}">
        <div class="alert alert-success">${param.message}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
    </c:if>

    <div class="demo-box" style="display: flex; justify-content: space-between; align-items: center;">
        <div>
            <strong>Demo Account:</strong> <code>demo@petcare.com</code> / <code>admin123</code>
        </div>
        <button type="button" onclick="fillDemo()" style="background: rgba(59,130,246,0.25); border: 1px solid #3b82f6; color: #93c5fd; border-radius: 6px; padding: 4px 8px; font-size: 11px; cursor: pointer;">Fill Demo</button>
    </div>

    <form action="${pageContext.request.contextPath}/login" method="post" id="loginForm">
        <input type="hidden" name="redirect" value="${param.redirect}">
        <div class="form-group">
            <label class="form-label" for="emailInput">Email Address</label>
            <input type="email" id="emailInput" name="email" class="form-control"
                   placeholder="name@example.com"
                   value="${email != null ? email : ''}"
                   required autofocus autocomplete="email">
        </div>
        <div class="form-group">
            <div class="label-row">
                <label class="form-label" for="passwordInput" style="margin-bottom:0;">Password</label>
                <a href="${pageContext.request.contextPath}/forgot-password" class="forgot-link">Forgot password?</a>
            </div>
            <input type="password" id="passwordInput" name="password" class="form-control"
                   placeholder="••••••••" value="" required autocomplete="current-password">
        </div>
        <button type="submit" class="btn-submit">Sign In</button>
    </form>

    <div class="register-link">
        Don't have an account? <a href="${pageContext.request.contextPath}/register">Create Account</a>
    </div>
</div>

<script>
function fillDemo() {
    document.getElementById('emailInput').value = 'demo@petcare.com';
    document.getElementById('passwordInput').value = 'admin123';
}
</script>
</body>
</html>
