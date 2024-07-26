package dream.berd.compiler.parser.expressions;

import java.util.List;

public class FunctionCall implements Expression {
    private String name;
    private List<Expression> arguments;

    public FunctionCall(String name, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public void debug(int depth) {
        System.out.println("| ".repeat(depth) + "=FUNCTION CALL=");
        System.out.println("| ".repeat(depth) + " Name: " + name);
        System.out.println("| ".repeat(depth) + " Arguments:");
        arguments.forEach(a -> a.debug(depth + 1));
    }
}
