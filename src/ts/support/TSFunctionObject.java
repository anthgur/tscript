package ts.support;

public abstract class TSFunctionObject extends TSObject implements TSCode {
    final protected TSLexicalEnvironment scope;
    final protected String[] formalParams;

    public TSFunctionObject(TSLexicalEnvironment scope,
                            String[] formalParams) {
        this.scope = scope;
        this.formalParams = formalParams;
    }

    public TSFunctionObject(String ident,
                            TSLexicalEnvironment scope,
                            String[] formalParams) {
        TSLexicalEnvironment env = TSLexicalEnvironment.newDeclarativeEnvironment(scope);
        env.declareFunctionName(ident, this);
        this.scope = env;
        this.formalParams = formalParams;
    }

    @Override
    public boolean isCallable() {
        return true;
    }
}
