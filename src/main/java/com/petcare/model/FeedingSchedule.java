package com.petcare.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class FeedingSchedule implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String petId;
    private String petName;
    private String userId;
    private String foodType;
    private String quantity;
    private String feedingTime;
    private String frequency;
    private String status; // 'Active', 'Paused', 'Completed'
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public FeedingSchedule() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFoodType() { return foodType; }
    public void setFoodType(String foodType) { this.foodType = foodType; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getFeedingTime() { return feedingTime; }
    public void setFeedingTime(String feedingTime) { this.feedingTime = feedingTime; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
