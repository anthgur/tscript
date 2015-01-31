package ts.support;

// TODO implement, this is a husk
public class TSNull extends TSPrimitive {
    public static final TSNull nullValue = new TSNull();

    @Override
    public TSNumber toNumber() {
        return TSNumber.plusZeroValue;
    }

    @Override
    public TSBoolean toBoolean() {
        return TSBoolean.falseValue;
    }

    @Override
    public TSString toStr() {
        return TSString.create("null");
    }
}
