/**
 * Traverse an AST to generate Java code.
 *
 */

package ts.tree.visit;

import ts.Message;
import ts.Main;
import ts.tree.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Does a traversal of the AST to generate Java code to execute the program
 * represented by the AST.
 * <p>
 * Uses a static nested class, Encode.ReturnValue, for the type parameter.
 * This class contains two String fields: one for the temporary variable
 * containing the result of executing code for an AST node; one for the
 * code generated for the AST node.
 * <p>
 * The "visit" method is overloaded for each tree node type.
 */
public final class Encode extends TreeVisitorBase<Encode.ReturnValue>
{
  /**
   * Static nested class to represent the return value of the Encode methods.
   * <p>
   * Contains the following fields:
   * <p>
   * <ul>
   * <li> a String containing the result operand name<p>
   * <li> a String containing the code to be generated<p>
   * </ul>
   * Only expressions generate results, so the result operand name
   * will be null in other cases, such as statements.
   */
  static public class ReturnValue
  {
    public String result;

    public String code;

    // initialize both fields
    private ReturnValue()
    {
      result = null;
      code = null;
    }

    // for non-expressions
    public ReturnValue(final String code)
    {
      this();
      this.code = code;
    }

    // for most expressions
    public ReturnValue(final String result, final String code)
    {
      this();
      this.result = result;
      this.code = code;
    }
  }

  // simple counter for expression temps
  private int nextTemp = 0;

  // by default start output indented 2 spaces and increment
  // indentation by 2 spaces
  public Encode()
  {
    this(2, 2);
  }

  // initial indentation value
  private final int initialIndentation;

  // current indentation amount
  private int indentation;

  // how much to increment the indentation by at each level
  // using an increment of zero would mean no indentation
  private final int increment;

  // increase indentation by one level
  private void increaseIndentation()
  {
    indentation += increment;
  }

  // decrease indentation by one level
  private void decreaseIndentation()
  {
    indentation -= increment;
  }

  public Encode(final int initialIndentation, final int increment)
  {
    // setup indentation
    this.initialIndentation = initialIndentation;
    this.indentation = initialIndentation;
    this.increment = increment;
  }

  // generate a string of spaces for current indentation level
  private String indent()
  {
    String ret = "";
    for (int i = 0; i < indentation; i++)
    {
      ret += " ";
    }
    return ret;
  }

  // generate main method signature
  public String mainMethodSignature()
  {
    return "public static void main(String args[])";
  }

  // generate and return prologue code for the main method body
  public String mainPrologue(String filename)
  {
    String ret = "";
    ret += indent() + "{\n";
    increaseIndentation();
    ret += indent() + "TSLexicalEnvironment " + "lexEnviron" + " = " +
      "TSLexicalEnvironment.newDeclarativeEnvironment(null);\n";
    return ret;
  }

  // generate and return epilogue code for main method body
  public String mainEpilogue()
  {
    decreaseIndentation();
    String ret = "";
    ret += indent() + "}";
    return ret;
  }

  // return string for name of next expression temp
  private String getTemp()
  {
    String ret = "temp" + nextTemp;
    nextTemp += 1;
    return ret;
  }

  // visit a list of ASTs and generate code for each of them in order
  // use wildcard for generality: list of Statements, list of Expressions, etc
  public List<Encode.ReturnValue> visitEach(final Iterable<?> nodes)
  {
    List<Encode.ReturnValue> ret = new ArrayList<Encode.ReturnValue>();

    for (final Object node : nodes)
    {
      ret.add(visitNode((Tree) node));
    }
    return ret;
  }
  
  // gen and return code for a binary operator
  public Encode.ReturnValue visit(final BinaryOperator binaryOperator)
  {
    String result = getTemp();

    Encode.ReturnValue leftReturnValue = visitNode(binaryOperator.getLeft());
    String code = leftReturnValue.code;

    Encode.ReturnValue rightReturnValue = visitNode(binaryOperator.getRight());
    code += rightReturnValue.code;

    String methodName = getMethodNameForBinaryOperator(binaryOperator);
    code += indent() + "TSValue " + result + " = " + leftReturnValue.result +
      "." + methodName + "(" + rightReturnValue.result + ");\n";

    return new Encode.ReturnValue(result, code);
  }

  // support routine for handling binary operators
  private static String getMethodNameForBinaryOperator(
    final BinaryOperator opNode)
  {
    final Binop op = opNode.getOp();

    switch (op) {
      case ADD:
        return "add";
      case ASSIGN:
        return "simpleAssignment";
      case MULTIPLY:
        return "multiply";
      default:
        assert false: "unexpected operator: " + opNode.getOpString();
    }
    // cannot reach
    return null;
  }

  // process an expression statement
  public Encode.ReturnValue visit(final ExpressionStatement
    expressionStatement)
  {
    Encode.ReturnValue exp = visitNode(expressionStatement.getExp());
    String code = indent() + "Message.setLineNumber(" +
      expressionStatement.getLineNumber() + ");\n";
    code += exp.code;
    return new Encode.ReturnValue(code);
  }

  public Encode.ReturnValue visit(final Identifier identifier)
  {
    String result = getTemp();
    String code = indent() + "TSValue " + result +
      " = " + "lexEnviron" +
      ".getIdentifierReference(TSString.create(\"" +
      identifier.getName() + "\"));\n";

    return new Encode.ReturnValue(result, code);
  }

  public Encode.ReturnValue visit(final NumericLiteral numericLiteral)
  {
    String result = getTemp();
    String code = indent() + "TSValue " + result + " = " + "TSNumber.create" +
      "(" + numericLiteral.getValue() + ");\n";

    return new Encode.ReturnValue(result, code);
  }

  public Encode.ReturnValue visit(final PrintStatement printStatement)
  {
    Encode.ReturnValue exp = visitNode(printStatement.getExp());
    String code = indent() + "Message.setLineNumber(" +
      printStatement.getLineNumber() + ");\n";
    code += exp.code;
    code += indent() + "System.out.println(" + exp.result +
      ".toStr().getInternal());\n";
    return new Encode.ReturnValue(code);
  }

  public Encode.ReturnValue visit(final VarStatement varStatement)
  {
    String code = indent() + "Message.setLineNumber(" +
      varStatement.getLineNumber() + ");\n";

    code += indent() + "lexEnviron.declareVariable(TSString.create" +
      "(\"" + varStatement.getName() + "\"), false);\n";

    return new Encode.ReturnValue(code);
  }

}

