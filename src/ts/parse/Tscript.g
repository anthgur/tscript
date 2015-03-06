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
  : se=sourceElements EOF
    { semanticValue = $se.lval; }
  ;

sourceElements
  returns [ List<Statement> lval ]
  : s=sourceElement
    { $lval = new ArrayList<Statement>();
      $lval.add($s.lval); }
  | se=sourceElements s=sourceElement
    { $lval = $se.lval;
      $lval.add($s.lval); }
  ;

sourceElement
  returns [ Statement lval ]
  : s=statement
    { $lval = $s.lval; }
  | f=functionDeclaration
    { $lval = $f.lval; }
  ;

functionBody
  returns [ List<Statement> lval ]
  : // empty rule
    { $lval = new ArrayList<Statement>(); }
  | se=sourceElements
    { $lval = $se.lval; }
  ;

statement
  returns [ Statement lval ]
  : v=varStatement
    { $lval = $v.lval; }
  | e=expressionStatement
    { $lval = $e.lval; }
  | p=printStatement
    { $lval = $p.lval; }
  | i=iterationStatement
    { $lval = $i.lval; }
  | l=ifStatement
    { $lval = $l.lval; }
  | b=blockStatement
    { $lval = $b.lval; }
  | t=tryStatement
    { $lval = $t.lval; }
  | th=throwStatement
    { $lval = $th.lval; }
  | BREAK SEMICOLON
    { $lval = new BreakStatement(loc($start)); }
  | CONTINUE SEMICOLON
    { $lval = new ContinueStatement(loc($start)); }
  | SEMICOLON
    { $lval = new EmptyStatement(loc($start)); }
  ;

tryStatement
  returns [ Statement lval ]
  : TRY b=blockStatement c=catchStatement
    { $lval = new TryStatement(loc($start), $b.lval, $c.lval, null); }
  | TRY b=blockStatement f=finallyStatement
    { $lval = new TryStatement(loc($start), $b.lval, null, $f.lval); }
  | TRY b=blockStatement c=catchStatement f=finallyStatement
    { $lval = new TryStatement(loc($start), $b.lval, $c.lval, $f.lval); }
  ;

throwStatement
  returns [ Statement lval ]
  : THROW e=expression SEMICOLON
    { $lval = new ThrowStatement(loc($start), $e.lval); }
  ;

catchStatement
  returns [ CatchStatement lval ]
  : CATCH LPAREN i=IDENTIFIER RPAREN b=blockStatement
    { $lval = new CatchStatement(loc($start), $i.text, $b.lval); }
  ;

finallyStatement
  returns [ BlockStatement lval ]
  : FINALLY b=blockStatement
    { $lval = $b.lval; }
  ;

ifStatement
  returns [ Statement lval ]
  : IF LPAREN e=expression RPAREN s1=statement ELSE s2=statement
    { $lval = buildIfStatement(loc($start), $e.lval, $s1.lval, $s2.lval); }
  | IF LPAREN e=expression RPAREN s=statement
    { $lval = buildIfStatement(loc($start), $e.lval, $s.lval); }
  ;

iterationStatement
  returns [ Statement lval ]
  : WHILE LPAREN e=expression RPAREN s=statement
    { $lval = buildWhileStatement(loc($start), $e.lval, $s.lval); }
  ;

blockStatement
  returns [ BlockStatement lval ]
  : LCURLY sl=statementList RCURLY
    { $lval = buildBlockStatement(loc($start), $sl.lval); }
  ;

statementList
  returns [ List<Statement> lval ]
  : // empty rule
    { $lval = new ArrayList<Statement>(); }
  | sl=statementList s=statement
    { $sl.lval.add($s.lval);
      $lval = $sl.lval; }
  ;

varStatement
  returns [ Statement lval ]
  : VAR vl=variableDeclarationList SEMICOLON
    { $lval = buildVarStatement(loc($start), $vl.lval); }
  ;

variableDeclarationList
  returns [ List<Statement> lval ]
  : v=variableDeclaration
    { $lval = new ArrayList<Statement>();
      $lval.add($v.lval); }
  | vl=variableDeclarationList COMMA v=variableDeclaration
    { $vl.lval.add($v.lval);
      $lval = $vl.lval; }
  ;

variableDeclaration
  returns [ Statement lval ]
  : IDENTIFIER
    { $lval = buildVarDeclaration(loc($start),
                $IDENTIFIER.text); }
  | IDENTIFIER i=initializer
    { $lval = buildVarDeclaration(loc($start),
                $IDENTIFIER.text, $i.lval); }
  ;

initializer
  returns [ Expression lval ]
  : EQUAL a=assignmentExpression
    { $lval = $a.lval; }
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
  | f=functionExpression
    { $lval = $f.lval; }
  ;

functionDeclaration
  returns [ Statement lval ]
  : FUNCTION i=IDENTIFIER LPAREN fpl=formalParameterList RPAREN LCURLY f=functionBody RCURLY
    { $lval = new FunctionDeclaration(loc($start), $i.text, $fpl.lval, $f.lval); }
  ;

functionExpression
  returns [ Expression lval ]
  : FUNCTION LPAREN fpl=formalParameterList RPAREN LCURLY f=functionBody RCURLY
    { $lval = new FunctionExpression(loc($start), null, $fpl.lval, $f.lval); }
  ;

formalParameterList
  returns [ List<String> lval ]
  : // empty rule
    { $lval = new ArrayList<>(); }
  | fpl=formalParameterList COMMA i=IDENTIFIER
    { $lval.add($i.text); }
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
  | l=multiplicativeExpression FSLASH r=unaryExpression
      { $lval = buildBinaryOperator(loc($start), BinaryOpcode.DIVIDE,
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
  | STRING_LITERAL
    { $lval = buildStringLiteral(loc($start), $STRING_LITERAL.text); }
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

fragment DOUBLE_STRING_CHARS : ( ~[\\\"] | [\\n] )*;
fragment SINGLE_STRING_CHARS : ( ~[\\\'] | [\\n] )*;

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

STRING_LITERAL
  : '\"' DOUBLE_STRING_CHARS '\"'
  | '\'' SINGLE_STRING_CHARS '\''
  ;

LCURLY : [{];
RCURLY : [}];
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
COMMA : [,];
FSLASH : [//];

// keywords start here
PRINT : 'print';
VAR : 'var';
WHILE : 'while';
IF : 'if';
ELSE : 'else';
BREAK : 'break';
CONTINUE : 'continue';
THROW : 'throw';
TRY : 'try';
CATCH : 'catch';
FINALLY : 'finally';
FUNCTION: 'function';

IDENTIFIER : IdentifierCharacters;

// skip whitespace and comments

WhiteSpace : SpaceTokens+ -> skip;

