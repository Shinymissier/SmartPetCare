<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${mode == 'edit' ? 'Edit Medical Record' : 'Add Medical Record'}" scope="request"/>
<c:set var="activePage" value="medical" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div class="card" style="max-width: 750px; margin: 0 auto;">
    <div class="card-header">
        <h3 class="card-title">${mode == 'edit' ? 'Edit Medical Encounter' : 'Log Clinical Encounter'}</h3>
        <a href="${pageContext.request.contextPath}/medical-records" class="btn btn-outline btn-sm">&larr; Back</a>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/medical-records" method="post">
            <input type="hidden" name="id" value="${record != null ? record.id : ''}">

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Select Pet <span style="color: var(--danger);">*</span></label>
                    <select name="petId" class="form-control" required>
                        <option value="">-- Choose Companion --</option>
                        <c:forEach var="p" items="${pets}">
                            <option value="${p.id}" ${(record != null && record.petId == p.id) || (preselectedPetId == p.id) ? 'selected' : ''}>
                                ${p.name} (${p.species})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Visit Date <span style="color: var(--danger);">*</span></label>
                    <input type="date" name="visitDate" class="form-control" value="${record != null ? record.visitDate : ''}" required>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Diagnosis <span style="color: var(--danger);">*</span></label>
                <input type="text" name="diagnosis" class="form-control" value="${record != null ? record.diagnosis : ''}" placeholder="e.g. Otitis Externa, Annual Dental Scaling, Dermatitis" required>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Treatment Provided</label>
                    <textarea name="treatment" class="form-control" rows="2" placeholder="e.g. Ear flush and topical application">${record != null ? record.treatment : ''}</textarea>
                </div>
                <div class="form-group">
                    <label class="form-label">Prescription / Medication</label>
                    <textarea name="medication" class="form-control" rows="2" placeholder="e.g. Otomax drops twice daily for 7 days">${record != null ? record.medication : ''}</textarea>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Attending Veterinarian</label>
                    <input type="text" name="vetName" class="form-control" value="${record != null ? record.vetName : ''}" placeholder="e.g. Dr. Alex Morales">
                </div>
                <div class="form-group">
                    <label class="form-label">Clinic Name</label>
                    <input type="text" name="clinicName" class="form-control" value="${record != null ? record.clinicName : ''}" placeholder="e.g. Valley Animal Hospital">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Total Cost ($)</label>
                    <input type="number" step="0.01" min="0.00" name="cost" class="form-control" value="${record != null ? record.cost : '0.00'}" placeholder="0.00">
                </div>
                <div class="form-group">
                    <label class="form-label">Follow-up Date</label>
                    <input type="date" name="followUpDate" class="form-control" value="${record != null ? record.followUpDate : ''}">
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Additional Observations &amp; Notes</label>
                <textarea name="notes" class="form-control" rows="3" placeholder="Recovery progress, dietary adjustments...">${record != null ? record.notes : ''}</textarea>
            </div>

            <div style="display: flex; gap: 12px; justify-content: flex-end; margin-top: 24px;">
                <a href="${pageContext.request.contextPath}/medical-records" class="btn btn-outline">Cancel</a>
                <button type="submit" class="btn btn-primary">${mode == 'edit' ? 'Update Entry' : 'Save Entry'}</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
