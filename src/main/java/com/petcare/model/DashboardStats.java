package com.petcare.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private int totalPets;
    private int upcomingVaccinations;
    private int overdueVaccinations;
    private int completedVaccinations;
    private int upcomingAppointments;
    private int totalMedicalRecords;
    private int activeFeedingSchedules;
    private int pendingReminders;

    private Map<String, Integer> speciesDistribution = new HashMap<>();
    private Map<String, Integer> vaccinationStatusCounts = new HashMap<>();
    private List<String> recentActivities = new ArrayList<>();

    public DashboardStats() {}

    public int getTotalPets() { return totalPets; }
    public void setTotalPets(int totalPets) { this.totalPets = totalPets; }

    public int getUpcomingVaccinations() { return upcomingVaccinations; }
    public void setUpcomingVaccinations(int upcomingVaccinations) { this.upcomingVaccinations = upcomingVaccinations; }

    public int getOverdueVaccinations() { return overdueVaccinations; }
    public void setOverdueVaccinations(int overdueVaccinations) { this.overdueVaccinations = overdueVaccinations; }

    public int getCompletedVaccinations() { return completedVaccinations; }
    public void setCompletedVaccinations(int completedVaccinations) { this.completedVaccinations = completedVaccinations; }

    public int getUpcomingAppointments() { return upcomingAppointments; }
    public void setUpcomingAppointments(int upcomingAppointments) { this.upcomingAppointments = upcomingAppointments; }

    public int getTotalMedicalRecords() { return totalMedicalRecords; }
    public void setTotalMedicalRecords(int totalMedicalRecords) { this.totalMedicalRecords = totalMedicalRecords; }

    public int getActiveFeedingSchedules() { return activeFeedingSchedules; }
    public void setActiveFeedingSchedules(int activeFeedingSchedules) { this.activeFeedingSchedules = activeFeedingSchedules; }

    public int getPendingReminders() { return pendingReminders; }
    public void setPendingReminders(int pendingReminders) { this.pendingReminders = pendingReminders; }

    public Map<String, Integer> getSpeciesDistribution() { return speciesDistribution; }
    public void setSpeciesDistribution(Map<String, Integer> speciesDistribution) { this.speciesDistribution = speciesDistribution; }

    public Map<String, Integer> getVaccinationStatusCounts() { return vaccinationStatusCounts; }
    public void setVaccinationStatusCounts(Map<String, Integer> vaccinationStatusCounts) { this.vaccinationStatusCounts = vaccinationStatusCounts; }

    public List<String> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<String> recentActivities) { this.recentActivities = recentActivities; }
}
