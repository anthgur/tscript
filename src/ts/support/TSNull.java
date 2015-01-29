package ts.support;

// TODO implement, this is a husk
public class TSNull extends TSPrimitive {
    @Override
    public TSNumber toNumber() {
        return null;
    }

    @Override
    public TSBoolean toBoolean() {
        return null;
    }

    @Override
    public TSString toStr() {
        return super.toStr();
    }
}
