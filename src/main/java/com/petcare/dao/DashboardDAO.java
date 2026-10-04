package com.petcare.dao;

import com.petcare.config.DatabaseConnection;
import com.petcare.model.DashboardStats;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {

    public DashboardStats getDashboardStats(String userId) {
        DashboardStats stats = new DashboardStats();
        LocalDate today = LocalDate.now();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Total Pets
            String sqlPets = "SELECT COUNT(*) FROM pets WHERE user_id = ?::uuid";
            try (PreparedStatement ps = conn.prepareStatement(sqlPets)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.setTotalPets(rs.getInt(1));
                }
            }

            // 2. Upcoming Vaccinations (next 30 days)
            String sqlUpcoming = "SELECT COUNT(*) FROM vaccinations WHERE user_id = ?::uuid AND next_due_date >= ? AND next_due_date <= ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpcoming)) {
                ps.setString(1, userId);
                ps.setDate(2, Date.valueOf(today));
                ps.setDate(3, Date.valueOf(today.plusDays(30)));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.setUpcomingVaccinations(rs.getInt(1));
                }
            }

            // 3. Overdue Vaccinations (due date < today and status != 'Completed')
            String sqlOverdue = "SELECT COUNT(*) FROM vaccinations WHERE user_id = ?::uuid AND next_due_date < ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlOverdue)) {
                ps.setString(1, userId);
                ps.setDate(2, Date.valueOf(today));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.setOverdueVaccinations(rs.getInt(1));
                }
            }

            // 3b. Completed Vaccinations
            String sqlCompletedCount = "SELECT COUNT(*) FROM vaccinations WHERE user_id = ?::uuid AND status = 'Completed'";
            try (PreparedStatement ps = conn.prepareStatement(sqlCompletedCount)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.setCompletedVaccinations(rs.getInt(1));
                }
            }

            // 4. Upcoming Appointments
            String sqlAppt = "SELECT COUNT(*) FROM appointments WHERE user_id = ?::uuid AND appointment_date >= CURRENT_TIMESTAMP AND status = 'Scheduled'";
            try (PreparedStatement ps = conn.prepareStatement(sqlAppt)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.setUpcomingAppointments(rs.getInt(1));
                }
            }

            // 5. Total Medical Records
            String sqlMed = "SELECT COUNT(*) FROM medical_records WHERE user_id = ?::uuid";
            try (PreparedStatement ps = conn.prepareStatement(sqlMed)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.setTotalMedicalRecords(rs.getInt(1));
                }
            }

            // 6. Active Feeding Schedules
            String sqlFeed = "SELECT COUNT(*) FROM feeding_schedules WHERE user_id = ?::uuid AND status = 'Active'";
            try (PreparedStatement ps = conn.prepareStatement(sqlFeed)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.setActiveFeedingSchedules(rs.getInt(1));
                }
            }

            // 7. Pending Reminders
            String sqlRem = "SELECT COUNT(*) FROM reminders WHERE user_id = ?::uuid AND is_completed = FALSE";
            try (PreparedStatement ps = conn.prepareStatement(sqlRem)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.setPendingReminders(rs.getInt(1));
                }
            }

            // 8. Species Distribution for Chart
            Map<String, Integer> speciesMap = new HashMap<>();
            String sqlSpecies = "SELECT species, COUNT(*) FROM pets WHERE user_id = ?::uuid GROUP BY species";
            try (PreparedStatement ps = conn.prepareStatement(sqlSpecies)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        speciesMap.put(rs.getString(1), rs.getInt(2));
                    }
                }
            }
            stats.setSpeciesDistribution(speciesMap);

            // 9. Vaccination Status Counts
            Map<String, Integer> statusMap = new HashMap<>();
            statusMap.put("Completed", 0);
            statusMap.put("Upcoming", stats.getUpcomingVaccinations());
            statusMap.put("Overdue", stats.getOverdueVaccinations());

            String sqlCompleted = "SELECT COUNT(*) FROM vaccinations WHERE user_id = ?::uuid AND (next_due_date IS NULL OR next_due_date > ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlCompleted)) {
                ps.setString(1, userId);
                ps.setDate(2, Date.valueOf(today.plusDays(30)));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) statusMap.put("Completed", rs.getInt(1));
                }
            }
            stats.setVaccinationStatusCounts(statusMap);

            // 10. Recent Activities Feed
            List<String> activities = new ArrayList<>();
            String sqlRecent = "SELECT p.name AS pet_name, v.vaccine_name, v.vaccination_date FROM vaccinations v " +
                               "JOIN pets p ON v.pet_id = p.id WHERE v.user_id = ?::uuid ORDER BY v.created_at DESC LIMIT 4";
            try (PreparedStatement ps = conn.prepareStatement(sqlRecent)) {
                ps.setString(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        activities.add("Vaccinated " + rs.getString("pet_name") + " with " + rs.getString("vaccine_name") + " on " + rs.getDate("vaccination_date"));
                    }
                }
            }
            stats.setRecentActivities(activities);

        } catch (SQLException e) {
            System.err.println("Error calculating dashboard stats: " + e.getMessage());
        }

        return stats;
    }
}
