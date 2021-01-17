package me.aurelducyoni.dicewars.gui.models.exceptions;

public class NotContiguousException extends Throwable {

    private static final long serialVersionUID = -7870651939046834427L;

    public NotContiguousException() {
        super("All territories in the map must be contiguous.");
    }

}
