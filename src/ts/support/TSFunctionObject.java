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

    public TSFunctionObject(String ident,
                            TSLexicalEnvironment scope,
                            List<String> formalParams) {
        TSLexicalEnvironment env = TSLexicalEnvironment.newDeclarativeEnvironment(scope);
        env.declareFunctionName(ident, this);
        this.scope = env;
        this.formalParams = formalParams;
    }
}
