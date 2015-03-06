package ts.support;

public interface TSCode {
    TSValue execute(TSLexicalEnvironment env, TSValue ths,
                    TSValue[] args, Boolean isConstructor);
}
