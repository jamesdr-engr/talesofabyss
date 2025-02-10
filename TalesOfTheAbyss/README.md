# Tales of the Abyss

A text-based RPG game with database support for user accounts and save games.

## Setup Instructions for Eclipse

1. **Create New Java Project**
   - Open Eclipse
   - File -> New -> Java Project
   - Project name: `TalesOfTheAbyss`
   - Click "Finish"

2. **Import Source Files**
   - Copy all .java files into the `src` folder of your Eclipse project
   - Files to copy:
     - TalesOfTheAbyss.java
     - Game.java
     - Player.java
     - Enemy.java
     - EnemyFactory.java
     - Combat.java
     - Location.java
     - Puzzle.java
     - LoginSystem.java
     - DatabaseManager.java

3. **Install SQLite JDBC Driver**
   - Download SQLite JDBC driver:
     - Go to https://github.com/xerial/sqlite-jdbc/releases
     - Download latest .jar file (e.g., `sqlite-jdbc-3.42.0.0.jar`)
   
   - Add to Eclipse Project:
     1. Create `lib` folder in project root
     2. Copy downloaded .jar file to `lib` folder
     3. Right-click project -> Build Path -> Configure Build Path
     4. Click "Libraries" tab
     5. Click "Add JARs"
     6. Navigate to project's lib folder
     7. Select the SQLite JDBC .jar file
     8. Click "Apply and Close"

4. **Run the Game**
   - Expand your project in Package Explorer
   - Find TalesOfTheAbyss.java
   - Right-click -> Run As -> Java Application

## Game Features

- User account system with login/registration
- Character creation and progression
- Turn-based combat system
- Inventory management
- Achievement tracking
- Automatic save/load functionality
- Database persistence for all game data

## Database Schema

The game uses SQLite with the following tables:
- users: Store user accounts
- player_characters: Store character data
- inventory: Track items
- achievements: Record player achievements

## Troubleshooting

1. **Database Connection Error**
   - Make sure SQLite JDBC driver is properly added to build path
   - Check if database file has write permissions

2. **Compilation Errors**
   - Ensure all source files are in the correct package
   - Verify SQLite JDBC driver is in build path

3. **Runtime Errors**
   - Check console for specific error messages
   - Verify database file permissions
   - Ensure all game files are present
