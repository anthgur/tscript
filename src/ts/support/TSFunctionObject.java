package ts.support;

public abstract class TSFunctionObject extends TSObject implements TSCode {
    final protected TSLexicalEnvironment scope;
    final protected String[] formalParams;

    {
        this.klass = TSString.create("Function");
    }

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

    protected TSLexicalEnvironment setupCallContext(TSValue[] args) {
        TSLexicalEnvironment env
                = TSLexicalEnvironment.newDeclarativeEnvironment(scope);
        TSValue arg;
        for (int i = 0; i < formalParams.length; i++) {
            if(i < args.length) {
                arg = args[i];
            } else {
                arg = TSUndefined.value;
            }
            env.declareParameter(formalParams[i], arg);
        }
        return env;
    }

    @Override
    public boolean isCallable() {
        return true;
    }

    @Override
    public TSCode asFunction() {
        return this;
    }

    @Override
    public TSValue construct(TSValue[] args) {
        TSObject obj = new TSObject(this);
        TSValue result = this.execute(obj, args, true);
        if (result.isObject()) {
            return result;
        }
        return obj;
    }
}
