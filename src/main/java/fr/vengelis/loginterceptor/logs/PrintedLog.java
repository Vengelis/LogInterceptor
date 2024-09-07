package fr.vengelis.loginterceptor.logs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.logging.Level;

public class PrintedLog {

    private static final Gson gson = new GsonBuilder().create();

    private final String line;

    public PrintedLog(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }

    public PrintedLog print() {
        System.out.println(line);
        return this;
    }

    public String serialize() {
        return gson.toJson(this);
    }

    public static PrintedLog deserialize(String json) {
        return gson.fromJson(json, PrintedLog.class);
    }
}
