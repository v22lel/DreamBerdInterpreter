package dream.berd.compiler.parser;

import dream.berd.compiler.Token;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParseException extends Exception {
    private int line;
    private String title;
    private String message;

    public ParseException(int line, String title, String message) {
        super("ParseException");
        this.line = line;
        this.title = title;
        this.message = message;
    }

    public static ParseException notAStatement(int line) {
        return new ParseException(line, "Not a Statement", "Expressions are not allowed as Statements!");
    }

    public void print() {
        System.err.println("Line " + line + ": " + title);
        System.err.println(message);
    }

    private static String tokensToString(Token.Type... tokens) {
        return Arrays.stream(tokens).map(Object::toString).collect(Collectors.joining(", "));
    }

    public static ParseException unexpectedToken(Token found, Token.Type... expected) {
        return new ParseException(found.getLine(), "Unexpected Token", "Found " + found.debugString() + ", expected " + tokensToString(expected));
    }

    public static ParseException unexpectedToken(Token found, String expected) {
        return new ParseException(found.getLine(), "Unexpected Token", "Found " + found.debugString() + ", expected " + expected);
    }

    public static ParseException endOfStream(int line, Token.Type... expected) {
        return new ParseException(line, "Unexpected end of tokens", "No more tokens to parse, expected " + tokensToString(expected));
    }
}
