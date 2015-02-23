package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class IfStatement extends Statement {
    private Expression expr;
    private Statement ifStat;
    private Statement elseStat;

    public IfStatement(Location loc,
                       Expression expr,
                       Statement ifStat,
                       Statement elseStat) {
        super(loc);
        this.expr = expr;
        this.ifStat = ifStat;
        this.elseStat = elseStat;
    }

    public Expression getExpr() {
        return expr;
    }

    public Statement getIfStat() {
        return ifStat;
    }

    public Statement getElseStat() {
        return elseStat;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
