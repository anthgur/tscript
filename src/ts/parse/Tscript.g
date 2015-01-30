//
// an ANTLR parser specification for a Tscript subset
//

grammar Tscript;

@header {
  package ts.parse;
  import ts.Location;
  import ts.tree.*;
  import static ts.parse.TreeBuilder.*;
  import java.util.List;
  import java.util.ArrayList;
}

@members {
  // grab location info (filename/line/column) from token
  // in order to stick into AST nodes for later error reporting
  public Location loc(final Token token)
  {
    return new Location(getSourceName(), token.getLine(),
      token.getCharPositionInLine());
  }

  // a program is a list of statements
  // i.e. root of AST is stored here
  // set by the action for the start symbol
  private List<Statement> semanticValue;
  public List<Statement> getSemanticValue()
  {
    return semanticValue;
  }
}

// grammar proper

program
  : sl=statementList EOF
    { semanticValue = $sl.lval; }
  ;

statementList
  returns [ List<Statement> lval ]
  : // empty rule
    { $lval = new ArrayList<Statement>(); }
  | sl=statementList s=statement
    { $sl.lval.add($s.lval);
      $lval = $sl.lval; }
  ;

statement
  returns [ Statement lval ]
  : v=varStatement
    { $lval = $v.lval; }
  | e=expressionStatement
    { $lval = $e.lval; }
  | p=printStatement
    { $lval = $p.lval; }
  ;

varStatement
  returns [ Statement lval ]
  : VAR IDENTIFIER SEMICOLON
    { $lval = buildVarStatement(loc($start), $IDENTIFIER.text); }
  ;

expressionStatement
  returns [ Statement lval ]
  : e=expression SEMICOLON
    { $lval = buildExpressionStatement(loc($start), $e.lval); }
  ;

printStatement
  returns [ Statement lval ]
  : PRINT e=expression SEMICOLON
    { $lval = buildPrintStatement(loc($start), $e.lval); }
  ;

expression
  returns [ Expression lval ]
  : a=assignmentExpression
    { $lval = $a.lval; }
  ;

newExpression
  returns [ Expression lval ]
  : a=memberExpression
    { $lval = $a.lval; }
  ;

memberExpression
  returns [ Expression lval ]
  : a=primaryExpression
    { $lval = $a.lval; }
  ;

assignmentExpression
  returns [ Expression lval ]
  : a=conditionalExpression
    { $lval = $a.lval; }
  | l=leftHandSideExpression EQUAL r=assignmentExpression
    { checkAssignmentDestination(loc($start), $l.lval);
      $lval = buildBinaryOperator(loc($start), BinaryOpcode.ASSIGN,
        $l.lval, $r.lval); }
  ;

conditionalExpression
  returns [ Expression lval ]
  : a=logicalOrExpression
    { $lval = $a.lval; }
  ;

logicalOrExpression
  returns [ Expression lval ]
  : a=logicalAndExpression
    { $lval = $a.lval; }
  ;

logicalAndExpression
  returns [ Expression lval ]
  : a=bitwiseOrExpression
    { $lval = $a.lval; }
  ;

bitwiseOrExpression
  returns [ Expression lval ]
  : a=bitwiseXorExpression
    { $lval = $a.lval; }
  ;

bitwiseXorExpression
  returns [ Expression lval ]
  : a=bitwiseAndExpression
    { $lval = $a.lval; }
  ;

bitwiseAndExpression
  returns [ Expression lval ]
  : a=equalityExpression
    { $lval = $a.lval; }
  ;

leftHandSideExpression
  returns [ Expression lval ]
  : p=newExpression
    { $lval = $p.lval; }
  ;

postfixExpression
  returns [ Expression lval ]
  : l=leftHandSideExpression
    { $lval = $l.lval; }
  ;

unaryExpression
  returns [ Expression lval ]
  : l=postfixExpression
    { $lval = $l.lval; }
  | EXCLAMATION r=unaryExpression
    { $lval = buildUnaryOperator(loc($start), UnaryOpcode.NOT, $r.lval); }
  | PLUS r=unaryExpression
    { $lval = buildUnaryOperator(loc($start), UnaryOpcode.PLUS, $r.lval); }
  | MINUS r=unaryExpression
      { $lval = buildUnaryOperator(loc($start), UnaryOpcode.MINUS, $r.lval); }
  ;

