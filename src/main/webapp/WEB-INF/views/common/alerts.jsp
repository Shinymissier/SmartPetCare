<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty param.message}">
    <div class="alert alert-success">
        <span>${param.message}</span>
        <button type="button" class="btn-sm btn-outline" style="border:none;" onclick="this.parentElement.remove();">&times;</button>
    </div>
</c:if>

<c:if test="${not empty param.error}">
    <div class="alert alert-danger">
        <span>${param.error}</span>
        <button type="button" class="btn-sm btn-outline" style="border:none;" onclick="this.parentElement.remove();">&times;</button>
    </div>
</c:if>

<c:if test="${not empty successMessage}">
    <div class="alert alert-success">
        <span>${successMessage}</span>
        <button type="button" class="btn-sm btn-outline" style="border:none;" onclick="this.parentElement.remove();">&times;</button>
    </div>
</c:if>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">
        <span>${errorMessage}</span>
        <button type="button" class="btn-sm btn-outline" style="border:none;" onclick="this.parentElement.remove();">&times;</button>
    </div>
</c:if>
