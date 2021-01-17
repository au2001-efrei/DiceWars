package me.aurelducyoni.dicewars.cli.exceptions;

public class SelfTargetException extends Throwable {

    public SelfTargetException() {
        super("You can't attack your own territory.");
    }

}
