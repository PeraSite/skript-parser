package io.github.syst3ms.skriptparser;

import io.github.syst3ms.skriptparser.event.ScriptLoadContext;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.registration.SkriptAddon;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link SkriptAddon} representing Skript itself
 */
public class Skript extends SkriptAddon {
    private final List<String> mainArgs;
    private List<Trigger> mainTriggers = new ArrayList<>();

    public Skript(List<String> mainArgs) {
        this.mainArgs = mainArgs;
    }

    @Override
    public void handleTrigger(Trigger trigger) {
        mainTriggers.add(trigger);
    }

    @Override
    public void finishedLoading() {
        for (Trigger trigger : mainTriggers) {
            Statement.runAll(trigger, new ScriptLoadContext(mainArgs));
        }
    }
}
