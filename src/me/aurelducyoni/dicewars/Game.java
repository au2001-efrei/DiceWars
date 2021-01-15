package me.aurelducyoni.dicewars;

import java.io.File;
import java.util.*;

public class Game implements Runnable {

    public static final int TERRITORIES_PER_PLAYER = 4;
    public static final int DICE_PER_PLAYER = TERRITORIES_PER_PLAYER * 4;
    public static final int MIN_DICE_PER_TERRITORY = 1;
    public static final int MAX_DICE_PER_TERRITORY = 8;
    public static final Game GAME = new Game();
    public static final Random RANDOM = new Random();

    private me.aurelducyoni.dicewars.Map map = null;
    private java.util.Map<Integer, Player> players = null;

    private Game() {}

    private void createMap() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Do you want to read the map from a file (y/N)? ");
        if (scanner.nextLine().toLowerCase().startsWith("y")) {
            System.out.print("Enter the path to the map file: ");

            File file = null;
            do {
                if (file != null)
                    System.out.print("File not found. Try again: ");

                file = new File(scanner.nextLine());
            } while (!file.isFile());

            map = new me.aurelducyoni.dicewars.Map(file);
            players = new HashMap<>();
            for (Territory territory : map.getTerritories().values()) {
                Player player = territory.getOwner();
                players.put(player.getId(), player);
            }
        } else {
            System.out.print("Enter the number of players: ");
            int playerCount = scanner.nextInt();

            players = new HashMap<>();
            for (int i = 0; i < playerCount; i++) {
                Player player = new Player();
                players.put(player.getId(), player);
            }
            map = new me.aurelducyoni.dicewars.Map(players);
        }
    }

    public void run() {
        List<Player> rounds = new LinkedList<>(players.values());
        Collections.shuffle(rounds);

        map.display();
        while (map.getWinner() == null) {
            Player player = rounds.remove(0);
            rounds.add(player);

            if (player.canAttack()) {
                player.startPlaying();
                do {
                    player.attackTerritory();
                    map.display();
                } while (player.isPlaying() && player.canAttack());
            }

            int newDice = player.getMaxContiguous();

            List<Territory> dices = new LinkedList<>();

            for (Territory territory : player.getTerritories().values()) {
                for (int j = territory.getStrength(); j < MAX_DICE_PER_TERRITORY; j++)
                    dices.add(territory);
            }

            for (int i = 0; i < newDice; i++) {
                Territory territory = dices.remove(Game.RANDOM.nextInt(dices.size()));
                territory.setStrength(territory.getStrength() + 1);
            }
            System.out.println("You received " + newDice + " new dice.");
            map.display();
        }

        Player winner = map.getWinner();
        System.out.println("Player " + winner.getId() + " has conquered all the map. Congratulations!");
    }

    public static void main(String[] args) {
        Game.GAME.createMap();
        Game.GAME.run();
    }

}
