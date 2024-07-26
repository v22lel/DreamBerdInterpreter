package dream.berd.compiler.parser.expressions;

import dream.berd.compiler.parser.expressions.ops.Operation;

public class Binary implements Expression {
    private Expression left;
    private Expression right;
    private Operation operation;

    public Binary(Expression left, Expression right, Operation operation) {
        this.left = left;
        this.right = right;
        this.operation = operation;
    }

    @Override
    public void debug(int depth) {
        System.out.println("| ".repeat(depth) + "=BINARY=");
        System.out.println("| ".repeat(depth) + " Left:");
        left.debug(depth + 1);
        System.out.println("| ".repeat(depth) + " Right:");
        right.debug(depth + 1);
        System.out.println("| ".repeat(depth) + " Operation: " + operation.toString());
    }
}
