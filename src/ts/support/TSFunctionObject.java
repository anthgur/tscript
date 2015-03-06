package ts.support;

import java.util.List;

public abstract class TSFunctionObject extends TSObject implements TSCode {
    final private TSLexicalEnvironment outer;
    final private List<String> formalParams;

    public TSFunctionObject(TSLexicalEnvironment outer,
                            List<String> formalParams) {
        this.outer = outer;
        this.formalParams = formalParams;
    }
}
