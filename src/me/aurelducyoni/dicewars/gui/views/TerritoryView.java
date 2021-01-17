package me.aurelducyoni.dicewars.gui.views;

import me.aurelducyoni.dicewars.gui.controllers.TerritoryController;
import me.aurelducyoni.dicewars.gui.models.TerritoryModel;

import javax.swing.*;
import java.awt.*;

public class TerritoryView extends JPanel {

    private final GameView gameView;
    private final TerritoryModel model;
    private final JLabel id, player, dice;

    public TerritoryView(GameView gameView, TerritoryModel model) {
        this.gameView = gameView;
        this.model = model;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalGlue());

        id = new JLabel();
        id.setFont(id.getFont().deriveFont(14F));
        id.setHorizontalAlignment(SwingConstants.CENTER);
        add(id);

        player = new JLabel();
        player.setFont(player.getFont().deriveFont(14F));
        player.setHorizontalAlignment(SwingConstants.CENTER);
        add(player);

        dice = new JLabel();
        dice.setFont(dice.getFont().deriveFont(14F));
        dice.setHorizontalAlignment(SwingConstants.CENTER);
        add(dice);

        add(Box.createVerticalGlue());

        new TerritoryController(this);

        update();
    }

    public void update() {
        TerritoryModel selected = gameView.getModel().getSelected();

        if (model == null) {
            id.setText("");
            player.setText("");
            dice.setText("");
            setBackground(selected != null ? Color.WHITE.darker().darker().darker().darker() : Color.WHITE);
            setCursor(Cursor.getDefaultCursor());
            return;
        }

        id.setText("ID #" + model.getId());
        player.setText("Player " + model.getOwner().getId());
        dice.setText(model.getStrength() + " dice");

        if (selected != null) {
            if (selected != model && !selected.canAttack(model)) {
                setBackground(model.getOwner().getColor().darker().darker().darker().darker());
                setCursor(Cursor.getDefaultCursor());
            } else {
                setBackground(model.getOwner().getColor());
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        } else {
            setBackground(model.getOwner().getColor());
            setCursor(model.getOwner().isPlaying() && model.canAttack() ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
        }
    }

    public GameView getGameView() {
        return gameView;
    }

    public TerritoryModel getModel() {
        return model;
    }

    public JLabel getIdLabel() {
        return id;
    }

    public JLabel getPlayerLabel() {
        return player;
    }

    public JLabel getDiceLabel() {
        return dice;
    }

}
