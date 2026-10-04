package com.petcare.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseConnection {

    private static boolean initialized = false;

    static {
        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found: " + e.getMessage());
        }

        try {
            // Load H2 driver for fallback mode
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("H2 JDBC Driver not found: " + e.getMessage());
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        AppConfig config = AppConfig.getInstance();
        Connection conn = null;

        // Try Supabase PostgreSQL JDBC if configured
        if ("SUPABASE_JDBC".equalsIgnoreCase(config.getDatabaseMode()) && config.isSupabaseConfigured()) {
            try {
                conn = DriverManager.getConnection(
                    config.getSupabaseJdbcUrl(),
                    config.getSupabaseJdbcUser(),
                    config.getSupabaseJdbcPassword()
                );
                return conn;
            } catch (SQLException e) {
                System.err.println("Warning: Failed to connect to Supabase PostgreSQL JDBC: " + e.getMessage());
                System.err.println("Falling back to local database engine...");
            }
        }

        // Local fallback (H2 in PostgreSQL compatibility mode)
        conn = DriverManager.getConnection(
            config.getLocalJdbcUrl(),
            config.getLocalJdbcUser(),
            config.getLocalJdbcPassword()
        );

        if (!initialized) {
            initializeLocalDatabase(conn);
            initialized = true;
        }

        return conn;
    }

    private static void initializeLocalDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            System.out.println("Initializing local database schema & seed data...");
            
            // Execute table creations compatible with both Postgres and H2
            String schemaSql = 
                "CREATE TABLE IF NOT EXISTS profiles (" +
                "    id VARCHAR(50) PRIMARY KEY," +
                "    email VARCHAR(255) UNIQUE NOT NULL," +
                "    password_hash VARCHAR(255) NOT NULL," +
                "    full_name VARCHAR(150) NOT NULL," +
                "    phone VARCHAR(50)," +
                "    avatar_url CLOB," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");" +
                "CREATE TABLE IF NOT EXISTS pets (" +
                "    id VARCHAR(50) PRIMARY KEY," +
                "    user_id VARCHAR(50) NOT NULL," +
                "    name VARCHAR(100) NOT NULL," +
                "    species VARCHAR(50) NOT NULL," +
                "    breed VARCHAR(100)," +
                "    date_of_birth DATE," +
                "    gender VARCHAR(20) DEFAULT 'Unknown'," +
                "    weight DECIMAL(6, 2) DEFAULT 0.00," +
                "    color VARCHAR(60)," +
                "    allergies CLOB," +
                "    health_conditions CLOB," +
                "    notes CLOB," +
                "    photo_url CLOB," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");" +
                "CREATE TABLE IF NOT EXISTS vaccinations (" +
                "    id VARCHAR(50) PRIMARY KEY," +
                "    pet_id VARCHAR(50) NOT NULL," +
                "    user_id VARCHAR(50) NOT NULL," +
                "    vaccine_name VARCHAR(120) NOT NULL," +
                "    vaccination_date DATE NOT NULL," +
                "    next_due_date DATE," +
                "    vet_name VARCHAR(120)," +
                "    clinic_name VARCHAR(150)," +
                "    notes CLOB," +
                "    status VARCHAR(30) DEFAULT 'Completed'," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");" +
                "CREATE TABLE IF NOT EXISTS medical_records (" +
                "    id VARCHAR(50) PRIMARY KEY," +
                "    pet_id VARCHAR(50) NOT NULL," +
                "    user_id VARCHAR(50) NOT NULL," +
                "    visit_date DATE NOT NULL," +
                "    diagnosis CLOB NOT NULL," +
                "    treatment CLOB," +
                "    medication CLOB," +
                "    vet_name VARCHAR(120)," +
                "    clinic_name VARCHAR(150)," +
                "    cost DECIMAL(10, 2) DEFAULT 0.00," +
                "    follow_up_date DATE," +
                "    notes CLOB," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");" +
                "CREATE TABLE IF NOT EXISTS feeding_schedules (" +
                "    id VARCHAR(50) PRIMARY KEY," +
                "    pet_id VARCHAR(50) NOT NULL," +
                "    user_id VARCHAR(50) NOT NULL," +
                "    food_type VARCHAR(100) NOT NULL," +
                "    quantity VARCHAR(60) NOT NULL," +
                "    feeding_time VARCHAR(20) NOT NULL," +
                "    frequency VARCHAR(50) DEFAULT 'Daily'," +
                "    status VARCHAR(30) DEFAULT 'Active'," +
                "    notes CLOB," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");" +
                "CREATE TABLE IF NOT EXISTS feeding_logs (" +
                "    id VARCHAR(50) PRIMARY KEY," +
                "    pet_id VARCHAR(50) NOT NULL," +
                "    schedule_id VARCHAR(50)," +
                "    user_id VARCHAR(50) NOT NULL," +
                "    fed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    status VARCHAR(30) DEFAULT 'Fed'," +
                "    notes CLOB" +
                ");" +
                "CREATE TABLE IF NOT EXISTS appointments (" +
                "    id VARCHAR(50) PRIMARY KEY," +
                "    pet_id VARCHAR(50) NOT NULL," +
                "    user_id VARCHAR(50) NOT NULL," +
                "    title VARCHAR(150) NOT NULL," +
                "    appointment_type VARCHAR(50) NOT NULL," +
                "    appointment_date TIMESTAMP NOT NULL," +
                "    provider_name VARCHAR(150)," +
                "    location CLOB," +
                "    status VARCHAR(30) DEFAULT 'Scheduled'," +
                "    notes CLOB," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");" +
                "CREATE TABLE IF NOT EXISTS reminders (" +
                "    id VARCHAR(50) PRIMARY KEY," +
                "    user_id VARCHAR(50) NOT NULL," +
                "    pet_id VARCHAR(50)," +
                "    title VARCHAR(200) NOT NULL," +
                "    reminder_date DATE NOT NULL," +
                "    reminder_type VARCHAR(50) DEFAULT 'General'," +
                "    is_completed BOOLEAN DEFAULT FALSE," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

            for (String sql : schemaSql.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql.trim());
                }
            }

            // Insert default demo user (demo@petcare.com)
            String demoHash = com.petcare.util.PasswordUtil.hashPassword("admin123");
            stmt.execute("MERGE INTO profiles (id, email, password_hash, full_name, phone, avatar_url) " +
                         "KEY(id) VALUES ('a0000000-0000-0000-0000-000000000001', 'demo@petcare.com', '" + demoHash + "', 'Sarah Jenkins', '+1 (555) 234-5678', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=200&q=80')");

            // Insert Shiny's account (shinymissier@gmail.com / admin123)
            stmt.execute("MERGE INTO profiles (id, email, password_hash, full_name, phone, avatar_url) " +
                         "KEY(email) VALUES ('u0000000-0000-0000-0000-000000000002', 'shinymissier@gmail.com', '" + demoHash + "', 'shiny', '+1 (555) 123-4567', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80')");

            // Seed Chittu for Shiny's account
            stmt.execute("MERGE INTO pets (id, user_id, name, species, breed, date_of_birth, gender, weight, color, allergies, health_conditions, notes, photo_url) " +
                         "KEY(id) VALUES ('c562bc15-c35c-4ca1-9527-37cc1f06b10c', 'u0000000-0000-0000-0000-000000000002', 'Chittu', 'Bird', 'love birds', '2025-02-20', 'Female', 1.00, 'pink', 'None', 'Optimal health', 'Playful love bird.', 'https://images.unsplash.com/photo-1552728089-57bdde30beb3?auto=format&fit=crop&w=400&q=80')");

            stmt.execute("MERGE INTO pets (id, user_id, name, species, breed, date_of_birth, gender, weight, color, allergies, health_conditions, notes, photo_url) " +
                         "KEY(id) VALUES ('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'Bruno', 'Dog', 'Golden Retriever', '2023-04-15', 'Male', 28.50, 'Golden Yellow', 'Chicken protein', 'None', 'Friendly, energetic and loves swimming.', 'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=400&q=80')");

            stmt.execute("MERGE INTO pets (id, user_id, name, species, breed, date_of_birth, gender, weight, color, allergies, health_conditions, notes, photo_url) " +
                         "KEY(id) VALUES ('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'Luna', 'Cat', 'Siamese', '2022-08-20', 'Female', 4.20, 'Seal Point', 'None known', 'Mild sensitive stomach', 'Affectionate indoor cat.', 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=400&q=80')");

            stmt.execute("MERGE INTO vaccinations (id, pet_id, user_id, vaccine_name, vaccination_date, next_due_date, vet_name, clinic_name, notes, status) " +
                         "KEY(id) VALUES ('c0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'Rabies (3-Year)', '2024-05-10', '2027-05-10', 'Dr. Alex Morales', 'Valley Animal Hospital', 'Administered right hind leg.', 'Completed')");

            stmt.execute("MERGE INTO vaccinations (id, pet_id, user_id, vaccine_name, vaccination_date, next_due_date, vet_name, clinic_name, notes, status) " +
                         "KEY(id) VALUES ('c0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'DHPP Core Booster', '2025-10-15', '2026-10-15', 'Dr. Alex Morales', 'Valley Animal Hospital', 'Annual booster required.', 'Upcoming')");

            stmt.execute("MERGE INTO vaccinations (id, pet_id, user_id, vaccine_name, vaccination_date, next_due_date, vet_name, clinic_name, notes, status) " +
                         "KEY(id) VALUES ('c0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'FVRCP Feline Core', '2025-02-14', '2026-02-14', 'Dr. Emily Chen', 'Paws & Whiskers Clinic', 'Overdue annual feline core vaccination.', 'Overdue')");

            stmt.execute("MERGE INTO medical_records (id, pet_id, user_id, visit_date, diagnosis, treatment, medication, vet_name, clinic_name, cost, follow_up_date, notes) " +
                         "KEY(id) VALUES ('d0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', '2026-01-20', 'Minor ear infection (Otitis Externa)', 'Ear flush and topical antibacterial drops', 'Otomax ointment 2x daily', 'Dr. Alex Morales', 'Valley Animal Hospital', 85.00, '2026-02-05', 'Ear condition cleared up.')");

            stmt.execute("MERGE INTO appointments (id, pet_id, user_id, title, appointment_type, appointment_date, provider_name, location, status, notes) " +
                         "KEY(id) VALUES ('f0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'Annual Grooming & Spa', 'Grooming', '2026-10-15 10:00:00', 'Happy Tails Spa', '124 Elm Street', 'Scheduled', 'Full de-shedding bath.')");

            stmt.execute("MERGE INTO feeding_schedules (id, pet_id, user_id, food_type, quantity, feeding_time, frequency, status, notes) " +
                         "KEY(id) VALUES ('e0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'Salmon & Sweet Potato Kibble', '1.5 cups', '08:00 AM', 'Twice Daily', 'Active', 'Avoid chicken protein.')");

            stmt.execute("MERGE INTO reminders (id, user_id, pet_id, title, reminder_date, reminder_type, is_completed) " +
                         "KEY(id) VALUES ('90000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'Monthly Heartworm Chewable', '2026-10-05', 'Medication', FALSE)");

            System.out.println("Local database initialized successfully with demo data!");
        } catch (SQLException e) {
            System.err.println("Error initializing local database schema: " + e.getMessage());
        }
    }
}
