<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Medical Records" scope="request"/>
<c:set var="activePage" value="medical" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
    <div>
        <h2 style="font-size: 20px; font-weight: 700; color: var(--text-main);">Clinical Medical Records</h2>
        <p style="font-size: 13px; color: var(--text-muted);">Manage diagnoses, prescriptions, surgeries, costs, and follow-up examinations.</p>
    </div>
    <a href="${pageContext.request.contextPath}/medical-records?action=new" class="btn btn-primary">+ Add Medical Record</a>
</div>

<!-- Filter Bar -->
<div class="card" style="margin-bottom: 20px; padding: 12px 16px;">
    <form action="${pageContext.request.contextPath}/medical-records" method="get" style="display: flex; gap: 12px; align-items: center;">
        <span style="font-size: 13px; font-weight: 600; color: var(--text-muted);">Filter Pet:</span>
        <select name="petId" class="form-control" style="width: 200px; padding: 4px 8px; font-size: 12px;" onchange="this.form.submit()">
            <option value="">All Pets</option>
            <c:forEach var="p" items="${pets}">
                <option value="${p.id}" ${selectedPetId == p.id ? 'selected' : ''}>${p.name} (${p.species})</option>
            </c:forEach>
        </select>
        <c:if test="${not empty selectedPetId}">
            <a href="${pageContext.request.contextPath}/medical-records" class="btn btn-outline btn-sm">Clear Filter</a>
        </c:if>
    </form>
</div>

<div class="card">
    <div class="card-body" style="padding: 0;">
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Pet Name</th>
                        <th>Visit Date</th>
                        <th>Diagnosis</th>
                        <th>Treatment</th>
                        <th>Medication</th>
                        <th>Veterinarian</th>
                        <th>Cost</th>
                        <th>Follow-up</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="m" items="${records}">
                        <tr>
                            <td><strong><c:out value="${m.petName}"/></strong></td>
                            <td><c:out value="${m.visitDate}"/></td>
                            <td><strong><c:out value="${m.diagnosis}"/></strong></td>
                            <td><c:out value="${m.treatment != null ? m.treatment : '-'}"/></td>
                            <td><c:out value="${m.medication != null ? m.medication : '-'}"/></td>
                            <td>
                                <c:out value="${m.vetName != null ? m.vetName : 'N/A'}"/>
                                <c:if test="${not empty m.clinicName}"><br><small style="color: var(--text-muted);"><c:out value="${m.clinicName}"/></small></c:if>
                            </td>
                            <td>$<c:out value="${m.cost}"/></td>
                            <td><c:out value="${m.followUpDate != null ? m.followUpDate : 'None'}"/></td>
                            <td>
                                <div style="display: flex; gap: 6px;">
                                    <a href="${pageContext.request.contextPath}/medical-records?action=edit&id=${m.id}" class="btn btn-outline btn-sm">Edit</a>
                                    <form action="${pageContext.request.contextPath}/medical-records" method="post" style="display: inline;" onsubmit="return confirm('Delete this medical entry?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${m.id}">
                                        <button type="submit" class="btn btn-danger btn-sm">&times;</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty records}">
                        <tr>
                            <td colspan="9" style="text-align: center; color: var(--text-muted); padding: 32px;">
                                No medical history records found.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
