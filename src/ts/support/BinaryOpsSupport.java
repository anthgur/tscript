package ts.support;

import ts.Message;
import ts.tree.BinaryOpcode;

/**
 * Support for multiplicative operators
 * http://www.ecma-international.org/ecma-262/5.1/#sec-11.5
 */
public final class BinaryOpsSupport {
    private static final String EQ_LOG_FMT =
            "Equality comparision:\n\tlhs: %s rhs: %s\n\tlhsType: %s rhsType: %s";

    public static TSValue add(TSValue lhs, TSValue rhs) {
        final TSPrimitive leftValue, rightValue;
        leftValue = lhs.toPrimitive();
        rightValue = rhs.toPrimitive();

        final Class<? extends TSValue> lhsType, rhsType;
        lhsType = leftValue.getClass();
        rhsType = rightValue.getClass();

        final TSValue result;

        if (lhsType == TSString.class || rhsType == TSString.class) {
            result = TSString.create(leftValue.toString()
                    + rightValue.toString());
        } else {
            result = TSNumber.create(leftValue.toNumber().unbox()
                    + rightValue.toNumber().unbox());
        }

        return result;
    }

    public static TSNumber multiply(final TSValue lhs, final TSValue rhs) {
        TSNumber leftNum = lhs.toNumber();
        TSNumber rightNum = rhs.toNumber();
        return TSNumber.create(leftNum.unbox() * rightNum.unbox());
    }

    public static TSNumber subtract(final TSValue lhs, final TSValue rhs) {
        TSNumber leftNum = lhs.toNumber();
        TSNumber rightNum = rhs.toNumber();
        return TSNumber.create(leftNum.unbox() - rightNum.unbox());
    }

    public static TSNumber divide(final TSValue lhs, final TSValue rhs) {
        final double lhsDbl, rhsDbl;
        lhsDbl = lhs.toNumber().unbox();
        rhsDbl = rhs.toNumber().unbox();

        /*
        if(Double.isNaN(lhsDbl) || Double.isNaN(rhsDbl)) {
            return TSNumber.create(Double.NaN);
        }
        */
        return TSNumber.create(lhsDbl / rhsDbl);
    }

    public static TSNumber modulo(final TSValue lhs, final TSValue rhs) {
        assert false : "modulo not implemented";
        return null;
    }

    public static TSBoolean lessThan(final TSValue lhs, final TSValue rhs) {
        return ltgt(lhs, rhs, BinaryOpcode.LESS_THAN);
    }

    public static TSBoolean greaterThan(final TSValue lhs, final TSValue rhs) {
        return ltgt(rhs, lhs, BinaryOpcode.GREATER_THAN);
    }

    // abstract equality comparison algorithm
    // http://www.ecma-international.org/ecma-262/5.1/#sec-11.9.3
    public static TSBoolean abstractEquals(final TSValue lhs, final TSValue rhs) {
        // let's blow apart Java's type system shall we?
        final Class<? extends TSValue> lhsType, rhsType;
        lhsType = lhs.getClass();
        rhsType = rhs.getClass();

        Message.log(String.format(EQ_LOG_FMT, lhs, rhs, lhsType, rhsType));

        // clause 1
        if(lhsType == rhsType) {
            // 1.a, 1.b
            if(lhsType == TSUndefined.class || lhsType == TSNull.class){
                return TSBoolean.trueValue;
            // 1.c
            } else if(lhsType == TSNumber.class) {
                final double lhsDbl, rhsDbl;
                lhsDbl = lhs.toNumber().unbox();
                rhsDbl = rhs.toNumber().unbox();

                if (Double.isNaN(lhsDbl) || Double.isNaN(rhsDbl)){
                    return TSBoolean.falseValue;
                } else if ((lhsDbl == rhsDbl) ||
                        (lhsDbl == -0.0 && rhsDbl == +0.0) ||
                        (lhsDbl == +0.0 && rhsDbl == -0.0)) {
                    return TSBoolean.trueValue;
                } else {
                    return TSBoolean.falseValue;
                }
            // 1.d
            } else if(lhsType == TSString.class) {
                return lhs.toString().equals(rhs.toString())
                        ? TSBoolean.trueValue
                        : TSBoolean.falseValue;
            // 1.e
            } else if(lhsType == TSBoolean.class) {
                return lhs.toBoolean().unbox() == rhs.toBoolean().unbox()
                        ? TSBoolean.trueValue
                        : TSBoolean.falseValue;
            // 1.f
            } else {
                // TODO implement Object comparison
                assert false : "equality error: lhs " + lhsType + ", rhs " + rhsType;
            }
        // clause 2,3
        } else if((lhsType == TSNull.class && rhsType == TSUndefined.class) ||
                (lhsType == TSUndefined.class && rhsType == TSNull.class)) {
            return TSBoolean.trueValue;
        // clause 4
        } else if(lhsType == TSNumber.class && rhsType == TSString.class) {
            return abstractEquals(lhs, rhs.toNumber());
        // clause 5
        } else if(lhsType == TSString.class && rhsType == TSNumber.class) {
            return abstractEquals(lhs.toNumber(), rhs);
        // clause 6
        } else if(lhsType == TSBoolean.class) {
            return abstractEquals(lhs, rhs.toNumber());
        // clause 7
        } else if(rhsType == TSBoolean.class) {
            return abstractEquals(lhs.toNumber(), rhs);
        }
        // clause 8, 9
        // TODO for objects

        // clause 10
        return TSBoolean.falseValue;
    }

