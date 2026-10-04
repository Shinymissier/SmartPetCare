package com.petcare.dao;

import com.petcare.config.DatabaseConnection;
import com.petcare.model.Reminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReminderDAO {

    public List<Reminder> findAllByUserId(String userId) {
        List<Reminder> list = new ArrayList<>();
        String sql = "SELECT r.*, p.name AS pet_name FROM reminders r " +
                     "LEFT JOIN pets p ON r.pet_id = p.id " +
                     "WHERE r.user_id = ?::uuid " +
                     "ORDER BY r.is_completed ASC, r.reminder_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReminder(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reminders: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Reminder r) {
        String sql = "INSERT INTO reminders (id, user_id, pet_id, title, reminder_date, reminder_type, is_completed) " +
                     "VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (r.getId() == null || r.getId().isEmpty()) {
                r.setId(UUID.randomUUID().toString());
            }
            ps.setString(1, r.getId());
            ps.setString(2, r.getUserId());
            ps.setString(3, (r.getPetId() != null && !r.getPetId().trim().isEmpty()) ? r.getPetId().trim() : null);
            ps.setString(4, r.getTitle());
            ps.setDate(5, r.getReminderDate());
            ps.setString(6, r.getReminderType() != null ? r.getReminderType() : "General");
            ps.setBoolean(7, r.isCompleted());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting reminder: " + e.getMessage());
            return false;
        }
    }

    public boolean toggleComplete(String id, String userId) {
        String sql = "UPDATE reminders SET is_completed = NOT is_completed WHERE id = ?::uuid AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error toggling reminder: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id, String userId) {
        String sql = "DELETE FROM reminders WHERE id = ?::uuid AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting reminder: " + e.getMessage());
            return false;
        }
    }

    private Reminder mapReminder(ResultSet rs) throws SQLException {
        Reminder r = new Reminder();
        r.setId(rs.getString("id"));
        r.setUserId(rs.getString("user_id"));
        r.setPetId(rs.getString("pet_id"));
        try {
            r.setPetName(rs.getString("pet_name"));
        } catch (SQLException ignored) {}
        r.setTitle(rs.getString("title"));
        r.setReminderDate(rs.getDate("reminder_date"));
        r.setReminderType(rs.getString("reminder_type"));
        r.setCompleted(rs.getBoolean("is_completed"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
}
