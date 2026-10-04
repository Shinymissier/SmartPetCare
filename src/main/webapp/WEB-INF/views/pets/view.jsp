<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Pet Health History - ${pet.name}" scope="request"/>
<c:set var="activePage" value="pets" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<!-- Pet Banner Card -->
<div class="card" style="margin-bottom: 24px;">
    <div class="card-body" style="display: flex; gap: 24px; align-items: center;">
        <img src="${pet.photoUrl != null ? pet.photoUrl : 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=400&q=80'}" alt="${pet.name}" style="width: 110px; height: 110px; border-radius: 8px; object-fit: cover; border: 1px solid var(--border-color);">
        <div style="flex: 1;">
            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                <div>
                    <h2 style="font-size: 22px; font-weight: 700; color: var(--text-main);"><c:out value="${pet.name}"/></h2>
                    <p style="font-size: 13px; color: var(--text-muted);"><c:out value="${pet.species}"/> &bull; <c:out value="${pet.breed}"/> &bull; <c:out value="${pet.gender}"/></p>
                </div>
                <div style="display: flex; gap: 8px;">
                    <a href="${pageContext.request.contextPath}/vaccinations?action=new&petId=${pet.id}" class="btn btn-primary btn-sm">+ Add Vaccination</a>
                    <a href="${pageContext.request.contextPath}/medical-records?action=new&petId=${pet.id}" class="btn btn-outline btn-sm">+ Add Medical Record</a>
                    <a href="${pageContext.request.contextPath}/pets?action=edit&id=${pet.id}" class="btn btn-outline btn-sm">Edit Details</a>
                </div>
            </div>

            <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-top: 16px; font-size: 13px; background: var(--bg-surface); padding: 12px 16px; border-radius: 6px;">
                <div><span style="color: var(--text-muted); font-size: 11px; display: block;">DATE OF BIRTH</span><strong><c:out value="${pet.dateOfBirth != null ? pet.dateOfBirth : 'N/A'}"/></strong></div>
                <div><span style="color: var(--text-muted); font-size: 11px; display: block;">CURRENT WEIGHT</span><strong><c:out value="${pet.weight}"/> kg</strong></div>
                <div><span style="color: var(--text-muted); font-size: 11px; display: block;">COLOR / COAT</span><strong><c:out value="${pet.color != null ? pet.color : 'N/A'}"/></strong></div>
                <div><span style="color: var(--text-muted); font-size: 11px; display: block;">KNOWN ALLERGIES</span><strong style="color: ${not empty pet.allergies ? 'var(--danger)' : 'inherit'}"><c:out value="${not empty pet.allergies ? pet.allergies : 'None'}"/></strong></div>
            </div>
        </div>
    </div>
</div>

<!-- Tabs: Vaccination History & Medical Encounters -->
<div class="card">
    <div class="card-header">
        <h3 class="card-title">Complete Health &amp; Immunization Records</h3>
    </div>
    <div class="card-body">
        <!-- Vaccinations -->
        <h4 style="font-size: 14px; font-weight: 700; margin-bottom: 12px; color: var(--text-main);">1. Vaccination Records (${vaccinations.size()})</h4>
        <div class="table-responsive" style="margin-bottom: 32px;">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Vaccine Name</th>
                        <th>Administered Date</th>
                        <th>Next Due Date</th>
                        <th>Veterinarian</th>
                        <th>Clinic</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="v" items="${vaccinations}">
                        <tr>
                            <td><strong><c:out value="${v.vaccineName}"/></strong></td>
                            <td><c:out value="${v.vaccinationDate}"/></td>
                            <td><c:out value="${v.nextDueDate != null ? v.nextDueDate : 'N/A'}"/></td>
                            <td><c:out value="${v.vetName != null ? v.vetName : 'N/A'}"/></td>
                            <td><c:out value="${v.clinicName != null ? v.clinicName : 'N/A'}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${v.status == 'Overdue'}"><span class="badge badge-overdue">Overdue</span></c:when>
                                    <c:when test="${v.status == 'Upcoming'}"><span class="badge badge-upcoming">Upcoming</span></c:when>
                                    <c:otherwise><span class="badge badge-completed">Completed</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/vaccinations?action=edit&id=${v.id}" class="btn btn-outline btn-sm">Edit</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty vaccinations}">
                        <tr><td colspan="7" style="text-align: center; color: var(--text-muted); padding: 16px;">No vaccination records logged for this pet yet.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <!-- Medical Records -->
        <h4 style="font-size: 14px; font-weight: 700; margin-bottom: 12px; color: var(--text-main);">2. Clinical &amp; Medical History (${medicalRecords.size()})</h4>
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Visit Date</th>
                        <th>Diagnosis</th>
                        <th>Treatment</th>
                        <th>Medication</th>
                        <th>Cost</th>
                        <th>Follow-up</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="m" items="${medicalRecords}">
                        <tr>
                            <td><c:out value="${m.visitDate}"/></td>
                            <td><strong><c:out value="${m.diagnosis}"/></strong></td>
                            <td><c:out value="${m.treatment != null ? m.treatment : 'N/A'}"/></td>
                            <td><c:out value="${m.medication != null ? m.medication : 'None'}"/></td>
                            <td>$<c:out value="${m.cost}"/></td>
                            <td><c:out value="${m.followUpDate != null ? m.followUpDate : 'None'}"/></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/medical-records?action=edit&id=${m.id}" class="btn btn-outline btn-sm">Edit</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty medicalRecords}">
                        <tr><td colspan="7" style="text-align: center; color: var(--text-muted); padding: 16px;">No medical records on file.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
