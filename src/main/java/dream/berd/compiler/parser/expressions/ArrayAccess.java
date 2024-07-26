package dream.berd.compiler.parser.expressions;

public class ArrayAccess implements Expression {
    private Expression array;
    private Expression index;

    public ArrayAccess(Expression array, Expression index) {
        this.array = array;
        this.index = index;
    }

    @Override
    public void debug(int depth) {
        System.out.println("| ".repeat(depth) + "=ARRAY ACCESS=");
        System.out.println("| ".repeat(depth) + " Array:");
        array.debug(depth + 1);
        System.out.println("| ".repeat(depth) + " Index:");
        index.debug(depth + 1);
    }
}
