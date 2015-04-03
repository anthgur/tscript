package ts.support;

import java.util.List;

public interface TSCode {
    TSPrimitive execute(TSValue ths, TSValue[] args, boolean isConstructor);
}
