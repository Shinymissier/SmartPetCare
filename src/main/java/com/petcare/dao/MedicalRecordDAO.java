package com.petcare.dao;

import com.petcare.config.DatabaseConnection;
import com.petcare.model.MedicalRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MedicalRecordDAO {

    public List<MedicalRecord> findAllByUserId(String userId) {
        List<MedicalRecord> list = new ArrayList<>();
        String sql = "SELECT m.*, p.name AS pet_name FROM medical_records m " +
                     "JOIN pets p ON m.pet_id = p.id " +
                     "WHERE m.user_id = ?::uuid " +
                     "ORDER BY m.visit_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRecord(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching medical records: " + e.getMessage());
        }
        return list;
    }

    public List<MedicalRecord> findByPetIdAndUserId(String petId, String userId) {
        List<MedicalRecord> list = new ArrayList<>();
        String sql = "SELECT m.*, p.name AS pet_name FROM medical_records m " +
                     "JOIN pets p ON m.pet_id = p.id " +
                     "WHERE m.pet_id = ?::uuid AND m.user_id = ?::uuid " +
                     "ORDER BY m.visit_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, petId);
            ps.setString(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRecord(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pet medical records: " + e.getMessage());
        }
        return list;
    }

    public MedicalRecord findByIdAndUserId(String id, String userId) {
        String sql = "SELECT m.*, p.name AS pet_name FROM medical_records m " +
                     "JOIN pets p ON m.pet_id = p.id " +
                     "WHERE m.id = ?::uuid AND m.user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRecord(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding medical record by id: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(MedicalRecord m) {
        String sql = "INSERT INTO medical_records (id, pet_id, user_id, visit_date, diagnosis, treatment, medication, vet_name, clinic_name, cost, follow_up_date, notes) " +
                     "VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (m.getId() == null || m.getId().isEmpty()) {
                m.setId(UUID.randomUUID().toString());
            }
            ps.setString(1, m.getId());
            ps.setString(2, m.getPetId());
            ps.setString(3, m.getUserId());
            ps.setDate(4, m.getVisitDate());
            ps.setString(5, m.getDiagnosis());
            ps.setString(6, m.getTreatment());
            ps.setString(7, m.getMedication());
            ps.setString(8, m.getVetName());
            ps.setString(9, m.getClinicName());
            ps.setBigDecimal(10, m.getCost());
            ps.setDate(11, m.getFollowUpDate());
            ps.setString(12, m.getNotes());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting medical record: " + e.getMessage());
            return false;
        }
    }

    public boolean update(MedicalRecord m) {
        String sql = "UPDATE medical_records SET pet_id = ?::uuid, visit_date = ?, diagnosis = ?, treatment = ?, medication = ?, " +
                     "vet_name = ?, clinic_name = ?, cost = ?, follow_up_date = ?, notes = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE id = ?::uuid AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getPetId());
            ps.setDate(2, m.getVisitDate());
            ps.setString(3, m.getDiagnosis());
            ps.setString(4, m.getTreatment());
            ps.setString(5, m.getMedication());
            ps.setString(6, m.getVetName());
            ps.setString(7, m.getClinicName());
            ps.setBigDecimal(8, m.getCost());
            ps.setDate(9, m.getFollowUpDate());
            ps.setString(10, m.getNotes());
            ps.setString(11, m.getId());
            ps.setString(12, m.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating medical record: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id, String userId) {
        String sql = "DELETE FROM medical_records WHERE id = ?::uuid AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting medical record: " + e.getMessage());
            return false;
        }
    }

    private MedicalRecord mapRecord(ResultSet rs) throws SQLException {
        MedicalRecord m = new MedicalRecord();
        m.setId(rs.getString("id"));
        m.setPetId(rs.getString("pet_id"));
        m.setUserId(rs.getString("user_id"));
        try {
            m.setPetName(rs.getString("pet_name"));
        } catch (SQLException ignored) {}
        m.setVisitDate(rs.getDate("visit_date"));
        m.setDiagnosis(rs.getString("diagnosis"));
        m.setTreatment(rs.getString("treatment"));
        m.setMedication(rs.getString("medication"));
        m.setVetName(rs.getString("vet_name"));
        m.setClinicName(rs.getString("clinic_name"));
        m.setCost(rs.getBigDecimal("cost"));
        m.setFollowUpDate(rs.getDate("follow_up_date"));
        m.setNotes(rs.getString("notes"));
        m.setCreatedAt(rs.getTimestamp("created_at"));
        m.setUpdatedAt(rs.getTimestamp("updated_at"));
        return m;
    }
}
