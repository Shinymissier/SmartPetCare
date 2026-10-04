<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Data Management" scope="request"/>
<c:set var="activePage" value="xml" scope="request"/>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>

<div style="margin-bottom: 24px;">
    <h2 style="font-size: 20px; font-weight: 700; color: var(--text-main);">Data Backup & Restore</h2>
    <p style="font-size: 13px; color: var(--text-muted);">
        Easily export your pet records for backup, or import data to restore your profiles.
    </p>
</div>

<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
    <!-- Export Section -->
    <div class="card">
        <div class="card-header">
            <h3 class="card-title">Export Records</h3>
        </div>
        <div class="card-body" style="display: flex; flex-direction: column; gap: 12px;">
            <p style="font-size: 13px; color: var(--text-muted); margin-bottom: 12px;">
                Download a complete backup of your pet profiles, medical history, and appointments.
            </p>

            <a href="${pageContext.request.contextPath}/xml-management?action=export-download&type=all" class="btn btn-primary" style="justify-content: center; padding: 12px;">
                <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24" style="width: 18px; height: 18px;"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"/></svg>
                Download Complete Backup
            </a>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-top: 12px;">
                <a href="${pageContext.request.contextPath}/xml-management?action=export-download&type=pets" class="btn btn-outline">
                    Pets Only
                </a>
                <a href="${pageContext.request.contextPath}/xml-management?action=export-download&type=medical" class="btn btn-outline">
                    Medical History
                </a>
                <a href="${pageContext.request.contextPath}/xml-management?action=export-download&type=vaccinations" class="btn btn-outline">
                    Vaccinations
                </a>
                <a href="${pageContext.request.contextPath}/xml-management?action=export-download&type=appointments" class="btn btn-outline">
                    Appointments
                </a>
            </div>

            <div style="margin-top: 16px; padding: 16px; background: var(--bg-surface); border-radius: var(--radius-md); border: 1px solid var(--border-color);">
                <a href="${pageContext.request.contextPath}/xml-management?action=xslt-report" target="_blank" class="btn btn-outline btn-block" style="border-color: var(--primary); color: var(--primary);">
                    <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24" style="width: 16px; height: 16px;"><path stroke-linecap="round" stroke-linejoin="round" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"/></svg>
                    Printable Clinical Report
                </a>
            </div>
        </div>
    </div>

    <!-- Import Section -->
    <div class="card">
        <div class="card-header">
            <h3 class="card-title">Import Records</h3>
        </div>
        <div class="card-body">
            <c:if test="${empty parseResult}">
                <form action="${pageContext.request.contextPath}/xml-management" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="import-upload">
                    
                    <p style="font-size: 13px; color: var(--text-muted); margin-bottom: 20px;">
                        Upload a previously downloaded backup file to restore your data.
                    </p>

                    <div class="form-group" style="border: 2px dashed var(--border-color); padding: 32px; text-align: center; border-radius: var(--radius-lg); margin-bottom: 20px; transition: all var(--t-fast);" onmouseover="this.style.borderColor='var(--primary)'" onmouseout="this.style.borderColor='var(--border-color)'">
                        <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24" style="width: 48px; height: 48px; color: var(--text-muted); margin-bottom: 12px;"><path stroke-linecap="round" stroke-linejoin="round" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"/></svg>
                        <br>
                        <label for="fileUpload" style="cursor: pointer; color: var(--primary); font-weight: 600;">Browse for file</label>
                        <input type="file" id="fileUpload" name="xmlFile" accept=".xml" style="display: none;" onchange="document.getElementById('fileName').textContent = this.files[0].name">
                        <div id="fileName" style="margin-top: 8px; font-size: 12px; color: var(--text-main);"></div>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block">
                        Upload &amp; Preview
                    </button>
                </form>
            </c:if>

            <c:if test="${not empty parseResult}">
                <div style="margin-bottom: 20px;">
                    <div class="alert alert-success">
                        <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24" style="width: 20px; height: 20px;"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
                        File analyzed successfully.
                    </div>

                    <div style="background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 16px;">
                        <h4 style="font-size: 14px; font-weight: 600; margin-bottom: 8px; color: var(--text-main);">Found in backup:</h4>
                        <ul style="list-style: none; padding: 0; margin: 0; font-size: 13px; color: var(--text-secondary);">
                            <li style="display: flex; justify-content: space-between; padding: 6px 0; border-bottom: 1px solid var(--border-dark);">
                                <span>Pet Profiles:</span>
                                <strong>${parseResult.pets.size()}</strong>
                            </li>
                            <li style="display: flex; justify-content: space-between; padding: 6px 0;">
                                <span>Vaccination Records:</span>
                                <strong>${parseResult.vaccinations.size()}</strong>
                            </li>
                        </ul>
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/xml-management" method="post">
                    <input type="hidden" name="action" value="import-confirm">
                    <div style="display: flex; gap: 12px;">
                        <button type="submit" class="btn btn-primary" style="flex: 1;">
                            Confirm &amp; Restore Data
                        </button>
                        <a href="${pageContext.request.contextPath}/xml-management" class="btn btn-outline">Cancel</a>
                    </div>
                </form>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
