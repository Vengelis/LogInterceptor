package fr.vengelis.loginterceptor.logs;

import fr.vengelis.loginterceptor.LogInter;

import java.util.regex.Pattern;

public class InterceptLog {

    private final String name;
    private final String trigger;
    private final String execute;

    public InterceptLog(String name, String trigger, String execute) {
        this.name = name;
        this.trigger = trigger;
        this.execute = execute;
    }

    public boolean match(String line) {
        return Pattern.compile(trigger, Pattern.CASE_INSENSITIVE).matcher(line).find();
    }

    public void execute(String line) {
        String cmd = execute.replace("%log%", line);
        LogInter.get().sendCommandToProcess(cmd);
    }

    public String getName() {
        return name;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getExecute() {
        return execute;
    }
}
