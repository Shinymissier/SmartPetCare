package com.petcare.config;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

public class AppConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final AppConfig INSTANCE = new AppConfig();
    private final Properties properties = new Properties();

    private String databaseMode = "LOCAL_FALLBACK"; // SUPABASE_JDBC, SUPABASE_REST, LOCAL_FALLBACK
    private String supabaseUrl = "";
    private String supabaseAnonKey = "";
    private String supabaseServiceKey = "";
    private String supabaseJdbcUrl = "";
    private String supabaseJdbcUser = "postgres";
    private String supabaseJdbcPassword = "";
    private String localJdbcUrl = "jdbc:h2:file:C:/Users/SHINY/pet/data/petcaredb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE";
    private String localJdbcUser = "sa";
    private String localJdbcPassword = "";

    private AppConfig() {
        loadProperties();
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    private void loadProperties() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("supabase.properties")) {
            if (in != null) {
                properties.load(in);
                this.databaseMode = getEnvOrProp("DB_MODE", "database.mode", "LOCAL_FALLBACK");
                this.supabaseUrl = getEnvOrProp("SUPABASE_URL", "supabase.url", "");
                this.supabaseAnonKey = getEnvOrProp("SUPABASE_ANON_KEY", "supabase.anon.key", "");
                this.supabaseServiceKey = getEnvOrProp("SUPABASE_SERVICE_ROLE_KEY", "supabase.service.role.key", "");
                this.supabaseJdbcUrl = getEnvOrProp("SUPABASE_JDBC_URL", "supabase.jdbc.url", "");
                this.supabaseJdbcUser = getEnvOrProp("SUPABASE_JDBC_USER", "supabase.jdbc.user", "postgres");
                this.supabaseJdbcPassword = getEnvOrProp("SUPABASE_JDBC_PASSWORD", "supabase.jdbc.password", "");
                this.localJdbcUrl = getEnvOrProp("LOCAL_JDBC_URL", "local.jdbc.url", localJdbcUrl);
                this.localJdbcUser = getEnvOrProp("LOCAL_JDBC_USER", "local.jdbc.user", "sa");
                this.localJdbcPassword = getEnvOrProp("LOCAL_JDBC_PASSWORD", "local.jdbc.password", "");
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load supabase.properties: " + e.getMessage());
        }
    }

    private String getEnvOrProp(String envKey, String propKey, String defaultValue) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }
        String propVal = properties.getProperty(propKey);
        if (propVal != null && !propVal.trim().isEmpty()) {
            return propVal.trim();
        }
        return defaultValue;
    }

    public void updateRuntimeConfig(String mode, String url, String anonKey, String jdbcUrl, String user, String pass) {
        if (mode != null) this.databaseMode = mode;
        if (url != null) this.supabaseUrl = url;
        if (anonKey != null) this.supabaseAnonKey = anonKey;
        if (jdbcUrl != null) this.supabaseJdbcUrl = jdbcUrl;
        if (user != null) this.supabaseJdbcUser = user;
        if (pass != null) this.supabaseJdbcPassword = pass;
    }

    public boolean isSupabaseConfigured() {
        return (supabaseUrl != null && !supabaseUrl.isEmpty() && !supabaseUrl.contains("your-project-id"))
                || (supabaseJdbcUrl != null && !supabaseJdbcUrl.isEmpty() && !supabaseJdbcUrl.contains("your-project"));
    }

    public String getDatabaseMode() { return databaseMode; }
    public void setDatabaseMode(String databaseMode) { this.databaseMode = databaseMode; }

    public String getSupabaseUrl() { return supabaseUrl; }
    public void setSupabaseUrl(String supabaseUrl) { this.supabaseUrl = supabaseUrl; }

    public String getSupabaseAnonKey() { return supabaseAnonKey; }
    public void setSupabaseAnonKey(String supabaseAnonKey) { this.supabaseAnonKey = supabaseAnonKey; }

    public String getSupabaseServiceKey() { return supabaseServiceKey; }
    public void setSupabaseServiceKey(String supabaseServiceKey) { this.supabaseServiceKey = supabaseServiceKey; }

    public String getSupabaseJdbcUrl() { return supabaseJdbcUrl; }
    public void setSupabaseJdbcUrl(String supabaseJdbcUrl) { this.supabaseJdbcUrl = supabaseJdbcUrl; }

    public String getSupabaseJdbcUser() { return supabaseJdbcUser; }
    public void setSupabaseJdbcUser(String supabaseJdbcUser) { this.supabaseJdbcUser = supabaseJdbcUser; }

    public String getSupabaseJdbcPassword() { return supabaseJdbcPassword; }
    public void setSupabaseJdbcPassword(String supabaseJdbcPassword) { this.supabaseJdbcPassword = supabaseJdbcPassword; }

    public String getLocalJdbcUrl() { return localJdbcUrl; }
    public String getLocalJdbcUser() { return localJdbcUser; }
    public String getLocalJdbcPassword() { return localJdbcPassword; }
}
