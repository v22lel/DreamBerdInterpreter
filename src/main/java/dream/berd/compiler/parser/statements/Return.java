package dream.berd.compiler.parser.statements;

import dream.berd.compiler.TokenStream;
import dream.berd.compiler.parser.ParseException;
import dream.berd.compiler.parser.expressions.Expression;

public class Return implements Statement{
    private StatementInfo info;
    private Expression toReturn;

    public Return(TokenStream tokens, int line) throws ParseException {
        info = new StatementInfo(line);
        toReturn = Expression.parse(tokens, line);
    }

    @Override
    public void debug(int depth) {
        System.out.println("| ".repeat(depth) + " =RETURN=");
        System.out.println("| ".repeat(depth) + "toReturn:");
        toReturn.debug(depth + 1);
    }

    @Override
    public StatementInfo getInfo() {
        return info;
    }
}
