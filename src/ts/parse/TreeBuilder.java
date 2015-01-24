package ts.parse;

import ts.Location;
import ts.Message;
import ts.tree.*;

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
  public static Statement buildVarStatement(final Location loc,
    final String name)
  {
    Message.log("TreeBuilder: VarStatement (" + name + ")");
    return new VarStatement(loc, name);
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
      @see Binop
   */
  public static Expression buildBinaryOperator(final Location loc,
    final Binop op,
    final Expression left, final Expression right)
  {
    Message.log("TreeBuilder: Binop " + op.toString());

    return new BinaryOperator(loc, op, left, right);
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
  public static Expression buildNumericLiteral(final Location loc,
    final String value)
  {
    double d = 0.0;

    try
    {
      d = Double.parseDouble(value);
    }
    catch(NumberFormatException nfe)
    {
      Message.bug(loc, "numeric literal not parsable");
    }
    Message.log("TreeBuilder: NumericLiteral " + d);
    return new NumericLiteral(loc, d);
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
