package com.petcare.dao;

import com.petcare.config.DatabaseConnection;
import com.petcare.model.Pet;

import java.sql.Connection;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PetDAO {

    public List<Pet> findAllByUserId(String userId) {
        List<Pet> list = new ArrayList<>();
        String sql = "SELECT * FROM pets WHERE user_id = ?::uuid ORDER BY name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pets for user: " + e.getMessage());
        }
        return list;
    }

    public void seedDemoDataForUser(String userId) {
        String pet1Id = UUID.randomUUID().toString();
        String pet2Id = UUID.randomUUID().toString();
        String pet3Id = UUID.randomUUID().toString();

        String sqlPets = 
            "INSERT INTO pets (id, user_id, name, species, breed, date_of_birth, gender, weight, color, allergies, health_conditions, notes, photo_url) VALUES " +
            "('" + pet1Id + "'::uuid, '" + userId + "'::uuid, 'Bruno', 'Dog', 'Golden Retriever', '2023-04-15', 'Male', 28.50, 'Golden Yellow', 'Chicken protein', 'Optimal health', 'Friendly and energetic companion.', 'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=400&q=80'), " +
            "('" + pet2Id + "'::uuid, '" + userId + "'::uuid, 'Luna', 'Cat', 'Siamese', '2022-08-20', 'Female', 4.20, 'Seal Point', 'None known', 'Mild sensitive stomach', 'Affectionate indoor feline.', 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=400&q=80'), " +
            "('" + pet3Id + "'::uuid, '" + userId + "'::uuid, 'Milo', 'Dog', 'Beagle', '2024-01-10', 'Male', 11.20, 'Tricolor', 'Grass pollen', 'Booster due', 'Playful scent tracking puppy.', 'https://images.unsplash.com/photo-1537151625747-768eb6cf92b2?auto=format&fit=crop&w=400&q=80')";

        String sqlVacs = 
            "INSERT INTO vaccinations (id, pet_id, user_id, vaccine_name, vaccination_date, next_due_date, vet_name, clinic_name, notes, status) VALUES " +
            "('" + UUID.randomUUID() + "'::uuid, '" + pet1Id + "'::uuid, '" + userId + "'::uuid, 'Rabies (3-Year)', '2024-05-10', '2027-05-10', 'Dr. Alex Morales', 'Valley Animal Hospital', 'Administered right hind leg.', 'Completed'), " +
            "('" + UUID.randomUUID() + "'::uuid, '" + pet1Id + "'::uuid, '" + userId + "'::uuid, 'DHPP Core Booster', '2025-10-15', '2026-10-15', 'Dr. Alex Morales', 'Valley Animal Hospital', 'Annual core protection.', 'Upcoming'), " +
            "('" + UUID.randomUUID() + "'::uuid, '" + pet2Id + "'::uuid, '" + userId + "'::uuid, 'FVRCP Feline Core', '2025-02-14', '2026-02-14', 'Dr. Emily Chen', 'Paws & Whiskers Clinic', 'Annual booster overdue.', 'Overdue'), " +
            "('" + UUID.randomUUID() + "'::uuid, '" + pet3Id + "'::uuid, '" + userId + "'::uuid, 'Bordetella (Kennel Cough)', '2026-03-01', '2026-11-01', 'Dr. Alex Morales', 'Valley Animal Hospital', 'Oral dose administered.', 'Upcoming')";

        String sqlMeds = 
            "INSERT INTO medical_records (id, pet_id, user_id, visit_date, diagnosis, treatment, medication, vet_name, clinic_name, cost, follow_up_date, notes) VALUES " +
            "('" + UUID.randomUUID() + "'::uuid, '" + pet1Id + "'::uuid, '" + userId + "'::uuid, '2026-01-20', 'Minor ear infection (Otitis Externa)', 'Ear flush and topical antibacterial drops', 'Otomax ointment 2x daily', 'Dr. Alex Morales', 'Valley Animal Hospital', 85.00, '2026-02-05', 'Infection fully cleared upon re-check.'), " +
            "('" + UUID.randomUUID() + "'::uuid, '" + pet2Id + "'::uuid, '" + userId + "'::uuid, '2026-02-15', 'Routine dental cleaning and checkup', 'Ultrasonic scaling under mild sedation', 'None', 'Dr. Emily Chen', 'Paws & Whiskers Clinic', 140.00, NULL, 'Clean teeth and healthy gums.')";

        String sqlAppts = 
            "INSERT INTO appointments (id, pet_id, user_id, title, appointment_type, appointment_date, provider_name, location, status, notes) VALUES " +
            "('" + UUID.randomUUID() + "'::uuid, '" + pet1Id + "'::uuid, '" + userId + "'::uuid, 'Annual Coat Grooming & Bath', 'Grooming', '2026-10-15 10:00:00', 'Happy Tails Spa', '124 Elm Street', 'Scheduled', 'Full de-shedding bath and nail trim.'), " +
            "('" + UUID.randomUUID() + "'::uuid, '" + pet2Id + "'::uuid, '" + userId + "'::uuid, 'FVRCP Booster Catchup Exam', 'Veterinary', '2026-10-20 14:30:00', 'Dr. Emily Chen', 'Paws & Whiskers Clinic', 'Scheduled', 'Annual feline health review.')";

        String sqlFeeds = 
            "INSERT INTO feeding_schedules (id, pet_id, user_id, food_type, quantity, feeding_time, frequency, status, notes) VALUES " +
            "('" + UUID.randomUUID() + "'::uuid, '" + pet1Id + "'::uuid, '" + userId + "'::uuid, 'Salmon & Sweet Potato Kibble', '1.5 cups', '08:00 AM', 'Twice Daily', 'Active', 'Avoid chicken protein due to allergy.'), " +
            "('" + UUID.randomUUID() + "'::uuid, '" + pet2Id + "'::uuid, '" + userId + "'::uuid, 'Royal Canin Siamese Wet Pouch', '1 pouch (85g)', '07:30 AM', 'Daily', 'Active', 'Serve at room temperature.')";

        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(sqlPets);
            stmt.execute(sqlVacs);
            stmt.execute(sqlMeds);
            stmt.execute(sqlAppts);
            stmt.execute(sqlFeeds);
        } catch (SQLException e) {
            System.err.println("Notice seeding user data: " + e.getMessage());
        }
    }

    public Pet findByIdAndUserId(String id, String userId) {
        String sql = "SELECT * FROM pets WHERE id = ?::uuid AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding pet by id: " + e.getMessage());
        }
        return null;
    }

    public Pet findByNameAndUserId(String name, String userId) {
        String sql = "SELECT * FROM pets WHERE LOWER(name) = LOWER(?) AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding pet by name: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(Pet pet) {
        String sql = "INSERT INTO pets (id, user_id, name, species, breed, date_of_birth, gender, weight, color, allergies, health_conditions, notes, photo_url) " +
                     "VALUES (?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (pet.getId() == null || pet.getId().isEmpty()) {
                pet.setId(UUID.randomUUID().toString());
            }
            ps.setString(1, pet.getId());
            ps.setString(2, pet.getUserId());
            ps.setString(3, pet.getName());
            ps.setString(4, pet.getSpecies());
            ps.setString(5, pet.getBreed());
            ps.setDate(6, pet.getDateOfBirth());
            ps.setString(7, pet.getGender());
            ps.setBigDecimal(8, pet.getWeight());
            ps.setString(9, pet.getColor());
            ps.setString(10, pet.getAllergies());
            ps.setString(11, pet.getHealthConditions());
            ps.setString(12, pet.getNotes());
            ps.setString(13, pet.getPhotoUrl());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting pet: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Pet pet) {
        String sql = "UPDATE pets SET name = ?, species = ?, breed = ?, date_of_birth = ?, gender = ?, weight = ?, color = ?, " +
                     "allergies = ?, health_conditions = ?, notes = ?, photo_url = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE id = ?::uuid AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pet.getName());
            ps.setString(2, pet.getSpecies());
            ps.setString(3, pet.getBreed());
            ps.setDate(4, pet.getDateOfBirth());
            ps.setString(5, pet.getGender());
            ps.setBigDecimal(6, pet.getWeight());
            ps.setString(7, pet.getColor());
            ps.setString(8, pet.getAllergies());
            ps.setString(9, pet.getHealthConditions());
            ps.setString(10, pet.getNotes());
            ps.setString(11, pet.getPhotoUrl());
            ps.setString(12, pet.getId());
            ps.setString(13, pet.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating pet: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id, String userId) {
        String sql = "DELETE FROM pets WHERE id = ?::uuid AND user_id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting pet: " + e.getMessage());
            return false;
        }
    }

    private Pet mapPet(ResultSet rs) throws SQLException {
        Pet p = new Pet();
        p.setId(rs.getString("id"));
        p.setUserId(rs.getString("user_id"));
        p.setName(rs.getString("name"));
        p.setSpecies(rs.getString("species"));
        p.setBreed(rs.getString("breed"));
        p.setDateOfBirth(rs.getDate("date_of_birth"));
        p.setGender(rs.getString("gender"));
        p.setWeight(rs.getBigDecimal("weight"));
        p.setColor(rs.getString("color"));
        p.setAllergies(rs.getString("allergies"));
        p.setHealthConditions(rs.getString("health_conditions"));
        p.setNotes(rs.getString("notes"));
        p.setPhotoUrl(rs.getString("photo_url"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        return p;
    }
}
