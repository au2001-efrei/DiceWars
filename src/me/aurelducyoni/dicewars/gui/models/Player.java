package me.aurelducyoni.dicewars.gui.models;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Player {

    private static int MAX_ID = 0;

    private final int id;
    private final java.util.Map<Integer, TerritoryModel> territories;
    private boolean playing = false;

    public Player(int id) {
        this.id = id;
        territories = new HashMap<>();
    }

    public Player() {
        this(++MAX_ID);
    }

    public Player(java.util.Map<Integer, TerritoryModel> territories) {
        this();

        this.territories.putAll(territories);
    }

    public int getId() {
        return id;
    }

    public Color getColor() {
        float hue = 0;
        if (this.id > 1) {
            int i, step = 1;

            for (i = this.id - 2; i >= step; ) {
                i -= step;
                step *= 2;
            }

            hue = (0.5F + i) / step;
        }

        return Color.getHSBColor(hue, 0.8F, 0.8F);
    }

    public java.util.Map<Integer, TerritoryModel> getTerritories() {
        return Collections.unmodifiableMap(territories);
    }

    public void addTerritory(TerritoryModel territory) {
        territories.put(territory.getId(), territory);
    }

    public void removeTerritory(TerritoryModel territory) {
        territories.remove(territory.getId(), territory);
    }

    public int getMaxContiguous() {
        List<Set<TerritoryModel>> groups = new ArrayList<>();
        for (TerritoryModel territory : territories.values()) {
            Set<TerritoryModel> group = new HashSet<>();
            group.add(territory);
            groups.add(group);
        }

        merge: while (true) {
            for (int i = 1; i < groups.size(); i++) {
                for (int j = 0; j < i; j++) {
                    for (TerritoryModel territory1 : groups.get(i)) {
                        for (TerritoryModel territory2 : groups.get(j)) {
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
        for (Set<TerritoryModel> group : groups)
            if (group.size() > maxContiguous)
                maxContiguous = group.size();

        return maxContiguous;
    }

    public void endTurn() {
        playing = false;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void startPlaying() {
        playing = true;
    }

    public boolean canAttack() {
        for (TerritoryModel territory : territories.values())
            if (territory.canAttack())
                return true;

        return false;
    }

}
