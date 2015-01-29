package ts.support;

import ts.Message;

/**
 * Support for multiplicative operators
 * http://www.ecma-international.org/ecma-262/5.1/#sec-11.5
 */
public final class BinaryOpsSupport {
    public static TSNumber add(TSValue lhs, TSValue rhs) {
        TSPrimitive leftValue = lhs.toPrimitive();
        TSPrimitive rightValue = rhs.toPrimitive();

        return TSNumber.create(
                leftValue.toNumber().unbox()
                        + rightValue.toNumber().unbox());
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
        assert false : "divide not implemented";
        return null;
    }

    public static TSNumber modulo(final TSValue lhs, final TSValue rhs) {
        assert false : "modulo not implemented";
        return null;
    }

    private static final String eqLogFmt =
            "Equality comparision:\n\tlhs: %s rhs: %s\n\tlhsType: %s rhsType: %s";

    // abstract equality comparison algorithm
    // http://www.ecma-international.org/ecma-262/5.1/#sec-11.9.3
    public static TSBoolean abstractEquals(final TSValue lhs, final TSValue rhs) {
        // let's blow apart Java's pathetic type system shall we?
        Class<? extends TSValue> lhsType = lhs.getClass();
        Class<? extends TSValue> rhsType = rhs.getClass();

        Message.log(String.format(eqLogFmt, lhs, rhs, lhsType, rhsType));

        // clause 1
        if(lhsType == rhsType) {
            // 1.a, 1.b
            if(lhsType == TSUndefined.class || lhsType == TSNull.class){
                return TSBoolean.trueValue;
            // 1.c
            } else if(lhsType == TSNumber.class) {
                // static types are so good!
                double lhsDbl = ((TSNumber) lhs).unbox();
                double rhsDbl = ((TSNumber) lhs).unbox();

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
                // LISP has so many parens am I right?
                return ((TSString) lhs).unbox().equals(((TSString) rhs).unbox())
                        ? TSBoolean.trueValue
                        : TSBoolean.falseValue;
            // 1.e
            } else if(lhsType == TSBoolean.class) {
                return ((TSBoolean) lhs).unbox() == ((TSBoolean) rhs).unbox()
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
}
