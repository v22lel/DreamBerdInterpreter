package dream.berd.compiler.parser.expressions;

import dream.berd.compiler.parser.expressions.ops.OperatorOperation;

public class Unary implements Expression {
    private Expression expr;
    private OperatorOperation operation;

    public Unary(Expression expr, OperatorOperation operation) {
        this.expr = expr;
        this.operation = operation;
    }

    @Override
    public void debug(int depth) {
        System.out.println("| ".repeat(depth) + "=UNARY=");
        System.out.println("| ".repeat(depth) + " Expr:");
        expr.debug(depth + 1);
        System.out.println("| ".repeat(depth) + " Operation: " + operation);
    }
}
