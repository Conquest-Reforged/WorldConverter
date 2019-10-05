package me.dags.converter.util.progress;

import java.util.function.Function;

public class FormattedText implements Piece {

    private final String text;
    private final String leftPad;
    private final String rightPad;
    private final int charWidth;
    private final float padRatio;
    private final Function<Float, Object> modifier;
    private final StringBuilder buffer = new StringBuilder(128);

    public FormattedText(String text, String leftPad, String rightPad) {
        this(text, leftPad, rightPad, f -> f);
    }

    public FormattedText(String text, String leftPad, String rightPad, Function<Float, Object> modifier) {
        this.text = text;
        this.leftPad = leftPad;
        this.rightPad = rightPad;
        this.modifier = modifier;
        int left = Math.max(1, leftPad.length());
        int right = Math.max(1, rightPad.length());
        this.padRatio = (left / (float) right) / (left + right);
        this.charWidth = String.format(text, modifier.apply(1F)).length();
    }

    @Override
    public String format(float progress) {
        buffer.setLength(0);
        String value = String.format(text, modifier.apply(progress));
        int padding = charWidth - value.length();
        int left = Math.round(padding * padRatio);
        int right = padding - left;
        pad(leftPad, left);
        buffer.append(value);
        pad(rightPad, right);
        return buffer.toString();
    }

    private void pad(String padding, int amount) {
        int mod = padding.length();
        if (mod == 0) {
            return;
        }
        for (int i = 0; i < amount; i++) {
            int j = i % mod;
            buffer.append(padding.charAt(j));
        }
    }
}
