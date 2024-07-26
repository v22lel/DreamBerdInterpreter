package dream.berd.compiler.api.types;

public interface Type {
    boolean isPrimitive();

    default PrimitiveType getPrimitive() {
        return (PrimitiveType) this;
    }

    default ObjectType getObject() {
        return (ObjectType) this;
    }

    static Type fromName(String name) {
        try {
            return PrimitiveType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException ignore) {
            return new ObjectType(name);
        }
    }
}
