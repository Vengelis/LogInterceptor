package fr.vengelis.loginterceptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import fr.vengelis.loginterceptor.logs.InterceptLog;
import fr.vengelis.loginterceptor.logs.InterceptLogManager;
import fr.vengelis.loginterceptor.logs.PrintedLog;
import fr.vengelis.loginterceptor.utils.ResourceExporter;
import org.yaml.snakeyaml.Yaml;
import sun.misc.Signal;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LogInter {

    private static LogInter instance;
    private final ResourceExporter RE = new ResourceExporter();
    private final InterceptLogManager ILM = new InterceptLogManager();
    private Process process;
    private String[] args;

    public LogInter(String[] args) {
        instance = this;
        this.args = args;
    }

    public void prepare() {
        try {
            RE.saveResource(new File(Main.WORKING_AREA), "/logintercept.yml", false);

            File config = new File(Main.WORKING_AREA + File.separator + "logintercept.yml");
            Yaml yaml = new Yaml();
            InputStream stm = new FileInputStream(config);

            Map<String, Object> data = yaml.load(stm);
            List<Object> listLogs = (List<Object>) data.get("logs");
            for (Object log : listLogs) {
                String[] s = log.toString()
                                .replace("{", "")
                                .replace("}", "")
                        .split(", ")
                        ;
                String name = s[0].replace("name=", "");
                String trigger = s[1].replace("trigger=", "");
                String execute = s[2].replace("execute=", "");

                ILM.register(name, trigger, execute);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void execute() {
        StringBuilder stb = new StringBuilder();

        String jarExec = "";
        String minRam = "";
        String maxRam = "";

        for (String arg : args) {
            String[] spt;
            if(arg.startsWith("--maxram=")) {
                spt = arg.split("=");
                maxRam = "-Xmx" + spt[1];
            } else if(arg.startsWith("--minram=")) {
                spt = arg.split("=");
                minRam = "-Xms" + spt[1];
            } else if(arg.startsWith("--dir")) {
                spt = arg.split("=");
                jarExec = "-jar " + spt[1];
            }
        }

        String fullCmd = "java " + minRam + " " + maxRam + " " + jarExec;

        new Thread(() -> {
            try {
                process = new ProcessBuilder()
                        .command(Arrays.asList(fullCmd.split(" ")))
                        .directory(new File(Main.WORKING_AREA))
                        .start();

                Signal.handle(new Signal("INT"), sig -> {
                    process.destroyForcibly();
                    System.exit(0);
                });

                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;

                try {
                    while ((line = br.readLine()) != null) {
                        PrintedLog log = new PrintedLog(line);
                        for (InterceptLog il : ILM.get()) {
                            if(il.match(line)) {
                                il.execute(line);
                            }
                        }
                        log.print();
                    }
                } catch (IOException e) {
                    System.out.println("Server is down");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }).start();
        Scanner keyboard = new Scanner(System.in);
        String input;

        while (true) {
            input = keyboard.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                sendCommandToProcess(input);
            } else {
                System.out.println("No command entered. Please try again.");
            }
        }
    }

    public boolean sendCommandToProcess(String command) {
        if(process != null) {
            if (process.isAlive()) {
                OutputStream outputStream = process.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream, true);
                writer.println(command);
                writer.flush();
                return true;
            } else {
                System.out.println("The process is not running.");
            }
        } else {
            System.out.println("The process is not running.");
        }
        return false;
    }

    public static LogInter get() {
        return instance;
    }
}
