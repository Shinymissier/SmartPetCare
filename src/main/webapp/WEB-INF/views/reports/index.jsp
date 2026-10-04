<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Health Reports" scope="request"/>
<c:set var="activePage" value="reports" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<!-- Controls bar (Hidden during print) -->
<div class="no-print" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
    <div>
        <h2 style="font-size: 20px; font-weight: 700; color: var(--text-main);">Clinical &amp; Health Reports</h2>
        <p style="font-size: 13px; color: var(--text-muted);">Generate consolidated veterinary certificates, immunization logs, and medical histories.</p>
    </div>
    <div style="display: flex; gap: 8px;">
        <button type="button" class="btn btn-outline" onclick="window.print()">&#128438; Print Report</button>
        <a href="${pageContext.request.contextPath}/xml-management?action=xslt-report" target="_blank" class="btn btn-primary">&rarr; XSLT Report</a>
    </div>
</div>

<!-- Filter card (Hidden during print) -->
<div class="card no-print" style="margin-bottom: 24px; padding: 12px 16px;">
    <form action="${pageContext.request.contextPath}/reports" method="get" style="display: flex; gap: 16px; align-items: center; flex-wrap: wrap;">
        <span style="font-size: 13px; font-weight: 600; color: var(--text-muted);">Report Category:</span>
        <select name="type" class="form-control" style="width: 200px;" onchange="this.form.submit()">
            <option value="health" ${reportType == 'health' ? 'selected' : ''}>Complete Pet Health Report</option>
            <option value="vaccinations" ${reportType == 'vaccinations' ? 'selected' : ''}>Vaccination Certificate</option>
            <option value="medical" ${reportType == 'medical' ? 'selected' : ''}>Medical &amp; Surgery History</option>
            <option value="appointments" ${reportType == 'appointments' ? 'selected' : ''}>Appointments Summary</option>
        </select>

        <span style="font-size: 13px; font-weight: 600; color: var(--text-muted);">Filter Companion:</span>
        <select name="petId" class="form-control" style="width: 180px;" onchange="this.form.submit()">
            <option value="">All Registered Pets</option>
            <c:forEach var="p" items="${pets}">
                <option value="${p.id}" ${selectedPetId == p.id ? 'selected' : ''}>${p.name} (${p.species})</option>
            </c:forEach>
        </select>
    </form>
</div>

