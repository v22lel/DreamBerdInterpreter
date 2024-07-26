package dream.berd.compiler.parser.statements;

import dream.berd.compiler.Token;
import dream.berd.compiler.TokenStream;
import dream.berd.compiler.parser.ParseException;
import dream.berd.compiler.parser.expressions.Expression;

public class When implements Statement{
    private StatementInfo info;
    private Expression condition;
    private Block block;

    public When(TokenStream tokens, int line) throws ParseException {
        int newLine = line;
        info = new StatementInfo(line);
        newLine = tokens.expectNext(newLine, Token.Type.L_PAREN).getLine();
        condition = Expression.parse(tokens, newLine);
        newLine = tokens.expectNext(newLine, Token.Type.R_PAREN).getLine();
        newLine = tokens.expectNext(newLine, Token.Type.L_BRACE).getLine();
        block = new Block(tokens, newLine);
    }

    public Expression getCondition() {
        return condition;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void debug(int depth) {
        System.out.println("| ".repeat(depth) + "=WHEN=");
        System.out.println("| ".repeat(depth) + "Condition:");
        condition.debug(depth + 1);
        System.out.println("| ".repeat(depth) + "Block:");
        block.debug(depth + 1);
    }

    @Override
    public StatementInfo getInfo() {
        return info;
    }
}
