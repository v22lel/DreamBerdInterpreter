package dream.berd.compiler.parser.statements;

import dream.berd.compiler.Token;
import dream.berd.compiler.TokenStream;
import dream.berd.compiler.parser.ParseException;
import dream.berd.compiler.parser.expressions.Expression;

public class Else implements Statement {
    private StatementInfo info;
    private Expression ifCondition;
    private Block block;

    public Else(Expression ifCondition, TokenStream tokens, int line) throws ParseException {
        info = new StatementInfo(line);
        this.ifCondition = ifCondition; //TODO: include 1 line blocks
        tokens.expectNext(line, Token.Type.L_BRACE);
        block = new Block(tokens, line);
        tokens.expectNext(line, Token.Type.R_BRACE);
    }

    public Expression getIfCondition() {
        return ifCondition;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void debug(int depth) {
        System.out.println("| ".repeat(depth) + "=ELSE=");
        System.out.println("| ".repeat(depth) + "Condition:");
        ifCondition.debug(depth + 1);
        System.out.println("| ".repeat(depth) + "Block:");
        block.debug(depth + 1);
    }

    @Override
    public StatementInfo getInfo() {
        return info;
    }
}
