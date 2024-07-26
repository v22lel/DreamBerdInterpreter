package dream.berd.compiler.api.types;

import dream.berd.compiler.Token;

public enum PrimitiveType implements Type {
    BOOL,
    INT,
    FLOAT,
    STRING,
    UNDEFINED;

    public static PrimitiveType ofLiteralToken(Token.Type type) {
        return switch (type) {
            case INT_LIT -> INT;
            case FLOAT_LIT -> FLOAT;
            case STRING_LIT -> STRING;
            default -> null;
        };
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }
}
