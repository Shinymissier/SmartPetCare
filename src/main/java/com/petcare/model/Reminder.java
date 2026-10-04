package com.petcare.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Reminder implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String petId;
    private String petName;
    private String title;
    private Date reminderDate;
    private String reminderType; // 'Vaccination', 'Medication', 'Appointment', 'Deworming', 'General'
    private boolean completed;
    private Timestamp createdAt;

    public Reminder() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getReminderDate() { return reminderDate; }
    public void setReminderDate(Date reminderDate) { this.reminderDate = reminderDate; }

    public String getReminderType() { return reminderType; }
    public void setReminderType(String reminderType) { this.reminderType = reminderType; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
