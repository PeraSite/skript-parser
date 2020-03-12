package io.github.syst3ms.skriptparser.context;

import java.util.List;

/**
 * The script loading context, equivalent to
 * {@code public static void main(String[] args)} in Java
 */
public class ScriptLoadContext implements TriggerContext {
    private final List<String> args;

    public ScriptLoadContext(List<String> args) {
        this.args = args;
    }

    @Override
    public String getName() {
        return "main";
    }

    public List<String> getArguments() {
        return args;
    }
}
