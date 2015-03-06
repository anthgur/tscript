package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

import java.util.List;

public class FunctionExpression extends Expression {
    private List<Statement> statementList;

    public FunctionExpression(Location loc, List<Statement> statementList) {
        super(loc);
    }

    public List<Statement> getStatementList() {
        return statementList;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
