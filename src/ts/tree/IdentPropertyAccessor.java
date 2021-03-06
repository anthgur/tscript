package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class IdentPropertyAccessor extends Expression {
    private Expression expr;
    private String ident;

    public IdentPropertyAccessor(Location loc, Expression expr, String ident) {
        super(loc);
        this.expr = expr;
        this.ident = ident;
    }

    public Expression getExpr() {
        return expr;
    }

    public String getIdent() {
        return ident;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
