package me.aurelducyoni.dicewars.cli;

import me.aurelducyoni.dicewars.cli.exceptions.InvalidTerritoryException;
import me.aurelducyoni.dicewars.cli.exceptions.SelfTargetException;
import me.aurelducyoni.dicewars.cli.exceptions.WeakTerritoryException;

import java.util.*;

public class Player {

    private static int MAX_ID = 0;

    private final int id;
    private final java.util.Map<Integer, Territory> territories;
    private boolean playing = false;

    public Player(int id) {
        this.id = id;
        territories = new HashMap<>();
    }

    public Player() {
        this(++MAX_ID);
    }

    public Player(java.util.Map<Integer, Territory> territories) {
        this();

        this.territories.putAll(territories);
    }

    public int getId() {
        return id;
    }

    public java.util.Map<Integer, Territory> getTerritories() {
        return Collections.unmodifiableMap(territories);
    }

    public void addTerritory(Territory territory) {
        territories.put(territory.getId(), territory);
    }

    public void removeTerritory(Territory territory) {
        territories.remove(territory.getId(), territory);
    }

    public int getMaxContiguous() {
        List<Set<Territory>> groups = new ArrayList<>();
        for (Territory territory : territories.values()) {
            Set<Territory> group = new HashSet<>();
            group.add(territory);
            groups.add(group);
        }

        merge: while (true) {
            for (int i = 1; i < groups.size(); i++) {
                for (int j = 0; j < i; j++) {
                    for (Territory territory1 : groups.get(i)) {
                        for (Territory territory2 : groups.get(j)) {
                            if (territory1.getNeighbors().containsValue(territory2)) {
                                groups.get(i).addAll(groups.get(j));
                                groups.remove(j);
                                continue merge;
                            }
                        }
                    }
                }
            }
            break;
        }

        int maxContiguous = 0;
        for (Set<Territory> group : groups)
            if (group.size() > maxContiguous)
                maxContiguous = group.size();

        return maxContiguous;
    }

    public void attackTerritory() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the ID of your territory and the territory to attack separated by a space, or press enter to end your turn.");
        System.out.print("Action: ");
        String action = scanner.nextLine();
        if (action.matches("^\\d+ \\d+$")) {
            int territoryId = Integer.parseInt(action.split(" ")[0], 10);
            int targetId = Integer.parseInt(action.split(" ")[1], 10);

            try {
                attackTerritory(territoryId, targetId);
            } catch (SelfTargetException | InvalidTerritoryException | WeakTerritoryException e) {
                System.out.println("Failed: " + e.getMessage());
            }
        } else endTurn();
    }

    public void attackTerritory(int territoryId, int targetId) throws InvalidTerritoryException, SelfTargetException, WeakTerritoryException {
        Territory attacker = territories.get(territoryId);
        if (attacker == null)
            throw new InvalidTerritoryException("attacker");

        if (attacker.getStrength() <= 1)
            throw new WeakTerritoryException();

        Territory target = attacker.getNeighbors().get(targetId);
        if (target == null)
            throw new InvalidTerritoryException("target");

        if (target.getOwner() == this)
            throw new SelfTargetException();

        attacker.attack(target);
    }

    public void endTurn() {
        playing = false;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void startPlaying() {
        System.out.println("Player " + id + ", your turn!");
        playing = true;
    }

    public boolean canAttack() {
        for (Territory territory : territories.values())
            if (territory.canAttack())
                return true;

        return false;
    }

}
