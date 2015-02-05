package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

public class StringLiteral extends Expression {
    private final String value;

    public StringLiteral(final Location loc, final String value) {
        super(loc);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public <T> T apply(TreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
