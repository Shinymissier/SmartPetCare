package com.petcare.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

public class Vaccination implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String petId;
    private String petName;
    private String userId;
    private String vaccineName;
    private Date vaccinationDate;
    private Date nextDueDate;
    private String vetName;
    private String clinicName;
    private String notes;
    private String status; // 'Completed', 'Upcoming', 'Overdue'
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Vaccination() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    public Date getVaccinationDate() { return vaccinationDate; }
    public void setVaccinationDate(Date vaccinationDate) { this.vaccinationDate = vaccinationDate; }

    public Date getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(Date nextDueDate) { 
        this.nextDueDate = nextDueDate;
        recalculateStatus();
    }

    public String getVetName() { return vetName; }
    public void setVetName(String vetName) { this.vetName = vetName; }

    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { 
        if (status == null || status.trim().isEmpty()) {
            recalculateStatus();
        }
        return status; 
    }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Calculate vaccination status automatically based on the due date.
     */
    public void recalculateStatus() {
        if (nextDueDate == null) {
            this.status = "Completed";
            return;
        }
        LocalDate now = LocalDate.now();
        LocalDate due = nextDueDate.toLocalDate();
        if (due.isBefore(now)) {
            this.status = "Overdue";
        } else if (due.isAfter(now.minusDays(1)) && due.isBefore(now.plusDays(31))) {
            this.status = "Upcoming";
        } else {
            this.status = "Completed";
        }
    }
}
