package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class TryStatement extends Statement {
    final BlockStatement block;
    final CatchStatement _catch;
    final BlockStatement _finally;

    public TryStatement(Location loc,
                        BlockStatement block,
                        CatchStatement _catch,
                        BlockStatement _finally) {
        super(loc);
        this.block = block;
        this._catch = _catch;
        this._finally = _finally;
    }

    public BlockStatement getBlock() {
        return block;
    }

    public BlockStatement getFinally() {
        return _finally;
    }

    public CatchStatement getCatch() {
        return _catch;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
