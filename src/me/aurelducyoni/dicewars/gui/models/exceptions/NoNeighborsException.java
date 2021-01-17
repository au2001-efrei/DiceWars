package me.aurelducyoni.dicewars.gui.models.exceptions;

public class NoNeighborsException extends Throwable {

    private static final long serialVersionUID = -9222784260639115360L;

    public NoNeighborsException(int id) {
        super("Territory #" + id + " has no neighbor.");
    }

}
