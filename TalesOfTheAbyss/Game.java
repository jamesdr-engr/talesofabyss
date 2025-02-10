package talesofabyss;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class Game {
    private Player player;
    private Location currentLocation;
    private Map<String, Location> locations;
    private Scanner scanner;
    private LoginSystem loginSystem;
    private DatabaseManager dbManager;

    public Game(LoginSystem loginSystem) {
        this.loginSystem = loginSystem;
        this.dbManager = loginSystem.getDatabaseManager();
        scanner = new Scanner(System.in);
        locations = new HashMap<>();
        initializeLocations();
        createOrLoadCharacter();
    }

    private void createOrLoadCharacter() {
        System.out.println("\nWelcome to Tales of the Abyss!");
        System.out.println("1. Create New Character");
        System.out.println("2. Load Existing Character");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            System.out.print("Enter character name: ");
            String name = scanner.nextLine();
            player = new Player(name);
            if (dbManager.createCharacter(loginSystem.getCurrentUserId(), player)) {
                System.out.println("Character created successfully!");
                // Add first achievement
                dbManager.unlockAchievement(loginSystem.getCurrentCharacterId(), "New Adventure", "Created your first character");
            }
        } else {
            // Load character logic would go here
            // For now, create a default character
            player = new Player("Hero");
        }
    }

    private void initializeLocations() {
        Location village = new Location("Village", "A small, quiet village where your journey begins.");
        Location forest = new Location("Forest", "A dark forest with tall trees. Strange creatures lurk in the shadows.");
        Location cave = new Location("Cave", "A damp cave with faint echoes. The air feels heavy with danger.");
        Location ruins = new Location("Ruins", "Ancient ruins filled with mystery and powerful guardians.");
        Location tower = new Location("Tower", "A towering structure reaching the skies. Legend says an artifact lies here.");

        ruins.setPuzzle(new Puzzle("What has keys but can't open locks?", "Keyboard", "You solved the riddle and gain a Magic Crystal!", "Magic Crystal"));

        village.addPath("north", forest);
        forest.addPath("south", village);
        forest.addPath("east", cave);
        forest.addPath("west", ruins);
        cave.addPath("west", forest);
        ruins.addPath("east", forest);
        ruins.addPath("north", tower);
        tower.addPath("south", ruins);

        locations.put("Village", village);
        locations.put("Forest", forest);
        locations.put("Cave", cave);
        locations.put("Ruins", ruins);
        locations.put("Tower", tower);

        currentLocation = village;
    }

    public void start() {
        System.out.println("You find yourself in a mysterious world, seeking an ancient artifact...");
        
        while (true) {
            clearConsole();
            System.out.println("\n=== " + currentLocation.getName() + " ===");
            System.out.println(currentLocation.getDescription());
            
            if (currentLocation.getPuzzle() != null && !currentLocation.getPuzzle().isSolved()) {
                solvePuzzle(currentLocation.getPuzzle());
            }
            
            System.out.println("\nWhat would you like to do?");
            System.out.println("1. Move");
            System.out.println("2. Check Status");
            System.out.println("3. Check Inventory");
            System.out.println("4. Search Area");
            System.out.println("5. Battle Enemy");
            System.out.println("6. Save Game");
            System.out.println("7. Load Game");
            System.out.println("8. Exit Game");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    move();
                    break;
                case "2":
                    player.showStatus();
                    waitForEnter();
                    break;
                case "3":
                    player.showInventory();
                    waitForEnter();
                    break;
                case "4":
                    searchArea();
                    waitForEnter();
                    break;
                case "5":
                    battleEnemy();
                    waitForEnter();
                    break;
                case "6":
                    saveGame();
                    waitForEnter();
                    break;
                case "7":
                    loadGame();
                    waitForEnter();
                    break;
                case "8":
                    System.out.println("Thank you for playing Tales of the Abyss!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    waitForEnter();
            }
        }
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void move() {
        System.out.println("\nAvailable paths:");
        for (String direction : currentLocation.getPaths().keySet()) {
            System.out.println("- " + direction);
        }

        System.out.println("\nWhere would you like to go?");
        String direction = scanner.nextLine().toLowerCase();
        Location nextLocation = currentLocation.getPaths().get(direction);

        if (nextLocation != null) {
            currentLocation = nextLocation;
            System.out.println("You move " + direction + " to the " + currentLocation.getName() + ".");
            System.out.println(currentLocation.getDescription());
        } else {
            System.out.println("You can't go that way.");
        }
    }

    private void searchArea() {
        if (currentLocation.getName().equals("Tower") && player.hasItem("Artifact")) {
            System.out.println("You already found the artifact. There's nothing more here.");
            return;
        }

        if (currentLocation.getName().equals("Tower") && player.getLevel() < 5) {
            System.out.println("The tower's magical barrier repels you. You need to be at least level 5 to search here.");
            return;
        }

        System.out.println("Searching the " + currentLocation.getName() + "...");
        if (currentLocation.getName().equals("Tower") && !player.hasItem("Artifact")) {
            System.out.println("You found the legendary artifact! You win the game! Congratulations!");
            System.exit(0);
        }

        if (Math.random() < 0.7) { // 70% chance to find something
            String[] items = {"Health Potion", "Steel Sword", "Magic Shield", "Steel Plate", "Enchanted Staff"};
            String foundItem = items[(int)(Math.random() * items.length)];
            System.out.println("You found a " + foundItem + "!");
            player.addItem(foundItem);
        } else {
            System.out.println("You found nothing of interest.");
        }
    }

    private void battleEnemy() {
        Enemy enemy = EnemyFactory.createEnemy(currentLocation.getName());
        Combat combat = new Combat(player, enemy);
        combat.start();
    }

    private void solvePuzzle(Puzzle puzzle) {
        System.out.println("\nPuzzle: " + puzzle.getQuestion());
        System.out.println("Your answer: ");
        String answer = scanner.nextLine();

        if (puzzle.solve(answer)) {
            System.out.println(puzzle.getRewardMessage());
            player.addItem(puzzle.getReward());
        } else {
            System.out.println("Incorrect answer. Try again later.");
        }
    }

    private void saveGame() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("savegame.txt"))) {
            writer.println(player.getName());
            writer.println(player.getHealth());
            writer.println(player.getLevel());
            for (String item : player.getInventory()) {
                writer.println(item);
            }
            writer.println("LOCATION:" + currentLocation.getName());
            
            // Save to database
            dbManager.saveCharacterProgress(loginSystem.getCurrentCharacterId(), player, currentLocation.getName());
            
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred while saving the game.");
        }
    }

    private void loadGame() {
        try (BufferedReader reader = new BufferedReader(new FileReader("savegame.txt"))) {
            String name = reader.readLine();
            int health = Integer.parseInt(reader.readLine());
            int level = Integer.parseInt(reader.readLine());
            List<String> inventory = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("LOCATION:")) {
                    String locationName = line.substring(9);
                    currentLocation = locations.get(locationName);
                } else {
                    inventory.add(line);
                }
            }
            // Create new player with loaded data
            player = new Player(name);
            for (int i = 1; i < level; i++) {
                player.gainExperience(100); // Level up to match saved level
            }
            for (String item : inventory) {
                player.addItem(item);
            }
            System.out.println("Game loaded successfully!");
        } catch (IOException | NumberFormatException e) {
            System.out.println("An error occurred while loading the game.");
        }
    }
}
