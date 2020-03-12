package io.github.syst3ms.skriptparser.premade.expressions;

import io.github.syst3ms.skriptparser.Main;
import io.github.syst3ms.skriptparser.context.TriggerContext;
import io.github.syst3ms.skriptparser.expressions.Expression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.ranges.RangeInfo;
import io.github.syst3ms.skriptparser.types.ranges.Ranges;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.function.BiFunction;

/**
 * Returns a arrays that contains 1 to N.
 *
 * @name Times
 * @pattern %object% times
 * @since ALPHA
 * @author PeraSite
 */
public class ExprTimes implements Expression<Object> {
    private Expression<?> to;

    static {
        Main.getMainRegistration().addExpression(
                ExprTimes.class,
                Object.class,
                false,
                "%object% times"
        );
        //Don't need to register Range cause already registered in ExprRange
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        to = expressions[0];
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getValues(TriggerContext ctx) {
        Object t = to.getSingle(ctx);
        if (t == null) {
            return new Object[0];
        }

        Object f = null;
        String simpleName = t.getClass().getSimpleName();
        //TODO: Use smarter ways to get "1" value from t.getClass()
        if (simpleName.equals("BigInteger")) {
            f = new BigInteger("1");
        } else if (simpleName.equals("Long")) {
            f = 1L;
        } else if (simpleName.equals("String")) {
            f = "1";
        }

        if (f == null) {
            return new Object[0];
        }


        RangeInfo<?, ?> range = Ranges.getRange(t.getClass());
        if (range != null) {
            return (Object[]) ((BiFunction<Object, Object, ?>) range.getFunction()).apply(f, t);
        }
        return new Object[0];
    }

    @Override
    public Class<?> getReturnType() {
        return to.getReturnType();
    }

    @Override
    public String toString(@Nullable TriggerContext ctx, boolean debug) {
        return to.toString(ctx, debug) + " times";
    }
}
