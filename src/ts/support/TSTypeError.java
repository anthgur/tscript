package ts.support;

public class TSTypeError extends TSException {
    public TSTypeError(TSValue value) {
        super(value);
    }
}
