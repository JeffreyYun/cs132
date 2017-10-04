package cs132;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Tokenizer {
    public static enum TokenType {
        LEFT_BRACE,
        RIGHT_BRACE,
        SYSTEM_OUT_PRINTLN,
        LEFT_PARENTHESIS,
        RIGHT_PARENTHESIS,
        SEMICOLON,
        IF,
        ELSE,
        WHILE,
        TRUE,
        FALSE,
        BANG
    };

    public static class TokenDefinition {
        public final TokenType tokenType;
        public final String tokenString;

        private TokenDefinition(TokenType tokenType, String tokenString) {
            this.tokenType = tokenType;
            this.tokenString = tokenString;
        }
    }

    public static class Token {
        public final TokenType tokenType;
        public final String tokenString;
        public final int lineNumber;
        public final int offset;

        private Token(TokenType tokenType, String tokenString, int lineNumber, int offset) {
            this.tokenType = tokenType;
            this.tokenString = tokenString;
            this.lineNumber = lineNumber;
            this.offset = offset;
        }
    }

    public static boolean tokenize(final String input, final int lineNumber, final List<Token> outputTokens) {
        int start = 0;
        int candidateLength = 1;        
        while (start != input.length()) {
            if (Character.isWhitespace(input.charAt(start))) {
                start += 1;
                continue;
            }

            final int lastCharacterIndex = start + candidateLength;            
            String candidate = input.substring(start, lastCharacterIndex);
            TokenDefinition tokenDefinition = candidateLength == 1 ? separatorMap.get(candidate) : tokenMap.get(candidate);            
            if (tokenDefinition != null) {
                outputTokens.add(new Token(tokenDefinition.tokenType, tokenDefinition.tokenString, lineNumber, start + 1));
                start += candidateLength;
                candidateLength = 1;
            } else {
                if (lastCharacterIndex >= input.length() || Character.isWhitespace(input.charAt(lastCharacterIndex))) {
                    return false;
                } else {
                    do {
                        candidateLength += 1;
                    } while (start + candidateLength < input.length()
                             && separatorMap.get(String.valueOf(input.charAt(start + candidateLength))) == null
                             && !Character.isWhitespace(input.charAt(start + candidateLength)));
                }
            }
        }
        return true;
    }

    private static final TokenDefinition[] tokenDefinitions = {
        new TokenDefinition(TokenType.SYSTEM_OUT_PRINTLN, "System.out.println"),
        new TokenDefinition(TokenType.IF, "if"),
        new TokenDefinition(TokenType.ELSE, "else"),
        new TokenDefinition(TokenType.WHILE, "while"),
        new TokenDefinition(TokenType.TRUE, "true"),
        new TokenDefinition(TokenType.FALSE, "false")
    };

    private static final TokenDefinition[] separators = {
        new TokenDefinition(TokenType.LEFT_BRACE, "{"),
        new TokenDefinition(TokenType.RIGHT_BRACE, "}"),
        new TokenDefinition(TokenType.LEFT_PARENTHESIS, "("),
        new TokenDefinition(TokenType.RIGHT_PARENTHESIS, ")"),
        new TokenDefinition(TokenType.SEMICOLON, ";"),
        new TokenDefinition(TokenType.BANG, "!")
    };

    private static final Map<String, TokenDefinition> tokenMap = createTokenMap();
    private static final Map<String, TokenDefinition> separatorMap = createSeparatorMap();
    
    private static Map<String, TokenDefinition> createTokenMap() {
        HashMap<String, TokenDefinition> map = new HashMap<>();
        for (TokenDefinition tokenDefinition : tokenDefinitions) {
            map.put(tokenDefinition.tokenString, tokenDefinition);
        }
        return map;
    }

    private static Map<String, TokenDefinition> createSeparatorMap() {
        HashMap<String, TokenDefinition> map = new HashMap<>();
        for (TokenDefinition tokenDefinition : separators) {
            map.put(tokenDefinition.tokenString, tokenDefinition);
        }
        return map;
    }
}