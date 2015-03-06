package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class ThrowStatement extends Statement {
    final private Expression expr;

    public ThrowStatement(Location loc, Expression expr) {
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
