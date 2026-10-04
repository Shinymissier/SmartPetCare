<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- TOP NAVIGATION BAR — replaces left sidebar --%>
<nav class="app-sidebar">
    <%-- Brand --%>
    <div class="sidebar-brand">
        <div class="brand-icon">🐾</div>
        <div class="brand-title">Smart Pet Care</div>
    </div>

    <%-- Horizontal Navigation Items --%>
    <ul class="sidebar-nav">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link ${activePage == 'dashboard' ? 'active' : ''}">
                <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/></svg>
                <span>Dashboard</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/pets" class="nav-link ${activePage == 'pets' ? 'active' : ''}">
                <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/></svg>
                <span>My Pets</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/vaccinations" class="nav-link ${activePage == 'vaccinations' ? 'active' : ''}">
                <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/></svg>
                <span>Vaccines</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/medical-records" class="nav-link ${activePage == 'medical' ? 'active' : ''}">
                <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"/></svg>
                <span>Medical</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/feeding" class="nav-link ${activePage == 'feeding' ? 'active' : ''}">
                <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
                <span>Feeding</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/appointments" class="nav-link ${activePage == 'appointments' ? 'active' : ''}">
                <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/></svg>
                <span>Appointments</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/reminders" class="nav-link ${activePage == 'reminders' ? 'active' : ''}">
                <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/></svg>
                <span>Reminders</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/xml-management" class="nav-link ${activePage == 'xml' ? 'active' : ''}">
                <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4"/></svg>
                <span>XML</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/reports" class="nav-link ${activePage == 'reports' ? 'active' : ''}">
                <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/></svg>
                <span>Reports</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/settings" class="nav-link ${activePage == 'settings' ? 'active' : ''}">
                <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/></svg>
                <span>Settings</span>
            </a>
        </li>
    </ul>

    <%-- Right: User + Actions --%>
    <div style="display:flex; align-items:center; gap:8px; flex-shrink:0; margin-left:auto;">
        <a href="${pageContext.request.contextPath}/pets?action=new" class="btn btn-primary btn-sm" style="border-radius:20px; padding:7px 16px;">
            + Add Pet
        </a>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-ghost btn-sm" title="Logout" style="padding:7px 10px; border-radius:20px; color:rgba(200,200,240,0.5);">
            <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/></svg>
        </a>
        <div class="sidebar-user">
            <img src="${sessionScope.currentUser.avatarUrl != null ? sessionScope.currentUser.avatarUrl : 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=200&q=80'}" alt="User" class="sidebar-avatar">
            <div class="user-info">
                <div class="user-name">${sessionScope.currentUser.fullName}</div>
            </div>
        </div>
    </div>
</nav>

<div class="app-main">
    <header class="app-topbar">
        <div class="page-header-title">
            <span style="color:var(--text-light);">Smart Pet Care</span>
            <span style="margin:0 8px; color:var(--text-light);">/</span>
            ${pageTitle != null ? pageTitle : "Dashboard"}
        </div>
        <div class="topbar-actions">
            <div class="live-clock" id="liveClock">
                <span class="pulse-dot" style="width:6px;height:6px;"></span>
                <span id="clockDisplay">--:--:--</span>
            </div>
        </div>
    </header>
    <main class="page-content">
        <%@ include file="alerts.jsp" %>
