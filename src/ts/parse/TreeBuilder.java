package ts.parse;

import ts.Location;
import ts.Message;
import ts.tree.*;

import java.util.List;

/**
 * Provides static methods for building AST nodes
 */
public class TreeBuilder {

  /**
   * Builds a var declaration node
   * @param loc Location in the source code
   * @param name Name of the var
   * @param expression The expression to be assigned to the new var
   * @return AST node capturing the semantics of var declaration
   */
  public static Statement buildVarDeclaration(final Location loc,
                                              final String name,
                                              final Expression expression) {
    Message.log("TreeBuilder: VarDeclaration (" + name + ")");
    return new VarDeclaration(loc, name, expression);
  }

  /**
   * Overloaded to auto-null the expression
   * @param loc Location in the source code
   * @param name Name of the var
   * @return AST node capturing the semantics of var declaration
   */
  public static Statement buildVarDeclaration(final Location loc,
                                              final String name) {
    return buildVarDeclaration(loc, name, null);
  }

  /**
   * Builds a var statement out of a declaration list
   * @param loc Location in the source code
   * @param varDeclList {@code List} representing var declarations
   * @return AST node capturing the semantics of var statements
   */
  public static Statement buildVarStatement(final Location loc,
                                            final List<Statement> varDeclList) {
    Message.log("TreeBuilder: VarStatement");
    return new VarStatement(loc, varDeclList);
  }

  /**
   * Build a expression statement.
   * @param  loc  location in source code (file, line, column)
   * @param  exp  expression subtree
   */
  public static Statement buildExpressionStatement(final Location loc,
                                                   final Expression exp) {
    Message.log("TreeBuilder: ExpressionStatement");
    return new ExpressionStatement(loc, exp);
  }

  /**
   * Build a binary operator
   * @param  loc   location in source code (file, line, column)
   * @param  op    the binary operator
   * @param  left  the left subtree
   * @param  right the right subtree
   * @see ts.tree.BinaryOpcode
   */
  public static Expression buildBinaryOperator(final Location loc,
                                               final BinaryOpcode op,
                                               final Expression left,
                                               final Expression right) {
    Message.log("TreeBuilder: Binop " + op.toString());

    return new BinaryOperator(loc, op, left, right);
  }

  /**
   * Builds a unary operator
   * @param loc Location in the source code
   * @param op The unary operator
   * @param expr The subtree for the operator
   */
  public static Expression buildUnaryOperator(final Location loc,
                                              final UnaryOpcode op,
                                              final Expression expr) {
    Message.log("TreeBuilder: Unop " + op.toString());
    return new UnaryOperator(loc, op, expr);
  }

  public static BlockStatement buildBlockStatement(final Location loc,
                                              final List<Statement> statementList) {
    Message.log("Treebuilder: BlockStatement");
    return new BlockStatement(loc, statementList);
  }

  public static Statement buildWhileStatement(final Location loc,
                                              final Expression expr,
                                              final Statement stat) {
    Message.log("TreeBuilder: WhileStatement");
    return new WhileStatement(loc, expr, stat);
  }

  /** Build a identifier expression.
   *
   *  @param  loc  location in source code (file, line, column)
   *  @param  name name of the identifier.
   */
  public static Expression buildIdentifier(final Location loc,
                                           final String name) {
    Message.log("TreeBuilder: Identifier (" + name + ")");
    return new Identifier(loc, name);
  }

  /** Build a numeric literal expression. Converts the String for
   *  the value to a double.
   *
   *  @param  loc   location in source code (file, line, column)
   *  @param  value value of the literal as a String
   */
  public static Expression buildDecimalLiteral(final Location loc,
                                               final String value) {
    double d = 0.0;

    try {
      d = Double.parseDouble(value);
    } catch(NumberFormatException nfe) {
      Message.bug(loc, "decimal literal not parsable");
    }

    Message.log("TreeBuilder: NumericLiteral " + d);
    return new NumericLiteral(loc, d);
  }

  public static Expression buildHexIntegerLiteral(final Location loc,
                                                  final String value) {
    double d = 0.0;

    try {
      d = Integer.parseInt(value.substring(2), 16);
    } catch (NumberFormatException e) {
      Message.bug(loc, "hex literal not parsable " + value);
    }

    Message.log("TreeBuilder: NumericLiteral " + d);
    return new NumericLiteral(loc, d);
  }

  public static Statement buildIfStatement(final Location loc,
                                           final Expression e,
                                           final Statement s1,
                                           final Statement s2) {
    Message.log("TreeBuilder: IfStatement");
    return new IfStatement(loc, e, s1, s2);
  }

  public static Statement buildIfStatement(final Location loc,
                                           final Expression e,
                                           final Statement s1) {
    Message.log("TreeBuilder: IfStatement");
    return new IfStatement(loc, e, s1, null);
  }

  public static Expression buildBooleanLiteral(final Location loc,
                                               final String value) {
    boolean b;

    if(value.equals("true")) {
      b = true;
    } else if(value.equals("false")) {
      b = false;
    } else {
      Message.bug(loc, "boolean literal not parsable");
      b = false;
    }

    return new BooleanLiteral(loc, b);
  }

  public static Expression buildStringLiteral(final Location loc,
                                              final String text) {
    String processed = text.substring(1, text.length() - 1);

    if (!processed.isEmpty()) {
      processed = processed.replace("\"", "\\\"");
    }

    return new StringLiteral(loc, processed);
  }

  /** Build a print statement.
   *
   *  @param  loc  location in source code (file, line, column)
   *  @param  exp  expression subtree.
   */
  public static Statement buildPrintStatement(final Location loc,
                                              final Expression exp) {
    Message.log("TreeBuilder: PrintStatement");
    return new PrintStatement(loc, exp);
  }

  //
  // methods to detect "early" (i.e. semantic) errors
  //

  // helper function to detect "reference expected" errors
  private static boolean producesReference(Node node) {
    if (node instanceof Identifier || node instanceof IdentPropertyAccessor || node instanceof ExprPropertyAccessor) {
      return true;
    }
    return false;
  }
  
  /** Used to detect non-references on left-hand-side of assignment.
   *
   *  @param  loc  location in source code (file, line, column)
   *  @param  node tree to be checked
   */
  public static void checkAssignmentDestination(Location loc, Node node) {
    if (!producesReference(node)) {
      Message.error(loc, "assignment destination must be a Reference");
    }
  }
}
