package dream.berd.compiler.parser.expressions;

import dream.berd.compiler.api.types.PrimitiveType;

public class Literal implements Expression {
    private final PrimitiveType type;
    private final Object value;

    public Literal(PrimitiveType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public PrimitiveType getType() {
        return type;
    }

    public <T> T getValue() {
        return (T) value;
    }

    @Override
    public void debug(int depth) {
        System.out.println("| ".repeat(depth) + "Literal: " + type + " " + value.toString());
    }
}
