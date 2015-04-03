package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

import java.util.List;

public class NewExpression extends Expression {
    private Expression expr;
    private List<Expression> args = null;

    public NewExpression(Location loc, Expression expr) {
        super(loc);
        this.expr = expr;
    }

    public NewExpression(Location loc, Expression expr, List<Expression> args) {
        super(loc);
        this.expr = expr;
        this.args = args;
    }

    public Expression getExpr() {
        return expr;
    }

    public List<Expression> getArgs() {
        return args;
    }

    @Override
    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
