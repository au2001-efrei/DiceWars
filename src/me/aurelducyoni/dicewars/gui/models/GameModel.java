package me.aurelducyoni.dicewars.gui.models;

import me.aurelducyoni.dicewars.gui.models.exceptions.NoNeighborsException;
import me.aurelducyoni.dicewars.gui.models.exceptions.NotContiguousException;

import java.io.*;
import java.util.*;

public class GameModel {

    public static final int TERRITORIES_PER_PLAYER = 4;
    public static final int DICE_PER_PLAYER = TERRITORIES_PER_PLAYER * 4;
    public static final int MIN_DICE_PER_TERRITORY = 1;
    public static final int MAX_DICE_PER_TERRITORY = 8;
    public static final Random RANDOM = new Random();

    private Map map = null;
    private java.util.Map<Integer, Player> players = null;
    private List<Player> rounds;
    private Player turn;
    private TerritoryModel selected = null;

    public GameModel(File file) throws IOException, NoNeighborsException, NotContiguousException {
        map = new Map(file);
        players = new HashMap<>();
        for (TerritoryModel territory : map.getTerritories().values()) {
            Player player = territory.getOwner();
            players.put(player.getId(), player);
        }

        rounds = new LinkedList<>(players.values());
        Collections.shuffle(rounds);

        nextRound();
    }

    public GameModel(int playerCount) {
        players = new HashMap<>();
        for (int i = 0; i < playerCount; i++) {
            Player player = new Player();
            players.put(player.getId(), player);
        }
        map = new Map(players);

        rounds = new LinkedList<>(players.values());
        Collections.shuffle(rounds);

        nextRound();
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public java.util.Map<Integer, Player> getPlayers() {
        return players;
    }

    public void setPlayers(java.util.Map<Integer, Player> players) {
        this.players = players;
        rounds = new LinkedList<>(players.values());
        Collections.shuffle(rounds);
    }

    public Player getTurn() {
        return turn;
    }

    public TerritoryModel getSelected() {
        return selected;
    }

    public void setSelected(TerritoryModel selected) {
        this.selected = selected;
    }

    public int distributeDice() {
        int newDice = turn.getMaxContiguous();

        List<TerritoryModel> dices = new LinkedList<>();

        for (TerritoryModel territory : turn.getTerritories().values()) {
            for (int j = territory.getStrength(); j < MAX_DICE_PER_TERRITORY; j++)
                dices.add(territory);
        }

        newDice = Math.min(newDice, dices.size());

        for (int i = 0; i < newDice; i++) {
            TerritoryModel territory = dices.remove(RANDOM.nextInt(dices.size()));
            territory.setStrength(territory.getStrength() + 1);
        }

        return newDice;
    }

    public void nextRound() {
        if (turn != null) turn.endTurn();
        do {
            turn = rounds.remove(0);
            rounds.add(turn);
        } while (!turn.canAttack());
        turn.startPlaying();
    }

    public void save(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeInt(rounds.size());
        for (Player player : rounds) oos.writeInt(player.getId());

        oos.writeInt(map.getSize());
        for (int y = 0; y < map.getSize(); y++) {
            for (int x = 0; x < map.getSize(); x++) {
                TerritoryModel territory = map.getMap()[y][x];
                if (territory != null) {
                    oos.writeInt(territory.getOwner().getId());
                    oos.writeInt(territory.getStrength());
                } else {
                    oos.writeInt(0);
                }
            }
        }

        oos.close();
        fos.close();
    }

}
