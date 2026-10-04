<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Dashboard Overview" scope="request"/>
<c:set var="activePage" value="dashboard" scope="request"/>
<%@ include file="common/header.jsp" %>
<%@ include file="common/sidebar.jsp" %>

<style>
    .bento-grid {
        display: grid;
        grid-template-columns: repeat(12, 1fr);
        gap: 20px;
        margin-bottom: 30px;
    }
    
    .bento-item {
        background: var(--bg-card);
        border: 1px solid var(--border-color);
        border-radius: 20px;
        padding: 24px;
        display: flex;
        flex-direction: column;
        transition: all 0.3s ease;
        position: relative;
        overflow: hidden;
    }
    .bento-item:hover {
        border-color: rgba(255, 255, 255, 0.12);
        box-shadow: var(--shadow-md);
    }
    
    .bento-item.col-span-12 { grid-column: span 12; }
    .bento-item.col-span-8 { grid-column: span 8; }
    .bento-item.col-span-7 { grid-column: span 7; }
    .bento-item.col-span-6 { grid-column: span 6; }
    .bento-item.col-span-5 { grid-column: span 5; }
    .bento-item.col-span-4 { grid-column: span 4; }
    .bento-item.col-span-3 { grid-column: span 3; }
    
    @media (max-width: 1200px) {
        .bento-item.col-span-8, .bento-item.col-span-4, .bento-item.col-span-5, .bento-item.col-span-7 {
            grid-column: span 12;
        }
    }

    .hero-banner {
        background: var(--gradient-hero);
        border: none;
        box-shadow: var(--shadow-glow);
    }
    
    .hero-title {
        font-size: 26px;
        font-weight: 900;
        color: #ffffff;
        margin-bottom: 10px;
        letter-spacing: -0.5px;
    }
    
    .bento-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }
    
    .bento-title {
        font-size: 15px;
        font-weight: 700;
        color: var(--text-main);
        display: flex;
        align-items: center;
        gap: 10px;
    }
    
    .stat-number {
        font-size: 36px;
        font-weight: 800;
        line-height: 1;
        margin-bottom: 8px;
        letter-spacing: -1px;
    }
    
    .custom-scroll::-webkit-scrollbar { width: 4px; }
    .custom-scroll::-webkit-scrollbar-track { background: transparent; }
    .custom-scroll::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 4px; }
    .custom-scroll:hover::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.2); }
</style>

