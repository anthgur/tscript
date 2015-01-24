package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class BooleanLiteral extends Expression {
    private boolean value;

    public boolean getValue() {
        return value;
    }

    public BooleanLiteral(final Location loc, final boolean value)
    {
        super(loc);
        this.value = value;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
