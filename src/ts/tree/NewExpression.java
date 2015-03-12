package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class NewExpression extends Expression {
    private Expression expr;

    public NewExpression(Location loc, Expression expr) {
        super(loc);
        this.expr = expr;
    }

    public Expression getExpr() {
        return expr;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
