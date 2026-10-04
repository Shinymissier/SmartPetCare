<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Account Settings" scope="request"/>
<c:set var="activePage" value="settings" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div class="settings-page">
    <!-- Page Header -->
    <div class="settings-hero">
        <div class="settings-hero-icon">
            <svg width="28" height="28" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/>
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
            </svg>
        </div>
        <div>
            <h1 class="settings-hero-title">Account Settings</h1>
            <p class="settings-hero-sub">Manage your profile, security and preferences</p>
        </div>
    </div>

    <div class="settings-grid">
        <!-- LEFT: Profile Card -->
        <div class="settings-card">
            <div class="settings-card-header">
                <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                </svg>
                Profile Information
            </div>
            <div class="settings-card-body">
                <!-- Avatar Preview -->
                <div class="avatar-section">
                    <img src="${user.avatarUrl != null && user.avatarUrl != '' ? user.avatarUrl : 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=200&q=80'}"
                         alt="Your Avatar" class="settings-avatar" id="avatarPreview">
                    <div class="avatar-meta">
                        <div class="avatar-name"><c:out value="${user.fullName}"/></div>
                        <div class="avatar-email"><c:out value="${user.email}"/></div>
                        <span class="badge badge-active">Active Account</span>
                    </div>
                </div>

                <hr class="settings-divider">

                <form action="${pageContext.request.contextPath}/settings" method="post">
                    <input type="hidden" name="action" value="update-profile">

                    <div class="form-group">
                        <label class="form-label">Full Name</label>
                        <input type="text" name="fullName" class="form-control" value="<c:out value='${user.fullName}'/>" required placeholder="Your full name">
                    </div>

                    <div class="form-group">
                        <label class="form-label">Email Address <span class="field-note">(cannot change)</span></label>
                        <input type="email" class="form-control field-locked" value="<c:out value='${user.email}'/>" disabled>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Phone Number</label>
                        <input type="text" name="phone" class="form-control" value="<c:out value='${user.phone}'/>" placeholder="+1 (555) 000-0000">
                    </div>

                    <div class="form-group">
                        <label class="form-label">Avatar Image URL</label>
                        <input type="url" name="avatarUrl" class="form-control" value="<c:out value='${user.avatarUrl}'/>" placeholder="https://..." id="avatarUrlInput">
                        <div class="field-hint">Paste a direct image link to update your profile photo</div>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block">
                        <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M5 13l4 4L19 7"/></svg>
                        Save Profile Changes
                    </button>
                </form>
            </div>
        </div>

        <!-- RIGHT: Security + System -->
        <div class="settings-col-right">
            <!-- Change Password -->
            <div class="settings-card">
                <div class="settings-card-header">
                    <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
                    </svg>
                    Security & Password
                </div>
                <div class="settings-card-body">
                    <form action="${pageContext.request.contextPath}/change-password" method="post">
                        <div class="form-group">
                            <label class="form-label">Current Password</label>
                            <input type="password" name="currentPassword" class="form-control" required placeholder="Enter current password">
                        </div>
                        <div class="form-group">
                            <label class="form-label">New Password</label>
                            <input type="password" name="newPassword" class="form-control" required placeholder="Min. 8 characters">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Confirm New Password</label>
                            <input type="password" name="confirmPassword" class="form-control" required placeholder="Repeat new password">
                        </div>
                        <button type="submit" class="btn btn-outline btn-block">Update Password</button>
                    </form>
                </div>
            </div>

            <!-- System Status -->
            <div class="settings-card">
                <div class="settings-card-header">
                    <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                    </svg>
                    System Status
                </div>
                <div class="settings-card-body">
                    <div class="status-row">
                        <span class="status-label">Database</span>
                        <span class="badge ${dbConnected ? 'badge-completed' : 'badge-overdue'}">
                            ${dbConnected ? '✓ Connected' : '✗ Offline'}
                        </span>
                    </div>
                    <div class="status-row">
                        <span class="status-label">Engine</span>
                        <span class="status-value"><c:out value="${dbInfo}"/></span>
                    </div>
                    <div class="status-row">
                        <span class="status-label">Mode</span>
                        <span class="status-value"><c:out value="${config.databaseMode}"/></span>
                    </div>
                    <div class="status-row">
                        <span class="status-label">Version</span>
                        <span class="status-value">Smart Pet Care v1.0</span>
                    </div>
                </div>
            </div>

            <!-- Danger Zone -->
            <div class="settings-card settings-card-danger">
                <div class="settings-card-header danger-header">
                    <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                    </svg>
                    Session
                </div>
                <div class="settings-card-body">
                    <p style="font-size:12.5px; color: var(--text-muted); margin-bottom: 14px;">Sign out from your current session on this device.</p>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-block">Sign Out</a>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
