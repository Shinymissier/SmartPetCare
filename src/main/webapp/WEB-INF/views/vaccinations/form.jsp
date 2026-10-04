<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${mode == 'edit' ? 'Edit Vaccination Record' : 'Record Vaccination'}" scope="request"/>
<c:set var="activePage" value="vaccinations" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div class="card" style="max-width: 750px; margin: 0 auto;">
    <div class="card-header">
        <h3 class="card-title">${mode == 'edit' ? 'Edit Vaccination Details' : 'Add New Vaccination Record'}</h3>
        <a href="${pageContext.request.contextPath}/vaccinations" class="btn btn-outline btn-sm">&larr; Back</a>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/vaccinations" method="post">
            <input type="hidden" name="id" value="${vaccination != null ? vaccination.id : ''}">

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Select Pet <span style="color: var(--danger);">*</span></label>
                    <select name="petId" class="form-control" required>
                        <option value="">-- Choose Companion --</option>
                        <c:forEach var="p" items="${pets}">
                            <option value="${p.id}" ${(vaccination != null && vaccination.petId == p.id) || (preselectedPetId == p.id) ? 'selected' : ''}>
                                ${p.name} (${p.species} - ${p.breed})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Vaccine Name <span style="color: var(--danger);">*</span></label>
                    <input type="text" name="vaccineName" class="form-control" value="${vaccination != null ? vaccination.vaccineName : ''}" placeholder="e.g. Rabies, DHPP Core, Bordetella" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Administered Date <span style="color: var(--danger);">*</span></label>
                    <input type="date" name="vaccinationDate" class="form-control" value="${vaccination != null ? vaccination.vaccinationDate : ''}" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Next Due Date (Booster)</label>
                    <input type="date" name="nextDueDate" class="form-control" value="${vaccination != null ? vaccination.nextDueDate : ''}">
                    <small style="color: var(--text-muted); font-size: 11px;">Status is automatically calculated from this due date.</small>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Veterinarian Name</label>
                    <input type="text" name="vetName" class="form-control" value="${vaccination != null ? vaccination.vetName : ''}" placeholder="e.g. Dr. Alex Morales">
                </div>
                <div class="form-group">
                    <label class="form-label">Clinic / Hospital</label>
                    <input type="text" name="clinicName" class="form-control" value="${vaccination != null ? vaccination.clinicName : ''}" placeholder="e.g. Valley Animal Hospital">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Status Override</label>
                    <select name="status" class="form-control">
                        <option value="">Auto (Calculate from due date)</option>
                        <option value="Completed" ${vaccination != null && vaccination.status == 'Completed' ? 'selected' : ''}>Completed</option>
                        <option value="Upcoming" ${vaccination != null && vaccination.status == 'Upcoming' ? 'selected' : ''}>Upcoming</option>
                        <option value="Overdue" ${vaccination != null && vaccination.status == 'Overdue' ? 'selected' : ''}>Overdue</option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Batch Number / Clinical Notes</label>
                <textarea name="notes" class="form-control" rows="3" placeholder="Lot number, injection location, or reactions...">${vaccination != null ? vaccination.notes : ''}</textarea>
            </div>

            <div style="display: flex; gap: 12px; justify-content: flex-end; margin-top: 24px;">
                <a href="${pageContext.request.contextPath}/vaccinations" class="btn btn-outline">Cancel</a>
                <button type="submit" class="btn btn-primary">${mode == 'edit' ? 'Update Record' : 'Save Record'}</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
