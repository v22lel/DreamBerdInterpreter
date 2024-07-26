package dream.berd.compiler.parser.expressions.ops;

import dream.berd.compiler.Token;

public class OperatorOperation implements Operation {
    private Token.Operator operator;

    public OperatorOperation(Token.Operator operator) {
        this.operator = operator;
    }

    @Override
    public boolean isCompare() {
        return false;
    }

    @Override
    public String toString() {
        return "Operator: " + operator;
    }
}
