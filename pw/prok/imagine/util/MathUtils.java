package pw.prok.imagine.util;

public class MathUtils {
    public static int roundUpDivision(int dividend, int divider) {
        if (dividend == 0) return 0;
        return (dividend + divider - 1) / divider;
    }
}
