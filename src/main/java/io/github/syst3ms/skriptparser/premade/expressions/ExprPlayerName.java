package io.github.syst3ms.skriptparser.premade.expressions;

import io.github.syst3ms.skriptparser.Main;
import io.github.syst3ms.skriptparser.context.TriggerContext;
import io.github.syst3ms.skriptparser.expressions.Expression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Returns a arrays that contains 1 to N.
 *
 * @name Times
 * @pattern %object% times
 * @since ALPHA
 * @author PeraSite
 */
public class ExprPlayerName implements Expression<String> {
    private Expression<Player> playerExpr;

    static {
        Main.getMainRegistration().addExpression(
                ExprPlayerName.class,
                String.class,
                false,
                "%player%'s name",
                "name [of] %player%"
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        playerExpr = (Expression<Player>) expressions[0];
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String[] getValues(TriggerContext ctx) {
        Player player = playerExpr.getSingle(ctx);
        if (player != null)
            return new String[]{player.getName()};
        else
            return new String[0];
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(@Nullable TriggerContext ctx, boolean debug) {
        return "name of " + playerExpr.toString(ctx, debug);
    }
}
