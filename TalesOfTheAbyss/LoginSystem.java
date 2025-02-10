package talesofabyss;

import java.util.Scanner;
import java.util.regex.Pattern;

public class LoginSystem {
    private DatabaseManager dbManager;
    private Scanner scanner;
    private int currentUserId;
    private int currentCharacterId;

    public LoginSystem() {
        dbManager = new DatabaseManager();
        scanner = new Scanner(System.in);
        currentUserId = -1;
        currentCharacterId = -1;
    }

    public boolean start() {
        while (true) {
            System.out.println("\n=== Tales of the Abyss ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    if (login()) {
                        return true;
                    }
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    System.out.println("Goodbye!");
                    return false;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private boolean login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentUserId = dbManager.loginUser(username, password);
        if (currentUserId != -1) {
            System.out.println("Login successful!");
            return true;
        } else {
            System.out.println("Invalid username or password.");
            return false;
        }
    }

    private void register() {
        System.out.print("Enter username (3-20 characters): ");
        String username = scanner.nextLine();
        if (!isValidUsername(username)) {
            System.out.println("Invalid username. Must be 3-20 characters long and contain only letters, numbers, and underscores.");
            return;
        }

        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        if (!isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }

        System.out.print("Enter password (min 6 characters): ");
        String password = scanner.nextLine();
        if (!isValidPassword(password)) {
            System.out.println("Invalid password. Must be at least 6 characters long.");
            return;
        }

        System.out.print("Confirm password: ");
        String confirmPassword = scanner.nextLine();
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        if (dbManager.registerUser(username, password, email)) {
            System.out.println("Registration successful! Please login to continue.");
        } else {
            System.out.println("Registration failed. Username or email might already be taken.");
        }
    }

    private boolean isValidUsername(String username) {
        return Pattern.matches("^[a-zA-Z0-9_]{3,20}$", username);
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentCharacterId(int characterId) {
        this.currentCharacterId = characterId;
    }

    public int getCurrentCharacterId() {
        return currentCharacterId;
    }

    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }
}
