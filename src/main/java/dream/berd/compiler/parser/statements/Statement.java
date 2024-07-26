package dream.berd.compiler.parser.statements;

import dream.berd.Utils;
import dream.berd.compiler.Token;
import dream.berd.compiler.TokenStream;
import dream.berd.compiler.Vars;
import dream.berd.compiler.api.lifetimes.Lifetime;
import dream.berd.compiler.api.lifetimes.LineLifetime;
import dream.berd.compiler.api.lifetimes.TimeLifetime;
import dream.berd.compiler.api.types.Mutability;
import dream.berd.compiler.api.types.Type;
import dream.berd.compiler.api.types.UnknownType;
import dream.berd.compiler.parser.ParseException;
import dream.berd.compiler.parser.expressions.Expression;
import dream.berd.compiler.parser.expressions.VariableDeclaration;

public interface Statement {

    void debug(int depth);

    StatementInfo getInfo();

    static Statement parse(Token tkn, TokenStream tokens) throws ParseException {
        return switch (tkn.getType()) {
            case IF -> {

                If ifStmt = new If(tokens, tkn.getLine());
                Vars.lastIf = ifStmt.getCondition();
                yield ifStmt;
            }
            case ELSE -> {
                if (Vars.lastIf == null) {
                    throw ParseException.unexpectedToken(tkn, Token.Type.IF);
                }
                var elseStmt = new Else(Vars.lastIf, tokens, tkn.getLine());
                Vars.lastIf = null;
                yield elseStmt;
            }
            case WHEN -> new When(tokens, tkn.getLine());
            case RETURN -> new Return(tokens, tkn.getLine());
            case CONST, VAR -> {
                var vd = parseVariableDec(tkn, tokens);
                Token em = tokens.expectNext(tkn.getLine(), Token.Type.EXCLAMATION_MARK, Token.Type.FLIPPED_EXCLAMATION_MARK);
                int rank = em.getRank();
                vd.getInfo().setRank(rank);
                yield vd;
            }
            default -> {
                tokens.putBack(tkn);
                var es = new ExpressionStatement(tokens, tkn.getLine());
                Token em = tokens.expectNext(tkn.getLine(), Token.Type.EXCLAMATION_MARK, Token.Type.FLIPPED_EXCLAMATION_MARK);
                int rank = em.getRank();
                es.getInfo().setRank(rank);
                yield es;
            }
        };
    }

    private static VariableDeclaration parseVariableDec(Token tkn, TokenStream tokens) throws ParseException {
        int mut = 0;
        if (tkn.getType() == Token.Type.VAR) {
            mut += 2;
        }
        Token next = tokens.expectNext(tkn.getLine(), Token.Type.VAR, Token.Type.CONST);
        if (next.getType() == Token.Type.VAR) {
            mut += 1;
        }

        Mutability mutability = Mutability.values()[mut];
        Token nameToken = tokens.expectNext(next.getLine(), Token.Type.IDENTIFIER);
        String name = nameToken.into();
        Type type = new UnknownType();
        Lifetime lifetime = null;
        Expression value = null;

        next = tokens.expectSome(nameToken.getLine());
        if (next.getType() == Token.Type.EXCLAMATION_MARK || next.getType() == Token.Type.FLIPPED_EXCLAMATION_MARK) {
            return new VariableDeclaration(name, value, type, lifetime, mutability, nameToken.getLine());
        }
        if (next.getType() == Token.Type.COMPARISON) {
            Token.Comparison comparison = next.into();
            if (comparison.getType() == Token.Comparison.Type.LESS) {
                Token lit = tokens.expectNext(next.getLine(), Token.Type.INT_LIT, Token.Type.FLOAT_LIT, Token.Type.INFINITY);
                if (lit.getType() == Token.Type.INFINITY) {
                    lifetime = new TimeLifetime(0, true);
                    tokens.expectNext(lit.getLine(), Token.Type.COMPARISON);
                } else {
                    float val = Utils.toPrimitiveFloat(lit.into());
                    Token peeked = tokens.peek().get(ParseException.endOfStream(lit.getLine(), Token.Type.TIME_UNIT, Token.Type.COMPARISON));
                    if (peeked.getType() == Token.Type.TIME_UNIT) {
                        tokens.next();
                        Token.TimeUnit timeUnit = peeked.into();
                        lifetime = new TimeLifetime((long) timeUnit.apply(val), false);
                    } else {
                        lifetime = new LineLifetime((int) val);
                    }
                    tokens.expectNext(peeked.getLine(), Token.Type.COMPARISON);
                }
                next = tokens.expectSome(next.getLine());
            }
        }
        if (next.getType() == Token.Type.EXCLAMATION_MARK || next.getType() == Token.Type.FLIPPED_EXCLAMATION_MARK) {
            return new VariableDeclaration(name, value, type, lifetime, mutability, next.getLine());
        }
        if (next.getType() == Token.Type.COLON) {
            Token typeToken = tokens.expectNext(next.getLine(), Token.Type.IDENTIFIER);
            String typeName = typeToken.into();
            type = Type.fromName(typeName);
            next = tokens.expectSome(nameToken.getLine());
        }
        if (next.getType() == Token.Type.EXCLAMATION_MARK || next.getType() == Token.Type.FLIPPED_EXCLAMATION_MARK) {
            return new VariableDeclaration(name, value, type, lifetime, mutability, next.getLine());
        }
        if (next.getType() == Token.Type.COMPARISON) {
            Token.Comparison comparison = next.into();
            if (comparison.getLevel() != 1) {
                throw new ParseException(next.getLine(), "Illegal amount of '='", "Expected '=', found '" + "=".repeat(comparison.getLevel()) + "'");
            }
            value = Expression.parse(tokens, next.getLine());
        }

        return new VariableDeclaration(name, value, type, lifetime, mutability, next.getLine());
    }
}
