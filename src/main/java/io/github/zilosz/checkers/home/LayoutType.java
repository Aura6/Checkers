package io.github.zilosz.checkers.home;

import io.github.zilosz.checkers.util.MathUtils;
import lombok.Getter;

import java.util.function.Predicate;

public enum LayoutType {
    EVEN("Even", MathUtils::isEven, 8, 6, 20),
    ODD("Odd", MathUtils::isOdd, 9, 7, 21);

    @Getter private final String name;
    @Getter private final Predicate<Double> validator;
    @Getter private final int value;
    @Getter private final int min;
    @Getter private final int max;

    LayoutType(String name, Predicate<Double> validator, int value, int min, int max) {
        this.name = name;
        this.validator = validator;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    @Override
    public String toString() {
        return name;
    }
}
