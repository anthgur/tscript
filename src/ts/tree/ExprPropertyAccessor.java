package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class ExprPropertyAccessor extends Expression {
    private Expression expr, prop;

    public ExprPropertyAccessor(Location loc, Expression expr, Expression prop) {
        super(loc);
        this.expr = expr;
        this.prop = prop;
    }

    public Expression getExpr() {
        return expr;
    }

    public Expression getProp() {
        return prop;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
