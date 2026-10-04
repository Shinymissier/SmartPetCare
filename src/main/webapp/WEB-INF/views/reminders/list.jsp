<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Reminders" scope="request"/>
<c:set var="activePage" value="reminders" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
    <div>
        <h2 style="font-size: 20px; font-weight: 700; color: var(--text-main);">Care &amp; Medication Reminders</h2>
        <p style="font-size: 13px; color: var(--text-muted);">Set task alerts for monthly deworming, heartworm chews, claw trimming, and clinic bookings.</p>
    </div>
</div>

<div style="display: grid; grid-template-columns: 2fr 1fr; gap: 24px;">
    <!-- Reminders List -->
    <div class="card">
        <div class="card-header">
            <h3 class="card-title">Pending &amp; Completed Reminders</h3>
        </div>
        <div class="card-body" style="padding: 0;">
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th style="width: 40px;">Done</th>
                            <th>Reminder Title</th>
                            <th>Pet</th>
                            <th>Target Date</th>
                            <th>Category</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="r" items="${reminders}">
                            <tr style="${r.completed ? 'opacity: 0.6;' : ''}">
                                <td>
                                    <form action="${pageContext.request.contextPath}/reminders" method="post" style="display: inline;">
                                        <input type="hidden" name="action" value="toggle">
                                        <input type="hidden" name="id" value="${r.id}">
                                        <input type="checkbox" onchange="this.form.submit()" ${r.completed ? 'checked' : ''} style="cursor: pointer;">
                                    </form>
                                </td>
                                <td>
                                    <span style="${r.completed ? 'text-decoration: line-through;' : 'font-weight: 600;'}">
                                        <c:out value="${r.title}"/>
                                    </span>
                                </td>
                                <td><c:out value="${r.petName != null ? r.petName : 'All Pets'}"/></td>
                                <td><c:out value="${r.reminderDate}"/></td>
                                <td><span class="badge badge-secondary"><c:out value="${r.reminderType}"/></span></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/reminders" method="post" style="display:inline;" onsubmit="return confirm('Delete reminder?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${r.id}">
                                        <button type="submit" class="btn btn-danger btn-sm">&times;</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty reminders}">
                            <tr><td colspan="6" style="text-align: center; color: var(--text-muted); padding: 32px;">No active reminders. Add one using the form on the right!</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Create Reminder Form -->
    <div class="card">
        <div class="card-header">
            <h3 class="card-title">+ Add Reminder</h3>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/reminders" method="post">
                <div class="form-group">
                    <label class="form-label">Reminder Title</label>
                    <input type="text" name="title" class="form-control" placeholder="e.g. Monthly NexGard chewable" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Companion (Optional)</label>
                    <select name="petId" class="form-control">
                        <option value="">General (No specific pet)</option>
                        <c:forEach var="p" items="${pets}">
                            <option value="${p.id}">${p.name} (${p.species})</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label">Reminder Date</label>
                    <input type="date" name="reminderDate" class="form-control" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Category</label>
                    <select name="reminderType" class="form-control">
                        <option value="Medication">Medication</option>
                        <option value="Vaccination">Vaccination</option>
                        <option value="Deworming">Deworming</option>
                        <option value="Appointment">Appointment</option>
                        <option value="General">General Care</option>
                    </select>
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%;">Create Alert</button>
            </form>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
