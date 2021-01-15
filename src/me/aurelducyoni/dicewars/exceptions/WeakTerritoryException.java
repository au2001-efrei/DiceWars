package me.aurelducyoni.dicewars.exceptions;

public class WeakTerritoryException extends Throwable {

    private static final long serialVersionUID = -3352601278389832924L;

    public WeakTerritoryException() {
        super("A territory can only attack with 2 dice or more.");
    }

}
