package ts.support;

/**
 * Support for additive operators
 * http://www.ecma-international.org/ecma-262/5.1/#sec-11.6
 */
public final class AddOps {
    public static TSNumber add(TSValue lhs, TSValue rhs) {
        TSPrimitive leftValue = lhs.toPrimitive();
        TSPrimitive rightValue = rhs.toPrimitive();

        return TSNumber.create(
                leftValue.toNumber().unbox() +
                rightValue.toNumber().unbox());
    }
}
