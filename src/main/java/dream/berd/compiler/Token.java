package dream.berd.compiler;

import dev.mv.utilsx.sequence.PutBackMultiPeek;

import java.util.concurrent.atomic.AtomicInteger;

public class Token {
    private int line;
    private Type type;
    private Object inner;

    public Token(int line, Type type) {
        this.line = line;
        this.type = type;
        inner = null;
    }

    public Token(int line, Type type, Object inner) {
        this.line = line;
        this.type = type;
        this.inner = inner;
    }

    public static Token numLit(int line, String str, boolean isFloat) {
        if (isFloat)
            return new Token(line, Type.FLOAT_LIT, Float.parseFloat(str));
        return new Token(line, Type.INT_LIT, Integer.parseInt(str));
    }

    public static Token operator(int line, Operator operator) {
        return new Token(line, Type.OPERATOR, operator);
    }

    public int getLine() {
        return line;
    }

    public Type getType() {
        return type;
    }


    public <T> T into() {
        return (T) inner;
    }

    public int getRank() {
        if (type == Type.EXCLAMATION_MARK) {
            return into();
        }
        if (type == Type.FLIPPED_EXCLAMATION_MARK) {
            return -(int)into();
        }
        return 0;
    }

    @Override
    public String toString() {
        String extraInfo = switch (type) {
            default -> "";
            case OPERATOR, TIME_UNIT, COMPARISON, IDENTIFIER, STRING_LIT, INT_LIT, FLOAT_LIT, EXCLAMATION_MARK -> " " + inner.toString();
        };

        return "Line " + line + ": " + type + extraInfo;
    }

    public String debugString() {
        String str;
        if (inner != null) {
            str = "'" + inner + "'";
        } else {
            str = type.toString();
        }
        return str;
    }

    public enum Type {
        ANY,
        INT_LIT,
        FLOAT_LIT,
        STRING_LIT,

        IDENTIFIER,
        OPERATOR,
        TIME_UNIT,
        COMPARISON,
        DOUBLE_ARROW,
        COMMA,
        DOT,
        COLON,
        QUESTION_MARK,

        //KEYWORDS
        DELETE,
        CLASS,
        IF,
        ELSE,
        WHEN,
        VAR,
        CONST,
        RETURN,
        EXPORT,
        TO,
        NEW,
        EXCLAMATION_MARK,
        FLIPPED_EXCLAMATION_MARK,
        TRUE,
        FALSE,
        MAYBE,
        UNDEFINED,
        NEXT,
        PREVIOUS,
        CURRENT,
        INFINITY,

        // Parentheses
        L_PAREN,
        R_PAREN,
        L_BRACKET,
        R_BRACKET,
        L_BRACE,
        R_BRACE,
        L_ANGLE_BRACKET,
        R_ANGLE_BRACKET;


        @Override
        public String toString() {
            String thingy = switch (this) {
                case ANY -> "any token";
                case INT_LIT -> "Integer Literal";
                case FLOAT_LIT -> "Float Literal";
                case STRING_LIT -> "String Literal";
                case IDENTIFIER -> "Identifier";
                case OPERATOR -> "Operator";
                case TIME_UNIT -> "Time Unit";
                case COMPARISON -> "Comparison";
                case DOUBLE_ARROW -> "=>";
                case COMMA -> ",";
                case DOT -> ".";
                case COLON -> ":";
                case QUESTION_MARK -> "?";
                case DELETE -> "Delete";
                case CLASS -> "Class";
                case IF -> "If";
                case ELSE -> "Else";
                case WHEN -> "When";
                case VAR -> "var";
                case CONST -> "const";
                case RETURN -> "return";
                case EXPORT -> "export";
                case TO -> "to";
                case NEW -> "new";
                case EXCLAMATION_MARK -> "!";
                case FLIPPED_EXCLAMATION_MARK -> "¡";
                case TRUE -> "true";
                case FALSE -> "false";
                case MAYBE -> "maybe";
                case UNDEFINED -> "undefined";
                case NEXT -> "next";
                case PREVIOUS -> "previous";
                case CURRENT -> "current";
                case INFINITY -> "Infinity";
                case L_PAREN -> "(";
                case R_PAREN -> ")";
                case L_BRACKET -> "[";
                case R_BRACKET -> "]";
                case L_BRACE -> "{";
                case R_BRACE -> "}";
                case L_ANGLE_BRACKET -> "<";
                case R_ANGLE_BRACKET -> ">";
            };
            return "'" + thingy + "'";
        }
    }

    public static class Operator {
        private boolean isAssign;
        private int amtWhitespace;
        private Type type;

