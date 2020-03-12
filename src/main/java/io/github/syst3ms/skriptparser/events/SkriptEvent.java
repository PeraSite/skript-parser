package io.github.syst3ms.skriptparser.events;

import io.github.syst3ms.skriptparser.statements.SyntaxElement;
import io.github.syst3ms.skriptparser.context.TriggerContext;
import io.github.syst3ms.skriptparser.statements.Statement;
import io.github.syst3ms.skriptparser.parsing.file.FileSection;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ScriptLoader;

import java.util.List;

/**
 * The entry point for all code in Skript. Once an event triggers, all of the code inside it is run
 */
public abstract class SkriptEvent implements SyntaxElement {

    /**
     * Whether this event should trigger, given the {@link TriggerContext}
     * @param ctx the TriggerContext to check
     * @return whether the event should trigger
     */
    public abstract boolean check(TriggerContext ctx);

    public List<Statement> loadSection(FileSection section, SkriptLogger logger) {
        return ScriptLoader.loadItems(section, logger);
    }
}
