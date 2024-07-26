package dream.berd.compiler.parser.statements;

import dream.berd.compiler.Token;
import dream.berd.compiler.TokenStream;
import dream.berd.compiler.parser.ParseException;
import dream.berd.compiler.parser.expressions.Expression;

public class If implements Statement {
    private StatementInfo info;
    private Expression condition;
    private Block block;

    public If(TokenStream tokens, int line) throws ParseException {
        info = new StatementInfo(line);
        tokens.expectNext(line, Token.Type.L_PAREN); //TODO: include 1 line blocks
        condition = Expression.parse(tokens, line);
        tokens.expectNext(line, Token.Type.R_PAREN);
        tokens.expectNext(line, Token.Type.L_BRACE);
        block = new Block(tokens, line);
        tokens.expectNext(line, Token.Type.R_BRACE);
    }

    public Expression getCondition() {
        return condition;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void debug(int depth) {
        System.out.println("| ".repeat(depth) + "=IF=");
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
