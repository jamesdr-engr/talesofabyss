package talesofabyss;

import java.util.Random;

public class EnemyFactory {
    public static Enemy createEnemy(String location) {
        Random random = new Random();
        
        switch(location.toLowerCase()) {
            case "forest":
                return createForestEnemy();
            case "cave":
                return createCaveEnemy();
            case "ruins":
                return createRuinsEnemy();
            case "tower":
                return createTowerEnemy();
            default:
                return createBasicEnemy();
        }
    }

    private static Enemy createForestEnemy() {
        String[] forestEnemies = {
            "Wolf|30|15|5|Howl|Wolf Pelt|20",
            "Bandit|40|12|8|Steal|Gold Pouch|25",
            "Dark Elf|35|18|6|Magic Arrow|Magic Essence|30"
        };
        return createEnemyFromString(getRandomEnemy(forestEnemies));
    }

    private static Enemy createCaveEnemy() {
        String[] caveEnemies = {
            "Troll|60|20|12|Smash|Troll Hide|40",
            "Giant Bat|25|12|4|Screech|Bat Wing|15",
            "Cave Spider|30|16|6|Poison|Spider Venom|25"
        };
        return createEnemyFromString(getRandomEnemy(caveEnemies));
    }

    private static Enemy createRuinsEnemy() {
        String[] ruinsEnemies = {
            "Ancient Guardian|70|25|15|Stone Shield|Ancient Core|50",
            "Ghost|40|20|8|Possess|Ectoplasm|35",
            "Skeleton Warrior|45|22|10|Bone Strike|Enchanted Bone|30"
        };
        return createEnemyFromString(getRandomEnemy(ruinsEnemies));
    }

    private static Enemy createTowerEnemy() {
        String[] towerEnemies = {
            "Dragon Cultist|80|30|20|Dragon's Breath|Dragon Scale|60",
            "Corrupted Mage|65|35|15|Dark Magic|Magic Crystal|55",
            "Gargoyle|75|28|25|Stone Form|Gargoyle Heart|50"
        };
        return createEnemyFromString(getRandomEnemy(towerEnemies));
    }

    private static Enemy createBasicEnemy() {
        String[] basicEnemies = {
            "Goblin|20|10|5|Sneak|Goblin Ear|10",
            "Wild Dog|25|12|3|Bite|Dog Tooth|15",
            "Thief|30|15|4|Backstab|Stolen Goods|20"
        };
        return createEnemyFromString(getRandomEnemy(basicEnemies));
    }

    private static String getRandomEnemy(String[] enemies) {
        Random random = new Random();
        return enemies[random.nextInt(enemies.length)];
    }

    private static Enemy createEnemyFromString(String enemyString) {
        String[] parts = enemyString.split("\\|");
        return new Enemy(
            parts[0],                    // name
            Integer.parseInt(parts[1]),  // health
            Integer.parseInt(parts[2]),  // attack
            Integer.parseInt(parts[3]),  // defense
            parts[4],                    // specialAbility
            parts[5],                    // dropItem
            Integer.parseInt(parts[6])   // experienceValue
        );
    }
}
