package ts.support;

import java.util.List;

public interface TSCode {
    TSValue execute(TSValue ths, List<TSValue> args, boolean isConstructor);
}
