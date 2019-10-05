package me.dags.converter.util.log;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public interface ILogger {

    ILogger flush();

    ILogger log(Object text);

    ILogger footer(Object footer);

    ILogger add(PrintStream stream);

    default ILogger log(Object... args) {
        StringBuilder sb = new StringBuilder(50);
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(args[i]);
        }
        return log(sb.toString());
    }

    default ILogger log(Throwable error) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        return log(sw.toString());
    }

    default ILogger log(String message, Throwable error) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println(message);
        error.printStackTrace(pw);
        return log(sw.toString());
    }

    default ILogger logf(String format, Object... args) {
        return log(String.format(format, args));
    }

    default ILogger newLine() {
        return log("");
    }
}
