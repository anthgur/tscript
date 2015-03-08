package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

import java.util.List;

public class CallExpression extends Expression {
    final Expression expr;
    final List<Expression> args;

    public CallExpression(Location loc,
                          Expression expr,
                          List<Expression> args) {
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
