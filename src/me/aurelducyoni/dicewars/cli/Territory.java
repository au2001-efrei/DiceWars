package me.aurelducyoni.dicewars.cli;

import java.util.Collections;
import java.util.HashMap;

public class Territory {

    private static int MAX_ID = 0;

    private final int id, x, y;
    private Player owner;
    private int strength;
    private final java.util.Map<Integer, Territory> neighbors;

    public Territory(int x, int y, Player owner, int strength) {
        this.id = ++MAX_ID;
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.strength = strength;

        neighbors = new HashMap<>();
    }

    public Territory(int x, int y, Player owner, int strength, java.util.Map<Integer, Territory> neighbors) {
        this(x, y, owner, strength);

        this.neighbors.putAll(neighbors);
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int[] getDiceRoll() {
        int[] roll = new int[strength];
        for (int i = 0; i < strength; i++)
            roll[i] = Game.RANDOM.nextInt(6) + 1;

        return roll;
    }

    public java.util.Map<Integer, Territory> getNeighbors() {
        return Collections.unmodifiableMap(neighbors);
    }

    public void addNeighbor(Territory neighbor) {
        neighbors.put(neighbor.getId(), neighbor);
    }

    public boolean canAttack() {
        if (strength <= 1) return false;

        for (Territory neighbor : neighbors.values())
            if (neighbor.getOwner() != owner)
                return true;

        return false;
    }

    public boolean attack(Territory target) {
        if (!neighbors.containsValue(target) || target.getOwner() == owner || strength <= 1)
            return false;

        int selfRoll = 0;
        System.out.print("You roll...");
        for (int dice : getDiceRoll()) {
            System.out.print(" " + dice);
            selfRoll += dice;
        }
        System.out.println();

        int targetRoll = 0;
        System.out.print("They roll...");
        for (int dice : target.getDiceRoll()) {
            System.out.print(" " + dice);
            targetRoll += dice;
        }
        System.out.println();

        if (selfRoll > targetRoll) {
            System.out.println("You win! You have successfully conquered territory #" + target.getId() + " of player " + target.getOwner().getId() + ".");
            target.owner.removeTerritory(target);
            target.owner = owner;
            owner.addTerritory(target);
            target.strength = strength - 1;
            strength = 1;
            return true;
        } else if (selfRoll < targetRoll) {
            System.out.println("You lose! " + (strength - 1) + " of your dices have been removed from territory #" + id + ".");
            strength = 1;
        } else {
            System.out.println("Draw!");
        }

        return false;
    }

}
