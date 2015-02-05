
package ts.tree;

import ts.Location;
import ts.tree.visit.TreeVisitor;

/**
 * AST var statement node
 *
 */
public final class VarDeclaration extends Statement {
  private String name;
  private Expression expression;

  public VarDeclaration(final Location loc,
                        final String name,
                        final Expression expr) {
    super(loc);
    this.name = name;
    this.expression = expr;
  }

  public String getName() {
    return name;
  }

  public Expression getExpression() {
    return expression;
  }

  public <T> T apply(TreeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}

