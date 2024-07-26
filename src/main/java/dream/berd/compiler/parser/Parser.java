package dream.berd.compiler.parser;

import dream.berd.compiler.Token;
import dream.berd.compiler.TokenStream;
import dream.berd.compiler.parser.expressions.Expression;
import dream.berd.compiler.parser.statements.*;

public class Parser {
    public void parse(TokenStream tokens) throws ParseException {
        Block ast = new Block(1);

        tokens.faultyForEach(tkn -> {
            ast.addStatement(Statement.parse(tkn, tokens));
        });
        ast.debug(0);
    }
}
