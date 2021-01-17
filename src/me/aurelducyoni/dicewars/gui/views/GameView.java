package me.aurelducyoni.dicewars.gui.views;

import me.aurelducyoni.dicewars.gui.controllers.GameController;
import me.aurelducyoni.dicewars.gui.models.GameModel;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {

    private final GameModel model;
    private final JPanel grid;
    private final JLabel turn;
    private final JButton endTurn;
    private final TerritoryView[][] territories;

    public GameView(GameModel model) {
        this.model = model;

        int width = Math.min(model.getMap().getSize() * 200, Toolkit.getDefaultToolkit().getScreenSize().width);
        int height = Math.min(model.getMap().getSize() * 200 + 50, Toolkit.getDefaultToolkit().getScreenSize().height);

        turn = new JLabel();
        turn.setBounds(10, 10, width - 120, 30);
        turn.setFont(turn.getFont().deriveFont(18F));
        add(turn);

        endTurn = new JButton("End turn");
        endTurn.setBounds(width - 110, 10, 100, 30);
        add(endTurn);

        grid = new JPanel();
        grid.setBounds(0, 50, width, height - 50);
        grid.setLayout(new GridLayout(model.getMap().getSize(), model.getMap().getSize()));

        territories = new TerritoryView[model.getMap().getSize()][model.getMap().getSize()];
        for (int y = 0; y < model.getMap().getSize(); y++) {
            for (int x = 0; x < model.getMap().getSize(); x++) {
                territories[y][x] = new TerritoryView(this, model.getMap().getMap()[y][x]);
                grid.add(territories[y][x]);
            }
        }

        add(grid);

        add(new JLabel());
        setTitle("Dice Wars");
        setResizable(false);
        setSize(width, height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        new GameController(this);

        update();
    }

    public void update() {
        turn.setText("Turn: Player " + model.getTurn().getId());
        for (int y = 0; y < model.getMap().getSize(); y++) {
            for (int x = 0; x < model.getMap().getSize(); x++) {
                territories[y][x].update();
            }
        }
    }

    public GameModel getModel() {
        return model;
    }

    public JPanel getGrid() {
        return grid;
    }

    public JLabel getTurn() {
        return turn;
    }

    public JButton getEndTurn() {
        return endTurn;
    }

    public TerritoryView[][] getTerritories() {
        return territories;
    }

}
