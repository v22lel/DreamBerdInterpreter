package dream.berd.compiler.parser.expressions.ops;

import dream.berd.compiler.Token;

public class CompareOperation implements Operation {
    private Token.Comparison base;

    @Override
    public boolean isCompare() {
        return true;
    }
}