additiveExpression
  returns [ Expression lval ]
  : m=multiplicativeExpression
    { $lval = $m.lval; }
  | l=additiveExpression PLUS r=multiplicativeExpression
    { $lval = buildBinaryOperator(loc($start), BinaryOpcode.ADD,
        $l.lval, $r.lval); }
  | l=additiveExpression MINUS r=multiplicativeExpression
      { $lval = buildBinaryOperator(loc($start), BinaryOpcode.SUBTRACT,
          $l.lval, $r.lval); }
  ;

multiplicativeExpression
  returns [ Expression lval ]
  : p=unaryExpression
    { $lval = $p.lval; }
  | l=multiplicativeExpression ASTERISK r=unaryExpression
    { $lval = buildBinaryOperator(loc($start), BinaryOpcode.MULTIPLY,
      $l.lval, $r.lval); }
  ;

primaryExpression
  returns [ Expression lval ]
  : IDENTIFIER
    { $lval = buildIdentifier(loc($start), $IDENTIFIER.text); }
  | DECIMAL_LITERAL
    { $lval = buildDecimalLiteral(loc($start), $DECIMAL_LITERAL.text); }
  | HEX_INTEGER_LITERAL
    { $lval = buildHexIntegerLiteral(loc($start), $HEX_INTEGER_LITERAL.text); }
  | BOOLEAN_LITERAL
    { $lval = buildBooleanLiteral(loc($start), $BOOLEAN_LITERAL.text); }
  | NULL_LITERAL
    { $lval = new NullLiteral(loc($start)); }
  | LPAREN e=expression RPAREN
    { $lval = $e.lval; }
  ;

equalityExpression
  returns [ Expression lval ]
  : r=relationalExpression
    { $lval = $r.lval; }
  | l=equalityExpression DOUBLE_EQUAL r=relationalExpression
    { $lval = buildBinaryOperator(loc($start), BinaryOpcode.EQUALITY,
      $l.lval, $r.lval); }
  ;

relationalExpression
  returns [ Expression lval ]
  : s=shiftExpression
    { $lval = $s.lval; }
  | l=relationalExpression LESS r=shiftExpression
    { $lval = buildBinaryOperator(loc($start), BinaryOpcode.LESS_THAN,
      $l.lval, $r.lval); }
  | l=relationalExpression GREATER r=shiftExpression
    { $lval = buildBinaryOperator(loc($start), BinaryOpcode.GREATER_THAN,
      $l.lval, $r.lval); }
  ;

shiftExpression
  returns [ Expression lval ]
  : a=additiveExpression
    { $lval = $a.lval; }
  ;

// fragments to support the lexer rules

fragment DIGIT : [0-9];

fragment HEX_DIGIT : [0-9a-fA-F];

fragment HEX_INDICATOR : [0] ( [x] | [X] );

fragment EXPONENT_INDICATOR : [e] | [E];

// cannot have a leading 0 unless the literal is just 0
fragment INTEGER_LITERAL : ([1-9] DIGIT*) | [0];

fragment SIGNED_INTEGER : ( [-] | [+] )? INTEGER_LITERAL;

fragment EXPONENT_PART : EXPONENT_INDICATOR SIGNED_INTEGER;

fragment IdentifierCharacters : [a-zA-Z_$] [a-zA-Z0-9_$]*;

fragment SpaceTokens : SpaceChars | LineTerminator | EndOfLineComment;

fragment SpaceChars : ' ' | '\t' | '\f';

fragment EndOfLineComment : '//' ( ~[\n\r] )* (LineTerminator | EOF);

fragment LineTerminator : '\r' '\n' | '\r' | '\n';

// lexer rules
//   keywords must appear before IDENTIFIER

// The following two rules support NumericLiteral as defined by
// http://www.ecma-international.org/ecma-262/5.1/#sec-7.8.3

// DecimalLiteral
DECIMAL_LITERAL
  : INTEGER_LITERAL [.] DIGIT* EXPONENT_PART?
  | [.] DIGIT* EXPONENT_PART?
  | INTEGER_LITERAL EXPONENT_PART?
  ;

// HexIntegerLiteral
HEX_INTEGER_LITERAL : HEX_INDICATOR HEX_DIGIT+;

BOOLEAN_LITERAL : 'true' | 'false';

NULL_LITERAL : 'null';

LPAREN : [(];
RPAREN : [)];
SEMICOLON : [;];
EQUAL : [=];
DOUBLE_EQUAL: EQUAL EQUAL;
PLUS : [+];
MINUS : [-];
ASTERISK : [*];
EXCLAMATION : [!];
LESS : [<];
GREATER : [>];

// keywords start here
PRINT : 'print';
VAR : 'var';

IDENTIFIER : IdentifierCharacters;

// skip whitespace and comments

WhiteSpace : SpaceTokens+ -> skip;

