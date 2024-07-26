package dream.berd.compiler.parser.statements;

import dream.berd.compiler.TokenStream;
import dream.berd.compiler.parser.ParseException;
import dream.berd.compiler.parser.expressions.Expression;

public class ExpressionStatement implements Statement {
    private StatementInfo info;
    private Expression expression;

    public ExpressionStatement(TokenStream tokens, int line) throws ParseException {
        info = new StatementInfo(line);
        this.expression = Expression.parse(tokens, line, true);
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void debug(int depth) {
        expression.debug(depth + 1);
    }

    @Override
    public StatementInfo getInfo() {
        return info;
    }
}
