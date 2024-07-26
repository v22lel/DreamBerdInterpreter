package dream.berd.compiler.api.types;

public class ObjectType implements Type {
    private String name;

    public ObjectType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ObjectType: " + name;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }
}
