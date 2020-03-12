package io.github.syst3ms.skriptparser.premade.comparators;

import io.github.syst3ms.skriptparser.types.comparisons.Comparator;
import io.github.syst3ms.skriptparser.types.comparisons.Comparators;
import io.github.syst3ms.skriptparser.types.comparisons.Relation;
import io.github.syst3ms.skriptparser.util.math.BigDecimalMath;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberComparator extends Comparator<Number, Number> {

    static {
        Comparators.registerComparator(Number.class, Number.class, new NumberComparator());
    }

    public NumberComparator() {
        super(true);
    }

    @SuppressWarnings("unchecked")
    public Relation apply(Number number, Number number2) {
        if (number.getClass() == number2.getClass()) {
            return Relation.get(((Comparable<? super Number>) number).compareTo(number2));
        } else if (number instanceof BigDecimal || number2 instanceof BigDecimal) {
            BigDecimal bd = BigDecimalMath.getBigDecimal(number);
            BigDecimal bd2 = BigDecimalMath.getBigDecimal(number2);
            return Relation.get(bd.compareTo(bd2));
        } else if ((number instanceof BigInteger || number2 instanceof BigInteger) && (number instanceof Long || number2 instanceof Long)) {
            BigInteger bi = BigDecimalMath.getBigInteger(number);
            BigInteger bi2 = BigDecimalMath.getBigInteger(number2);
            return Relation.get(bi.compareTo(bi2));
        } else if ((number instanceof Double || number instanceof Long) &&
                (number2 instanceof Double || number2 instanceof Long)) {
            double d = number.doubleValue() - number2.doubleValue();
            return Double.isNaN(d) ? Relation.NOT_EQUAL : Relation.get(d);
        } else {
            BigDecimal bd = BigDecimalMath.getBigDecimal(number);
            BigDecimal bd2 = BigDecimalMath.getBigDecimal(number2);
            return Relation.get(bd.compareTo(bd2));
        }
    }
}
