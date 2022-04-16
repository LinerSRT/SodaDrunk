package ru.liner.sodadrunk.math;

import java.util.Random;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 16.04.2022, суббота
 **/
public enum Operator {
    ADD,
    SUBTRACT,
    DIVIDE,
    MULTIPLY;

    public static Operator random(){
        return values()[new Random().nextInt(values().length)];
    }

    public int result(Operator operator, int one, int other) {
        switch (operator) {
            case ADD:
                return one + other;
            case SUBTRACT:
                return one - other;
            case MULTIPLY:
                return one * other;
            case DIVIDE:
                return (one == 0 || other == 0) ? 0 : one / other;
        }
        return 0;
    }

}