        public Operator(AtomicInteger amtWhitespace, boolean isAssign, Type type) {
            this.amtWhitespace = amtWhitespace.getAndSet(0);
            this.isAssign = isAssign;
            this.type = type;
        }

        public Operator(AtomicInteger amtWhitespace, Type type, PutBackMultiPeek<Character> iter) {
            this.amtWhitespace = amtWhitespace.getAndSet(0);
            if (type == Type.PLUS) {
                if (iter.peek().getOr('\0').equals('+')) {
                    iter.next();
                    this.type = Type.PLUS_PLUS;
                    return;
                }
            }
            if (type == Type.MINUS) {
                if (iter.peek().getOr('\0').equals('-')) {
                    iter.next();
                    this.type = Type.MINUS_MINUS;
                    return;
                }
            }
            if (iter.peek().getOr('\0').equals('=')) {
                this.isAssign = true;
                iter.next();
            }
            this.type = type;
        }

        public boolean isAssign() { return isAssign; }

        public boolean isUnary() {
            return switch (type) {
                case PLUS, MINUS, MULTIPLY, MODULO, DIVIDE, POWER, UNSIGNED_LEFT_SHIFT, UNSIGNED_RIGHT_SHIFT,
                     SIGNED_LEFT_SHIFT, SIGNED_RIGHT_SHIFT -> false;
                case PLUS_PLUS, MINUS_MINUS, NOT, NONE -> true;
            };
        }

        public Type getType()     { return type; }

        @Override
        public String toString() {
            return type + (isAssign ? "_EQUALS" : "");
        }

        public int getPriority() {
            int prio = switch (type) {
                case PLUS -> 1;
                case PLUS_PLUS -> -1;
                case MINUS -> 1;
                case MINUS_MINUS -> -1;
                case MULTIPLY -> 2;
                case DIVIDE -> 2;
                case MODULO -> 2;
                case POWER -> 3;
                case UNSIGNED_LEFT_SHIFT -> 0;
                case UNSIGNED_RIGHT_SHIFT -> 0;
                case SIGNED_LEFT_SHIFT -> 0;
                case SIGNED_RIGHT_SHIFT -> 0;
                case NOT -> -1;
                case NONE -> 0;
            };
            return prio + amtWhitespace * 100;
        }

        public enum Type {
            PLUS,
            PLUS_PLUS,
            MINUS,
            MINUS_MINUS,
            MULTIPLY,
            DIVIDE,
            MODULO,
            POWER,
            UNSIGNED_LEFT_SHIFT,
            UNSIGNED_RIGHT_SHIFT,
            SIGNED_LEFT_SHIFT,
            SIGNED_RIGHT_SHIFT,
            NOT,
            NONE
        }
    }

    public static class TimeUnit {
        private Type type;

        public TimeUnit(Type type) { this.type = type; }
        public Type getType()      { return type; }

        public float apply(float value) {
            return switch (type) {
                case MS_MILLISECONDS -> value;
                case CS_CENTISECONDS -> value * 10;
                case DS_DECISECONDS -> value * 100;
                case S_SECONDS -> value * 1000;
                case M_MINUTES -> value * 60 * 1000;
                case H_HOURS -> value * 60 * 60 * 1000;
                case D_DAYS -> value * 24 * 60 * 60 * 1000;
                case Y_YEARS -> value * 365.25f * 24 * 60 * 60 * 1000;
                case DEC_DECADES -> value * 10 * 365.25f * 24 * 60 * 60 * 1000;
                case C_CENTURIES -> value * 100 * 365.25f * 24 * 60 * 60 * 1000;
                case MIL_MILLENNIA -> value * 1000 * 365.25f * 24 * 60 * 60 * 1000;
            };
        }

        @Override
        public String toString() {
            return type.toString();
        }

        public enum Type {
            MS_MILLISECONDS,
            CS_CENTISECONDS,
            DS_DECISECONDS,
            S_SECONDS,
            M_MINUTES,
            H_HOURS,
            D_DAYS,
            Y_YEARS,
            DEC_DECADES,
            C_CENTURIES,
            MIL_MILLENNIA,
        }
    }

    public static class Comparison {
        private Type type;
        private boolean isNot;
        private int level;

        public Comparison(Type type, int level, boolean isNot) {
            this.type = type;
            this.level = level;
            this.isNot = isNot;
        }

        public Type getType() {
            return type;
        }

        public int getLevel() {
            return level;
        }

        public boolean isNot() {
            return isNot;
        }

        @Override
        public String toString() {
            return (isNot ? "NOT_" : "") + type + ", Level: "+ level;
        }

        public enum Type {
            EQUALS,
            LESS,
            LESS_EQUALS,
            GREATER,
            GREATER_EQUALS
        }
    }
}
