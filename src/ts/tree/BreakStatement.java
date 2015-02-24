package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class BreakStatement extends Statement {
    public BreakStatement(Location loc) {
        super(loc);
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