<!-- Official Report Printable Document Container -->
<div class="card" style="padding: 32px; background: #ffffff;">
    <div style="border-bottom: 2px solid var(--primary); padding-bottom: 16px; margin-bottom: 24px; display: flex; justify-content: space-between; align-items: flex-start;">
        <div>
            <h1 style="font-size: 24px; color: var(--text-main); margin-bottom: 4px;">Smart Pet Care &amp; Health Report</h1>
            <p style="color: var(--text-muted); font-size: 13px;">Official Companion Veterinary Summary &bull; Confidential Medical Record</p>
        </div>
        <div style="text-align: right; font-size: 12px; color: var(--text-muted);">
            <div>Report Date: <strong><%= java.time.LocalDate.now() %></strong></div>
            <div>Owner: <strong><c:out value="${sessionScope.currentUser.fullName}"/></strong></div>
            <div>Source: Supabase PostgreSQL</div>
        </div>
    </div>

    <!-- Pet Summary -->
    <h3 style="font-size: 16px; font-weight: 700; margin-bottom: 12px; border-bottom: 1px solid var(--border-color); padding-bottom: 6px;">1. Pet Profiles Overview</h3>
    <div class="table-responsive" style="margin-bottom: 28px;">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Pet Name</th>
                    <th>Species &amp; Breed</th>
                    <th>DOB</th>
                    <th>Gender</th>
                    <th>Weight</th>
                    <th>Allergies &amp; Health Notes</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty selectedPet}">
                        <tr>
                            <td><strong><c:out value="${selectedPet.name}"/></strong></td>
                            <td><c:out value="${selectedPet.species}"/> - <c:out value="${selectedPet.breed}"/></td>
                            <td><c:out value="${selectedPet.dateOfBirth}"/></td>
                            <td><c:out value="${selectedPet.gender}"/></td>
                            <td><c:out value="${selectedPet.weight}"/> kg</td>
                            <td><c:out value="${not empty selectedPet.allergies ? selectedPet.allergies : 'None'}"/></td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="p" items="${pets}">
                            <tr>
                                <td><strong><c:out value="${p.name}"/></strong></td>
                                <td><c:out value="${p.species}"/> - <c:out value="${p.breed}"/></td>
                                <td><c:out value="${p.dateOfBirth}"/></td>
                                <td><c:out value="${p.gender}"/></td>
                                <td><c:out value="${p.weight}"/> kg</td>
                                <td><c:out value="${not empty p.allergies ? p.allergies : 'None'}"/></td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <!-- Vaccinations Section -->
    <c:if test="${reportType == 'health' or reportType == 'vaccinations'}">
        <h3 style="font-size: 16px; font-weight: 700; margin-bottom: 12px; border-bottom: 1px solid var(--border-color); padding-bottom: 6px;">2. Immunization &amp; Vaccination Certificate</h3>
        <div class="table-responsive" style="margin-bottom: 28px;">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Pet</th>
                        <th>Vaccine Name</th>
                        <th>Administered</th>
                        <th>Next Due Date</th>
                        <th>Veterinarian / Hospital</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="v" items="${vaccinations}">
                        <tr>
                            <td><strong><c:out value="${v.petName}"/></strong></td>
                            <td><c:out value="${v.vaccineName}"/></td>
                            <td><c:out value="${v.vaccinationDate}"/></td>
                            <td><c:out value="${v.nextDueDate != null ? v.nextDueDate : 'N/A'}"/></td>
                            <td><c:out value="${v.vetName}"/> (<c:out value="${v.clinicName}"/>)</td>
                            <td>
                                <c:choose>
                                    <c:when test="${v.status == 'Completed'}"><span class="badge badge-completed">Completed</span></c:when>
                                    <c:when test="${v.status == 'Upcoming'}"><span class="badge badge-upcoming">Upcoming</span></c:when>
                                    <c:otherwise><span class="badge badge-overdue">Overdue</span></c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty vaccinations}">
                        <tr><td colspan="6" style="text-align: center; color: var(--text-muted);">No vaccinations on file.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </c:if>

    <!-- Medical Encounters Section -->
    <c:if test="${reportType == 'health' or reportType == 'medical'}">
        <h3 style="font-size: 16px; font-weight: 700; margin-bottom: 12px; border-bottom: 1px solid var(--border-color); padding-bottom: 6px;">3. Clinical Diagnoses &amp; Treatments</h3>
        <div class="table-responsive" style="margin-bottom: 28px;">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Pet</th>
                        <th>Visit Date</th>
                        <th>Diagnosis</th>
                        <th>Treatment &amp; Prescriptions</th>
                        <th>Cost</th>
                        <th>Follow-up</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="m" items="${medicalRecords}">
                        <tr>
                            <td><strong><c:out value="${m.petName}"/></strong></td>
                            <td><c:out value="${m.visitDate}"/></td>
                            <td><strong><c:out value="${m.diagnosis}"/></strong></td>
                            <td>
                                <c:out value="${m.treatment}"/>
                                <c:if test="${not empty m.medication}"><br><em>Rx: <c:out value="${m.medication}"/></em></c:if>
                            </td>
                            <td>$<c:out value="${m.cost}"/></td>
                            <td><c:out value="${m.followUpDate != null ? m.followUpDate : 'None'}"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty medicalRecords}">
                        <tr><td colspan="6" style="text-align: center; color: var(--text-muted);">No medical records on file.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </c:if>

    <!-- Print Footer -->
    <div style="border-top: 1px solid var(--border-color); padding-top: 16px; margin-top: 32px; display: flex; justify-content: space-between; font-size: 11px; color: var(--text-muted);">
        <div>Smart Pet Care System &bull; Verified Database Records</div>
        <div>Page 1 of 1</div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
