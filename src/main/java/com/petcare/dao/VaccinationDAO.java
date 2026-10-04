package com.petcare.dao;

import com.petcare.config.DatabaseConnection;
import com.petcare.model.Vaccination;
import com.petcare.util.DateUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VaccinationDAO {

    public List<Vaccination> findAllByUserId(String userId) {
        List<Vaccination> list = new ArrayList<>();
        String sql = "SELECT v.*, p.name AS pet_name FROM vaccinations v " +
                     "JOIN pets p ON v.pet_id = p.id " +
                     "WHERE v.user_id = ?::uuid " +
                     "ORDER BY v.next_due_date ASC NULLS LAST, v.vaccination_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapVaccination(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching vaccinations for user: " + e.getMessage());
        }
        return list;
    }

    public List<Vaccination> findByPetIdAndUserId(String petId, String userId) {
        List<Vaccination> list = new ArrayList<>();
        String sql = "SELECT v.*, p.name AS pet_name FROM vaccinations v " +
                     "JOIN pets p ON v.pet_id = p.id " +
                     "WHERE v.pet_id = ?::uuid AND v.user_id = ?::uuid " +
                     "ORDER BY v.vaccination_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, petId);
            ps.setString(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapVaccination(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching vaccinations for pet: " + e.getMessage());
        }
        return list;
    }

    public Vaccination findByIdAndUserId(String id, String userId) {
        String sql = "SELECT v.*, p.name AS pet_name FROM vaccinations v " +
                     "JOIN pets p ON v.pet_id = p.id " +
                     "WHERE v.id = ?::uuid AND v.user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapVaccination(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding vaccination by id: " + e.getMessage());
        }
        return null;
    }

    public List<Vaccination> getUpcomingVaccinations(String userId) {
        List<Vaccination> list = new ArrayList<>();
        String sql = "SELECT v.*, p.name AS pet_name FROM vaccinations v " +
                     "JOIN pets p ON v.pet_id = p.id " +
                     "WHERE v.user_id = ?::uuid AND v.next_due_date >= ? AND v.next_due_date <= ? " +
                     "ORDER BY v.next_due_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            LocalDate today = LocalDate.now();
            ps.setString(1, userId);
            ps.setDate(2, Date.valueOf(today));
            ps.setDate(3, Date.valueOf(today.plusDays(30)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vaccination v = mapVaccination(rs);
                    v.setStatus("Upcoming");
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching upcoming vaccinations: " + e.getMessage());
        }
        return list;
    }

    public List<Vaccination> getOverdueVaccinations(String userId) {
        List<Vaccination> list = new ArrayList<>();
        String sql = "SELECT v.*, p.name AS pet_name FROM vaccinations v " +
                     "JOIN pets p ON v.pet_id = p.id " +
                     "WHERE v.user_id = ?::uuid AND v.next_due_date < ? " +
                     "ORDER BY v.next_due_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            LocalDate today = LocalDate.now();
            ps.setString(1, userId);
            ps.setDate(2, Date.valueOf(today));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vaccination v = mapVaccination(rs);
                    v.setStatus("Overdue");
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching overdue vaccinations: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Vaccination v) {
        String sql = "INSERT INTO vaccinations (id, pet_id, user_id, vaccine_name, vaccination_date, next_due_date, vet_name, clinic_name, notes, status) " +
                     "VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (v.getId() == null || v.getId().isEmpty()) {
                v.setId(UUID.randomUUID().toString());
            }
            v.recalculateStatus();
            ps.setString(1, v.getId());
            ps.setString(2, v.getPetId());
            ps.setString(3, v.getUserId());
            ps.setString(4, v.getVaccineName());
            ps.setDate(5, v.getVaccinationDate());
            ps.setDate(6, v.getNextDueDate());
            ps.setString(7, v.getVetName());
            ps.setString(8, v.getClinicName());
            ps.setString(9, v.getNotes());
            ps.setString(10, v.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting vaccination: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Vaccination v) {
        String sql = "UPDATE vaccinations SET pet_id = ?::uuid, vaccine_name = ?, vaccination_date = ?, next_due_date = ?, " +
                     "vet_name = ?, clinic_name = ?, notes = ?, status = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE id = ?::uuid AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            v.recalculateStatus();
            ps.setString(1, v.getPetId());
            ps.setString(2, v.getVaccineName());
            ps.setDate(3, v.getVaccinationDate());
            ps.setDate(4, v.getNextDueDate());
            ps.setString(5, v.getVetName());
            ps.setString(6, v.getClinicName());
            ps.setString(7, v.getNotes());
            ps.setString(8, v.getStatus());
            ps.setString(9, v.getId());
            ps.setString(10, v.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating vaccination: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id, String userId) {
        String sql = "DELETE FROM vaccinations WHERE id = ?::uuid AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting vaccination: " + e.getMessage());
            return false;
        }
    }

    private Vaccination mapVaccination(ResultSet rs) throws SQLException {
        Vaccination v = new Vaccination();
        v.setId(rs.getString("id"));
        v.setPetId(rs.getString("pet_id"));
        v.setUserId(rs.getString("user_id"));
        try {
            v.setPetName(rs.getString("pet_name"));
        } catch (SQLException ignored) {}
        v.setVaccineName(rs.getString("vaccine_name"));
        v.setVaccinationDate(rs.getDate("vaccination_date"));
        v.setNextDueDate(rs.getDate("next_due_date"));
        v.setVetName(rs.getString("vet_name"));
        v.setClinicName(rs.getString("clinic_name"));
        v.setNotes(rs.getString("notes"));
        v.setStatus(rs.getString("status"));
        // Recalculate status dynamically if dates have changed
        v.recalculateStatus();
        v.setCreatedAt(rs.getTimestamp("created_at"));
        v.setUpdatedAt(rs.getTimestamp("updated_at"));
        return v;
    }
}
