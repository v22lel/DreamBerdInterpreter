package dream.berd.compiler.parser.expressions;

import dream.berd.compiler.api.lifetimes.Lifetime;
import dream.berd.compiler.api.types.Mutability;
import dream.berd.compiler.api.types.Type;
import dream.berd.compiler.parser.statements.Statement;
import dream.berd.compiler.parser.statements.StatementInfo;

public class VariableDeclaration implements Statement {
    private StatementInfo info;
    private String name;
    private Expression value;
    private Type type;
    private Lifetime lifetime;
    private Mutability mutability;

    public VariableDeclaration(String name, Expression value, Type type, Lifetime lifetime, Mutability mutability, int line) {
        info = new StatementInfo(line);
        this.name = name;
        this.value = value;
        this.type = type;
        this.lifetime = lifetime;
        this.mutability = mutability;
    }

    public String getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    public Lifetime getLifetime() {
        return lifetime;
    }

    public Mutability getMutability() {
        return mutability;
    }

    @Override
    public void debug(int depth) {
        System.out.println("| ".repeat(depth) + "=VARIABLE DECLARATION=");
        System.out.println("| ".repeat(depth) + " Name: " + name);
        System.out.println("| ".repeat(depth) + " mutability: " + mutability);
        System.out.println("| ".repeat(depth) + " lifetime: " + lifetime);
        System.out.println("| ".repeat(depth) + " type: " + type);
        System.out.println("| ".repeat(depth) + " value: ");
        if (value != null) {
            value.debug(depth + 1);
        }
    }

    @Override
    public StatementInfo getInfo() {
        return info;
    }
}
