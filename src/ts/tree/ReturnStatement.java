package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class ReturnStatement extends Statement {
    final Expression expr;

    public ReturnStatement(Location loc, Expression expr) {
        super(loc);
        this.expr = expr;
    }

    public Expression getExpr() {
        return expr;
    }

    public Location getLoc() {
        return loc;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