.settings-page { max-width: 1100px; }

.settings-hero {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 32px;
}

.settings-hero-icon {
    width: 52px; height: 52px;
    border-radius: 16px;
    background: var(--primary-light);
    color: var(--primary);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.settings-hero-title {
    font-size: 24px;
    font-weight: 800;
    color: var(--text-main);
    letter-spacing: -0.4px;
    margin-bottom: 4px;
}

.settings-hero-sub {
    font-size: 13.5px;
    color: var(--text-muted);
}

.settings-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
    align-items: start;
}

@media (max-width: 900px) {
    .settings-grid { grid-template-columns: 1fr; }
}

.settings-col-right {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.settings-card {
    background: var(--bg-card);
    border: 1px solid var(--bg-glass-border);
    border-radius: 18px;
    overflow: hidden;
    transition: all 0.2s;
}

.settings-card:hover {
    border-color: rgba(255,107,53,0.15);
}

.settings-card-danger {
    border-color: rgba(251,113,133,0.15);
}

.settings-card-header {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 16px 22px;
    border-bottom: 1px solid var(--border-color);
    font-size: 14px;
    font-weight: 700;
    color: var(--text-main);
}

.danger-header { color: var(--danger); }

.settings-card-body { padding: 22px; }

/* Avatar */
.avatar-section {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 20px;
}

.settings-avatar {
    width: 64px; height: 64px;
    border-radius: 16px;
    object-fit: cover;
    border: 3px solid var(--primary-light);
    flex-shrink: 0;
}

.avatar-name {
    font-size: 15px;
    font-weight: 700;
    color: var(--text-main);
    margin-bottom: 2px;
}

.avatar-email {
    font-size: 12px;
    color: var(--text-muted);
    margin-bottom: 6px;
}

.settings-divider {
    border: none;
    border-top: 1px solid var(--border-dark);
    margin: 0 0 20px;
}

.field-note {
    font-weight: 400;
    color: var(--text-light);
    font-size: 11px;
}

.field-locked {
    opacity: 0.5;
    cursor: not-allowed;
}

.field-hint {
    font-size: 11px;
    color: var(--text-light);
    margin-top: 5px;
}

.btn-block {
    width: 100%;
    justify-content: center;
    margin-top: 4px;
}

/* Status rows */
.status-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px solid var(--border-dark);
}

.status-row:last-child { border-bottom: none; }

.status-label {
    font-size: 12.5px;
    color: var(--text-muted);
    font-weight: 500;
}

.status-value {
    font-size: 12.5px;
    color: var(--text-secondary);
    font-weight: 600;
    text-align: right;
    max-width: 200px;
    word-break: break-word;
}
</style>

<script>
// Live avatar preview
const avatarInput = document.getElementById('avatarUrlInput');
const avatarPreview = document.getElementById('avatarPreview');
if (avatarInput && avatarPreview) {
    avatarInput.addEventListener('input', () => {
        if (avatarInput.value) avatarPreview.src = avatarInput.value;
    });
}
</script>

<%@ include file="../common/footer.jsp" %>
