package ts.tree;

import ts.Location;
import ts.tree.Expression;
import ts.tree.visit.TreeVisitor;

public class NullLiteral extends Expression {

    public NullLiteral(final Location loc)
    {
        super(loc);
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
