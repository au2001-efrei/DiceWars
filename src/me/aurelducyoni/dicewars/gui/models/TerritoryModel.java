package me.aurelducyoni.dicewars.gui.models;

import java.util.Collections;
import java.util.HashMap;

public class TerritoryModel {

    private static int MAX_ID = 0;

    private final int id, x, y;
    private Player owner;
    private int strength;
    private final java.util.Map<Integer, TerritoryModel> neighbors;

    public TerritoryModel(int x, int y, Player owner, int strength) {
        this.id = ++MAX_ID;
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.strength = strength;

        neighbors = new HashMap<>();
    }

    public TerritoryModel(int x, int y, Player owner, int strength, java.util.Map<Integer, TerritoryModel> neighbors) {
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
            roll[i] = GameModel.RANDOM.nextInt(6) + 1;

        return roll;
    }

    public java.util.Map<Integer, TerritoryModel> getNeighbors() {
        return Collections.unmodifiableMap(neighbors);
    }

    public void addNeighbor(TerritoryModel neighbor) {
        neighbors.put(neighbor.getId(), neighbor);
    }

    public boolean canAttack() {
        if (strength <= 1) return false;

        for (TerritoryModel neighbor : neighbors.values())
            if (neighbor.getOwner() != owner)
                return true;

        return false;
    }

    public boolean canAttack(TerritoryModel target) {
        if (strength <= 1) return false;
        if (!neighbors.containsValue(target)) return false;
        if (target.getOwner() == owner) return false;
        return true;
    }

}
