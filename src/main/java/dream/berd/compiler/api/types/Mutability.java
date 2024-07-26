package dream.berd.compiler.api.types;

public enum Mutability {
    IMMUTABLE,
    EDITABLE,
    CHANGEABLE,
    MUTABLE;

    public boolean isGoodEnough(Mutability other) {
        return this.compareTo(other) >= 0;
    }
}
