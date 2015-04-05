package ts.support;

public interface TSCode {
    TSValue execute(TSValue ths, TSValue[] args, boolean isConstructor);
}
