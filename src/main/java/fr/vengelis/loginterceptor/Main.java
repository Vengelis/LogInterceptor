package fr.vengelis.loginterceptor;

import java.io.File;
import java.net.URISyntaxException;

public class Main {

    public static String WORKING_AREA;

    public static void main(String[] args) {

        String startupCommand = System.getProperty("sun.java.command");
        String[] stArgs = startupCommand.split(" ");

        try {
            WORKING_AREA = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            LogInter app = new LogInter(stArgs);
            app.prepare();
            app.execute();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
