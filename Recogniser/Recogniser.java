/***
 ***
 *** Recogniser.java            
 ***
 ***/

/* At this stage, this parser accepts a subset of VC defined	by
 * the following grammar. 
 *
 * You need to modify the supplied parsing methods (if necessary) and 
 * add the missing ones to obtain a parser for the VC language.
 *
 * 19-Feb-2022

program       -> func-decl

// declaration

func-decl     -> void identifier "(" ")" compound-stmt

identifier    -> ID

// statements 
compound-stmt -> "{" stmt* "}" 
stmt          -> continue-stmt
    	      |  expr-stmt
continue-stmt -> continue ";"
expr-stmt     -> expr? ";"

// expressions 
expr                -> assignment-expr
assignment-expr     -> additive-expr
additive-expr       -> multiplicative-expr
                    |  additive-expr "+" multiplicative-expr
multiplicative-expr -> unary-expr
	            |  multiplicative-expr "*" unary-expr
unary-expr          -> "-" unary-expr
		    |  primary-expr

primary-expr        -> identifier
 		    |  INTLITERAL
		    | "(" expr ")"
*/

package VC.Recogniser;

import VC.Scanner.Scanner;
import VC.Scanner.SourcePosition;
import VC.Scanner.Token;
import VC.ErrorReporter;

public class Recogniser {

  private Scanner scanner;
  private ErrorReporter errorReporter;
  private Token currentToken;

  public Recogniser (Scanner lexer, ErrorReporter reporter) {
    scanner = lexer;
    errorReporter = reporter;

    currentToken = scanner.getToken();
  }

// match checks to see f the current token matches tokenExpected.
// If so, fetches the next token.
// If not, reports a syntactic error.

  void match(int tokenExpected) throws SyntaxError {
    if (currentToken.kind == tokenExpected) {
      currentToken = scanner.getToken();
    } else {
      syntacticError("\"%\" expected here", Token.spell(tokenExpected));
    }
  }

 // accepts the current token and fetches the next
  void accept() {
    currentToken = scanner.getToken();
  }

  void syntacticError(String messageTemplate, String tokenQuoted) throws SyntaxError {
    SourcePosition pos = currentToken.position;
    errorReporter.reportError(messageTemplate, tokenQuoted, pos);
    throw(new SyntaxError());
  }


// ========================== PROGRAMS ========================

  public void parseProgram() {
    System.out.println("in parseProgram");
    try {
      int i = 1;
      while (currentToken.kind != Token.EOF) {
        System.out.println("#" + Integer.toString(i));
        parseType();
        parseIdent();
        if (currentToken.kind == Token.LPAREN) {
          parseFuncDecl();
        } else {
          parseVarDecl();
        }
        i += 1;
      }
      if (currentToken.kind != Token.EOF) {
        syntacticError("\"%\" wrong result type for a function", currentToken.spelling);
      }
    }
    catch (SyntaxError s) {  }
  }

// ========================== DECLARATIONS ========================

  void parseFuncDecl() throws SyntaxError {
    System.out.println("in parseFuncDecl");

    // parseType();
    // parseIdent();
    parseParaList();
    parseCompoundStmt();
  }

  // void parseVarDeclList() throws SyntaxError {
  //   System.out.println("in parseVarDeclList");

  //   parseVarDecl();
  //   while (isType()) {
  //     parseVarDecl();
  //   }
  // }

  void parseVarDecl() throws SyntaxError {
    System.out.println("in parseVarDecl");

    // parseType();
    parseInitDeclaratorList();
    System.out.println("after parseInitDeclList()");
    match(Token.SEMICOLON);
  }

  void parseInitDeclaratorList() throws SyntaxError {
    System.out.println("in parseInitDeclaratorList");

    parseInitDeclarator();
    while (currentToken.kind == Token.COMMA) {
      match(Token.COMMA); // TODO: might need to change to accept or somethin
      parseInitDeclarator();
    }
  }

  void parseInitDeclarator() throws SyntaxError {
    System.out.println("in parseInitDeclarator");

    parseDeclarator();
    if (currentToken.kind == Token.EQ) {
      parseInitialiser();
    }
  }

