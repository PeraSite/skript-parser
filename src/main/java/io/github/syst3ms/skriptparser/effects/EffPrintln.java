package io.github.syst3ms.skriptparser.effects;

import io.github.syst3ms.skriptparser.Main;
import io.github.syst3ms.skriptparser.event.TriggerContext;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.Nullable;

public class EffPrintln extends Effect {
    private Expression<String> string;

    static {
        Main.getMainRegistration().addEffect(
            EffPrintln.class,
            "println %string%"
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        string = (Expression<String>) expressions[0];
        return true;
    }

    @Override
    public void execute(TriggerContext e) {
        String str = string.getSingle(e);
        if (str == null)
            return;
        System.out.println(str);
    }

    @Override
    public String toString(@Nullable TriggerContext ctx, boolean debug) {
        return "println " + string.toString(ctx, debug);
    }
}
