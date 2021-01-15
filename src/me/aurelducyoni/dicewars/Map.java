package me.aurelducyoni.dicewars;

import me.aurelducyoni.dicewars.exceptions.NoNeighborsException;
import me.aurelducyoni.dicewars.exceptions.NotContiguousException;

import java.io.File;
import java.util.*;

public class Map {

    private final int size;
    private final Territory[][] map;

    public Map(File file) {
        // TODO

        size = 0;
        map = new Territory[0][0];
    }

    public Map(java.util.Map<Integer, Player> players) {
        size = (int) Math.ceil(Math.sqrt(players.size() * Game.TERRITORIES_PER_PLAYER));
        map = new Territory[size][size];

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
                List<Territory> dices = new LinkedList<>();

                for (int i = 0; i < Game.TERRITORIES_PER_PLAYER; i++) {
                    AbstractMap.SimpleEntry<Integer, Integer> coordinates = territories.remove(random.nextInt(territories.size()));
                    int x = coordinates.getKey(), y = coordinates.getValue();

                    Territory territory = new Territory(player, Game.MIN_DICE_PER_TERRITORY);
                    map[y][x] = territory;

                    for (int j = territory.getStrength(); j < Game.MAX_DICE_PER_TERRITORY; j++)
                        dices.add(territory);
                }

                for (int i = 0; i < Game.DICE_PER_PLAYER - Game.TERRITORIES_PER_PLAYER * Game.MIN_DICE_PER_TERRITORY; i++) {
                    Territory territory = dices.remove(random.nextInt(dices.size()));
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
                Territory territory = map[y][x];
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
        List<Set<Territory>> groups = new ArrayList<>();
        for (Territory territory : getTerritories().values()) {
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

        if (groups.size() > 1)
            throw new NotContiguousException();
    }

    public int getSize() {
        return size;
    }

    public Territory[][] getMap() {
        return map;
    }

    public java.util.Map<Integer, Territory> getTerritories() {
        java.util.Map<Integer, Territory> territories = new HashMap<>();
        for (Territory[] line : map)
            for (Territory territory : line)
                if (territory != null)
                    territories.put(territory.getId(), territory);
        return territories;
    }

    public Player getWinner() {
        Player winner = null;

        for (Territory[] line : map) {
            for (Territory territory : line) {
                if (territory == null) continue;
                if (winner != null && territory.getOwner() != winner) return null;
                winner = territory.getOwner();
            }
        }

        return winner;
    }

    public void display() {
        String[][] labels = new String[size][size];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Territory territory = map[y][x];
                if (territory != null) {
                    labels[y][x] = "ID #" + territory.getId() + "\n";
                    labels[y][x] += "Player " + territory.getOwner().getId() + "\n";
                    labels[y][x] += territory.getStrength() + " dice";
                } else labels[y][x] = "";
            }
        }

        int[] widths = new int[size], heights = new int[size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                String[] lines = labels[y][x].split("\n");
                for (String line : lines)
                    if (line.length() > widths[x])
                        widths[x] = line.length();
                if (lines.length > heights[y])
                    heights[y] = lines.length;
            }
        }

        StringBuilder lineDelimiterBuilder = new StringBuilder();
        for (int x = 0; x < size; x++) {
            lineDelimiterBuilder.append("+");

            for (int i = 0; i < widths[x] + 2; i++)
                lineDelimiterBuilder.append("-");
        }
        lineDelimiterBuilder.append("+");
        String lineDelimiter = lineDelimiterBuilder.toString();

        for (int y = 0; y < size; y++) {
            System.out.println(lineDelimiter);
            for (int i = 0; i < heights[y]; i++) {
                for (int x = 0; x < size; x++) {
                    System.out.print("|");

                    String[] lines = labels[y][x].split("\n");
                    if (i < lines.length) {
                        String label = lines[i];
                        int left = (widths[x] - label.length()) / 2;
                        int right = widths[x] - label.length() - left;
                        for (int j = 0; j < left + 1; j++)
                            System.out.print(" ");

                        System.out.print(label);

                        for (int j = 0; j < right + 1; j++)
                            System.out.print(" ");
                    } else {
                        for (int j = 0; j < widths[x] + 2; j++)
                            System.out.print(" ");
                    }
                }

                System.out.println("|");
            }
        }
        System.out.println(lineDelimiter);
    }

}
