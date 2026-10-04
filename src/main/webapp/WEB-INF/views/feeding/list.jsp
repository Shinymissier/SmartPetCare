<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Feeding Schedule & Management" scope="request"/>
<c:set var="activePage" value="feeding" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
    <div>
        <h2 style="font-size: 20px; font-weight: 700; color: var(--text-main);">Pet Feeding Schedules</h2>
        <p style="font-size: 13px; color: var(--text-muted);">Manage daily rations, nutritional portions, feeding times, and track daily meal logs.</p>
    </div>
</div>

<div style="display: grid; grid-template-columns: 2fr 1fr; gap: 24px;">
    <!-- Active Schedules Table -->
    <div class="card">
        <div class="card-header">
            <h3 class="card-title">Recurring Meal Schedules</h3>
        </div>
        <div class="card-body" style="padding: 0;">
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Pet</th>
                            <th>Food Type</th>
                            <th>Quantity</th>
                            <th>Time</th>
                            <th>Frequency</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="s" items="${schedules}">
                            <tr>
                                <td><strong><c:out value="${s.petName}"/></strong></td>
                                <td><c:out value="${s.foodType}"/></td>
                                <td><c:out value="${s.quantity}"/></td>
                                <td><strong><c:out value="${s.feedingTime}"/></strong></td>
                                <td><c:out value="${s.frequency}"/></td>
                                <td>
                                    <span class="badge ${s.status == 'Active' ? 'badge-active' : 'badge-secondary'}">
                                        <c:out value="${s.status}"/>
                                    </span>
                                </td>
                                <td>
                                    <div style="display: flex; gap: 6px;">
                                        <!-- Quick Fed Log -->
                                        <form action="${pageContext.request.contextPath}/feeding" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="log">
                                            <input type="hidden" name="scheduleId" value="${s.id}">
                                            <input type="hidden" name="petId" value="${s.petId}">
                                            <input type="hidden" name="status" value="Fed">
                                            <button type="submit" class="btn btn-primary btn-sm" title="Log Meal as Fed">&#10003; Fed</button>
                                        </form>
                                        <!-- Delete -->
                                        <form action="${pageContext.request.contextPath}/feeding" method="post" style="display:inline;" onsubmit="return confirm('Remove schedule?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${s.id}">
                                            <button type="submit" class="btn btn-danger btn-sm">&times;</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty schedules}">
                            <tr>
                                <td colspan="7" style="text-align: center; color: var(--text-muted); padding: 24px;">
                                    No feeding routines configured yet.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Add Schedule Form -->
    <div class="card">
        <div class="card-header">
            <h3 class="card-title">+ New Feeding Routine</h3>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/feeding" method="post">
                <div class="form-group">
                    <label class="form-label">Pet</label>
                    <select name="petId" class="form-control" required>
                        <c:forEach var="p" items="${pets}">
                            <option value="${p.id}">${p.name} (${p.species})</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label">Food Type &amp; Brand</label>
                    <input type="text" name="foodType" class="form-control" placeholder="e.g. Salmon Kibble, Wet Pate" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Portion Quantity</label>
                    <input type="text" name="quantity" class="form-control" placeholder="e.g. 1.5 cups or 85g pouch" required>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Feeding Time</label>
                        <input type="text" name="feedingTime" class="form-control" placeholder="e.g. 08:00 AM" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Frequency</label>
                        <select name="frequency" class="form-control">
                            <option value="Daily">Daily</option>
                            <option value="Twice Daily">Twice Daily</option>
                            <option value="Three Times Daily">Three Times Daily</option>
                            <option value="Weekly">Weekly</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Special Dietary Instructions</label>
                    <textarea name="notes" class="form-control" rows="2" placeholder="Allergies, warm water mix, etc."></textarea>
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%;">Add Routine</button>
            </form>
        </div>
    </div>
</div>

<!-- Feeding Logs Section -->
<div class="card">
    <div class="card-header">
        <h3 class="card-title">Recent Meal Completion History</h3>
    </div>
    <div class="card-body" style="padding: 0;">
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Pet</th>
                        <th>Logged At</th>
                        <th>Status</th>
                        <th>Notes</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="log" items="${recentLogs}">
                        <tr>
                            <td><strong><c:out value="${log.petName != null ? log.petName : 'Pet'}"/></strong></td>
                            <td><c:out value="${log.fedAt}"/></td>
                            <td><span class="badge badge-completed"><c:out value="${log.status}"/></span></td>
                            <td><c:out value="${not empty log.notes ? log.notes : '-'}"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty recentLogs}">
                        <tr><td colspan="4" style="text-align: center; color: var(--text-muted); padding: 16px;">No recent meals logged yet. Click "&#10003; Fed" above to track meal completion.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
