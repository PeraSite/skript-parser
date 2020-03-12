package io.github.syst3ms.skriptparser.premade.events;

import io.github.syst3ms.skriptparser.Main;
import io.github.syst3ms.skriptparser.context.ScriptLoadContext;
import io.github.syst3ms.skriptparser.context.TriggerContext;
import io.github.syst3ms.skriptparser.expressions.Expression;
import io.github.syst3ms.skriptparser.events.SkriptEvent;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.Nullable;

/**
 * The script loading event.
 *
 * @name On Script Load
 * @type EVENT
 * @pattern [on] script load[ing]
 * @since ALPHA
 * @author Syst3ms
 */
public class ScriptLoadEvent extends SkriptEvent {

    static {
        Main.getMainRegistration()
            .newEvent(ScriptLoadEvent.class, "script load[ing]")
            .setHandledContexts(ScriptLoadContext.class)
            .register();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        return ctx instanceof ScriptLoadContext;
    }

    @Override
    public String toString(@Nullable TriggerContext ctx, boolean debug) {
        return "script loading";
    }
}
