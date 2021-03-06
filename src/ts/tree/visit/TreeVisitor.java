
package ts.tree.visit;

import ts.tree.*;

/**
 * All visitor classes for ASTs will implement this interface, which
 *   is parameterized by return type.
 *
 */
public interface TreeVisitor<T>
{
  T visit(BinaryOperator binaryOperator);

  T visit(BlockStatement blockStatement);

  T visit(UnaryOperator unaryOperator);

  T visit(ExpressionStatement expressionStatement);

  T visit(Identifier identifier);

  T visit(NumericLiteral numericLiteral);

  T visit(BooleanLiteral booleanLiteral);

  T visit(NullLiteral nullLiteral);

  T visit(StringLiteral stringLiteral);

  T visit(PrintStatement printStatement);

  T visit(VarDeclaration varDeclaration);

  T visit(VarStatement varStatement);

  T visit(EmptyStatement emptyStatement);

  T visit(WhileStatement whileStatement);

  T visit(IfStatement ifStatement);

  T visit(BreakStatement breakStatement);

  T visit(ContinueStatement continueStatement);

  T visit(TryStatement tryStatement);

  T visit(CatchStatement catchStatement);

  T visit(ThrowStatement throwStatement);

  T visit(Expression expression);

  T visit(FunctionExpression func);

  T visit(CallExpression call);

  T visit(ReturnStatement ret);

  T visit(IdentPropertyAccessor accessor);

  T visit(ExprPropertyAccessor accessor);

  T visit(NewExpression n);
}
