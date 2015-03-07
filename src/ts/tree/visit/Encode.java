/**
 * Traverse an AST to generate Java code.
 *
 */

package ts.tree.visit;

import ts.Message;
import ts.support.TSValue;
import ts.tree.*;
import ts.tree.visit.encode.BinaryOps;
import ts.tree.visit.encode.UnaryOps;

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
public final class Encode extends TreeVisitorBase<Encode.ReturnValue> {
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
  static public class ReturnValue {
    public String result;

    public String code;

    // initialize both fields
    private ReturnValue() {
      result = null;
      code = null;
    }

    // for non-expressions
    public ReturnValue(final String code) {
      this();
      this.code = code;
    }

    // for most expressions
    public ReturnValue(final String result, final String code) {
      this();
      this.result = result;
      this.code = code;
    }
  }

  private List<Encode.ReturnValue> functions = new ArrayList<ReturnValue>();

  // simple counter for expression temps
  private int nextTemp = 0;
  private int nextFunc = 0;
  private int iterationStatementLevel = 0;

  // by default start output indented 2 spaces and increment
  // indentation by 2 spaces
  public Encode() {
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
  private void increaseIndentation() {
    indentation += increment;
  }

  // decrease indentation by one level
  private void decreaseIndentation() {
    indentation -= increment;
  }

  public Encode(final int initialIndentation, final int increment) {
    // setup indentation
    this.initialIndentation = initialIndentation;
    this.indentation = initialIndentation;
    this.increment = increment;
  }

  // generate a string of spaces for current indentation level
  private String indent() {
    String ret = "";
    for (int i = 0; i < indentation; i++) {
      ret += " ";
    }
    return ret;
  }

  // generate main method signature
  public String mainMethodSignature() {
    return "public static void main(String args[])";
  }

  public String executeSignature() {
    return "public TSValue execute(TSLexicalEnvironment lexEnviron0, TSValue ths, List args, boolean isConstructor)";
  }

  // generate and return prologue code for the main method body
  public String mainPrologue(String filename) {
    StringBuilder codeBuilder = new StringBuilder(indent());
    codeBuilder.append("{\n");
    increaseIndentation();
    codeBuilder.append(indent());
    codeBuilder.append("try {\n");
    increaseIndentation();
    codeBuilder.append(indent());
    codeBuilder.append("TSLexicalEnvironment lexEnviron0 = TSLexicalEnvironment.getGlobalEnv();\n");
    return codeBuilder.toString();
  }

  // generate and return epilogue code for main method body
  public String mainEpilogue() {
    decreaseIndentation();
    StringBuilder codeBuilder = new StringBuilder(indent());
    codeBuilder.append("} catch (TSException e) {\n");
    codeBuilder.append(indent());
    codeBuilder.append(indent());
    codeBuilder.append("Message.executionError(e.getValue().toString());\n");
    codeBuilder.append(indent());
    codeBuilder.append("}\n");
    decreaseIndentation();
    codeBuilder.append(indent());
    codeBuilder.append("}");
    return codeBuilder.toString();
  }

  public String functionComments() {
    StringBuilder codeBuilder = new StringBuilder("/*\n");
    for (Encode.ReturnValue er : functions) {
      codeBuilder.append(er.result);
      codeBuilder.append("\n");
      codeBuilder.append(er.code);
    }
    codeBuilder.append("*/");
    return codeBuilder.toString();
  }

  // return string for name of next expression temp
  private String getTemp() {
    String ret = "temp" + nextTemp;
    nextTemp += 1;
    return ret;
  }

  public List<Encode.ReturnValue> getFunctions() {
    return functions;
  }

  // visit a list of ASTs and generate code for each of them in order
  // use wildcard for generality: list of Statements, list of Expressions, etc
  public List<Encode.ReturnValue> visitEach(final Iterable<?> nodes) {
    List<Encode.ReturnValue> ret = new ArrayList<Encode.ReturnValue>();

    for (final Object node : nodes) {
      ret.add(visitNode((Tree) node));
    }
    return ret;
  }
  
  // gen and return code for a binary operator
  public Encode.ReturnValue visit(final BinaryOperator opNode) {
    String result = getTemp();

    Encode.ReturnValue leftReturnValue = visitNode(opNode.getLeft());
    String code = leftReturnValue.code;

    Encode.ReturnValue rightReturnValue = visitNode(opNode.getRight());
    code += rightReturnValue.code;

    code += indent() + "TSValue " + result + " = " +
            BinaryOps.encode(opNode, leftReturnValue, rightReturnValue);

    return new Encode.ReturnValue(result, code);
  }

  public ReturnValue visit(UnaryOperator opNode) {
    String result = getTemp();

    Encode.ReturnValue exprReturn = visitNode(opNode.getExpr());
    String code = exprReturn.code;

    code += indent() + "TSValue " + result + " = "
            + UnaryOps.encode(opNode, exprReturn);

    return new Encode.ReturnValue(result, code);
  }

  public Encode.ReturnValue visit(final Expression expr) {
    return visitNode(expr);
  }

  // process an expression statement
  public Encode.ReturnValue visit(final ExpressionStatement expressionStatement) {
    Encode.ReturnValue exp = visitNode(expressionStatement.getExp());
    String code = indent() + "Message.setLineNumber(" +
      expressionStatement.getLineNumber() + ");\n";
    code += exp.code;

    return new Encode.ReturnValue(code);
  }

  public Encode.ReturnValue visit(final Identifier identifier) {
    String result = getTemp();
    String code = indent() + "TSValue " + result +
      " = " + "lexEnviron0" +
      ".getIdentifierReference(TSString.create(\"" +
      identifier.getName() + "\"));\n";

    return new Encode.ReturnValue(result, code);
  }

  public Encode.ReturnValue visit(final StringLiteral stringLiteral) {
    String result = getTemp();
    String code = indent() + "TSValue " + result + " = " + "TSString.create" +
            "(\"" + stringLiteral.getValue() + "\");\n";

    return new Encode.ReturnValue(result, code);
  }

  public Encode.ReturnValue visit(final NumericLiteral numericLiteral) {
    String result = getTemp();
    String code = indent() + "TSValue " + result + " = " + "TSNumber.create" +
      "(" + numericLiteral.getValue() + ");\n";

    return new Encode.ReturnValue(result, code);
  }

  public Encode.ReturnValue visit(final BooleanLiteral booleanLiteral) {
    String result = getTemp();
    String code = indent() + "TSValue " + result + " = " + "TSBoolean.create" +
            "(" + booleanLiteral.getValue() + ");\n";
    return new Encode.ReturnValue(result, code);
  }

  public Encode.ReturnValue visit(final NullLiteral nullLiteral) {
    String result = getTemp();
    String code = indent() + "TSValue " + result + " = " + "TSNull.nullValue;\n";
    return new Encode.ReturnValue(result, code);
  }

  public Encode.ReturnValue visit(final PrintStatement printStatement) {
    Encode.ReturnValue exp = visitNode(printStatement.getExp());
    String code = indent() + "Message.setLineNumber(" +
      printStatement.getLineNumber() + ");\n";
    code += exp.code;
    code += indent() + "System.out.println(" + exp.result +
      ".toStr().unbox());\n";
    return new Encode.ReturnValue(code);
  }

  public Encode.ReturnValue visit(final VarDeclaration varDeclaration) {
    String varName = "TSString.create(\"" + varDeclaration.getName() + "\")";

    String code = indent() + "Message.setLineNumber(" +
      varDeclaration.getLineNumber() + ");\n";

    code += indent() + "lexEnviron0.declareVariable(" +
            varName + ", false);\n";

    final Expression assignExpr = varDeclaration.getExpression();

    if(assignExpr != null) {
      Encode.ReturnValue rhs = visitNode(assignExpr);
      code += rhs.code + indent() +
              "lexEnviron0.getIdentifierReference(" +
              varName + ").simpleAssignment(" +
              rhs.result + ");\n";
    }

    return new Encode.ReturnValue(code);
  }

  public Encode.ReturnValue visit(final VarStatement varStatement) {
    StringBuilder codeBuilder = new StringBuilder();
    for (Encode.ReturnValue r :
            visitEach(varStatement.getVarDeclList())) {
      codeBuilder.append(r.code);
    }
    return new Encode.ReturnValue(codeBuilder.toString());
  }

  public Encode.ReturnValue visit(final BlockStatement blockStatement) {
    StringBuilder codeBuilder = new StringBuilder(indent());
    codeBuilder.append("{\n");
    increaseIndentation();
    for(Encode.ReturnValue rv :
            visitEach(blockStatement.getStatementList())) {
      codeBuilder.append(rv.code);
    }
    decreaseIndentation();
    codeBuilder.append(indent());
    codeBuilder.append("}\n");
    return new Encode.ReturnValue(codeBuilder.toString());
  }

  public Encode.ReturnValue visit(final EmptyStatement emptyStatement) {
    return new Encode.ReturnValue(indent() + "if (true) {} // EmptyStatement\n");
  }

  public Encode.ReturnValue visit(final BreakStatement breakStatement) {
    if (iterationStatementLevel > 0) {
      return new Encode.ReturnValue(indent() + "break; // BreakStatement\n");
    }
    Message.error(breakStatement.getLoc(), "Invalid BreakStatement");
    return null;
  }

  public Encode.ReturnValue visit(final ContinueStatement continueStatement) {
    if (iterationStatementLevel > 0) {
      return new Encode.ReturnValue(indent() + "continue; // ContinueStatement\n");
    }
    Message.error(continueStatement.getLoc(), "Invalid BreakStatement");
    return null;
  }

  public Encode.ReturnValue visit(final WhileStatement whileStatement) {
    iterationStatementLevel++;
    StringBuilder codeBuilder = new StringBuilder(indent());
    codeBuilder.append("while(true) {\n");
    increaseIndentation();
    Encode.ReturnValue expression = visitNode(whileStatement.getExpression());
    Encode.ReturnValue statement = visitNode(whileStatement.getStatement());
    codeBuilder.append(expression.code);
    codeBuilder.append(indent());
    codeBuilder.append("if(!");
    codeBuilder.append(expression.result);
    codeBuilder.append(".getValue().toBoolean().unbox()) break;\n");
    codeBuilder.append(statement.code);
    decreaseIndentation();
    codeBuilder.append(indent());
    codeBuilder.append("}\n");
    iterationStatementLevel--;
    return new Encode.ReturnValue(codeBuilder.toString());
  }

  public Encode.ReturnValue visit(final IfStatement theIf) {
    final Statement lse = theIf.getElseStat();
    final Encode.ReturnValue expression, ifStatement, elseStatement;
    expression = visitNode(theIf.getExpr());
    ifStatement = visitNode(theIf.getIfStat());
    StringBuilder codeBuilder = new StringBuilder(expression.code);
    codeBuilder.append(indent());
    codeBuilder.append("if (");
    codeBuilder.append(expression.result);
    codeBuilder.append(".getValue().toBoolean().unbox()) {\n");
    codeBuilder.append(ifStatement.code);
    codeBuilder.append(indent());
    codeBuilder.append("}\n");
    if (lse != null) {
      elseStatement = visitNode(lse);
      codeBuilder.append(indent());
      codeBuilder.append("else {\n");
      codeBuilder.append(elseStatement.code);
      codeBuilder.append(indent());
      codeBuilder.append("}\n");
    }

    return new Encode.ReturnValue(codeBuilder.toString());
  }

  public Encode.ReturnValue visit(final TryStatement tryStatement) {
    final CatchStatement catchStatement;
    final BlockStatement finallyBlock;
    StringBuilder codeBuilder = new StringBuilder(indent());
    codeBuilder.append("try\n");
    codeBuilder.append(visitNode(tryStatement.getBlock()).code);
    if((catchStatement = tryStatement.getCatch()) != null) {
      codeBuilder.append(visitNode(catchStatement).code);
    }
    if((finallyBlock = tryStatement.getFinally()) != null) {
      codeBuilder.append(indent());
      codeBuilder.append("finally\n");
      codeBuilder.append(visitNode(finallyBlock).code);
    }
    return new Encode.ReturnValue(codeBuilder.toString());
  }

  public Encode.ReturnValue visit(final CatchStatement catchStatement) {
    StringBuilder codeBuilder = new StringBuilder(indent());
    codeBuilder.append("catch (TSException e)\n");
    codeBuilder.append(indent());
    codeBuilder.append("{\n");
    increaseIndentation();
    codeBuilder.append(indent());
    codeBuilder.append("TSLexicalEnvironment lexEnviron0 =\n");
    codeBuilder.append(indent());
    codeBuilder.append("    TSLexicalEnvironment.newDeclarativeEnvironment(lexEnviron0);\n");
    codeBuilder.append(indent());
    codeBuilder.append("lexEnviron0.declareParameter(\"");
    codeBuilder.append(catchStatement.getIdent().getName());
    codeBuilder.append("\", e.getValue());\n");
    codeBuilder.append(visit(catchStatement.getBlock()).code);
    decreaseIndentation();
    codeBuilder.append(indent());
    codeBuilder.append("}\n");
    return new Encode.ReturnValue(codeBuilder.toString());
  }

  public Encode.ReturnValue visit(final ThrowStatement throwStatement) {
    Encode.ReturnValue expr = visit(throwStatement.getExpr());
    StringBuilder codeBuilder = new StringBuilder(expr.code);
    codeBuilder.append(indent());
    codeBuilder.append("throw new TSException(");
    codeBuilder.append(expr.result);
    codeBuilder.append(".getValue());\n");
    return new Encode.ReturnValue(codeBuilder.toString());
  }

  public Encode.ReturnValue visit(final CallExpression call) {
    final Encode.ReturnValue ref = visitNode(call.getExpr());
    final String func = getTemp()
            , result = getTemp();

    String code = ref.code + indent() +  "TSValue "
            + func + " = " + ref.result + ".getValue();\n"
            + "if (!" + func + ".isObject()) { throw new TSTypeError(TSString.create(\"Type error\")); }\n"
            + "if (!" + func + ".isCallable()) { throw new TSTypeError(TSString.create(\"Type error\")); }\n"
            + "TSValue ths = TSUndefined.value;\n"
            + "List arr = new ArrayList();\n"
            + "TSValue " + result + " = "
            + "((TSCode) " + func + ").execute(lexEnviron0, ths, arr, false);\n";
    return new Encode.ReturnValue(result, code);
  }

  public Encode.ReturnValue visit(final FunctionExpression func) {
    final Encode.ReturnValue er = genFunctionBody(func.getBody());
    final String ident
            , result = getTemp()
            , params = getTemp();

    functions.add(er);

    // repack the formal params
    String code = indent() + "List " + params + " = new ArrayList();\n";
    for (String p : func.getFormalParameters()) {
      code += params + ".add(\"" + p + "\");\n";
    }

    // instantiate the object
    code += indent() + "TSValue " + result + " = new " + er.result + "(";
    if((ident = func.getIdent()) != null) {
      code += "\"" + ident + "\", ";
    }
    code += "lexEnviron0, " + params + ");\n";
    return new Encode.ReturnValue(result, code);
  }

  private Encode.ReturnValue genFunctionBody(List<Statement> body) {
    String name = "Func" + nextFunc++;
    String code = "{\n";
    code += indent() + "TSLexicalEnvironment lexEnviron0 = $1;\n";
    code += indent() + "lexEnviron0 = TSLexicalEnvironment.newDeclarativeEnvironment(this.scope);";
    for (Encode.ReturnValue er : visitEach(body)) {
      code += er.code;
    }
    code += indent();
    // TODO return undefined here?
    code += "return TSUndefined.value;\n}\n";
    return new Encode.ReturnValue(name, code);
  }
}
