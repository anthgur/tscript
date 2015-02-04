package ts.parse;

import ts.Location;
import ts.Message;
import ts.tree.*;

import java.util.List;

/**
 * Provides static methods for building AST nodes
 */
public class TreeBuilder
{

  /** Build a "var" statement.
   *
   *  @param  loc  location in source code (file, line, column)
   *  @param  name name of variable being declared.
   */
  public static Statement buildVarDeclaration(final Location loc,
                                              final String name,
                                              final Expression expression)
  {
    Message.log("TreeBuilder: VarDeclaration (" + name + ")");
    return new VarDeclaration(loc, name, expression);
  }

  public static Statement buildVarDeclaration(final Location loc,
                                              final String name)
  {
    Message.log("TreeBuilder: VarDeclaration (" + name + ")");
    return new VarDeclaration(loc, name, null);
  }

  public static Statement buildVarStatement(final Location loc,
                                            final List<Statement> varDeclList) {
    Message.log("TreeBuilder: VarStatement");
    return new VarStatement(loc, varDeclList);
  }

  /** Build a expression statement.
   *
   *  @param  loc  location in source code (file, line, column)
   *  @param  exp  expression subtree
   */
  public static Statement buildExpressionStatement(final Location loc,
    final Expression exp)
  {
    Message.log("TreeBuilder: ExpressionStatement");
    return new ExpressionStatement(loc, exp);
  }

  /** Build a binary operator.
   *
   *  @param  loc   location in source code (file, line, column)
   *  @param  op    the binary operator
   *  @param  left  the left subtree
   *  @param  right the right subtree
      @see ts.tree.BinaryOpcode
   */
  public static Expression buildBinaryOperator(final Location loc,
    final BinaryOpcode op,
    final Expression left, final Expression right)
  {
    Message.log("TreeBuilder: Binop " + op.toString());

    return new BinaryOperator(loc, op, left, right);
  }

  public static Expression buildUnaryOperator
          (final Location loc, final UnaryOpcode op, final Expression expr) {
    Message.log("TreeBuilder: Unop " + op.toString());
    return new UnaryOperator(loc, op, expr);
  }

  /** Build a identifier expression.
   *
   *  @param  loc  location in source code (file, line, column)
   *  @param  name name of the identifier.
   */
  public static Expression buildIdentifier(final Location loc,
    final String name)
  {
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
    final String value)
  {
    double d = 0.0;

    try
    {
      d = Double.parseDouble(value);
    }
    catch(NumberFormatException nfe)
    {
      Message.bug(loc, "decimal literal not parsable");
    }
    Message.log("TreeBuilder: NumericLiteral " + d);
    return new NumericLiteral(loc, d);
  }

  public static Expression buildHexIntegerLiteral
          (final Location loc, final String value) {
    double d = 0.0;

    try
    {
      d = Integer.parseInt(value.substring(2), 16);
    } catch (NumberFormatException e) {
      Message.bug(loc, "hex literal not parsable " + value);
    }
    Message.log("TreeBuilder: NumericLiteral " + d);
    return new NumericLiteral(loc, d);
  }

  public static Expression buildBooleanLiteral
          (final Location loc, final String value)
  {
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
    final String processed =
            text.substring(1,text.length()-1).replace("\"", "\\\"");
    return new StringLiteral(loc, processed);
  }

  /** Build a print statement.
   *
   *  @param  loc  location in source code (file, line, column)
   *  @param  exp  expression subtree.
   */
  public static Statement buildPrintStatement(final Location loc,
    final Expression exp)
  {
    Message.log("TreeBuilder: PrintStatement");
    return new PrintStatement(loc, exp);
  }

  //
  // methods to detect "early" (i.e. semantic) errors
  //

  // helper function to detect "reference expected" errors
  private static boolean producesReference(Node node)
  {
    if (node instanceof Identifier)
    {
      return true;
    }
    return false;
  }
  
  /** Used to detect non-references on left-hand-side of assignment.
   *
   *  @param  loc  location in source code (file, line, column)
   *  @param  node tree to be checked
   */
  public static void checkAssignmentDestination(Location loc, Node node)
  {
    if (!producesReference(node))
    {
      Message.error(loc, "assignment destination must be a Reference");
    }
  }

}