    // helper for dispatching relational operators
    private static TSBoolean ltgt(final TSValue lhs,
                                  final TSValue rhs,
                                  final BinaryOpcode opcode) {
        final TSValue result;

        // see link below for the enum dispatch reasoning
        // http://www.ecma-international.org/ecma-262/5.1/#sec-11.8.1
        switch (opcode) {
            case LESS_THAN:
                result = abstractCompare(lhs, rhs, true);
                break;
            case GREATER_THAN:
                result = abstractCompare(lhs, rhs, false);
                break;
            default:
                // if this falls through something terrible has happened
                assert false : "ltgt must called with invalid opcode: " + opcode;
                // unreachable, but Java is dumb and this makes it happy
                return null;
        }

        // assume TSBoolean in false branch else I suck as a programmer
        return result.getClass() == TSUndefined.class
                ? TSBoolean.falseValue
                : (TSBoolean) result;
    }

    // abstract relational comparison algorithm
    // http://www.ecma-international.org/ecma-262/5.1/#sec-11.8.5
    private static TSValue abstractCompare(final TSValue lhs,
                                           final TSValue rhs,
                                           final boolean leftFirst) {
        final TSPrimitive lhsPrim, rhsPrim;

        // preserve evaluation order according to clauses 1, 2 of
        // http://www.ecma-international.org/ecma-262/5.1/#sec-11.8.5
        if(leftFirst) {
            lhsPrim = lhs.toPrimitive();
            rhsPrim = rhs.toPrimitive();
        } else {
            rhsPrim = rhs.toPrimitive();
            lhsPrim = lhs.toPrimitive();
        }

        final Class<? extends TSValue> lhsPrimType, rhsPrimType;
        lhsPrimType = lhs.getClass();
        rhsPrimType = rhs.getClass();

        // clause 3
        if(!(lhsPrimType == TSString.class && rhsPrimType == TSString.class)) {
            final double lhsDdl, rhsDbl;
            lhsDdl = lhsPrim.toNumber().unbox();
            rhsDbl = rhsPrim.toNumber().unbox();

            // 3.c, 3.d
            if(Double.isNaN(lhsDdl) || Double.isNaN(rhsDbl)) {
                return TSUndefined.value;
            } else if((lhsDdl == rhsDbl) ||
                    (lhs == TSNumber.minusZeroValue && rhs == TSNumber.plusZeroValue) ||
                    (lhs == TSNumber.plusZeroValue && rhs == TSNumber.minusZeroValue)) {
                return TSBoolean.falseValue;
            } else if(lhsDdl == Double.POSITIVE_INFINITY) {
                return TSBoolean.falseValue;
            } else if(rhsDbl == Double.POSITIVE_INFINITY) {
                return TSBoolean.trueValue;
            } else if(rhsDbl == Double.NEGATIVE_INFINITY) {
                return TSBoolean.falseValue;
            } else if(lhsDdl == Double.NEGATIVE_INFINITY) {
                return TSBoolean.trueValue;
            } else {
                return lhsDdl < rhsDbl
                        ? TSBoolean.trueValue
                        : TSBoolean.falseValue;
            }
        }

        // clause 4
        final String lhsString, rhsString;
        lhsString = lhsPrim.toString();
        rhsString = rhsPrim.toString();

        // 4.a
        if(lhsString.startsWith(rhsString)) {
            return TSBoolean.falseValue;
        // 4.b
        } else if(rhsString.startsWith(lhsString)) {
            return TSBoolean.trueValue;
        // 4.c
        } else {
            return lhsString.compareTo(rhsString) < 0
                    ? TSBoolean.trueValue
                    : TSBoolean.falseValue;
        }
    }
}
