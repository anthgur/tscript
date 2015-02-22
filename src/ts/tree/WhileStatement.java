package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class WhileStatement extends Statement {
    private final Expression expression;
    private final Statement statement;

    public WhileStatement(Location loc,
                          Expression expression,
                          Statement statement) {
        super(loc);
        this.expression = expression;
        this.statement = statement;
    }

    public Expression getExpression() {
        return expression;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
