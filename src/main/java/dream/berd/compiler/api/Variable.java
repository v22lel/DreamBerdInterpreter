package dream.berd.compiler.api;

import dream.berd.compiler.api.lifetimes.Lifetime;
import dream.berd.compiler.api.types.Mutability;
import dream.berd.compiler.api.types.Type;

public class Variable {
    private String name;
    private Type type;
    private Lifetime lifetime;
    private Mutability mutability;
}
