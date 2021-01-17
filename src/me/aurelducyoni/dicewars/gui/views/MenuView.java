package me.aurelducyoni.dicewars.gui.views;

import me.aurelducyoni.dicewars.gui.controllers.MenuController;
import me.aurelducyoni.dicewars.gui.models.MenuModel;

import javax.swing.*;

public class MenuView extends JFrame {

    private final MenuModel model;
    private final JLabel title, players;
    private final JButton minus, plus, play, resume;

    public MenuView(MenuModel model) {
        this.model = model;

        title = new JLabel("Dice Wars");
        title.setBounds(0, 30, 500, 30);
        title.setFont(title.getFont().deriveFont(24F));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        minus = new JButton("-");
        minus.setBounds(100, 110, 30, 30);
        add(minus);

        plus = new JButton("+");
        plus.setBounds(370, 110, 30, 30);
        add(plus);

        players = new JLabel();
        players.setBounds(150, 110, 200, 30);
        players.setFont(players.getFont().deriveFont(18F));
        players.setHorizontalAlignment(SwingConstants.CENTER);
        add(players);

        play = new JButton("Play");
        play.setBounds(145, 200, 100, 30);
        add(play);

        resume = new JButton("Resume");
        resume.setBounds(255, 200, 100, 30);
        add(resume);

        add(new JLabel());
        setTitle("Dice Wars");
        setResizable(false);
        setSize(500, 300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        new MenuController(this);

        update();
    }

    public void update() {
        players.setText(model.getPlayerCount() + " players");
        minus.setEnabled(model.getPlayerCount() > MenuModel.MIN_PLAYER_COUNT);
        resume.setEnabled(MenuModel.SAVE_FILE.isFile());
    }

    public MenuModel getModel() {
        return model;
    }

    public JLabel getTitleLabel() {
        return title;
    }

    public JLabel getPlayersLabel() {
        return players;
    }

    public JButton getMinusButton() {
        return minus;
    }

    public JButton getPlusButton() {
        return plus;
    }

    public JButton getPlayButton() {
        return play;
    }

    public JButton getResumeButton() {
        return resume;
    }

}
