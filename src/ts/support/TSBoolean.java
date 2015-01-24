package ts.support;

public class TSBoolean extends TSPrimitive {
    // Built in values for absolute truth
    public static final TSBoolean trueValue = new TSBoolean(true);
    public static final TSBoolean falseValue = new TSBoolean(false);

    private final boolean value;

    private TSBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public TSNumber toNumber() {
        // true  => 1
        // false => 0
        // http://www.ecma-international.org/ecma-262/5.1/#sec-9.3
        return value ? TSNumber.oneValue : TSNumber.zeroValue;
    }
}
