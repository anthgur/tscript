package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

import java.util.List;

public class BlockStatement extends Statement {
    private List<Statement> statementList;

    public BlockStatement(Location loc, List<Statement> statementList) {
        super(loc);
        this.statementList = statementList;
    }

    public List<Statement> getStatementList() {
        return statementList;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
