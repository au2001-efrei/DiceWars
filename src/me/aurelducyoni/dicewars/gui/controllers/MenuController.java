package me.aurelducyoni.dicewars.gui.controllers;

import me.aurelducyoni.dicewars.gui.models.GameModel;
import me.aurelducyoni.dicewars.gui.models.MenuModel;
import me.aurelducyoni.dicewars.gui.models.exceptions.NoNeighborsException;
import me.aurelducyoni.dicewars.gui.models.exceptions.NotContiguousException;
import me.aurelducyoni.dicewars.gui.views.GameView;
import me.aurelducyoni.dicewars.gui.views.MenuView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MenuController implements ActionListener {

    private final MenuView view;

    public MenuController(MenuView view) {
        this.view = view;

        view.getMinusButton().addActionListener(this);
        view.getPlusButton().addActionListener(this);
        view.getPlayButton().addActionListener(this);
        view.getResumeButton().addActionListener(this);
    }

    public MenuView getView() {
        return view;
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(view.getMinusButton())) {
            view.getModel().setPlayerCount(view.getModel().getPlayerCount() - 1);
            view.update();
        } else if (event.getSource().equals(view.getPlusButton())) {
            view.getModel().setPlayerCount(view.getModel().getPlayerCount() + 1);
            view.update();
        } else if (event.getSource().equals(view.getPlayButton())) {
            GameModel gameModel = new GameModel(view.getModel().getPlayerCount());
            GameView gameView = new GameView(gameModel);

            view.setVisible(false);
            gameView.setVisible(true);
            view.dispose();

            try {
                gameModel.save(MenuModel.SAVE_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (event.getSource().equals(view.getResumeButton())) {
            try {
                GameModel gameModel = new GameModel(MenuModel.SAVE_FILE);
                GameView gameView = new GameView(gameModel);

                view.setVisible(false);
                gameView.setVisible(true);
                view.dispose();
            } catch (IOException | NoNeighborsException | NotContiguousException e) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(view,
                        "Failed to resume game. Check that the save file has not been tempered with.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
