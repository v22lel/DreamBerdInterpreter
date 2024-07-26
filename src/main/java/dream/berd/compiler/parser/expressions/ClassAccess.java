package dream.berd.compiler.parser.expressions;

public class ClassAccess implements Expression {
    private Expression classExpr;
    private Expression access;

    public ClassAccess(Expression classExpr, Expression access) {
        this.classExpr = classExpr;
        this.access = access;
    }

    public Expression getClassExpr() {
        return classExpr;
    }

    public Expression getAccess() {
        return access;
    }

    @Override
    public void debug(int depth) {
        System.out.println("| ".repeat(depth) + "=CLASS ACCESS=");
        System.out.println("| ".repeat(depth) + " Class:");
        classExpr.debug(depth + 1);
        System.out.println("| ".repeat(depth) + " Access:");
        access.debug(depth + 1);
    }
}
