/*
 *
 *
 * 	 Scanner.java 	                         
 *
 *
 */

package VC.Scanner;

import VC.ErrorReporter;

public final class Scanner { 

  private SourceFile sourceFile;
  private boolean debug;

  private ErrorReporter errorReporter;
  private StringBuffer currentSpelling;
  private char currentChar;
  private SourcePosition sourcePos;

	private int lineNumber;
	private int colNumber;
	// private boolean scanningComments;

// =========================================================

  public Scanner(SourceFile source, ErrorReporter reporter) {
    sourceFile = source;
    errorReporter = reporter;
    currentChar = sourceFile.getNextChar();
    debug = false;

    // you may initialise your counters for line and column numbers here
		lineNumber = 1;
		colNumber = 1;
		// scanningComments = false;
  }

  public void enableDebugging() {
    debug = true;
  }

  // accept gets the next character from the source program.

  private void accept() {
    if (currentChar == '\\') {
      if (inspectChar(1) == '\\') {
        currentSpelling = currentSpelling.append('\\');
      } else if (inspectChar(1) == '\'') {
        currentSpelling = currentSpelling.append('\'');
      } else if (inspectChar(1) == '"') {
        currentSpelling = currentSpelling.append('\"');
      } else if (inspectChar(1) == 'b') {
        currentSpelling = currentSpelling.append('\b');
      } else if (inspectChar(1) == 'f') {
        currentSpelling = currentSpelling.append('\f');
      } else if (inspectChar(1) == 'n') {
        currentSpelling = currentSpelling.append('\n');
      } else if (inspectChar(1) == 'r') {
        currentSpelling = currentSpelling.append('\r');
      } else if (inspectChar(1) == 't') {
        currentSpelling = currentSpelling.append('\t');
      } else {
        SourcePosition illEscCharSourcePos = new SourcePosition(lineNumber, sourcePos.charStart - 1, colNumber);
        currentSpelling = currentSpelling.append("\\" + Character.toString(inspectChar(1)));
        errorReporter.reportError("%illegal escape character", "\\" + Character.toString(inspectChar(1)) + ": ", illEscCharSourcePos);
      }
    } else {
      currentSpelling = currentSpelling.append(currentChar);
    }
		
    if (currentChar == '\n') {
			lineNumber += 1;
			sourcePos.lineStart = sourcePos.lineFinish = lineNumber;
			colNumber = 1;
			sourcePos.charStart = colNumber;
			sourcePos.charFinish = colNumber;
		} /*else if (currentChar == '\t') {
			sourcePos.lineStart = sourcePos.lineFinish = lineNumber;
			sourcePos.charStart = colNumber;
			sourcePos.charFinish = colNumber;
			colNumber += (colNumber % 8 == 0) ? 8 : (colNumber % 8);
		}*/ else {
			sourcePos.lineStart = sourcePos.lineFinish = lineNumber;
			if (currentSpelling.toString().length() == 1) {
				sourcePos.charStart = colNumber;
			}
			sourcePos.charFinish = colNumber;
      if (currentChar == '\t') {
        colNumber += (colNumber % 8 == 0) ? 8 : (colNumber % 8);
      } else {
			  colNumber += 1;
      }
		}
    
    currentChar = sourceFile.getNextChar();
  }

	// discards the currentChar and gets the next character
	private void discard() {
		if (currentChar == '\n') {
			lineNumber += 1;
			sourcePos.lineStart = sourcePos.lineFinish = lineNumber;
			colNumber = 1;
			sourcePos.charStart = colNumber;
			sourcePos.charFinish = colNumber;
		} /*else if (currentChar == '\t') {
			sourcePos.lineStart = sourcePos.lineFinish = lineNumber;
			sourcePos.charStart = colNumber;
			sourcePos.charFinish = colNumber;
      System.out.println(colNumber);
			colNumber += (colNumber % 8 == 0) ? 8 : (colNumber % 8);
      System.out.println(colNumber);
		}*/ else {
			sourcePos.lineStart = sourcePos.lineFinish = lineNumber;
			sourcePos.charStart = colNumber;
			sourcePos.charFinish = colNumber;
			// colNumber += 1;
      if (currentChar == '\t') {
        colNumber += (colNumber % 8 == 0) ? 8 : (colNumber % 8);
      } else {
			  colNumber += 1;
      }
		}
		currentChar = sourceFile.getNextChar();
	}

  // inspectChar returns the n-th character after currentChar
  // in the input stream. 
  //
  // If there are fewer than nthChar characters between currentChar 
  // and the end of file marker, SourceFile.eof is returned.
  // 
  // Both currentChar and the current position in the input stream
  // are *not* changed. Therefore, a subsequent call to accept()
  // will always return the next char after currentChar.

  private char inspectChar(int nthChar) {
    return sourceFile.inspectChar(nthChar);
  }