<div class="animate-in">
    <!-- Top Bar / Real Time -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 16px;">
        <h1 style="font-size: 24px; font-weight: 900; letter-spacing: -0.5px; color: var(--text-main);">Dashboard Overview</h1>
        
        <div style="display: flex; gap: 16px; align-items: center; background: var(--success-bg); padding: 10px 20px; border-radius: 20px; border: 1px solid rgba(16,185,129,0.2); font-size: 12px; font-weight: 700;">
            <div style="display: flex; align-items: center; gap: 8px; color: var(--success);">
                <span class="pulse-dot"></span>
                System Active
            </div>
            <div style="width: 1px; height: 14px; background: rgba(16,185,129,0.3);"></div>
            <div style="color: var(--text-muted);">Live Data</div>
        </div>
    </div>

    <div class="bento-grid">
        <!-- 1. Hero Banner (Span 8) -->
        <div class="bento-item col-span-8 hero-banner" style="justify-content: center;">
            <div style="display: flex; justify-content: space-between; align-items: center; height: 100%;">
                <div style="z-index: 2;">
                    <h2 class="hero-title">Welcome back, <c:out value="${sessionScope.userName != null ? sessionScope.userName : 'Pet Owner'}"/>! ✨</h2>
                    <p style="color: rgba(255,255,255,0.75); font-size: 14px; max-width: 480px; margin-bottom: 24px; line-height: 1.6;">
                        Your companion care hub is live. <strong style="color: var(--accent-sky);">${stats.upcomingVaccinations != null ? stats.upcomingVaccinations : 0}</strong> upcoming vaccines and <strong style="color: var(--accent-rose);">${stats.overdueVaccinations != null ? stats.overdueVaccinations : 0}</strong> overdue alerts need attention.
                    </p>
                    <div style="display: flex; gap: 12px; flex-wrap: wrap;">
                        <a href="${pageContext.request.contextPath}/pets?action=new" class="btn" style="background: rgba(255,255,255,0.15); border: 1px solid rgba(255,255,255,0.3); color: #fff; border-radius: 12px; padding: 10px 20px;">
                            <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path d="M12 5v14m-7-7h14"/></svg>
                            Add Pet
                        </a>
                        <a href="${pageContext.request.contextPath}/vaccinations?action=new" class="btn" style="background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: rgba(255,255,255,0.85); border-radius: 12px; padding: 10px 20px;">Log Vaccine</a>
                        <a href="${pageContext.request.contextPath}/xml-management" class="btn" style="background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1); color: rgba(255,255,255,0.85); border-radius: 12px; padding: 10px 20px;">Data Backup &rarr;</a>
                    </div>
                </div>
                <div style="opacity: 0.2; position: absolute; right: -20px; bottom: -20px; z-index: 1;">
                    <svg width="180" height="180" viewBox="0 0 24 24" fill="none" stroke="var(--primary)" stroke-width="0.4" style="filter: drop-shadow(0 0 30px rgba(6,182,212,0.4));">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                    </svg>
                </div>
            </div>
        </div>

        <!-- 2. Quick Stats (Span 4, internal 2x2 grid) -->
        <div class="bento-item col-span-4" style="padding: 20px; background: transparent; border: none;">
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 14px; height: 100%;">
                <div style="background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 16px; padding: 20px; display: flex; flex-direction: column; justify-content: center;">
                    <div style="color: var(--primary); font-size: 11px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.8px; margin-bottom: 8px;">Total Pets</div>
                    <div class="stat-number counter-animate" style="color: var(--primary);" data-count="${stats.totalPets != null ? stats.totalPets : 0}">${stats.totalPets != null ? stats.totalPets : 0}</div>
                </div>
                <div style="background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 16px; padding: 20px; display: flex; flex-direction: column; justify-content: center;">
                    <div style="color: var(--warning); font-size: 11px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.8px; margin-bottom: 8px;">Upcoming</div>
                    <div class="stat-number counter-animate" style="color: var(--warning);" data-count="${stats.upcomingVaccinations != null ? stats.upcomingVaccinations : 0}">${stats.upcomingVaccinations != null ? stats.upcomingVaccinations : 0}</div>
                </div>
                <div style="background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 16px; padding: 20px; display: flex; flex-direction: column; justify-content: center;">
                    <div style="color: var(--danger); font-size: 11px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.8px; margin-bottom: 8px;">Overdue</div>
                    <div class="stat-number counter-animate" style="color: var(--danger);" data-count="${stats.overdueVaccinations != null ? stats.overdueVaccinations : 0}">${stats.overdueVaccinations != null ? stats.overdueVaccinations : 0}</div>
                </div>
                <div style="background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 16px; padding: 20px; display: flex; flex-direction: column; justify-content: center;">
                    <div style="color: var(--info); font-size: 11px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.8px; margin-bottom: 8px;">Appts</div>
                    <div class="stat-number counter-animate" style="color: var(--info);" data-count="${stats.upcomingAppointments != null ? stats.upcomingAppointments : 0}">${stats.upcomingAppointments != null ? stats.upcomingAppointments : 0}</div>
                </div>
            </div>
        </div>

        <!-- 3. Pets Showcase (Span 5) -->
        <div class="bento-item col-span-5" style="padding: 24px; min-height: 400px;">
            <div class="bento-header">
                <div class="bento-title">
                    <span style="width: 10px; height: 10px; border-radius: 50%; background: var(--primary); box-shadow: 0 0 10px var(--primary-glow);"></span>
                    My Companions
                </div>
                <a href="${pageContext.request.contextPath}/pets" style="font-size: 13px; color: var(--primary); text-decoration: none; font-weight: 600; padding: 4px 10px; background: var(--primary-light); border-radius: 20px;">Manage All</a>
            </div>
            
            <div class="custom-scroll" style="display: flex; flex-direction: column; gap: 12px; overflow-y: auto; flex: 1; padding-right: 8px; margin-bottom: 16px;">
                <c:forEach var="p" items="${pets}">
                    <a href="${pageContext.request.contextPath}/pets?action=view&id=${p.id}" style="display: flex; align-items: center; gap: 16px; padding: 12px 16px; background: rgba(255,255,255,0.02); border: 1px solid var(--border-color); border-radius: 14px; text-decoration: none; transition: all 0.2s;">
                        <img src="${p.photoUrl != null ? p.photoUrl : 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=100&q=80'}" alt="${p.name}" style="width: 54px; height: 54px; border-radius: 12px; object-fit: cover; box-shadow: 0 4px 10px rgba(0,0,0,0.2);">
                        <div style="flex: 1;">
                            <div style="font-size: 15px; font-weight: 700; color: var(--text-main); margin-bottom: 2px;"><c:out value="${p.name}"/></div>
                            <div style="font-size: 12px; color: var(--text-muted);"><c:out value="${p.species}"/> &bull; <c:out value="${p.breed}"/></div>
                        </div>
                        <svg width="18" height="18" fill="none" stroke="var(--text-muted)" stroke-width="2" viewBox="0 0 24 24"><path d="M9 18l6-6-6-6"/></svg>
                    </a>
                </c:forEach>
                <c:if test="${empty pets}">
                    <div style="text-align: center; color: var(--text-muted); padding: 50px 0; font-size: 13px;">
                        <svg width="32" height="32" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24" style="margin: 0 auto 12px; display: block; opacity: 0.3;"><path d="M12 5v14m-7-7h14"/></svg>
                        No pets registered yet.
                    </div>
                </c:if>
            </div>
            <a href="${pageContext.request.contextPath}/pets?action=new" style="display: flex; justify-content: center; align-items: center; gap: 8px; padding: 16px; border: 1px dashed var(--border-color); border-radius: 14px; background: rgba(255,255,255,0.01); color: var(--text-secondary); text-decoration: none; font-size: 13px; font-weight: 600; margin-top: auto; transition: all 0.2s;">
                <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M12 5v14m-7-7h14"/></svg>
                Register New Companion
            </a>
        </div>

        <!-- 4. Schedule & Action Queue (Span 7) -->
        <div class="bento-item col-span-7" style="padding: 24px; min-height: 400px;">
            <div class="bento-header">
                <div class="bento-title">
                    <span style="width: 10px; height: 10px; border-radius: 50%; background: var(--accent-rose); box-shadow: 0 0 10px var(--accent-rose);"></span>
                    Action Queue &amp; Schedule
                </div>
                <div style="display: flex; gap: 8px;">
                    <c:if test="${not empty overdueVaccinations}">
                        <span class="badge badge-overdue" style="padding: 4px 12px;">${overdueVaccinations.size()} Overdue</span>
                    </c:if>
                </div>
            </div>
            
            <div class="table-responsive custom-scroll" style="height: calc(100% - 50px); overflow-y: auto; border: 1px solid var(--border-color); border-radius: 14px;">
                <table class="data-table" style="width: 100%; margin: 0;">
                    <thead style="position: sticky; top: 0; background: var(--bg-card); z-index: 10; box-shadow: 0 1px 0 var(--border-color);">
                        <tr>
                            <th style="padding: 16px 20px;">Companion</th>
                            <th style="padding: 16px 20px;">Details</th>
                            <th style="padding: 16px 20px;">Date</th>
                            <th style="padding: 16px 20px;">Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Overdue Vaccinations -->
                        <c:forEach var="v" items="${overdueVaccinations}">
                            <tr style="background: var(--danger-bg);">
                                <td><strong style="color: var(--text-main); font-size: 13px;"><c:out value="${v.petName}"/></strong></td>
                                <td><div style="font-size: 13px; color: var(--text-secondary);"><c:out value="${v.vaccineName}"/></div></td>
                                <td><strong style="color: var(--accent-rose); font-size: 13px;"><c:out value="${v.nextDueDate}"/></strong></td>
                                <td><span class="badge badge-overdue">Action Required</span></td>
                            </tr>
                        </c:forEach>
                        <!-- Upcoming Vaccinations -->
                        <c:forEach var="v" items="${upcomingVaccinations}">
                            <tr>
                                <td><strong style="color: var(--text-main); font-size: 13px;"><c:out value="${v.petName}"/></strong></td>
                                <td><div style="font-size: 13px; color: var(--text-secondary);"><c:out value="${v.vaccineName}"/></div></td>
                                <td><strong style="color: var(--text-main); font-size: 13px;"><c:out value="${v.nextDueDate}"/></strong></td>
                                <td><span class="badge badge-upcoming">Upcoming</span></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty overdueVaccinations and empty upcomingVaccinations}">
                            <tr>
                                <td colspan="4" style="text-align: center; color: var(--text-muted); padding: 60px 20px;">
                                    <svg width="32" height="32" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24" style="margin: 0 auto 12px; display: block; opacity: 0.3;"><path d="M5 13l4 4L19 7"/></svg>
                                    All schedules are clear. You're up to date!
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- 5. Upcoming Appointments (Span 4) -->
        <div class="bento-item col-span-4" style="padding: 24px;">
            <div class="bento-header">
                <div class="bento-title">
                    <span style="width: 10px; height: 10px; border-radius: 50%; background: var(--accent-cyan); box-shadow: 0 0 10px var(--accent-cyan);"></span>
                    Appointments
                </div>
            </div>
            <div class="custom-scroll" style="display: flex; flex-direction: column; gap: 12px; overflow-y: auto; max-height: 250px; padding-right: 8px;">
                <c:forEach var="a" items="${upcomingAppointments}">
                    <div style="padding: 16px; border-radius: 14px; background: rgba(255,255,255,0.02); border: 1px solid var(--border-color); border-left: 4px solid var(--accent-cyan); transition: background 0.2s;">
                        <div style="display: flex; justify-content: space-between; margin-bottom: 8px; align-items: flex-start;">
                            <strong style="font-size: 14px; color: var(--text-main); line-height: 1.3;"><c:out value="${a.title}"/></strong>
                            <span class="badge badge-secondary" style="font-size: 10px;"><c:out value="${a.appointmentType}"/></span>
                        </div>
                        <div style="font-size: 12px; color: var(--accent-cyan); font-weight: 600; display: flex; align-items: center; gap: 6px;">
                            <svg width="12" height="12" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>
                            <c:out value="${a.appointmentDate}"/>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${empty upcomingAppointments}">
                    <div style="color: var(--text-muted); font-size: 13px; text-align: center; padding: 30px 0;">No pending appointments.</div>
                </c:if>
            </div>
        </div>

        <!-- 6. Activity Feed (Span 4) -->
        <div class="bento-item col-span-4" style="padding: 24px;">
            <div class="bento-header">
                <div class="bento-title">
                    <span style="width: 10px; height: 10px; border-radius: 50%; background: var(--success); box-shadow: 0 0 10px var(--success);"></span>
                    Activity Feed
                </div>
            </div>
            <div class="custom-scroll" style="display: flex; flex-direction: column; gap: 18px; overflow-y: auto; max-height: 250px; padding-right: 8px;">
                <c:forEach var="act" items="${stats.recentActivities}" varStatus="loop">
                    <div style="display: flex; gap: 14px; align-items: flex-start;">
                        <div style="width: 10px; height: 10px; border-radius: 50%; background: var(--primary); margin-top: 5px; flex-shrink: 0; box-shadow: 0 0 8px var(--primary-glow);"></div>
                        <div style="font-size: 13px; color: var(--text-secondary); line-height: 1.6;">
                            <c:out value="${act}"/>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${empty stats.recentActivities}">
                    <div style="color: var(--text-muted); font-size: 13px; text-align: center; padding: 30px 0;">No recent activity.</div>
                </c:if>
            </div>
        </div>

        <!-- 7. Data & Backups (Span 4) -->
        <div class="bento-item col-span-4" style="padding: 24px; background: rgba(6, 182, 212, 0.03);">
            <div class="bento-header">
                <div class="bento-title">
                    <span style="width: 10px; height: 10px; border-radius: 50%; background: var(--primary); box-shadow: 0 0 10px var(--primary-glow);"></span>
                    Data &amp; Reports
                </div>
            </div>
            <div style="display: flex; flex-direction: column; gap: 12px; height: 100%; justify-content: center; margin-bottom: 20px;">
                <a href="${pageContext.request.contextPath}/xml-management?action=export-download&type=all" style="display: flex; align-items: center; gap: 14px; padding: 16px; background: rgba(255,255,255,0.03); border: 1px solid var(--border-color); border-radius: 14px; color: var(--text-main); font-size: 14px; font-weight: 600; text-decoration: none; transition: all 0.2s;">
                    <svg width="20" height="20" fill="none" stroke="var(--primary)" stroke-width="2" viewBox="0 0 24 24"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4m4-5l5 5 5-5m-5 5V3"/></svg>
                    Export All Data
                </a>
                <a href="${pageContext.request.contextPath}/xml-management" style="display: flex; align-items: center; justify-content: center; gap: 10px; padding: 16px; background: var(--gradient-primary); border-radius: 14px; color: #fff; font-size: 14px; font-weight: 600; text-decoration: none; margin-top: auto; transition: all 0.2s; box-shadow: 0 4px 15px var(--primary-glow);">
                    <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"/></svg>
                    Data Backup Studio
                </a>
            </div>
        </div>
    </div>
</div>

<style>
@keyframes livePulse {
    0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7); }
    70% { transform: scale(1.15); box-shadow: 0 0 0 8px rgba(16, 185, 129, 0); }
    100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
}
</style>

<script>
document.addEventListener("DOMContentLoaded", function() {
    // --- Counter Animation ---
    document.querySelectorAll('.counter-animate').forEach(function(el) {
        const target = parseInt(el.getAttribute('data-count')) || 0;
        let current = 0;
        const increment = Math.max(1, Math.ceil(target / 30));
        const timer = setInterval(function() {
            current += increment;
            if (current >= target) {
                current = target;
                clearInterval(timer);
            }
            el.textContent = current;
        }, 40);
    });

    // --- Auto-refresh data every 30 seconds ---
    setInterval(function() {
        fetch(window.APP_CONTEXT + '/dashboard?format=json')
            .catch(function() {});
    }, 30000);
});
</script>

<%@ include file="common/footer.jsp" %>
