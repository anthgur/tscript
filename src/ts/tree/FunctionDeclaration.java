package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

import java.util.List;

public class FunctionDeclaration extends Statement {
    final String ident;
    final List<String> formalParameters;
    final List<Statement> body;

    public FunctionDeclaration(Location loc,
                               String ident,
                               List<String> formalParameters,
                               List<Statement> body) {
        super(loc);
        this.ident = ident;
        this.formalParameters = formalParameters;
        this.body = body;
    }

    public String getIdent() {
        return ident;
    }

    public List<Statement> getBody() {
        return body;
    }

    public List<String> getFormalParameters() {
        return formalParameters;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return null;
    }
}
