package dream.berd.compiler.api.types;

public class UnknownType implements Type {
    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public String toString() {
        return "Unknown";
    }
}
