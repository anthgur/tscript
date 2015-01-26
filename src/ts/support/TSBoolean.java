package ts.support;

public final class TSBoolean extends TSPrimitive {
    // Built in values for absolute truth
    public static final TSBoolean trueValue = new TSBoolean(true);
    public static final TSBoolean falseValue = new TSBoolean(false);

    private final boolean value;

    public static TSBoolean create(boolean value) {
        return value ? trueValue : falseValue;
    }

    private TSBoolean(boolean value) {
        this.value = value;
    }

    public TSBoolean negate() {
        return value ? falseValue : trueValue;
    }

    public boolean unbox() {
        return value;
    }

    @Override
    public TSNumber toNumber() {
        // true  => 1
        // false => 0
        // http://www.ecma-international.org/ecma-262/5.1/#sec-9.3
        return value ? TSNumber.oneValue : TSNumber.plusZeroValue;
    }

    @Override
    public TSBoolean toBoolean() {
        return this;
    }

    @Override
    public TSString toStr() {
        return value ? TSString.create("true") : TSString.create("false");
    }
}
