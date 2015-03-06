package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class TryStatement extends Statement {
    final BlockStatement block;
    final CatchStatement _catch;
    final BlockStatement _finally;
    final Kind kind;

    public enum Kind {
        CATCH,
        FINALLY,
        CATCH_FINALLY
    }

    public TryStatement(Location loc,
                        BlockStatement block,
                        CatchStatement _catch,
                        BlockStatement _finally) {
        super(loc);
        this.block = block;
        this._catch = _catch;
        this._finally = _finally;

        if(_catch != null && _finally == null) {
            kind = Kind.CATCH;
        } else if(_catch == null && _finally != null) {
            kind = Kind.FINALLY;
        } else {
            kind = Kind.CATCH_FINALLY;
        }
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

    public Kind getKind() {
        return kind;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
