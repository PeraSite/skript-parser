package io.github.syst3ms.skriptparser.util;

import io.github.syst3ms.skriptparser.Skript;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions for file parsing
 */
public class FileUtils {
    public static final Pattern LEADING_WHITESPACE_PATTERN = Pattern.compile("(\\s+)\\S.*");
    public static final String MULTILINE_SYNTAX_TOKEN = "\\";
    private static File jarFile;

    public static List<String> readAllLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        InputStreamReader in = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(in);
        String line;
        StringBuilder multilineBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (line.replace("\\" + MULTILINE_SYNTAX_TOKEN, "\0")
                    .endsWith(MULTILINE_SYNTAX_TOKEN)) {
                multilineBuilder.append(line, 0, line.length() - 1).append("\0");
            } else if (multilineBuilder.length() > 0) {
                multilineBuilder.append(line);
                lines.add(trimMultilineIndent(multilineBuilder.toString()));
                multilineBuilder.setLength(0);
            } else {
                lines.add(line);
            }
        }
        if (multilineBuilder.length() > 0) {
            multilineBuilder.deleteCharAt(multilineBuilder.length() - 1);
            lines.add(trimMultilineIndent(multilineBuilder.toString()));
        }
        return lines;
    }

    public static int getIndentationLevel(String line) {
        Matcher m = LEADING_WHITESPACE_PATTERN.matcher(line);
        if (m.matches()) {
            return StringUtils.count(m.group(1), "\t", "    ");
        } else {
            return 0;
        }
    }

    private static String trimMultilineIndent(String multilineText) {
        String[] lines = multilineText.split("\0");
        // Inspired from Kotlin's trimIndent() function
        int baseIndent = Arrays.stream(lines)
                .skip(1) // First line's indent should be ignored
                .mapToInt(FileUtils::getIndentationLevel)
                .min()
                .orElse(0);
        if (baseIndent == 0)
            return multilineText.replace("\0", "");
        Pattern pat = Pattern.compile("\\t| {4}");
        StringBuilder sb = new StringBuilder(lines[0]);
        for (String line : Arrays.copyOfRange(lines, 1, lines.length)) {
            Matcher m = pat.matcher(line);
            for (int i = 0; i < baseIndent && m.find(); i++) {
                line = line.replaceFirst(m.group(), "");
            }
            sb.append(line);
        }
        return sb.toString();
    }

    public static void loadClasses(String basePackage, String... subPackages) throws IOException, URISyntaxException {
        for (int i = 0; i < subPackages.length; i++)
            subPackages[i] = subPackages[i].replace('.', '/') + "/";
        basePackage = basePackage.replace('.', '/') + "/";
        try (JarFile jar = new JarFile(getFile())) {
            Enumeration<JarEntry> entries = jar.entries();
            List<String> classes = new ArrayList<>();
            while (entries.hasMoreElements()) {
                JarEntry e = entries.nextElement();
                if (e.getName().startsWith(basePackage) && e.getName().endsWith(".class")) {
                    boolean load = subPackages.length == 0;
                    for (final String sub : subPackages) {
                        if (e.getName().startsWith(sub, basePackage.length())) {
                            load = true;
                            break;
                        }
                    }
                    if (load) {
                        final String c = e.getName().replace('/', '.').substring(0, e.getName().length() - ".class".length());
                        classes.add(c);
                    }
                }
            }

            ClassLoader classLoader = FileUtils.class.getClassLoader();

            classes.sort((s1, s2) -> {
                try {
                    RegisterPriority a1 = Class.forName(s1, false, classLoader).getAnnotation(RegisterPriority.class);
                    RegisterPriority a2 = Class.forName(s2, false, classLoader).getAnnotation(RegisterPriority.class);
                    Priority p1 = a1 == null ? Priority.NORMAL : a1.priority();
                    Priority p2 = a2 == null ? Priority.NORMAL : a2.priority();
                    return Integer.compare(p1.value, p2.value);
                } catch (final ClassNotFoundException | ExceptionInInitializerError ex) {
                    ex.printStackTrace();
                }
                return 0;
            });

            for (String clazz : classes) {
                try {
                    Class.forName(clazz, true, classLoader);
                } catch (final ClassNotFoundException | ExceptionInInitializerError ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static File getFile() throws URISyntaxException {
        if (jarFile == null) {
            jarFile = new File(Skript.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        }
        return jarFile;
    }
}
