package talesofabyss;

import java.util.*;

public class Player {
    private String name;
    private int health;
    private int maxHealth;
    private int attack;
    private int defense;
    private int level;
    private int experience;
    private List<String> inventory;
    private Map<String, Integer> equipment;
    private final int INVENTORY_LIMIT = 10;

    public Player(String name) {
        this.name = name;
        this.level = 1;
        this.experience = 0;
        this.maxHealth = 100;
        this.health = maxHealth;
        this.attack = 10;
        this.defense = 5;
        this.inventory = new ArrayList<>();
        this.equipment = new HashMap<>();
        equipment.put("weapon", 0);
        equipment.put("armor", 0);
    }

    public void gainExperience(int exp) {
        experience += exp;
        System.out.println("Gained " + exp + " experience points!");
        
        // Check for level up
        int experienceNeeded = level * 100;
        if (experience >= experienceNeeded) {
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        maxHealth += 20;
        health = maxHealth;
        attack += 5;
        defense += 3;
        experience = 0;
        
        System.out.println("\n🎉 LEVEL UP! 🎉");
        System.out.println("You are now level " + level + "!");
        System.out.println("Max Health increased to " + maxHealth);
        System.out.println("Attack increased to " + attack);
        System.out.println("Defense increased to " + defense);
    }

    public int attack() {
        Random random = new Random();
        int weaponBonus = equipment.get("weapon");
        int damage = attack + weaponBonus + random.nextInt(6); // d6 roll
        return Math.max(1, damage);
    }

    public void takeDamage(int damage) {
        int armorBonus = equipment.get("armor");
        int actualDamage = Math.max(1, damage - (defense + armorBonus));
        health -= actualDamage;
        System.out.println("You take " + actualDamage + " damage. Current health: " + health + "/" + maxHealth);
    }

    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
        System.out.println("Healed " + amount + " points. Current health: " + health + "/" + maxHealth);
    }

    public void addItem(String item) {
        if (inventory.size() >= INVENTORY_LIMIT) {
            System.out.println("Your inventory is full! You cannot carry more items.");
            return;
        }

        inventory.add(item);
        System.out.println(item + " added to your inventory.");

        // Check if it's equipment
        if (isWeapon(item)) {
            System.out.println("This appears to be a weapon. Would you like to equip it? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            if (scanner.nextLine().toLowerCase().startsWith("y")) {
                equipWeapon(item);
            }
        } else if (isArmor(item)) {
            System.out.println("This appears to be armor. Would you like to equip it? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            if (scanner.nextLine().toLowerCase().startsWith("y")) {
                equipArmor(item);
            }
        }
    }

    private boolean isWeapon(String item) {
        return item.toLowerCase().contains("sword") || 
               item.toLowerCase().contains("axe") || 
               item.toLowerCase().contains("staff");
    }

    private boolean isArmor(String item) {
        return item.toLowerCase().contains("armor") || 
               item.toLowerCase().contains("shield") || 
               item.toLowerCase().contains("plate");
    }

    private void equipWeapon(String weapon) {
        equipment.put("weapon", calculateItemBonus(weapon));
        System.out.println("Equipped " + weapon + "! Attack bonus: +" + equipment.get("weapon"));
    }

    private void equipArmor(String armor) {
        equipment.put("armor", calculateItemBonus(armor));
        System.out.println("Equipped " + armor + "! Defense bonus: +" + equipment.get("armor"));
    }

    private int calculateItemBonus(String item) {
        // Simple bonus calculation based on item name
        if (item.toLowerCase().contains("magic") || item.toLowerCase().contains("enchanted")) {
            return 10;
        } else if (item.toLowerCase().contains("steel") || item.toLowerCase().contains("reinforced")) {
            return 7;
        } else {
            return 5;
        }
    }

    public void showStatus() {
        System.out.println("\n=== " + name + "'s Status ===");
        System.out.println("Level: " + level);
        System.out.println("Experience: " + experience + "/" + (level * 100));
        System.out.println("Health: " + health + "/" + maxHealth);
        System.out.println("Attack: " + attack + " (+" + equipment.get("weapon") + ")");
        System.out.println("Defense: " + defense + " (+" + equipment.get("armor") + ")");
    }

    public void showInventory() {
        System.out.println("\n=== Inventory (" + inventory.size() + "/" + INVENTORY_LIMIT + ") ===");
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
        } else {
            for (String item : inventory) {
                System.out.println("- " + item);
            }
        }
    }

    // Getters
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public String getName() { return name; }
    public List<String> getInventory() { return inventory; }
    public boolean hasItem(String item) { return inventory.contains(item); }
    public int getLevel() { return level; }
    public int getAttack() { return attack + equipment.get("weapon"); }
    public int getDefense() { return defense + equipment.get("armor"); }
}
