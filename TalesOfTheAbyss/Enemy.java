package talesofabyss;

import java.util.Random;

public class Enemy {
    private String name;
    private int health;
    private int maxHealth;
    private int attack;
    private int defense;
    private String specialAbility;
    private String dropItem;
    private int experienceValue;

    public Enemy(String name, int health, int attack, int defense, String specialAbility, String dropItem, int experienceValue) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.attack = attack;
        this.defense = defense;
        this.specialAbility = specialAbility;
        this.dropItem = dropItem;
        this.experienceValue = experienceValue;
    }

    public int attack() {
        Random random = new Random();
        // Base damage with some randomness
        int damage = attack + random.nextInt(5) - 2;
        return Math.max(1, damage); // Minimum damage of 1
    }

    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage - defense);
        health -= actualDamage;
        System.out.println(name + " takes " + actualDamage + " damage!");
    }

    public String useSpecialAbility() {
        return specialAbility;
    }

    public String getDropItem() {
        return dropItem;
    }

    public int getExperienceValue() {
        return experienceValue;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getDefense() {
        return defense;
    }
}