  void parseDeclarator() throws SyntaxError {
    System.out.println("in parseDeclarator");
    
    if (currentToken.kind == Token.ID) {
      parseIdent();
    }

    if (currentToken.kind == Token.LBRACKET) {
      match(Token.LBRACKET); // TODO: might need to change to accept or somethin
      if (currentToken.kind != Token.RBRACKET) {
        parseIntLiteral();
      }
      match(Token.RBRACKET); // TODO: might need to change to accept or somethin
    }
  }

  void parseInitialiser() throws SyntaxError {
    System.out.println("in parseInitialiser");

    if (currentToken.kind == Token.LCURLY) {
      match(Token.LCURLY); // TODO: might need to change to accept or somethin
      if (currentToken.kind != Token.RCURLY) {
        parseExpr();
        while (currentToken.kind == Token.COMMA) {
          match(Token.COMMA); // TODO: might need to change to accept or somethin
          parseExpr();
        }
      }
      match(Token.RCURLY); // TODO: might need to change to accept or somethin
    } else {
      parseExpr();
    }
  }

// ========================== PRIMITIVE TYPES ========================

  void parseType() throws SyntaxError {
    System.out.println("in parseType");

    if (currentToken.kind == Token.VOID) {
      match(Token.VOID);
    } else if (currentToken.kind == Token.BOOLEAN) {
      match(Token.BOOLEAN);
    } else if (currentToken.kind == Token.INT) {
      match(Token.INT);
    } else {
      match(Token.FLOAT);
    }
  }

  boolean isType() {
    System.out.println("in isType");
    return currentToken.kind == Token.VOID
          || currentToken.kind == Token.BOOLEAN
          || currentToken.kind == Token.INT
          || currentToken.kind == Token.FLOAT;
  }

// ========================== PARAMETERS ========================

  void parseParaList() throws SyntaxError {
    System.out.println("in parseParaList");
    
    match(Token.LPAREN);
    if (currentToken.kind != Token.RPAREN) {
      parseProperParaList();
    }
    match(Token.RPAREN);
  }

  void parseProperParaList() throws SyntaxError {
    System.out.println("in parseProperParaList");

    parseParaDecl();
    while (currentToken.kind == Token.COMMA) {
      match(Token.COMMA); // TODO: might need to change to accept or somethin
      parseParaDecl();
    }
  }

  void parseParaDecl() throws SyntaxError {
    System.out.println("in parseParaDecl");

    parseType();
    parseDeclarator();
  }

  void parseArgList() throws SyntaxError {
    System.out.println("in parseArgList");
    match(Token.LPAREN);
    if (currentToken.kind != Token.RPAREN) {
      parseProperArgList();
    }
    match(Token.RPAREN);
  }

  void parseProperArgList() throws SyntaxError {
    System.out.println("in parseProperArgList");

    parseArg();
    while (currentToken.kind == Token.COMMA) {
      match(Token.COMMA); // TODO: might need to change to accept or somethin
      parseArg();
    }
  }

  void parseArg() throws SyntaxError {
    System.out.println("in parseArg");

    parseExpr();
  }

// ======================= STATEMENTS ==============================

  void parseCompoundStmt() throws SyntaxError {
    System.out.println("in parseCompoundStmt");

    match(Token.LCURLY);
    while (currentToken.kind != Token.RCURLY) {
      System.out.println(currentToken.kind);
      if (isType()) {
        parseType();
        parseVarDecl();
      } else {
        parseStmtList();
      }
    }
    match(Token.RCURLY);
  }

 // Here, a new nonterminal has been introduced to define { stmt } *
  void parseStmtList() throws SyntaxError {
    System.out.println("in parseStmtList");

    while (currentToken.kind != Token.RCURLY) {
      parseStmt();
    }
  }

  void parseStmt() throws SyntaxError {
    System.out.println("in parseStmt");

    switch (currentToken.kind) {

    case Token.LCURLY:
      parseCompoundStmt();
      break;

    case Token.IF:
      parseIfStmt();
      break;

    case Token.FOR:
      parseForStmt();
      break;

    case Token.WHILE:
      parseWhileStmt();
      break;

    case Token.BREAK:
      parseBreakStmt();
      break;

    case Token.CONTINUE:
      parseContinueStmt();
      break;

    case Token.RETURN:
      parseReturnStmt();
      break;

    default:
      parseExprStmt();
      break;

    }
  }

