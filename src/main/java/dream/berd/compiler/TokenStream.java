package dream.berd.compiler;

import dev.mv.utilsx.generic.Option;
import dev.mv.utilsx.sequence.PutBackMultiPeek;
import dev.mv.utilsx.sequence.Sequence;
import dream.berd.compiler.parser.ParseException;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class TokenStream extends PutBackMultiPeek<Token> {
    public TokenStream(Sequence<Character> source) {
        super(new InnerSequence(source));
    }

    public Token expectNext(int line, Token.Type... expected) throws ParseException {
        var next = next().get(ParseException.endOfStream(line, expected));
        if (Arrays.stream(expected).noneMatch(t -> t == next.getType())) {
            throw ParseException.unexpectedToken(next, expected);
        }
        return next;
    }

    public Token expectSome(int line) throws ParseException {
        return next().get(ParseException.endOfStream(line));
    }

    private static class InnerSequence implements Sequence<Token> {
        private final PutBackMultiPeek<Character> source;
        private int line = 1;
        private final AtomicInteger whitespaceSkipped = new AtomicInteger(0);
        private boolean justGotLiteral = false;

        public InnerSequence(Sequence<Character> source) {
            if (source instanceof PutBackMultiPeek<Character> peek)
                this.source = peek;
            else this.source = source.putBackMultiPeek();
        }

        @Override
        public Option<Token> next() {
            var nextOpt = source.next();
            if (nextOpt.isNone()) return Option.none();

            char next = nextOpt.getUnchecked();

            while (System.lineSeparator().contains(String.valueOf(next))) {
                for (int i=0;i<System.lineSeparator().length();i++)
                    nextOpt = source.next();
                if (nextOpt.isNone()) return Option.none();
                next = nextOpt.getUnchecked();
                line++;
            }

            while (Character.isWhitespace(next)) {
                whitespaceSkipped.incrementAndGet();
                var no = source.next();
                if (no.isSome()) {
                    next = no.getUnchecked();
                } else {
                    return Option.none();
                }
            }

            char[] last = { '\0' };
            return switch (next) {
                case '+': {
                    if (Character.isDigit(source.peek().getOr('\0'))) {
                        yield getNumLit(next, last);
                    }
                    yield Option.some(Token.operator(line, new Token.Operator(whitespaceSkipped, Token.Operator.Type.PLUS, source)));
                }
                case '-': {
                    if (Character.isDigit(source.peek().getOr('\0'))) {
                        yield getNumLit(next, last);
                    }
                    yield Option.some(Token.operator(line, new Token.Operator(whitespaceSkipped, Token.Operator.Type.MINUS, source)));
                }
                case '*': yield Option.some(Token.operator(line, new Token.Operator(whitespaceSkipped, Token.Operator.Type.MULTIPLY, source)));
                case '/': yield Option.some(Token.operator(line, new Token.Operator(whitespaceSkipped, Token.Operator.Type.DIVIDE, source)));
                case '%': yield Option.some(Token.operator(line, new Token.Operator(whitespaceSkipped, Token.Operator.Type.MODULO, source)));
                case '^': yield Option.some(Token.operator(line, new Token.Operator(whitespaceSkipped, Token.Operator.Type.POWER, source)));
                case '>': {
                    int level = source.takeWhile(c -> (last[0] = c) == '=').count();
                    source.putBack(last[0]);
                    if (level > 0) yield Option.some(new Token(line, Token.Type.COMPARISON, new Token.Comparison(Token.Comparison.Type.GREATER_EQUALS, level, false)));

                    if (source.peek().getOr('\0').equals('>')) {
                        source.next();
                        if (source.peek().getOr('\0').equals('>')) {
                            source.next();
                            yield Option.some(Token.operator(line, new Token.Operator(whitespaceSkipped, Token.Operator.Type.UNSIGNED_RIGHT_SHIFT, source)));
                        }
                        yield Option.some(Token.operator(line, new Token.Operator(whitespaceSkipped, Token.Operator.Type.SIGNED_RIGHT_SHIFT, source)));
                    }
                    yield Option.some(new Token(line, Token.Type.COMPARISON, new Token.Comparison(Token.Comparison.Type.GREATER, 0, false)));
                }
                case '<': {
                    int level = source.takeWhile(c -> (last[0] = c) == '=').count();
                    source.putBack(last[0]);
                    if (level > 0) yield Option.some(new Token(line, Token.Type.COMPARISON, new Token.Comparison(Token.Comparison.Type.LESS_EQUALS, level, false)));

                    if (source.peek().getOr('\0').equals('<')) {
                        source.next();
                        if (source.peek().getOr('\0').equals('<')) {
                            source.next();
                            yield Option.some(Token.operator(line, new Token.Operator(whitespaceSkipped, Token.Operator.Type.UNSIGNED_LEFT_SHIFT, source)));
                        }
                        yield Option.some(Token.operator(line, new Token.Operator(whitespaceSkipped, Token.Operator.Type.SIGNED_LEFT_SHIFT, source)));
                    }
                    yield Option.some(new Token(line, Token.Type.COMPARISON, new Token.Comparison(Token.Comparison.Type.LESS, 0, false)));
                }

                case '=': {
                    if (source.peek().getOr('\0').equals('>')) {
                        source.next();
                        yield Option.some(new Token(line, Token.Type.DOUBLE_ARROW));
                    }

                    int level = source.takeWhile(c -> c == '=').count() + 1;
                    yield Option.some(new Token(line, Token.Type.COMPARISON, new Token.Comparison(Token.Comparison.Type.EQUALS, level, false)));
                }

                case ';': {
                    int level = source.takeWhile(c -> (last[0] = c) == '=').count();
                    source.putBack(last[0]);
                    if (level > 0) yield Option.some(new Token(line, Token.Type.COMPARISON, new Token.Comparison(Token.Comparison.Type.EQUALS, level, true)));
                    yield Option.some(new Token(line, Token.Type.OPERATOR, new Token.Operator(whitespaceSkipped, false, Token.Operator.Type.NOT)));
                }

                case '¡': yield Option.some(new Token(line, Token.Type.FLIPPED_EXCLAMATION_MARK));
                case '!': {
                    int amt = source.takeWhile(c -> (last[0] = c) == '!').count() + 1;
                    source.putBack(last[0]);
                    yield Option.some(new Token(line, Token.Type.EXCLAMATION_MARK, amt));
                }
                case '.': yield Option.some(new Token(line, Token.Type.DOT));
                case ',': yield Option.some(new Token(line, Token.Type.COMMA));
                case ':': yield Option.some(new Token(line, Token.Type.COLON));
                case '?': yield Option.some(new Token(line, Token.Type.QUESTION_MARK));

                case '(': yield Option.some(new Token(line, Token.Type.L_PAREN));
                case ')': yield Option.some(new Token(line, Token.Type.R_PAREN));
                case '[': yield Option.some(new Token(line, Token.Type.L_BRACKET));
                case ']': yield Option.some(new Token(line, Token.Type.R_BRACKET));
                case '{': yield Option.some(new Token(line, Token.Type.L_BRACE));
                case '}': yield Option.some(new Token(line, Token.Type.R_BRACE));

                case '\'', '"':
                    int[] eQtAmt = { 0 };
                    String qts = next +
                            source.takeWhile(c ->
                                            "\"'".indexOf(last[0] = c) >= 0)
                                    .collect("");
                    source.putBack(last[0]);
                    int dQtAmt =
                            (int) qts.chars()
                                    .filter(c -> c == '"' )
                                    .count();
                    int[] qtAmt = { dQtAmt*2 + qts.length()-dQtAmt };
                    boolean[] esc = { false };
                    Option<Token> tok =
                            Option.some(new Token(
                                    line,
                                    Token.Type.STRING_LIT,
                                    source.takeWhile(c ->
                                                    (esc[0] = (c == '\\') != esc[0]) // TODO: escaping
                                                            || "\"'".indexOf(last[0] = c) < 0)
                                            .collect("")
                                            .replaceAll("\\\\[^\\\\]", "")));
                    source.putBack(last[0]);
                    source.skipWhile(c -> {
                        if ("\"'".indexOf(last[0] = c) < 0)
                            return false;
                        eQtAmt[0] += c == '"' ? 2 : 1;
                        return eQtAmt[0] < qtAmt[0];
                    }).next();
                    if ("\"'".indexOf(last[0]) < 0)
                        source.putBack(last[0]);
                    yield tok;



                default:  {
                    if (Character.isDigit(next)) {
                        yield getNumLit(next, last);
                    }
                    /*
                    String s = next + source.takeWhile(c -> Character.isLetterOrDigit(last[0] = c) || c == '_' || System.lineSeparator().contains(c + "")).collect("");
                    String[] lines = s.split(System.lineSeparator(), -1);
                    line += lines.length;



                    for (String str : Arrays.stream(lines).filter(str -> !str.isBlank()).toList()) {

                    }
*/
                    String s = next + source.takeWhile(c -> Character.isLetterOrDigit(last[0] = c) || c == '_').collect("");
                    if (next == 0) yield Option.none();
                    if (last[0] != 0) source.putBack(last[0]);
                    yield switch (s) {
                        case "if"       : yield Option.some(new Token(line, Token.Type.IF));
                        case "else"     : yield Option.some(new Token(line, Token.Type.ELSE));
                        case "when"     : yield Option.some(new Token(line, Token.Type.WHEN));
                        case "previous" : yield Option.some(new Token(line, Token.Type.PREVIOUS));
                        case "current"  : yield Option.some(new Token(line, Token.Type.CURRENT));
                        case "next"     : yield Option.some(new Token(line, Token.Type.NEXT));
                        case "class"    : yield Option.some(new Token(line, Token.Type.CLASS));
                        case "var"      : yield Option.some(new Token(line, Token.Type.VAR));
                        case "const"    : yield Option.some(new Token(line, Token.Type.CONST));
                        case "return"   : yield Option.some(new Token(line, Token.Type.RETURN));
                        case "export"   : yield Option.some(new Token(line, Token.Type.EXPORT));
                        case "to"       : yield Option.some(new Token(line, Token.Type.TO));
                        case "new"      : yield Option.some(new Token(line, Token.Type.NEW));
                        case "true"     : yield Option.some(new Token(line, Token.Type.TRUE));
                        case "false"    : yield Option.some(new Token(line, Token.Type.FALSE));
                        case "maybe"    : yield Option.some(new Token(line, Token.Type.MAYBE));
                        case "undefined": yield Option.some(new Token(line, Token.Type.UNDEFINED));
                        case "Infinity" : yield Option.some(new Token(line, Token.Type.INFINITY));
                        case "delete"   : yield Option.some(new Token(line, Token.Type.DELETE));

                        default         : {
                            if (justGotLiteral) {
                                justGotLiteral = false;
                                yield switch (s) {
                                    case "ms": yield Option.some(new Token(line, Token.Type.TIME_UNIT, new Token.TimeUnit(Token.TimeUnit.Type.MS_MILLISECONDS)));
                                    case "cd": yield Option.some(new Token(line, Token.Type.TIME_UNIT, new Token.TimeUnit(Token.TimeUnit.Type.CS_CENTISECONDS)));
                                    case "ds": yield Option.some(new Token(line, Token.Type.TIME_UNIT, new Token.TimeUnit(Token.TimeUnit.Type.DS_DECISECONDS)));
                                    case "s": yield Option.some(new Token(line, Token.Type.TIME_UNIT, new Token.TimeUnit(Token.TimeUnit.Type.S_SECONDS)));
                                    case "m": yield Option.some(new Token(line, Token.Type.TIME_UNIT, new Token.TimeUnit(Token.TimeUnit.Type.M_MINUTES)));
                                    case "h": yield Option.some(new Token(line, Token.Type.TIME_UNIT, new Token.TimeUnit(Token.TimeUnit.Type.H_HOURS)));
                                    case "d": yield Option.some(new Token(line, Token.Type.TIME_UNIT, new Token.TimeUnit(Token.TimeUnit.Type.D_DAYS)));
                                    case "y": yield Option.some(new Token(line, Token.Type.TIME_UNIT, new Token.TimeUnit(Token.TimeUnit.Type.Y_YEARS)));
                                    case "dec": yield Option.some(new Token(line, Token.Type.TIME_UNIT, new Token.TimeUnit(Token.TimeUnit.Type.DEC_DECADES)));
                                    case "c": yield Option.some(new Token(line, Token.Type.TIME_UNIT, new Token.TimeUnit(Token.TimeUnit.Type.C_CENTURIES)));
                                    case "mil": yield Option.some(new Token(line, Token.Type.TIME_UNIT, new Token.TimeUnit(Token.TimeUnit.Type.MIL_MILLENNIA)));
                                    default: yield Option.some(new Token(line, Token.Type.IDENTIFIER, s));
                                };
                            }
                            yield Option.some(new Token(line, Token.Type.IDENTIFIER, s));
                        }
                    };
                }
            };
        }

        private Option<Token> getNumLit(char next, char[] last) {
            final boolean[] isFloat = { false };
            String sNum = next + source.takeWhile(c -> {
                if ((last[0] = c) == '.') isFloat[0] = true;
                return (c == '.' || Character.isDigit(c));
            }).collect("");
            source.putBack(last[0]);
            justGotLiteral = true;
            return Option.some(Token.numLit(line, sNum, isFloat[0]));
        }
    }
}


/*
                    boolean isFloat = false;
                    StringBuilder buffer = new StringBuilder();
                    while (true) {
                        if (next == '.') {
                            isFloat = true;
                        } else if (!Character.isDigit(next)) {
                            if (isFloat) {
                                yield Option.some(new Token(line, Token.Type.FLOAT_LIT, Float.parseFloat(buffer.toString())));
                            }
                            yield Option.some(new Token(line, Token.Type.INT_LIT, Integer.parseInt(buffer.toString())));
                        }
                        buffer.append(next);
                        next = source.next().getUnchecked();
                    }*/