import java.sql.*;
import java.util.UUID;

public class TestConn {
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://db.lzdygewiszzteajfwocp.supabase.co:5432/postgres?sslmode=require",
                "postgres", "#Shiny@2006$");
            System.out.println("SUCCESS! Connected.");

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, email, full_name FROM profiles");
            System.out.println("Profiles in DB:");
            String userId = null;
            while (rs.next()) {
                userId = rs.getString("id");
                System.out.println("  User: " + userId + " - " + rs.getString("email"));
            }

            rs = st.executeQuery("SELECT count(*) FROM pets");
            if (rs.next()) {
                System.out.println("Pets count: " + rs.getInt(1));
            }

            if (userId != null) {
                // Test null pet_id in reminders with ?::uuid
                String sqlRem = "INSERT INTO reminders (id, user_id, pet_id, title, reminder_date, reminder_type, is_completed) " +
                                "VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, ?, ?)";
                PreparedStatement psRem = conn.prepareStatement(sqlRem);
                String remId = UUID.randomUUID().toString();
                psRem.setString(1, remId);
                psRem.setString(2, userId);
                psRem.setNull(3, Types.OTHER); // or psRem.setString(3, null)
                psRem.setString(4, "Test Reminder");
                psRem.setDate(5, java.sql.Date.valueOf("2026-10-15"));
                psRem.setString(6, "General");
                psRem.setBoolean(7, false);
                try {
                    psRem.executeUpdate();
                    System.out.println("Reminder with null pet_id (setNull Types.OTHER) SUCCESS!");
                    conn.prepareStatement("DELETE FROM reminders WHERE id = '" + remId + "'::uuid").executeUpdate();
                } catch (Exception ex) {
                    System.out.println("Reminder setNull Types.OTHER FAILED: " + ex.getMessage());
                }

                // Test with psRem.setString(3, null)
                String remId2 = UUID.randomUUID().toString();
                psRem.setString(1, remId2);
                psRem.setString(2, userId);
                psRem.setString(3, null);
                psRem.setString(4, "Test Reminder 2");
                psRem.setDate(5, java.sql.Date.valueOf("2026-10-15"));
                psRem.setString(6, "General");
                psRem.setBoolean(7, false);
                try {
                    psRem.executeUpdate();
                    System.out.println("Reminder with null pet_id (setString null) SUCCESS!");
                    conn.prepareStatement("DELETE FROM reminders WHERE id = '" + remId2 + "'::uuid").executeUpdate();
                } catch (Exception ex) {
                    System.out.println("Reminder setString null FAILED: " + ex.getMessage());
                }
            } else {
                System.out.println("NO USERS IN profiles TABLE!");
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
