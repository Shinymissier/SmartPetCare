<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Appointments" scope="request"/>
<c:set var="activePage" value="appointments" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
    <div>
        <h2 style="font-size: 20px; font-weight: 700; color: var(--text-main);">Pet Appointments</h2>
        <p style="font-size: 13px; color: var(--text-muted);">Coordinate veterinary checkups, vaccination boosters, grooming sessions, and specialist visits.</p>
    </div>
</div>

<div style="display: grid; grid-template-columns: 2fr 1fr; gap: 24px;">
    <!-- Appointments Table -->
    <div class="card">
        <div class="card-header">
            <h3 class="card-title">Scheduled Encounters</h3>
        </div>
        <div class="card-body" style="padding: 0;">
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Pet</th>
                            <th>Title &amp; Type</th>
                            <th>Date &amp; Time</th>
                            <th>Provider / Location</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="a" items="${appointments}">
                            <tr>
                                <td><strong><c:out value="${a.petName}"/></strong></td>
                                <td>
                                    <strong><c:out value="${a.title}"/></strong><br>
                                    <small style="color: var(--text-muted);"><c:out value="${a.appointmentType}"/></small>
                                </td>
                                <td><c:out value="${a.appointmentDate}"/></td>
                                <td>
                                    <c:out value="${a.providerName != null ? a.providerName : 'N/A'}"/><br>
                                    <small style="color: var(--text-muted);"><c:out value="${a.location != null ? a.location : ''}"/></small>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${a.status == 'Scheduled'}"><span class="badge badge-scheduled">Scheduled</span></c:when>
                                        <c:when test="${a.status == 'Completed'}"><span class="badge badge-completed">Completed</span></c:when>
                                        <c:otherwise><span class="badge badge-secondary"><c:out value="${a.status}"/></span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div style="display: flex; gap: 6px;">
                                        <c:if test="${a.status == 'Scheduled'}">
                                            <form action="${pageContext.request.contextPath}/appointments" method="post" style="display:inline;">
                                                <input type="hidden" name="action" value="status">
                                                <input type="hidden" name="id" value="${a.id}">
                                                <input type="hidden" name="status" value="Completed">
                                                <button type="submit" class="btn btn-outline btn-sm" title="Mark as Completed">&#10003;</button>
                                            </form>
                                        </c:if>
                                        <form action="${pageContext.request.contextPath}/appointments" method="post" style="display:inline;" onsubmit="return confirm('Cancel appointment?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${a.id}">
                                            <button type="submit" class="btn btn-danger btn-sm">&times;</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty appointments}">
                            <tr>
                                <td colspan="6" style="text-align: center; color: var(--text-muted); padding: 32px;">
                                    No appointments scheduled.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Book Appointment Form -->
    <div class="card">
        <div class="card-header">
            <h3 class="card-title">+ Schedule Appointment</h3>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/appointments" method="post">
                <div class="form-group">
                    <label class="form-label">Select Pet</label>
                    <select name="petId" class="form-control" required>
                        <c:forEach var="p" items="${pets}">
                            <option value="${p.id}">${p.name} (${p.species})</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label">Appointment Title</label>
                    <input type="text" name="title" class="form-control" placeholder="e.g. Annual Core Booster &amp; Exam" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Appointment Type</label>
                    <select name="appointmentType" class="form-control">
                        <option value="Veterinary">Veterinary</option>
                        <option value="Grooming">Grooming</option>
                        <option value="Vaccination">Vaccination</option>
                        <option value="Checkup">General Checkup</option>
                        <option value="Training">Training</option>
                        <option value="Other">Other</option>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label">Date &amp; Time</label>
                    <input type="datetime-local" name="appointmentDate" class="form-control" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Provider / Doctor Name</label>
                    <input type="text" name="providerName" class="form-control" placeholder="e.g. Dr. Emily Chen">
                </div>

                <div class="form-group">
                    <label class="form-label">Location / Address</label>
                    <input type="text" name="location" class="form-control" placeholder="e.g. 124 Elm Street, Clinic 2">
                </div>

                <div class="form-group">
                    <label class="form-label">Special Notes</label>
                    <textarea name="notes" class="form-control" rows="2" placeholder="Fasting instructions, leash required..."></textarea>
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%;">Schedule</button>
            </form>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
