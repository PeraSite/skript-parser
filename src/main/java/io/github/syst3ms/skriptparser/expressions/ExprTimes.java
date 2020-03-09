package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Main;
import io.github.syst3ms.skriptparser.event.TriggerContext;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.ranges.RangeInfo;
import io.github.syst3ms.skriptparser.types.ranges.Ranges;
import io.github.syst3ms.skriptparser.util.ClassUtils;
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
    private RangeInfo<?, ?> range;

    static {
        Main.getMainRegistration().addExpression(
                ExprTimes.class,
                Object.class,
                false,
                "%objects% times"
        );
        //Don't need to register Range cause already registered in ExprRange
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        to = expressions[0];
        range = Ranges.getRange(ClassUtils.getCommonSuperclass(to.getReturnType()));
        if (range == null) {
            SkriptLogger logger = parseContext.getLogger();
            parseContext.getLogger().error("Cannot get a range to value from " + to.toString(null, logger.isDebug()), ErrorType.SEMANTIC_ERROR);
            return false;
        }
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

        return (Object[]) ((BiFunction) this.range.getFunction()).apply(f, t);
    }

    @Override
    public Class<?> getReturnType() {
        return range.getTo();
    }

    @Override
    public String toString(@Nullable TriggerContext ctx, boolean debug) {
        return to.toString(ctx, debug) + " times";
    }
}
