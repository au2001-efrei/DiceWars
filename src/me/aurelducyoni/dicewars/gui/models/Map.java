package me.aurelducyoni.dicewars.gui.models;

import me.aurelducyoni.dicewars.gui.models.exceptions.NoNeighborsException;
import me.aurelducyoni.dicewars.gui.models.exceptions.NotContiguousException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class Map {

    private final int size;
    private final TerritoryModel[][] map;

    public Map(File file) throws IOException, NoNeighborsException, NotContiguousException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);

        int playerCount = ois.readInt();
        java.util.Map<Integer, Player> players = new HashMap<>();

        for (int i = 0; i < playerCount; i++) {
            Player player = new Player(ois.readInt());
            players.put(player.getId(), player);
        }

        size = ois.readInt();
        map = new TerritoryModel[size][size];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int owner = ois.readInt();
                if (owner != 0) {
                    int strength = ois.readInt();
                    map[y][x] = new TerritoryModel(x, y, players.get(owner), strength);
                }
            }
        }

        ois.close();
        fis.close();

        initNeighbors();
    }

    public Map(java.util.Map<Integer, Player> players) {
        size = (int) Math.ceil(Math.sqrt(players.size() * GameModel.TERRITORIES_PER_PLAYER));
        map = new TerritoryModel[size][size];

        while (true) {
            List<AbstractMap.SimpleEntry<Integer, Integer>> territories = new LinkedList<>();
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    map[y][x] = null;
                    territories.add(new AbstractMap.SimpleEntry<>(x, y));
                }
            }

            Random random = new Random();

            for (Player player : players.values()) {
                List<TerritoryModel> dices = new LinkedList<>();

                for (int i = 0; i < GameModel.TERRITORIES_PER_PLAYER; i++) {
                    AbstractMap.SimpleEntry<Integer, Integer> coordinates = territories.remove(random.nextInt(territories.size()));
                    int x = coordinates.getKey(), y = coordinates.getValue();

                    TerritoryModel territory = new TerritoryModel(x, y, player, GameModel.MIN_DICE_PER_TERRITORY);
                    map[y][x] = territory;

                    for (int j = territory.getStrength(); j < GameModel.MAX_DICE_PER_TERRITORY; j++)
                        dices.add(territory);
                }

                for (int i = 0; i < GameModel.DICE_PER_PLAYER - GameModel.TERRITORIES_PER_PLAYER * GameModel.MIN_DICE_PER_TERRITORY; i++) {
                    TerritoryModel territory = dices.remove(random.nextInt(dices.size()));
                    territory.setStrength(territory.getStrength() + 1);
                }
            }

            try {
                initNeighbors();
            } catch (NoNeighborsException | NotContiguousException e) {
                continue;
            }
            break;
        }
    }

    private void initNeighbors() throws NoNeighborsException, NotContiguousException {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                TerritoryModel territory = map[y][x];
                if (territory == null) continue;

                territory.getOwner().addTerritory(territory);

                if (y > 0 && map[y - 1][x] != null) territory.addNeighbor(map[y - 1][x]);
                if (y < size - 1 && map[y + 1][x] != null) territory.addNeighbor(map[y + 1][x]);
                if (x > 0 && map[y][x - 1] != null) territory.addNeighbor(map[y][x - 1]);
                if (x < size - 1 && map[y][x + 1] != null) territory.addNeighbor(map[y][x + 1]);

                if (territory.getNeighbors().isEmpty())
                    throw new NoNeighborsException(territory.getId());
            }
        }

        checkContiguous();
    }

    public void checkContiguous() throws NotContiguousException {
        List<Set<TerritoryModel>> groups = new ArrayList<>();
        for (TerritoryModel territory : getTerritories().values()) {
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

        if (groups.size() > 1)
            throw new NotContiguousException();
    }

    public int getSize() {
        return size;
    }

    public TerritoryModel[][] getMap() {
        return map;
    }

    public java.util.Map<Integer, TerritoryModel> getTerritories() {
        java.util.Map<Integer, TerritoryModel> territories = new HashMap<>();
        for (TerritoryModel[] line : map)
            for (TerritoryModel territory : line)
                if (territory != null)
                    territories.put(territory.getId(), territory);
        return territories;
    }

    public Player getWinner() {
        Player winner = null;

        for (TerritoryModel[] line : map) {
            for (TerritoryModel territory : line) {
                if (territory == null) continue;
                if (winner != null && territory.getOwner() != winner) return null;
                winner = territory.getOwner();
            }
        }

        return winner;
    }

}
