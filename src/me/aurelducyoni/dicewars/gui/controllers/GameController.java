package me.aurelducyoni.dicewars.gui.controllers;

import me.aurelducyoni.dicewars.gui.models.MenuModel;
import me.aurelducyoni.dicewars.gui.views.GameView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class GameController implements ActionListener {

    private final GameView view;

    public GameController(GameView view) {
        this.view = view;

        view.getEndTurn().addActionListener(this);
    }

    public GameView getView() {
        return view;
    }

    public void actionPerformed(ActionEvent event) {
        view.getModel().setSelected(null);

        int newDice = view.getModel().distributeDice();
        JOptionPane.showMessageDialog(view,
                "You received " + newDice + " new dice. They were distributed randomly on your territories.",
                "End of turn",
                JOptionPane.INFORMATION_MESSAGE);

        view.getModel().nextRound();
        try {
            view.getModel().save(MenuModel.SAVE_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        view.update();
    }

}
