package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

import java.util.List;

public class FunctionExpression extends Expression {
    private List<Statement> body;

    public FunctionExpression(Location loc, List<Statement> body) {
        super(loc);
        this.body = body;
    }

    public List<Statement> getBody() {
        return body;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
