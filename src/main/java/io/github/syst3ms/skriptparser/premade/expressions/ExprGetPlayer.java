package io.github.syst3ms.skriptparser.premade.expressions;

import io.github.syst3ms.skriptparser.Main;
import io.github.syst3ms.skriptparser.context.TriggerContext;
import io.github.syst3ms.skriptparser.expressions.Expression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import static io.github.syst3ms.skriptparser.premade.types.TypePlayer.getOnlinePlayers;

/**
 * Returns a arrays that contains 1 to N.
 *
 * @name Times
 * @pattern %object% times
 * @since ALPHA
 * @author PeraSite
 */
public class ExprGetPlayer implements Expression<Player> {
    private Expression<String> playerNameExpr;

    static {
        Main.getMainRegistration().addExpression(
                ExprGetPlayer.class,
                Player.class,
                false,
                "[get|find] player [named|called] %string%"
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        playerNameExpr = (Expression<String>) expressions[0];
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Player[] getValues(TriggerContext ctx) {
        String playerName = playerNameExpr.getSingle(ctx);
        Player finded = null;
        for (Player target : getOnlinePlayers()) {
            if (target.getName().equals(playerName)) {
                finded = target;
            }
        }
        if (finded != null)
            return new Player[]{finded};
        else
            return new Player[0];
    }

    @Override
    public Class<? extends Player> getReturnType() {
        return Player.class;
    }

    @Override
    public String toString(@Nullable TriggerContext ctx, boolean debug) {
        return "player " + playerNameExpr.toString(ctx, debug);
    }
}
