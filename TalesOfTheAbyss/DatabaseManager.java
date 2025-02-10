import java.sql.*;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:talesofabyss.db";
    private Connection conn;

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    private void createTables() {
        try (Statement stmt = conn.createStatement()) {
            // Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    registration_date TEXT NOT NULL,
                    last_login TEXT NOT NULL
                )
            """);

            // Player Characters table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS player_characters (
                    character_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    character_name TEXT NOT NULL,
                    level INTEGER NOT NULL,
                    experience INTEGER NOT NULL,
                    health INTEGER NOT NULL,
                    attack INTEGER NOT NULL,
                    defense INTEGER NOT NULL,
                    current_location TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(user_id)
                )
            """);

            // Inventory table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS inventory (
                    inventory_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    character_id INTEGER NOT NULL,
                    item_name TEXT NOT NULL,
                    item_type TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    FOREIGN KEY (character_id) REFERENCES player_characters(character_id)
                )
            """);

            // Achievements table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS achievements (
                    achievement_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    character_id INTEGER NOT NULL,
                    achievement_name TEXT NOT NULL,
                    date_unlocked TEXT NOT NULL,
                    description TEXT NOT NULL,
                    FOREIGN KEY (character_id) REFERENCES player_characters(character_id)
                )
            """);

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean registerUser(String username, String password, String email) {
        String hashedPassword = hashPassword(password);
        String currentTime = LocalDateTime.now().toString();

        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO users (username, password, email, registration_date, last_login) VALUES (?, ?, ?, ?, ?)")) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email);
            pstmt.setString(4, currentTime);
            pstmt.setString(5, currentTime);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    public int loginUser(String username, String password) {
        String hashedPassword = hashPassword(password);
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT user_id FROM users WHERE username = ? AND password = ?")) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                updateLastLogin(userId);
                return userId;
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return -1;
    }

    private void updateLastLogin(int userId) {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE users SET last_login = ? WHERE user_id = ?")) {
            pstmt.setString(1, LocalDateTime.now().toString());
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating last login: " + e.getMessage());
        }
    }

    public boolean createCharacter(int userId, Player player) {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO player_characters (user_id, character_name, level, experience, health, attack, defense, current_location) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, player.getName());
            pstmt.setInt(3, player.getLevel());
            pstmt.setInt(4, 0); // Initial experience
            pstmt.setInt(5, player.getHealth());
            pstmt.setInt(6, player.getAttack());
            pstmt.setInt(7, player.getDefense());
            pstmt.setString(8, "Village"); // Starting location
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error creating character: " + e.getMessage());
            return false;
        }
    }

    public void saveCharacterProgress(int characterId, Player player, String currentLocation) {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE player_characters SET level = ?, experience = ?, health = ?, attack = ?, defense = ?, current_location = ? " +
                "WHERE character_id = ?")) {
            pstmt.setInt(1, player.getLevel());
            pstmt.setInt(2, 0); // Current experience
            pstmt.setInt(3, player.getHealth());
            pstmt.setInt(4, player.getAttack());
            pstmt.setInt(5, player.getDefense());
            pstmt.setString(6, currentLocation);
            pstmt.setInt(7, characterId);
            pstmt.executeUpdate();
            
            // Update inventory
            updateInventory(characterId, player.getInventory());
        } catch (SQLException e) {
            System.out.println("Error saving character progress: " + e.getMessage());
        }
    }

    private void updateInventory(int characterId, List<String> inventory) {
        try {
            // Clear current inventory
            PreparedStatement clearStmt = conn.prepareStatement("DELETE FROM inventory WHERE character_id = ?");
            clearStmt.setInt(1, characterId);
            clearStmt.executeUpdate();

            // Insert new inventory
            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO inventory (character_id, item_name, item_type, quantity) VALUES (?, ?, ?, ?)");
            
            for (String item : inventory) {
                insertStmt.setInt(1, characterId);
                insertStmt.setString(2, item);
                insertStmt.setString(3, determineItemType(item));
                insertStmt.setInt(4, 1);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error updating inventory: " + e.getMessage());
        }
    }

    private String determineItemType(String item) {
        item = item.toLowerCase();
        if (item.contains("sword") || item.contains("staff") || item.contains("axe")) {
            return "weapon";
        } else if (item.contains("armor") || item.contains("shield") || item.contains("plate")) {
            return "armor";
        } else if (item.contains("potion")) {
            return "consumable";
        } else {
            return "misc";
        }
    }

    public void unlockAchievement(int characterId, String achievementName, String description) {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO achievements (character_id, achievement_name, date_unlocked, description) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, characterId);
            pstmt.setString(2, achievementName);
            pstmt.setString(3, LocalDateTime.now().toString());
            pstmt.setString(4, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error unlocking achievement: " + e.getMessage());
        }
    }

    public List<String> getAchievements(int characterId) {
        List<String> achievements = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT achievement_name, date_unlocked FROM achievements WHERE character_id = ?")) {
            pstmt.setInt(1, characterId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                achievements.add(rs.getString("achievement_name") + " - " + rs.getString("date_unlocked"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving achievements: " + e.getMessage());
        }
        return achievements;
    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection: " + e.getMessage());
        }
    }
}
