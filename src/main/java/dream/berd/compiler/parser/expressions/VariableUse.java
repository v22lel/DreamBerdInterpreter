package dream.berd.compiler.parser.expressions;

public class VariableUse implements Expression {
    String name;

    public VariableUse(String name) {
        this.name = name;
    }

    @Override
    public void debug(int depth) {
        System.out.println("| ".repeat(depth) + "=VARIABLE USE=");
        System.out.println("| ".repeat(depth) + "Name: " + name);
    }
}
