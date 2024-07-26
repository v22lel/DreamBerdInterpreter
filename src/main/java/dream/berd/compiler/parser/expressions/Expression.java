package dream.berd.compiler.parser.expressions;

import dream.berd.compiler.Token;
import dream.berd.compiler.TokenStream;
import dream.berd.compiler.api.types.Bool;
import dream.berd.compiler.api.types.PrimitiveType;
import dream.berd.compiler.parser.ParseException;
import dream.berd.compiler.parser.expressions.ops.OperatorOperation;

import java.util.ArrayList;
import java.util.List;

public interface Expression {
    void debug(int depth);

    static Expression parse(TokenStream tokens, int oldLine) throws ParseException {
        return Expression.parse(tokens, oldLine, false);
    }

    static Expression parse(TokenStream tokens, int oldLine, boolean parseAsStatement) throws ParseException {
        Token next = tokens.expectSome(oldLine);
        int line = next.getLine();

        Expression expr = switch (next.getType()) {
            case IDENTIFIER -> {
                Token peeked = tokens.peek().get(ParseException.endOfStream(line));
                switch (peeked.getType()) {
                    case L_PAREN -> { //func call
                        tokens.next();
                        String funcName = next.into();
                        peeked = tokens.peek().get(ParseException.endOfStream(peeked.getLine()));
                        if (peeked.getType() == Token.Type.R_PAREN) {
                            tokens.next();
                            yield new FunctionCall(funcName, new ArrayList<>());
                        }

                        List<Expression> args = new ArrayList<>();
                        while (peeked.getType() != Token.Type.R_PAREN) {
                            Expression arg = Expression.parse(tokens, peeked.getLine());
                            args.add(arg);
                            var n = tokens.next();
                            if (n.isNone()) break;
                            tokens.putBack(n.get(ParseException.endOfStream(peeked.getLine())));
                            peeked = tokens.peek().get(ParseException.endOfStream(peeked.getLine()));
                            if (peeked.getType() == Token.Type.COMMA) {
                                tokens.next();
                            }
                        }
                        tokens.next();
                        yield new FunctionCall(funcName, args);
                    }
                    default -> {
                        if (parseAsStatement) {
                            throw ParseException.notAStatement(peeked.getLine());
                        }
                        yield new VariableUse(next.into());
                    }
                }
            }
            case INT_LIT, FLOAT_LIT, STRING_LIT -> {
                if (parseAsStatement) {
                    throw ParseException.notAStatement(next.getLine());
                }
                var peeked = tokens.peek().get(ParseException.endOfStream(next.getLine()));
                if (peeked.getType() != Token.Type.OPERATOR) {
                    tokens.next();
                    tokens.putBack(peeked);
                    yield new Literal(PrimitiveType.ofLiteralToken(next.getType()), next.into());
                } else {
                    yield null;
                }
            }
            case TRUE, FALSE, MAYBE -> {
                if (parseAsStatement) {
                    throw ParseException.notAStatement(next.getLine());
                }
                yield new Literal(PrimitiveType.BOOL, Bool.fromTokenType(next.getType()));
            }
            case UNDEFINED -> {
                if (parseAsStatement) {
                    throw ParseException.notAStatement(next.getLine());
                }
                yield new Literal(PrimitiveType.UNDEFINED, "undefined");
            }
            case OPERATOR -> {
                if (parseAsStatement) {
                    throw ParseException.notAStatement(next.getLine());
                }
                Token.Operator operator = next.into();
                switch (operator.getType()) {
                    case NOT -> {
                        yield new Unary(Expression.parse(tokens, line), new OperatorOperation(operator));
                    }
                    default -> {}
                }
                yield null;
            }
            case L_PAREN -> {
                Expression inner = parseBinaryExpression(tokens, line);
                tokens.expectNext(line, Token.Type.R_PAREN);
                yield inner;
            }
            default -> {
                if (parseAsStatement) {
                    throw ParseException.notAStatement(next.getLine());
                }
                yield null;
            }
        };

        var peekedOpt = tokens.peek(true);
        if (peekedOpt.isSome()) {
            var peeked = peekedOpt.get();
            Expression e;
            do {
                e = switch (peeked.getType()) {
                    case DOT -> {
                        tokens.next();
                        yield new ClassAccess(expr, Expression.parse(tokens, peeked.getLine()));
                    }
                    case L_BRACKET -> {
                        tokens.next();
                        var arr = new ArrayAccess(expr, Expression.parse(tokens, peeked.getLine()));
                        tokens.expectNext(next.getLine(), Token.Type.R_BRACKET);
                        yield arr;
                    }
                    default -> null;
                };

                if (e != null) {
                    peeked = tokens.peek(true).get(ParseException.endOfStream(peeked.getLine()));
                    expr = e;
                }
            }
            while (e != null);
        }

        return expr;
    }

    private static Expression parseBinaryExpression(TokenStream tokens, int line) throws ParseException {
        return parseBinaryExpression(tokens, line, 0);
    }

    private static Expression parseBinaryExpression(TokenStream tokens, int line, int prio) throws ParseException {
        Expression left = Expression.parse(tokens, line);
        Token next = tokens.expectSome(line);
        if (next.getType() == Token.Type.OPERATOR) {
            Token.Operator operator = next.into();
            int opPrio = operator.getPriority();
            if (prio < opPrio) {
                Expression right = Expression.parse(tokens, next.getLine());
                return new Binary(left, right, new OperatorOperation(operator));
            }
        }
        tokens.putBack(next);
        return Expression.parse(tokens, next.getLine());
    }
}
