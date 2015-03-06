package ts.support;

public class TSException extends RuntimeException {
    final private TSValue value;

    public TSException(TSValue value) {
        this.value = value;
    }

    public TSValue getValue() {
        return value;
    }
}
