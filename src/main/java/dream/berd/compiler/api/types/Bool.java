package dream.berd.compiler.api.types;

import dream.berd.compiler.Token;

public enum Bool {
    TRUE,
    FALSE,
    MAYBE;



    public static Bool fromTokenType(Token.Type type) {
        return switch (type) {
            case TRUE -> Bool.TRUE;
            case FALSE -> Bool.FALSE;
            default -> MAYBE;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case TRUE -> "true";
            case FALSE -> "false";
            case MAYBE -> "maybe";
        };
    }
}
