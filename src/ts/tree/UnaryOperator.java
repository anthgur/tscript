package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class UnaryOperator extends Expression {
    private Unop op;
    private Expression expr;

    public UnaryOperator(final Location loc,
                         final Unop op, final Expression expr) {
        super(loc);
        this.op = op;
        this.expr = expr;
    }

    public String getOpString() {
        return op.toString();
    }

    public Unop getOp() {
        return op;
    }

    public Expression getExpr() {
        return expr;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
