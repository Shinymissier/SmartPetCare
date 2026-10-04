<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="My Pets" scope="request"/>
<c:set var="activePage" value="pets" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
    <div>
        <h2 style="font-size: 20px; font-weight: 700; color: var(--text-main);">Registered Pets (${pets.size()})</h2>
        <p style="font-size: 13px; color: var(--text-muted);">Manage your companion profiles, weights, breeds, and medical logs.</p>
    </div>
    <a href="${pageContext.request.contextPath}/pets?action=new" class="btn btn-primary">+ Register New Pet</a>
</div>

<div class="pets-grid">
    <c:forEach var="pet" items="${pets}">
        <div class="pet-box">
            <img src="${pet.photoUrl != null && !pet.photoUrl.isEmpty() ? pet.photoUrl : 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=400&q=80'}" alt="${pet.name}" class="pet-box-img">
            <div class="pet-box-content">
                <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                    <div>
                        <h4 class="pet-box-title"><c:out value="${pet.name}"/></h4>
                        <div class="pet-box-meta"><c:out value="${pet.species}"/> &bull; <c:out value="${pet.breed}"/></div>
                    </div>
                    <span class="badge badge-secondary"><c:out value="${pet.gender}"/></span>
                </div>

                <div style="font-size: 12px; color: var(--text-muted); margin-bottom: 12px; display: grid; grid-template-columns: 1fr 1fr; gap: 4px;">
                    <div>Weight: <strong><c:out value="${pet.weight}"/> kg</strong></div>
                    <div>Color: <strong><c:out value="${pet.color}"/></strong></div>
                    <div style="grid-column: span 2;">DOB: <strong><c:out value="${pet.dateOfBirth}"/></strong></div>
                    <c:if test="${not empty pet.allergies}">
                        <div style="grid-column: span 2; color: #b91c1c;">Allergies: <c:out value="${pet.allergies}"/></div>
                    </c:if>
                </div>

                <div style="display: flex; gap: 8px; margin-top: auto; border-top: 1px solid var(--border-color); padding-top: 12px;">
                    <a href="${pageContext.request.contextPath}/pets?action=view&id=${pet.id}" class="btn btn-outline btn-sm" style="flex: 1;">Health Profile</a>
                    <a href="${pageContext.request.contextPath}/pets?action=edit&id=${pet.id}" class="btn btn-outline btn-sm">Edit</a>
                    <form action="${pageContext.request.contextPath}/pets" method="post" style="display: inline;" onsubmit="return confirm('Are you sure you want to delete this pet and all associated health records?');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${pet.id}">
                        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                    </form>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

<c:if test="${empty pets}">
    <div class="card" style="text-align: center; padding: 48px;">
        <h3 style="font-size: 16px; margin-bottom: 8px;">No pets registered yet</h3>
        <p style="color: var(--text-muted); font-size: 13px; margin-bottom: 16px;">Add your first pet to start tracking vaccinations and medical visits.</p>
        <a href="${pageContext.request.contextPath}/pets?action=new" class="btn btn-primary">+ Register Pet</a>
    </div>
</c:if>

<%@ include file="../common/footer.jsp" %>
