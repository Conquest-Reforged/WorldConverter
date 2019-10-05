package me.dags.converter.util.progress;

public class PlainText implements Piece {

    private final String text;

    public PlainText(String text) {
        this.text = text;
    }

    @Override
    public String format(float progress) {
        return text;
    }
}
