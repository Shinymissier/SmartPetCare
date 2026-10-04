package com.petcare.dao;

import com.petcare.config.DatabaseConnection;
import com.petcare.model.FeedingLog;
import com.petcare.model.FeedingSchedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FeedingDAO {

    public List<FeedingSchedule> findAllByUserId(String userId) {
        List<FeedingSchedule> list = new ArrayList<>();
        String sql = "SELECT s.*, p.name AS pet_name FROM feeding_schedules s " +
                     "JOIN pets p ON s.pet_id = p.id " +
                     "WHERE s.user_id = ?::uuid " +
                     "ORDER BY s.feeding_time ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSchedule(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching feeding schedules: " + e.getMessage());
        }
        return list;
    }

    public FeedingSchedule findByIdAndUserId(String id, String userId) {
        String sql = "SELECT s.*, p.name AS pet_name FROM feeding_schedules s " +
                     "JOIN pets p ON s.pet_id = p.id " +
                     "WHERE s.id = ?::uuid AND s.user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSchedule(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding feeding schedule: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(FeedingSchedule s) {
        String sql = "INSERT INTO feeding_schedules (id, pet_id, user_id, food_type, quantity, feeding_time, frequency, status, notes) " +
                     "VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (s.getId() == null || s.getId().isEmpty()) {
                s.setId(UUID.randomUUID().toString());
            }
            ps.setString(1, s.getId());
            ps.setString(2, s.getPetId());
            ps.setString(3, s.getUserId());
            ps.setString(4, s.getFoodType());
            ps.setString(5, s.getQuantity());
            ps.setString(6, s.getFeedingTime());
            ps.setString(7, s.getFrequency());
            ps.setString(8, s.getStatus() != null ? s.getStatus() : "Active");
            ps.setString(9, s.getNotes());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting feeding schedule: " + e.getMessage());
            return false;
        }
    }

    public boolean update(FeedingSchedule s) {
        String sql = "UPDATE feeding_schedules SET pet_id = ?::uuid, food_type = ?, quantity = ?, feeding_time = ?, " +
                     "frequency = ?, status = ?, notes = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE id = ?::uuid AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getPetId());
            ps.setString(2, s.getFoodType());
            ps.setString(3, s.getQuantity());
            ps.setString(4, s.getFeedingTime());
            ps.setString(5, s.getFrequency());
            ps.setString(6, s.getStatus());
            ps.setString(7, s.getNotes());
            ps.setString(8, s.getId());
            ps.setString(9, s.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating feeding schedule: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id, String userId) {
        String sql = "DELETE FROM feeding_schedules WHERE id = ?::uuid AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting feeding schedule: " + e.getMessage());
            return false;
        }
    }

    public boolean logFeeding(FeedingLog log) {
        String sql = "INSERT INTO feeding_logs (id, pet_id, schedule_id, user_id, fed_at, status, notes) " +
                     "VALUES (?::uuid, ?::uuid, ?::uuid, ?::uuid, CURRENT_TIMESTAMP, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (log.getId() == null || log.getId().isEmpty()) {
                log.setId(UUID.randomUUID().toString());
            }
            ps.setString(1, log.getId());
            ps.setString(2, log.getPetId());
            ps.setString(3, log.getScheduleId());
            ps.setString(4, log.getUserId());
            ps.setString(5, log.getStatus() != null ? log.getStatus() : "Fed");
            ps.setString(6, log.getNotes());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error logging feeding: " + e.getMessage());
            return false;
        }
    }

    public List<FeedingLog> getRecentLogs(String userId, int limit) {
        List<FeedingLog> list = new ArrayList<>();
        String sql = "SELECT l.*, p.name AS pet_name FROM feeding_logs l " +
                     "JOIN pets p ON l.pet_id = p.id " +
                     "WHERE l.user_id = ?::uuid " +
                     "ORDER BY l.fed_at DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FeedingLog fl = new FeedingLog();
                    fl.setId(rs.getString("id"));
                    fl.setPetId(rs.getString("pet_id"));
                    fl.setScheduleId(rs.getString("schedule_id"));
                    fl.setUserId(rs.getString("user_id"));
                    fl.setFedAt(rs.getTimestamp("fed_at"));
                    fl.setStatus(rs.getString("status"));
                    fl.setNotes(rs.getString("notes"));
                    list.add(fl);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting feeding logs: " + e.getMessage());
        }
        return list;
    }

    private FeedingSchedule mapSchedule(ResultSet rs) throws SQLException {
        FeedingSchedule s = new FeedingSchedule();
        s.setId(rs.getString("id"));
        s.setPetId(rs.getString("pet_id"));
        s.setUserId(rs.getString("user_id"));
        try {
            s.setPetName(rs.getString("pet_name"));
        } catch (SQLException ignored) {}
        s.setFoodType(rs.getString("food_type"));
        s.setQuantity(rs.getString("quantity"));
        s.setFeedingTime(rs.getString("feeding_time"));
        s.setFrequency(rs.getString("frequency"));
        s.setStatus(rs.getString("status"));
        s.setNotes(rs.getString("notes"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        return s;
    }
}
