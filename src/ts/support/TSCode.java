package ts.support;

import java.util.List;

public interface TSCode {
    TSValue execute(TSValue ths, TSValue[] args, boolean isConstructor);
}
