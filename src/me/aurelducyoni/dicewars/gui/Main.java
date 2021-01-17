package me.aurelducyoni.dicewars.gui;

import me.aurelducyoni.dicewars.gui.models.MenuModel;
import me.aurelducyoni.dicewars.gui.views.MenuView;

public class Main {

    public static void main(String[] args) {
        MenuModel model = new MenuModel();
        MenuView view = new MenuView(model);
        view.setVisible(true);
    }

}
