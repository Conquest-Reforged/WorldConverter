package me.dags.converter.util.progress;

import me.dags.converter.util.Threading;
import me.dags.converter.util.log.Logger;

import java.util.function.Consumer;

public class ProgressBar implements Consumer<Float> {

    private final Piece[] pieces;

    public ProgressBar(Piece... pieces) {
        this.pieces = pieces;
    }

    @Override
    public void accept(Float integer) {
        StringBuilder sb = new StringBuilder();
        for (Piece piece : pieces) {
            String part = piece.format(integer);
            sb.append(part);
        }
        Logger.footer(sb.toString());
    }

    public static ProgressBar console() {
        return new ProgressBar(
                p -> "[", new Bar("=", ">", 40), p -> "]",
                p -> String.format(" Progress: %.2f%%", p * 100F),
                p -> String.format(
                        " | Memory: %s/%sMB (%.2f%%)",
                        Threading.usedMemory(),
                        Threading.availableMemory(),
                        Threading.memoryUsage()
                )
        );
    }
}
