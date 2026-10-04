package com.petcare.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class FeedingLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String petId;
    private String scheduleId;
    private String userId;
    private Timestamp fedAt;
    private String status; // 'Fed', 'Skipped', 'Delayed'
    private String notes;

    public FeedingLog() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Timestamp getFedAt() { return fedAt; }
    public void setFedAt(Timestamp fedAt) { this.fedAt = fedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
