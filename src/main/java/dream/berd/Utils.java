package dream.berd;

public class Utils {
    public static boolean isCharNumberPiece(char c) {
        return Character.isDigit(c) || c == '.';
    }

    public static float toPrimitiveFloat(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).floatValue();
        } else if (value instanceof Float) {
            return (Float) value;
        } else {
            throw new IllegalArgumentException("Value must be an instance of Integer or Float");
        }
    }
}
