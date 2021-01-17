package me.aurelducyoni.dicewars.cli.exceptions;

public class InvalidTerritoryException extends Exception {

    private static final long serialVersionUID = -5296805967925291349L;

    public InvalidTerritoryException(String name) {
        super("Invalid " + name + " territory.");
    }

}
