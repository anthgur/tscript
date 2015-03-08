package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

import java.util.List;

public class FunctionExpression extends Expression {
    final private List<Statement> body;
    final private String ident;
    final private List<String> formalParameters;

    public FunctionExpression(Location loc,
                              String ident,
                              List<String> formalParameters,
                              List<Statement> body) {
        super(loc);
        this.ident = ident;
        this.formalParameters = formalParameters;
        this.body = body;
    }

    public List<Statement> getBody() {
        return body;
    }

    public String getIdent() {
        return ident;
    }

    public List<String> getFormalParameters() {
        return formalParameters;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
