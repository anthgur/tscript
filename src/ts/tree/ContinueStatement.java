package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class ContinueStatement extends Statement {
    public ContinueStatement(Location loc) {
        super(loc);
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