  private int nextToken() {
  // Tokens: separators, operators, literals, identifiers and keywords
       
    switch (currentChar) {
      // keywords & identifiers scanning is at the end
      // operators
      case '+':
        accept();
        return Token.PLUS;
      case '-':
        accept();
        return Token.MINUS;
      case '*':
        accept();
        return Token.MULT;
      case '/':
        if (inspectChar(1) == '/' || inspectChar(1) == '*') {
          skipSpaceAndComments();
        }
        accept();
        return Token.DIV;
      case '!':
        if (inspectChar(1) == '=') {
          accept();
          accept();
          return Token.NOTEQ;
        } else {
          accept();
          return Token.NOT;
        }
      case '=':
        if (inspectChar(1) == '=') {
          accept();
          accept();
          return Token.EQEQ;
        } else {
          accept();
          return Token.EQ;
        }
      case '<':
        if (inspectChar(1) == '=') {
          accept();
          accept();
          return Token.LTEQ;
        } else {
          accept();
          return Token.LT;
        }
      case '>':
        if (inspectChar(1) == '=') {
          accept();
          accept();
          return Token.GTEQ;
        } else {
          accept();
          return Token.GT;
        }
      case '&':
        if (inspectChar(1) == '&') {
          accept();
          accept();
          return Token.ANDAND;
        } else {
          currentSpelling.append(currentChar);
          discard();
          return Token.ERROR;
        }
      case '|':
        if (inspectChar(1) == '|') {
          accept();
          accept();
          return Token.OROR;
        } else {
          currentSpelling.append(currentChar);
          discard();
          return Token.ERROR;
        }
      // separators
      case '{':
        accept();
        return Token.LCURLY;
      case '}':
        accept();
        return Token.RCURLY;
      case '(':
        accept();
        return Token.LPAREN;
      case ')':
        accept();
        return Token.RPAREN;
      case '[':
        accept();
        return Token.LBRACKET;
      case ']':
        accept();
        return Token.RBRACKET;
      case ';':
        accept();
        return Token.SEMICOLON;
      case ',':
        accept();
        return Token.COMMA;
      // newlines
      case '\n':
        discard();
      // literals
      case '"':
				int illStrLitLine = lineNumber;
				int illStrLitCharStart = colNumber;
				int illStrLitCharFinish = colNumber;
        discard(); // discard opening "
        while (currentChar != '"') {
          if (currentChar == '\\') {
            accept(); // accept the \
            discard(); // discard the letter after \
          } else if (currentChar == '\n' || currentChar == SourceFile.eof) { // it's illegal to have newlines after the opening " and before the closing matching "
            sourcePos.lineStart = sourcePos.lineFinish = lineNumber;
            sourcePos.charStart = illStrLitCharStart;
            sourcePos.charFinish = colNumber - 1;
            SourcePosition illStrLitSourcePos = new SourcePosition(illStrLitLine, illStrLitCharStart, illStrLitCharFinish);
						errorReporter.reportError("%unterminated string", currentSpelling.toString() + ": ", illStrLitSourcePos);
            return Token.STRINGLITERAL;
          } else {
            accept();
          }
        }
        discard(); // discard closing "
				sourcePos.charStart = illStrLitCharStart;
        return Token.STRINGLITERAL;
      case '.':
				if (Character.isDigit(inspectChar(1))) {
          accept(); // accept period
          while (Character.isDigit(currentChar)) { // accepts digits after decimal point
            accept();
          }
          if ((currentChar == 'e' || currentChar == 'E') && (Character.isDigit(inspectChar(1)))) {
            // la
            accept(); // accepts 'e' || 'E'
            while (Character.isDigit(currentChar)) {
              accept(); // accepts exponent
            }
            return Token.FLOATLITERAL;
          } else if ((currentChar == 'e' || currentChar == 'E') && (inspectChar(1) == '+' || inspectChar(1) == '-') && (Character.isDigit(inspectChar(2)))) {
            accept(); // accepts 'e' || 'E'
            accept(); // accepts + || -
            while (Character.isDigit(currentChar)) {
              accept(); // accepts exponent
            }
            return Token.FLOATLITERAL;
          } else {
            return Token.FLOATLITERAL;
          }
        } else {
          currentSpelling.append(currentChar);
          discard();
          return Token.ERROR;
        }
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        while (Character.isDigit(currentChar)) { // accounts for multi-digit floats and ints
          accept();
        }
        if (currentChar == '.') {
          accept(); // accept period
          if (Character.isDigit(currentChar)) {
            while (Character.isDigit(currentChar)) { // accepts digits after decimal point
              accept();
            }
            if ((currentChar == 'e' || currentChar == 'E') && (Character.isDigit(inspectChar(1)))) {
              // la
              accept(); // accepts 'e' || 'E'
              while (Character.isDigit(currentChar)) {
                accept(); // accepts exponent
              }
              return Token.FLOATLITERAL;
            } else if ((currentChar == 'e' || currentChar == 'E') && (inspectChar(1) == '+' || inspectChar(1) == '-') && (Character.isDigit(inspectChar(2)))) {
              accept(); // accepts 'e' || 'E'
              accept(); // accepts + || -
              while (Character.isDigit(currentChar)) {
                accept(); // accepts exponent
              }
              return Token.FLOATLITERAL;
            } else {
              return Token.FLOATLITERAL;
            }
          } else {
            if (currentChar == 'e' || currentChar == 'E') {
              // digit + 'e'
              accept();
              if (currentChar == '+' || currentChar == '-') {
                // + || -
                accept();
              }
              while (Character.isDigit(currentChar)) {
                accept();
              }
              return Token.FLOATLITERAL;
            } else {
              return Token.FLOATLITERAL;
            }
          }
        } else if (currentChar == 'e' || currentChar == 'E') {
          // digit + 'e'
          accept();
          if (currentChar == '+' || currentChar == '-') {
            // + || -
            accept();
          }
          while (Character.isDigit(currentChar)) {
            accept();
          }
          return Token.FLOATLITERAL;
        } else {
          return Token.INTLITERAL;
        }
      // boolean literal is in checkKeywordsOrIdentifier()

      // ....
      case SourceFile.eof:
        currentSpelling.append(Token.spell(Token.EOF));
        return Token.EOF;
      default:
        break;
    }

    int koi = checkKeywordsOrIdentifier();
    if (koi > -1) return koi;

    accept(); 
    return Token.ERROR;
  }

