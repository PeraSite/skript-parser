package io.github.syst3ms.skriptparser;

import io.github.syst3ms.skriptparser.log.LogEntry;
import io.github.syst3ms.skriptparser.parsing.ScriptLoader;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.util.FileUtils;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Main {
    public static final String CONSOLE_FORMAT = "[%tT] %s: %s%n";
    private static SkriptRegistration registration;

    public static void main(String[] args) {
        Options options = new Options();

        Option scriptNameOption = new Option("s", "script", true, "Skript files that need to parse");
        scriptNameOption.setRequired(true);
        options.addOption(scriptNameOption);

        Option debugOption = new Option("d", "debug", false, "Turn on or off the debug log");
        options.addOption(debugOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        boolean debug = Boolean.parseBoolean(cmd.getOptionValue("debug"));
        String scriptName = cmd.getOptionValue("script");

        init(scriptName,
                Collections.singletonList("io.github.syst3ms.skriptparser.premade"),
                Arrays.asList("comparators", "conditions", "effects", "events", "expressions", "types"),
                Collections.emptyList(),
                debug,
                true);
    }

    /**
     * Starts the parser.
     * @param scriptName the name of the script to load
     * @param mainPackages packages inside which all subpackages containing classes to load may be present. Doesn't need
     *                     to contain Skript's own main packages.
     * @param subPackages the subpackages inside which classes to load may be present. Doesn't need to contain Skript's
     *                    own subpackages.
     * @param programArgs any other program arguments (typically from the command line)
     * @param debug whether to active debug mode or not
     * @param standalone whether the parser tries to load addons (standalone) or not (library)
     */
    public static void init(String scriptName, List<String> mainPackages, List<String> subPackages, List<String> programArgs, boolean debug, boolean standalone) {
        Skript skript = new Skript(programArgs);
        registration = new SkriptRegistration(skript);
        // Make sure Skript loads properly no matter what
        try {
            for (String mainPackage : mainPackages) {
                FileUtils.loadClasses(mainPackage, subPackages.toArray(new String[0]));
            }
            if (!standalone) {
                File addonFolder = new File(".", "addons");
                if (addonFolder.exists() && addonFolder.isDirectory()) {
                    File[] addons = addonFolder.listFiles();
                    if (addons != null) {
                        for (File addon : addons) {
                            if (addon.isFile() && addon.getName().endsWith(".jar")) {
                                URLClassLoader child = new URLClassLoader(
                                        new URL[]{addon.toURI().toURL()},
                                        Main.class.getClassLoader()
                                );
                                JarFile jar = new JarFile(addon);
                                Manifest manifest = jar.getManifest();
                                String main = manifest.getMainAttributes().getValue("Main-Class");
                                Class<?> mainClass = Class.forName(main, true, child);
                                try {
                                    Method init = mainClass.getDeclaredMethod("initAddon");
                                    init.invoke(null);
                                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            System.err.println("Error while loading classes:");
            e.printStackTrace();
        }
        registration.register();
        File script = new File(scriptName);
        List<LogEntry> logs = ScriptLoader.loadScript(script, debug);
        Calendar time = Calendar.getInstance();
        for (LogEntry log : logs) {
            System.out.printf(CONSOLE_FORMAT, time, log.getType().name(), log.getMessage());
        }
    }

    public static SkriptRegistration getMainRegistration() {
        return registration;
    }

}
