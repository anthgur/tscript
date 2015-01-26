package ts.support;

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

    public static TSNumber divide(final TSValue lhs, final TSValue rhs) {
        assert false : "divide not implemented";
        return null;
    }

    public static TSNumber modulo(final TSValue lhs, final TSValue rhs) {
        assert false : "modulo not implemented";
        return null;
    }
}
