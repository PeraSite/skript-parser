package io.github.syst3ms.skriptparser.premade.types;

import io.github.syst3ms.skriptparser.Main;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.types.changers.Arithmetic;
import io.github.syst3ms.skriptparser.util.Priority;
import io.github.syst3ms.skriptparser.util.RegisterPriority;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.function.Function;

@RegisterPriority(priority = Priority.LOWEST)
public class TypeNumber extends Type<Number> {
    static {
        Main.getMainRegistration().addType(new TypeNumber());
    }


    public TypeNumber() {
        super(Number.class, "number", "number@s");
    }

    @Override
    public @Nullable Function<String, ? extends Number> getLiteralParser() {
        return s -> {
            Number n;
            if (s.endsWith("L") || s.endsWith("l")) {
                try {
                    n = Long.parseLong(s.substring(0, s.length() - 1));
                } catch (NumberFormatException e) {
                    return null;
                }
            } else if (s.endsWith("D") || s.endsWith("d")) {
                try {
                    n = Double.parseDouble(s.substring(0, s.length() - 1));
                } catch (NumberFormatException e) {
                    return null;
                }
            } else if (s.contains(".")) {
                try {
                    n = new BigDecimal(s);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                try {
                    n = new BigInteger(s);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return n;
        };
    }

    @Override
    public Function<Number, String> getToStringFunction() {
        return o -> {
            if (o instanceof Long || o instanceof BigInteger) {
                return o.toString();
            } else if (o instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal) o;
                int significantDigits = bd.scale() <= 0
                        ? bd.precision() + bd.stripTrailingZeros().scale()
                        : bd.precision();
                return ((BigDecimal) o).setScale(Math.min(10, significantDigits), RoundingMode.HALF_UP)
                        .stripTrailingZeros()
                        .toPlainString();
            } else if (o instanceof Double) {
                return Double.toString((Double) o);
            }
            assert false;
            return null; // Can't happen, so we don't really have to worry about that
        };
    }

    @Override
    public @Nullable Arithmetic<Number, Number> getArithmetic() {
        return new Arithmetic<Number, Number>() {
            @Override
            public Number difference(Number first, Number second) {
                // Creating BigDecimals and BigIntegers from strings is costly, so we better make checks before resorting to that
                if (first instanceof BigDecimal || second instanceof BigDecimal) {
                    // String construction is required for BigDecimal, other methods aren't reliable
                    if (first instanceof BigDecimal && second instanceof BigDecimal) {
                        return ((BigDecimal) first).subtract((BigDecimal) second).abs();
                    } else if (first instanceof BigDecimal) {
                        return ((BigDecimal) first).subtract(new BigDecimal(second.toString())).abs();
                    } else {
                        return ((BigDecimal) second).subtract(new BigDecimal(first.toString())).abs();
                    }
                } else if (first instanceof Double || second instanceof Double) {
                    return Math.abs(first.doubleValue() - second.doubleValue());
                } else if (first instanceof BigInteger || second instanceof BigInteger) {
                    if (first instanceof BigInteger && second instanceof BigInteger) {
                        return ((BigInteger) first).subtract(((BigInteger) second)).abs();
                    } else if (first instanceof BigInteger) {
                        return ((BigInteger) first).subtract(BigInteger.valueOf(second.longValue())).abs();
                    } else {
                        return ((BigInteger) second).subtract(BigInteger.valueOf(second.longValue())).abs();
                    }
                } else {
                    return Math.abs(first.longValue() - second.longValue());
                }
            }

            @Override
            public Number add(Number value, Number difference) {
                if (value instanceof BigDecimal || difference instanceof BigDecimal) {
                    if (value instanceof BigDecimal && difference instanceof BigDecimal) {
                        return ((BigDecimal) value).add(((BigDecimal) difference));
                    } else if (value instanceof BigDecimal) {
                        return ((BigDecimal) value).add(new BigDecimal(difference.toString()));
                    } else {
                        return ((BigDecimal) difference).add(new BigDecimal(value.toString()));
                    }
                } else if (value instanceof Double || difference instanceof Double) {
                    return value.doubleValue() + difference.doubleValue();
                } else if (value instanceof BigInteger || difference instanceof BigInteger) {
                    if (value instanceof BigInteger && difference instanceof BigInteger) {
                        return ((BigInteger) value).add(((BigInteger) difference));
                    } else if (value instanceof BigInteger) {
                        return ((BigInteger) value).add(BigInteger.valueOf(difference.longValue()));
                    } else {
                        return ((BigInteger) difference).add(BigInteger.valueOf(value.longValue()));
                    }
                } else {
                    return value.longValue() + difference.longValue();
                }
            }

            @Override
            public Number subtract(Number value, Number difference) {
                if (value instanceof BigDecimal || difference instanceof BigDecimal) {
                    if (value instanceof BigDecimal && difference instanceof BigDecimal) {
                        return ((BigDecimal) value).subtract(((BigDecimal) difference));
                    } else if (value instanceof BigDecimal) {
                        return ((BigDecimal) value).subtract(new BigDecimal(difference.toString()));
                    } else {
                        return new BigDecimal(value.toString()).subtract((BigDecimal) difference);
                    }
                } else if (value instanceof Double || difference instanceof Double) {
                    return value.doubleValue() - difference.doubleValue();
                } else if (value instanceof BigInteger || difference instanceof BigInteger) {
                    if (value instanceof BigInteger && difference instanceof BigInteger) {
                        return ((BigInteger) value).subtract(((BigInteger) difference));
                    } else if (value instanceof BigInteger) {
                        return ((BigInteger) value).subtract(BigInteger.valueOf(difference.longValue()));
                    } else {
                        return BigInteger.valueOf(value.longValue()).subtract((BigInteger) difference);
                    }
                } else {
                    return value.longValue() - difference.longValue();
                }
            }

            @Override
            public Class<? extends Number> getRelativeType() {
                return Number.class;
            }
        };
    }
}
