package com.petcare.dao;

import com.petcare.config.DatabaseConnection;
import com.petcare.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppointmentDAO {

    public List<Appointment> findAllByUserId(String userId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS pet_name FROM appointments a " +
                     "JOIN pets p ON a.pet_id = p.id " +
                     "WHERE a.user_id = ?::uuid " +
                     "ORDER BY a.appointment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching appointments: " + e.getMessage());
        }
        return list;
    }

    public List<Appointment> getUpcomingAppointments(String userId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS pet_name FROM appointments a " +
                     "JOIN pets p ON a.pet_id = p.id " +
                     "WHERE a.user_id = ?::uuid AND a.appointment_date >= CURRENT_TIMESTAMP AND a.status = 'Scheduled' " +
                     "ORDER BY a.appointment_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAppointment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching upcoming appointments: " + e.getMessage());
        }
        return list;
    }

    public Appointment findByIdAndUserId(String id, String userId) {
        String sql = "SELECT a.*, p.name AS pet_name FROM appointments a " +
                     "JOIN pets p ON a.pet_id = p.id " +
                     "WHERE a.id = ?::uuid AND a.user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAppointment(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding appointment: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(Appointment a) {
        String sql = "INSERT INTO appointments (id, pet_id, user_id, title, appointment_type, appointment_date, provider_name, location, status, notes) " +
                     "VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (a.getId() == null || a.getId().isEmpty()) {
                a.setId(UUID.randomUUID().toString());
            }
            ps.setString(1, a.getId());
            ps.setString(2, a.getPetId());
            ps.setString(3, a.getUserId());
            ps.setString(4, a.getTitle());
            ps.setString(5, a.getAppointmentType());
            ps.setTimestamp(6, a.getAppointmentDate());
            ps.setString(7, a.getProviderName());
            ps.setString(8, a.getLocation());
            ps.setString(9, a.getStatus() != null ? a.getStatus() : "Scheduled");
            ps.setString(10, a.getNotes());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting appointment: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Appointment a) {
        String sql = "UPDATE appointments SET pet_id = ?::uuid, title = ?, appointment_type = ?, appointment_date = ?, " +
                     "provider_name = ?, location = ?, status = ?, notes = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE id = ?::uuid AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getPetId());
            ps.setString(2, a.getTitle());
            ps.setString(3, a.getAppointmentType());
            ps.setTimestamp(4, a.getAppointmentDate());
            ps.setString(5, a.getProviderName());
            ps.setString(6, a.getLocation());
            ps.setString(7, a.getStatus());
            ps.setString(8, a.getNotes());
            ps.setString(9, a.getId());
            ps.setString(10, a.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id, String userId) {
        String sql = "DELETE FROM appointments WHERE id = ?::uuid AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            return false;
        }
    }

    private Appointment mapAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setId(rs.getString("id"));
        a.setPetId(rs.getString("pet_id"));
        a.setUserId(rs.getString("user_id"));
        try {
            a.setPetName(rs.getString("pet_name"));
        } catch (SQLException ignored) {}
        a.setTitle(rs.getString("title"));
        a.setAppointmentType(rs.getString("appointment_type"));
        a.setAppointmentDate(rs.getTimestamp("appointment_date"));
        a.setProviderName(rs.getString("provider_name"));
        a.setLocation(rs.getString("location"));
        a.setStatus(rs.getString("status"));
        a.setNotes(rs.getString("notes"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setUpdatedAt(rs.getTimestamp("updated_at"));
        return a;
    }
}
