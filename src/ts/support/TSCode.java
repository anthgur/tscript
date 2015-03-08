package ts.support;

import java.util.List;

public interface TSCode {
    TSValue execute(TSLexicalEnvironment env, TSValue ths,
                    List<TSValue> args, boolean isConstructor);
}
