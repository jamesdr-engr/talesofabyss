package talesofabyss;

import java.util.Random;
import java.util.Scanner;

public class Combat {
    private Player player;
    private Enemy enemy;
    private Scanner scanner;
    private Random random;

    public Combat(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.scanner = new Scanner(System.in);
        this.random = new Random();
    }

    public void start() {
        System.out.println("\n⚔️ A " + enemy.getName() + " appears! ⚔️");
        
        while (enemy.isAlive() && player.getHealth() > 0) {
            showCombatStatus();
            playerTurn();
            
            if (enemy.isAlive()) {
                enemyTurn();
            }
        }

        if (player.getHealth() > 0) {
            handleVictory();
        } else {
            handleDefeat();
        }
    }

    private void showCombatStatus() {
        System.out.println("\n=== Combat Status ===");
        System.out.println("Player HP: " + player.getHealth() + "/" + player.getMaxHealth());
        System.out.println("Enemy HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth());
        System.out.println("==================");
    }

    private void playerTurn() {
        System.out.println("\nYour turn! What would you like to do?");
        System.out.println("1. Attack");
        System.out.println("2. Use Item");
        System.out.println("3. Attempt to Flee");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                handlePlayerAttack();
                break;
            case "2":
                handleUseItem();
                break;
            case "3":
                if (attemptFlee()) {
                    return;
                }
                break;
            default:
                System.out.println("Invalid choice. You hesitate and lose your turn!");
        }
    }

    private void handlePlayerAttack() {
        int damage = player.attack();
        System.out.println("You attack the " + enemy.getName() + "!");
        enemy.takeDamage(damage);
    }

    private void handleUseItem() {
        player.showInventory();
        if (player.getInventory().isEmpty()) {
            System.out.println("You have no items to use!");
            return;
        }

        System.out.println("Enter item name to use (or press Enter to cancel):");
        String itemChoice = scanner.nextLine();
        
        if (!itemChoice.isEmpty() && player.hasItem(itemChoice)) {
            if (itemChoice.toLowerCase().contains("potion")) {
                player.heal(30);
                player.getInventory().remove(itemChoice);
            } else {
                System.out.println("You can't use that item in combat!");
            }
        }
    }

    private boolean attemptFlee() {
        if (random.nextInt(100) < 40) {  // 40% chance to flee
            System.out.println("You successfully fled from the battle!");
            return true;
        } else {
            System.out.println("You failed to flee!");
            return false;
        }
    }

    private void enemyTurn() {
        // 20% chance for special ability
        if (random.nextInt(100) < 20) {
            System.out.println("\n" + enemy.getName() + " uses " + enemy.useSpecialAbility() + "!");
            int specialDamage = enemy.attack() * 2;
            player.takeDamage(specialDamage);
        } else {
            System.out.println("\n" + enemy.getName() + " attacks!");
            int damage = enemy.attack();
            player.takeDamage(damage);
        }
    }

    private void handleVictory() {
        System.out.println("\n🎉 Victory! You defeated the " + enemy.getName() + "! 🎉");
        
        // Grant experience
        player.gainExperience(enemy.getExperienceValue());
        
        // Drop item
        if (random.nextInt(100) < 70) {  // 70% chance to drop item
            String droppedItem = enemy.getDropItem();
            System.out.println("The enemy dropped: " + droppedItem);
            player.addItem(droppedItem);
        }
    }

    private void handleDefeat() {
        System.out.println("\n💀 You have been defeated by the " + enemy.getName() + "! 💀");
        System.out.println("Game Over");
        System.exit(0);
    }
}
