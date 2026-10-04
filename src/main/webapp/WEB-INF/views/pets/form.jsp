<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${mode == 'edit' ? 'Edit Pet Details' : 'Register New Pet'}" scope="request"/>
<c:set var="activePage" value="pets" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div class="card" style="max-width: 800px; margin: 0 auto;">
    <div class="card-header">
        <h3 class="card-title">${mode == 'edit' ? 'Edit Pet Profile' : 'Register Companion Profile'}</h3>
        <a href="${pageContext.request.contextPath}/pets" class="btn btn-outline btn-sm">&larr; Back to List</a>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/pets" method="post">
            <input type="hidden" name="id" value="${pet != null ? pet.id : ''}">

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Pet Name <span style="color: var(--danger); font-weight: bold;">*</span></label>
                    <input type="text" name="name" class="form-control" value="${pet != null ? pet.name : ''}" placeholder="e.g. Bruno" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Species <span style="color: var(--danger); font-weight: bold;">*</span></label>
                    <select name="species" class="form-control" required>
                        <option value="Dog" ${pet != null && pet.species == 'Dog' ? 'selected' : ''}>Dog</option>
                        <option value="Cat" ${pet != null && pet.species == 'Cat' ? 'selected' : ''}>Cat</option>
                        <option value="Bird" ${pet != null && pet.species == 'Bird' ? 'selected' : ''}>Bird</option>
                        <option value="Rabbit" ${pet != null && pet.species == 'Rabbit' ? 'selected' : ''}>Rabbit</option>
                        <option value="Other" ${pet != null && pet.species == 'Other' ? 'selected' : ''}>Other</option>
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Breed</label>
                    <input type="text" name="breed" class="form-control" value="${pet != null ? pet.breed : ''}" placeholder="e.g. Golden Retriever">
                </div>
                <div class="form-group">
                    <label class="form-label">Date of Birth</label>
                    <input type="date" name="dateOfBirth" class="form-control" value="${pet != null ? pet.dateOfBirth : ''}">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Gender</label>
                    <select name="gender" class="form-control">
                        <option value="Male" ${pet != null && pet.gender == 'Male' ? 'selected' : ''}>Male</option>
                        <option value="Female" ${pet != null && pet.gender == 'Female' ? 'selected' : ''}>Female</option>
                        <option value="Neutered Male" ${pet != null && pet.gender == 'Neutered Male' ? 'selected' : ''}>Neutered Male</option>
                        <option value="Spayed Female" ${pet != null && pet.gender == 'Spayed Female' ? 'selected' : ''}>Spayed Female</option>
                        <option value="Unknown" ${pet != null && pet.gender == 'Unknown' ? 'selected' : ''}>Unknown</option>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Weight (kg)</label>
                    <input type="number" step="0.01" min="0.01" max="500.00" name="weight" class="form-control" value="${pet != null ? pet.weight : ''}" placeholder="e.g. 25.50">
                </div>
                <div class="form-group">
                    <label class="form-label">Coat Color / Markings</label>
                    <input type="text" name="color" class="form-control" value="${pet != null ? pet.color : ''}" placeholder="e.g. Golden Yellow">
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Photo Image URL</label>
                <input type="url" name="photoUrl" class="form-control" value="${pet != null ? pet.photoUrl : ''}" placeholder="https://images.unsplash.com/...">
                <small style="color: var(--text-muted); font-size: 11px;">Leave empty for standard species avatar.</small>
            </div>

            <div class="form-group">
                <label class="form-label">Known Allergies</label>
                <input type="text" name="allergies" class="form-control" value="${pet != null ? pet.allergies : ''}" placeholder="e.g. Chicken protein, grass pollen">
            </div>

            <div class="form-group">
                <label class="form-label">Health Conditions</label>
                <input type="text" name="healthConditions" class="form-control" value="${pet != null ? pet.healthConditions : ''}" placeholder="e.g. None or Sensitive stomach">
            </div>

            <div class="form-group">
                <label class="form-label">Additional Clinical Notes</label>
                <textarea name="notes" class="form-control" rows="3" placeholder="Temperament, behavior notes, or feeding preferences...">${pet != null ? pet.notes : ''}</textarea>
            </div>

            <div style="display: flex; gap: 12px; justify-content: flex-end; margin-top: 24px;">
                <a href="${pageContext.request.contextPath}/pets" class="btn btn-outline">Cancel</a>
                <button type="submit" class="btn btn-primary">${mode == 'edit' ? 'Update Pet' : 'Register Pet'}</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