  void parseIfStmt() throws SyntaxError {
    System.out.println("in parseIfStmt");

    match(Token.IF);
    match(Token.LPAREN); // TODO: might need to change to accept() or somethin
    parseExpr();
    match(Token.RPAREN);
    parseStmt();
    // TODO: check for existence of 'else'

  }

  void parseForStmt() throws SyntaxError {
    System.out.println("in parseForStmt");

    match(Token.FOR);
    match(Token.LPAREN); // TODO: might need to change to accept() or somethin
    parseExpr();
    if (isExpr()) {
      parseExpr();
    }
    match(Token.SEMICOLON); // TODO: might need to change to accept() or somethin
    if (isExpr()) {
      parseExpr();
    }
    match(Token.SEMICOLON); // TODO: might need to change to accept() or somethin
    if (isExpr()) {
      parseExpr();
    }
    match(Token.RPAREN);
    parseStmt();

  }

  void parseWhileStmt() throws SyntaxError {
    System.out.println("in parseWhileStmt");

    match(Token.WHILE);
    match(Token.LPAREN); // TODO: might need to change to accept() or somethin
    parseExpr();
    match(Token.RPAREN);
    parseStmt();

  }

  void parseBreakStmt() throws SyntaxError {
    System.out.println("in parseBreakStmt");

    match(Token.BREAK);
    match(Token.SEMICOLON);

  }

  void parseContinueStmt() throws SyntaxError {
    System.out.println("in parseContinueStmt");

    match(Token.CONTINUE);
    match(Token.SEMICOLON);

  }

  void parseReturnStmt() throws SyntaxError {
    System.out.println("in parseReturnStmt");

    match(Token.RETURN);
    if (isExpr()) {
      parseExpr();
    }
    match(Token.SEMICOLON);

  }

  void parseExprStmt() throws SyntaxError {
    System.out.println("in parseExprStmt");

    if (isExpr()) {
        parseExpr();
        match(Token.SEMICOLON);
    } else {
      match(Token.SEMICOLON);
    }
  }

  boolean isExpr() {
    System.out.println("in isExpr");
    return currentToken.kind == Token.ID
          || currentToken.kind == Token.INT
          || currentToken.kind == Token.INTLITERAL
          || currentToken.kind == Token.FLOATLITERAL
          || currentToken.kind == Token.BOOLEANLITERAL
          || currentToken.kind == Token.STRINGLITERAL
          || currentToken.kind == Token.PLUS
          || currentToken.kind == Token.MINUS
          || currentToken.kind == Token.NOT
          || currentToken.kind == Token.LPAREN;
  }


// ======================= IDENTIFIERS ======================

 // Call parseIdent rather than match(Token.ID). 
 // In Assignment 3, an Identifier node will be constructed in here.

  void parseIdent() throws SyntaxError {
    System.out.println("in parseIdent");
    System.out.println(currentToken);

    if (currentToken.kind == Token.ID) {
      accept();
    } else 
      syntacticError("identifier expected here", "");
  }

// ======================= OPERATORS ======================

 // Call acceptOperator rather than accept(). 
 // In Assignment 3, an Operator Node will be constructed in here.

  void acceptOperator() throws SyntaxError {
    System.out.println("in parseOperator");

    currentToken = scanner.getToken();
  }


// ======================= EXPRESSIONS ======================

  void parseExpr() throws SyntaxError {
    System.out.println("in parseExpr");
    parseAssignExpr();
  }


  void parseAssignExpr() throws SyntaxError {
    System.out.println("in parseAssignExpr");

    // TODO: change this implementation to fit VC's spec
    parseCondOrExpr();
    while (currentToken.kind == Token.EQ) {
      acceptOperator();
      parseCondOrExpr();
    }
  }

  void parseCondOrExpr() throws SyntaxError {
    System.out.println("in parseCondOrExpr");

    parseCondAndExpr();
    while (currentToken.kind == Token.OROR) {
      acceptOperator();
      parseCondAndExpr();
    }
  }

