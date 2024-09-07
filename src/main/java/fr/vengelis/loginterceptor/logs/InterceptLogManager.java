package fr.vengelis.loginterceptor.logs;

import java.util.ArrayList;
import java.util.List;

public class InterceptLogManager {

    private final List<InterceptLog> interceptLogs = new ArrayList<>();

    public List<InterceptLog> get() {
        return interceptLogs;
    }

    public void register(String name, String trigger, String execute) {
        interceptLogs.add(new InterceptLog(name, trigger, execute));
        System.out.println("Registering new log interceptor : " + name);
    }

    public void register(InterceptLog i) {
        interceptLogs.add(i);
        System.out.println("Registering new log interceptor : " + i.getName());
    }
}
