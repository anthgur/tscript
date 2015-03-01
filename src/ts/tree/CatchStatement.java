package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class CatchStatement extends Statement {
    final Identifier ident;
    final BlockStatement block;

    public CatchStatement(Location loc,
                          String ident,
                          BlockStatement block) {
        super(loc);
        this.ident = new Identifier(loc, ident);
        this.block = block;
    }

    public Identifier getIdent() {
        return ident;
    }

    public BlockStatement getBlock() {
        return block;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
