package ts.support;

public class UnaryOpsSupport {
    public static TSBoolean logicalNot(TSValue val) {
        return val.toBoolean().negate();
    }

    public static TSNumber plus(final TSValue val) {
        return val.toNumber();
    }

    public static TSNumber minus(final TSValue val) {
        return val.toNumber().negate();
    }
}
