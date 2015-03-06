package ts.support;

import java.util.List;

public abstract class TSFunctionObject extends TSObject implements TSCode {
    final private TSLexicalEnvironment scope;
    final private List<String> formalParams;

    public TSFunctionObject(TSLexicalEnvironment scope,
                            List<String> formalParams) {
        this.scope = scope;
        this.formalParams = formalParams;
    }
}
