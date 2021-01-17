package me.aurelducyoni.dicewars.gui.controllers;

import me.aurelducyoni.dicewars.gui.models.MenuModel;
import me.aurelducyoni.dicewars.gui.models.Player;
import me.aurelducyoni.dicewars.gui.models.TerritoryModel;
import me.aurelducyoni.dicewars.gui.views.MenuView;
import me.aurelducyoni.dicewars.gui.views.TerritoryView;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class TerritoryController implements MouseListener {

    private final TerritoryView view;

    public TerritoryController(TerritoryView view) {
        this.view = view;

        view.addMouseListener(this);
    }

    public TerritoryView getView() {
        return view;
    }

    public void mouseReleased(MouseEvent event) {
        if (view.getModel() == null) return;

        TerritoryModel selected = view.getGameView().getModel().getSelected();
        if (selected == null) {
            if (!view.getModel().getOwner().isPlaying() || !view.getModel().canAttack()) return;

            view.getGameView().getModel().setSelected(view.getModel());
        } else {
            if (selected == view.getModel()) {
                view.getGameView().getModel().setSelected(null);
                view.getGameView().update();
                return;
            }
            if (!selected.canAttack(view.getModel())) return;

            int[] selfRoll = selected.getDiceRoll();
            int[] targetRoll = view.getModel().getDiceRoll();

            StringBuilder rollText = new StringBuilder("You roll...");

            int selfSum = 0;
            for (int dice : selfRoll) {
                selfSum += dice;
                rollText.append(" ").append(dice);
            }

            rollText.append("\nThey roll...");

            int targetSum = 0;
            for (int dice : targetRoll) {
                targetSum += dice;
                rollText.append(" ").append(dice);
            }

            if (selfSum > targetSum) {
                JOptionPane.showMessageDialog(view.getGameView(),
                        rollText + "\nYou win! You have successfully conquered territory #" + view.getModel().getId() + " of player " + view.getModel().getOwner().getId() + ".",
                        "Attack",
                        JOptionPane.INFORMATION_MESSAGE);
                view.getModel().getOwner().removeTerritory(view.getModel());
                view.getModel().setOwner(selected.getOwner());
                selected.getOwner().addTerritory(view.getModel());
                view.getModel().setStrength(selected.getStrength() - 1);
                selected.setStrength(1);
            } else if (selfSum < targetSum) {
                JOptionPane.showMessageDialog(view.getGameView(),
                        rollText + "\nYou lose! " + (selected.getStrength() - 1) + " of your dices have been removed from territory #" + selected.getId() + ".",
                        "Attack",
                        JOptionPane.INFORMATION_MESSAGE);
                selected.setStrength(1);
            } else {
                JOptionPane.showMessageDialog(view.getGameView(),
                        rollText + "\nDraw!",
                        "Attack",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            view.getGameView().getModel().setSelected(null);
            try {
                view.getGameView().getModel().save(MenuModel.SAVE_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        view.getGameView().update();

        Player winner = view.getGameView().getModel().getMap().getWinner();
        if (winner != null) {
            JOptionPane.showMessageDialog(view,
                    "Player " + winner.getId() + " has conquered all the map. Congratulations!",
                    "Game ended",
                    JOptionPane.INFORMATION_MESSAGE);

            MenuModel menuModel = new MenuModel();
            MenuView menuView = new MenuView(menuModel);

            view.getGameView().setVisible(false);
            menuView.setVisible(true);
            view.getGameView().dispose();

            MenuModel.SAVE_FILE.delete();
        }
    }

    public void mouseClicked(MouseEvent event) {}
    public void mousePressed(MouseEvent event) {}
    public void mouseEntered(MouseEvent event) {}
    public void mouseExited(MouseEvent event) {}

}
