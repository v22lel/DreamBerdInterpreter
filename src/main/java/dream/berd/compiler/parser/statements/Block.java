package dream.berd.compiler.parser.statements;

import dream.berd.compiler.Token;
import dream.berd.compiler.TokenStream;
import dream.berd.compiler.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class Block implements Statement {
    private StatementInfo info;
    private List<Statement> statements;

    public Block(int line) {
        statements = new ArrayList<>();
        info = new StatementInfo(line);
    }

    public Block(TokenStream tokens, int line) throws ParseException {
        this(line);
        int newLine = line;
        Token peeked = tokens.peek().get(ParseException.endOfStream(newLine, Token.Type.ANY));
        while (peeked.getType() != Token.Type.R_BRACE) {
            var token = tokens.next().get();
            newLine = token.getLine();
            Statement statement = Statement.parse(token, tokens);
            if (statement != null) {
                statements.add(statement);
            }
            peeked = tokens.peek(true).get(ParseException.endOfStream(newLine, Token.Type.ANY));
        }
        tokens.next();
    }

    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    @Override
    public void debug(int depth) {
        System.out.println("| ".repeat(depth) + "=BLOCK=");
        for (Statement statement : statements) {
            statement.debug(depth + 1);
        }
    }

    @Override
    public StatementInfo getInfo() {
        return info;
    }
}
