package me.aurelducyoni.dicewars.gui.models;

import java.io.File;

public class MenuModel {

    public static final int DEFAULT_PLAYER_COUNT = 6;
    public static final int MIN_PLAYER_COUNT = 2;
    public static final File SAVE_FILE = new File("save.bin");

    private int playerCount;

    public MenuModel(int playerCount) {
        this.playerCount = Math.max(playerCount, MIN_PLAYER_COUNT);
    }

    public MenuModel() {
        this(DEFAULT_PLAYER_COUNT);
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = Math.max(playerCount, MIN_PLAYER_COUNT);
    }
}
