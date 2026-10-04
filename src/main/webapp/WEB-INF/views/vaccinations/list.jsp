<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Vaccination Management" scope="request"/>
<c:set var="activePage" value="vaccinations" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
    <div>
        <h2 style="font-size: 20px; font-weight: 700; color: var(--text-main);">Vaccination Records</h2>
        <p style="font-size: 13px; color: var(--text-muted);">Track core and non-core vaccines, boosters, and overdue immunization schedules.</p>
    </div>
    <a href="${pageContext.request.contextPath}/vaccinations?action=new" class="btn btn-primary">+ Add Vaccination</a>
</div>

<!-- Filters Bar -->
<div class="card" style="margin-bottom: 20px; padding: 12px 16px;">
    <div style="display: flex; gap: 12px; align-items: center; flex-wrap: wrap;">
        <span style="font-size: 13px; font-weight: 600; color: var(--text-muted);">Filter Status:</span>
        <a href="${pageContext.request.contextPath}/vaccinations?filter=all" class="btn btn-sm ${activeFilter == 'all' ? 'btn-primary' : 'btn-outline'}">All</a>
        <a href="${pageContext.request.contextPath}/vaccinations?filter=upcoming" class="btn btn-sm ${activeFilter == 'upcoming' ? 'btn-primary' : 'btn-outline'}">Upcoming</a>
        <a href="${pageContext.request.contextPath}/vaccinations?filter=overdue" class="btn btn-sm ${activeFilter == 'overdue' ? 'btn-danger' : 'btn-outline'}">Overdue</a>

        <span style="font-size: 13px; font-weight: 600; color: var(--text-muted); margin-left: 16px;">Pet:</span>
        <form action="${pageContext.request.contextPath}/vaccinations" method="get" style="display: inline-block;">
            <select name="petId" class="form-control" style="width: 180px; padding: 4px 8px; font-size: 12px; display: inline-block;" onchange="this.form.submit()">
                <option value="">All Pets</option>
                <c:forEach var="p" items="${pets}">
                    <option value="${p.id}" ${selectedPetId == p.id ? 'selected' : ''}>${p.name} (${p.species})</option>
                </c:forEach>
            </select>
        </form>
    </div>
</div>

<div class="card">
    <div class="card-body" style="padding: 0;">
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Pet Name</th>
                        <th>Vaccine Name</th>
                        <th>Administered</th>
                        <th>Next Due Date</th>
                        <th>Veterinarian &amp; Clinic</th>
                        <th>Status</th>
                        <th>Notes</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="v" items="${vaccinations}">
                        <tr>
                            <td><strong><c:out value="${v.petName}"/></strong></td>
                            <td><c:out value="${v.vaccineName}"/></td>
                            <td><c:out value="${v.vaccinationDate}"/></td>
                            <td style="${v.status == 'Overdue' ? 'color: var(--danger); font-weight: 600;' : ''}">
                                <c:out value="${v.nextDueDate != null ? v.nextDueDate : 'None set'}"/>
                            </td>
                            <td>
                                <c:out value="${v.vetName != null ? v.vetName : 'N/A'}"/>
                                <c:if test="${not empty v.clinicName}"><br><small style="color: var(--text-muted);"><c:out value="${v.clinicName}"/></small></c:if>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${v.status == 'Overdue'}"><span class="badge badge-overdue">Overdue</span></c:when>
                                    <c:when test="${v.status == 'Upcoming'}"><span class="badge badge-upcoming">Upcoming</span></c:when>
                                    <c:otherwise><span class="badge badge-completed">Completed</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td><small style="color: var(--text-muted);"><c:out value="${v.notes != null ? v.notes : '-'}"/></small></td>
                            <td>
                                <div style="display: flex; gap: 6px;">
                                    <a href="${pageContext.request.contextPath}/vaccinations?action=edit&id=${v.id}" class="btn btn-outline btn-sm">Edit</a>
                                    <form action="${pageContext.request.contextPath}/vaccinations" method="post" style="display: inline;" onsubmit="return confirm('Delete this vaccination log?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${v.id}">
                                        <button type="submit" class="btn btn-danger btn-sm">&times;</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty vaccinations}">
                        <tr>
                            <td colspan="8" style="text-align: center; color: var(--text-muted); padding: 32px;">
                                No vaccination records found matching criteria.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
