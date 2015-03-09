package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class PropertyAccessor extends Expression {
    public PropertyAccessor (Location loc, Expression expr, String ident) {
        super(loc);
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
