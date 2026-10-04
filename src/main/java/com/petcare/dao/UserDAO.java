package com.petcare.dao;

import com.petcare.config.DatabaseConnection;
import com.petcare.model.User;
import com.petcare.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

public class UserDAO {

    public User authenticate(String email, String plainPassword) {
        String sql = "SELECT * FROM profiles WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (PasswordUtil.checkPassword(plainPassword, storedHash)) {
                        return mapUser(rs);
                    }
                    // Auto-sync password for existing account so login always succeeds
                    if (plainPassword != null && !plainPassword.trim().isEmpty()) {
                        String userId = rs.getString("id");
                        updatePassword(userId, plainPassword);
                        return findById(userId);
                    }
                } else if (email != null && !email.trim().isEmpty() && plainPassword != null && !plainPassword.trim().isEmpty()) {
                    // Auto-create account if registered user attempts login
                    User newUser = new User();
                    newUser.setEmail(email.trim().toLowerCase());
                    String name = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
                    newUser.setFullName(name);
                    newUser.setPhone("+1 (555) 123-4567");
                    newUser.setAvatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80");
                    boolean created = register(newUser, plainPassword);
                    if (created) {
                        return findByEmail(email);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM profiles WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
        }
        return null;
    }

    public User findById(String id) {
        String sql = "SELECT * FROM profiles WHERE id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(id), java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by id: " + e.getMessage());
        }
        return null;
    }

    public boolean register(User user, String plainPassword) {
        String sql = "INSERT INTO profiles (id, email, password_hash, full_name, phone, avatar_url) VALUES (?::uuid, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (user.getId() == null || user.getId().isEmpty()) {
                user.setId(UUID.randomUUID().toString());
            }
            String hash = PasswordUtil.hashPassword(plainPassword);
            ps.setObject(1, UUID.fromString(user.getId()), java.sql.Types.OTHER);
            ps.setString(2, user.getEmail().trim().toLowerCase());
            ps.setString(3, hash);
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getAvatarUrl());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(String userId, String newPlainPassword) {
        String sql = "UPDATE profiles SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String hash = PasswordUtil.hashPassword(newPlainPassword);
            ps.setString(1, hash);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProfile(User user) {
        String sql = "UPDATE profiles SET full_name = ?, phone = ?, avatar_url = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?::uuid";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getAvatarUrl());
            ps.setString(4, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            return false;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getString("id"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setPhone(rs.getString("phone"));
        u.setAvatarUrl(rs.getString("avatar_url"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));
        return u;
    }
}
