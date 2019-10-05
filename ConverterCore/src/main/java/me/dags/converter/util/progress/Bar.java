package me.dags.converter.util.progress;

public class Bar implements Piece {

    private final String body;
    private final String head;
    private final int sections;
    private final int charWidth;
    private final StringBuilder buffer;

    public Bar(String body, String head, int sections) {
        this.body = body;
        this.head = head;
        this.sections = sections;
        this.charWidth = (sections * body.length()) + (head.length());
        this.buffer = new StringBuilder(charWidth);
    }

    @Override
    public String format(float progress) {
        buffer.setLength(0);
        int pos = Math.round(progress * sections);
        for (int i = 0; i < pos; i++) {
            buffer.append(body);
        }
        buffer.append(head);
        for (int i = charWidth - buffer.length(); i > 0; i--) {
            buffer.append(' ');
        }
        return buffer.toString();
    }
}