  private int checkKeywordsOrIdentifier() {
    String newTokenString = "";
    int i = 0;
    while (Character.isAlphabetic(currentChar)) {
      newTokenString = newTokenString.concat(Character.toString(currentChar));
      accept();
    }
		Token newTok = new Token(Token.ID, newTokenString, sourcePos);
		if (newTokenString.equals("boolean")) {
			return Token.BOOLEAN;
		} else if (newTokenString.equals("break")) {
			return Token.BREAK;
		} else if (newTokenString.equals("continue")) {
			return Token.CONTINUE;
		} else if (newTokenString.equals("else")) {
			return Token.ELSE;
		} else if (newTokenString.equals("for")) {
			return Token.FOR;
		} else if (newTokenString.equals("float")) {
			return Token.FLOAT;
		} else if (newTokenString.equals("if")) {
			return Token.IF;
		} else if (newTokenString.equals("int")) {
			return Token.INT;
		} else if (newTokenString.equals("return")) {
			return Token.RETURN;
		} else if (newTokenString.equals("void")) {
			return Token.VOID;
		} else if (newTokenString.equals("while")) {
			return Token.WHILE;
		} else if (newTokenString.equals("true")) {
			return Token.BOOLEANLITERAL;
		} else if (newTokenString.equals("false")) {
			return Token.BOOLEANLITERAL;
		} else if (newTok.kind == Token.ID && !newTokenString.equals("")) {
			return Token.ID;
		} else {
			return -1;
		}
  }

  void skipSpaceAndComments() {
    int i = 0;
    // TODO: source position isn't fully correct
    while (true) {
      if (currentChar == '/') {
        if (inspectChar(1) == '/') {
          // single-line comment
          discard();
          discard();
          while (currentChar != '\n') {
            discard();
          }
          discard(); // discard newline
        } else if (inspectChar(1) == '*') {
          // multi-line comment
          int cmtLine = lineNumber;
          int cmtStart = colNumber;
          int cmtEnds = colNumber;
          discard(); // discard '/'
          discard(); // discard '*'
          while (currentChar != SourceFile.eof) {
            if (currentChar == '*' && inspectChar(1) == '/') {
              discard(); // discard '*'
              discard(); // discard '/'
              break;
            } else {
              discard();
            }
          }
          if (currentChar == SourceFile.eof) {
            SourcePosition cmtSourcePos = new SourcePosition(cmtLine, cmtStart, cmtEnds);
            errorReporter.reportError("%unterminated comment", ": ",
                cmtSourcePos);
          }
        }
      } else if (currentChar == ' ') {
        // whitespaces
        discard();
        while (currentChar == ' ') {
          discard();
        }
      } else if (currentChar == '\n') {
        // newlines
        discard();
        while (currentChar == '\n') {
          discard();
        }
      } else if (currentChar == '\t') {
        // tabs
        discard();
        while (currentChar == '\t') {
          discard();
        }
      }

      i += 1;
      if (((currentChar != '/' && inspectChar(1) != '*')
					&& (currentChar != '/' && inspectChar(1) != '/')
					&& (currentChar != '\n')
					&& (currentChar != ' ')
					&& (currentChar != '\t')) || i > 10)
				break;
    }
  }

  public Token getToken() {
    Token tok;
    int kind;

    // skip white space and comments

   currentSpelling = new StringBuffer("");
   sourcePos = new SourcePosition();
   skipSpaceAndComments();

   // You must record the position of the current token somehow

   kind = nextToken();

   tok = new Token(kind, currentSpelling.toString(), sourcePos);

   // * do not remove these three lines
   if (debug)
     System.out.println(tok);
   return tok;
   }

}
