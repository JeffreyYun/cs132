package cs132;

import java.util.List;

import static cs132.Tokenizer.Token;
import static cs132.Tokenizer.TokenType;

public class Parser {
    private List<Token> tokens;
    private int index = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public boolean isValid() {
        if (!S()) {
            return false;
        }
        try {
            expect(null);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean S() {
        try {
            Token token = next();
            if (token.tokenType == TokenType.LEFT_BRACE) {
                if (!L()) {
                    return false;
                }
                expect(TokenType.RIGHT_BRACE);
            } else if (token.tokenType == TokenType.SYSTEM_OUT_PRINTLN) {
                expect(TokenType.LEFT_PARENTHESIS);
                if (!E()) {
                    return false;
                }
                expect(TokenType.RIGHT_PARENTHESIS);
                expect(TokenType.SEMICOLON);
            } else if (token.tokenType == TokenType.IF) {
                expect(TokenType.LEFT_PARENTHESIS);
                if (!E()) {
                    return false;
                }
                expect(TokenType.RIGHT_PARENTHESIS);
                if (!S()) {
                    return false;
                }
                expect(TokenType.ELSE);
                if (!S()) {
                    return false;
                }
            } else if (token.tokenType == TokenType.WHILE) {
                expect(TokenType.LEFT_PARENTHESIS);
                if (!E()) {
                    return false;
                }
                expect(TokenType.RIGHT_PARENTHESIS);
                if (!S()) {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean L() {
        int index = this.index;
        if (S()) {
//            if (!L()) {
//                return false;
//            }
            L();
        } else {
            this.index = index;
        }
        return true;
    }

    private boolean E() {
        Token token = next();
        if (token.tokenType == TokenType.TRUE) {
        } else if (token.tokenType == TokenType.FALSE) {
        } else if (token.tokenType == TokenType.BANG) {
            if (!E()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private Token next() {
        if (index >= tokens.size()) {
            return null;
        } else {
            //System.err.println(tokens.get(index).tokenType.name());
            return tokens.get(index++);
        }
    }

    private void expect(TokenType tokenType) throws Exception {
        Token next = next();
        if (next == null) {
            if (tokenType != null) {
                throw new Exception("Expected " + tokenType.name());
            }
        } else {
            if (tokenType == null) {
                throw new Exception("Expected no symbol, got " + next.tokenType.name() + " instead");
            } else if (next.tokenType != tokenType) {
                throw new Exception("Expected " + tokenType.name() + ", got " + next.tokenType.name() + " instead");
            }
        }
    }
}