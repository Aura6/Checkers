package io.github.zilosz.checkers.util;

public class MathUtils {

    public static boolean isEven(double n) {
        return n % 2 == 0;
    }

    public static boolean testProbability(double percent) {
        return Math.random() < percent;
    }

    public static boolean isBetween(double value, double min, double max) {
        return min <= value && value <= max;
    }
}