  void parseCondAndExpr() throws SyntaxError {
    System.out.println("in parseCondAndExpr");

    parseEqualityExpr();
    while (currentToken.kind == Token.ANDAND) {
      acceptOperator();
      parseEqualityExpr();
    }
  }

  void parseEqualityExpr() throws SyntaxError {
    System.out.println("in parseEqualityExpr");

    parseRelExpr();
    while (currentToken.kind == Token.EQEQ || currentToken.kind == Token.NOTEQ) {
      acceptOperator();
      parseRelExpr();
    }
  }

  void parseRelExpr() throws SyntaxError {
    System.out.println("in parseRelExpr");

    parseAdditiveExpr();
    while (currentToken.kind == Token.LT || currentToken.kind == Token.LTEQ || currentToken.kind == Token.GT || currentToken.kind == Token.GTEQ) {
      acceptOperator();
      parseAdditiveExpr();
    }
  }

  void parseAdditiveExpr() throws SyntaxError {
    System.out.println("in parseAdditiveExpr");

    parseMultiplicativeExpr();
    while (currentToken.kind == Token.PLUS || currentToken.kind == Token.MINUS) {
      acceptOperator();
      parseMultiplicativeExpr();
    }
  }

  void parseMultiplicativeExpr() throws SyntaxError {
    System.out.println("in parseMultiplicativeExpr");

    parseUnaryExpr();
    while (currentToken.kind == Token.MULT || currentToken.kind == Token.DIV) {
      acceptOperator();
      parseUnaryExpr();
    }
  }

  void parseUnaryExpr() throws SyntaxError {
    System.out.println("in parseUnaryExpr");

    switch (currentToken.kind) {
      case Token.PLUS:
      case Token.MINUS:
      case Token.NOT:
        {
          acceptOperator();
          parseUnaryExpr();
        }
        break;

      default:
        parsePrimaryExpr();
        break;
       
    }
  }

  void parsePrimaryExpr() throws SyntaxError {
    System.out.println("in parsePrimaryExpr");
    System.out.println(currentToken);

    switch (currentToken.kind) {

      case Token.ID:
        parseIdent();
        if (currentToken.kind == Token.LPAREN) {
          parseArgList();
          break;
        } else if (currentToken.kind == Token.LBRACKET) {
          match(Token.LBRACKET); // TODO: might need to change to accept or somethin
          parseExpr();
          match(Token.RBRACKET); // TODO: might need to change to accept or somethin
          break;
        } else {
          break;
        }

      case Token.LPAREN:
        {
          accept();
          parseExpr();
	        match(Token.RPAREN);
        }
        break;

      case Token.INTLITERAL:
        parseIntLiteral();
        break;

      case Token.FLOATLITERAL:
        parseFloatLiteral();
        break;

      case Token.STRINGLITERAL:
        parseStringLiteral();
        break;

      case Token.BOOLEANLITERAL:
        parseBooleanLiteral();
        break;

      default:
        syntacticError("illegal primary expression", currentToken.spelling);
       
    }
  }

// ========================== LITERALS ========================

  // Call these methods rather than accept().  In Assignment 3, 
  // literal AST nodes will be constructed inside these methods. 

  void parseIntLiteral() throws SyntaxError {
    System.out.println("in parseIntLiteral");

    if (currentToken.kind == Token.INTLITERAL) {
      accept();
    } else 
      syntacticError("integer literal expected here", "");
  }

  void parseFloatLiteral() throws SyntaxError {
    System.out.println("in parseFloatLiteral");

    if (currentToken.kind == Token.FLOATLITERAL) {
      accept();
    } else 
      syntacticError("float literal expected here", "");
  }

  void parseBooleanLiteral() throws SyntaxError {
    System.out.println("in parseBooleanLit");

    if (currentToken.kind == Token.BOOLEANLITERAL) {
      accept();
    } else 
      syntacticError("boolean literal expected here", "");
  }

  void parseStringLiteral() throws SyntaxError {
    System.out.println("in parseStringLiteral");

    if (currentToken.kind == Token.STRINGLITERAL) {
      accept();
    } else 
      syntacticError("string literal expected here", "");
  }

}
