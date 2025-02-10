package talesofabyss;

import java.util.Map;
import java.util.HashMap;

public class Location {
    private String name;
    private String description;
    private Map<String, Location> paths;
    private Puzzle puzzle;

    public Location(String name, String description) {
        this.name = name;
        this.description = description;
        this.paths = new HashMap<>();
    }

    public void addPath(String direction, Location location) {
        paths.put(direction.toLowerCase(), location);
    }

    public Map<String, Location> getPaths() {
        return paths;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }
}
