package ts.support;

public abstract class TSFunctionObject extends TSObject implements TSCode {
    final private TSLexicalEnvironment outer;

    public TSFunctionObject(TSLexicalEnvironment outer) {
        this.outer = outer;
    }
}
