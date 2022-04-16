package ru.liner.sodadrunk.math;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 16.04.2022, суббота
 **/
public class Generator {
    private Random random;
    private int one;
    private int other;
    private int result;

    public Generator() {
        this.random = new Random();
        generate();
    }

    public int getOne() {
        return one;
    }

    public int getOther() {
        return other;
    }

    public void generate(){
        one = random.nextInt(12);
        other = random.nextInt(12);
        if(one == 0 || other == 0)
            generate();
        result = one * other;
    }

    public Integer[] getSuggestions() {
        List<Integer> integers = Arrays.asList(result, result + other, result - other);
        Collections.shuffle(integers);
        return integers.toArray(new Integer[0]);
    }

    public CharSequence[] getSuggestionsChar() {
        List<CharSequence> integers = Arrays.asList(String.valueOf(result),String.valueOf( result + other), String.valueOf(result - other));
        Collections.shuffle(integers);
        return integers.toArray(new CharSequence[0]);
    }

    public boolean validateResult(int result) {
        return this.result == result;
    }

    @Override
    public String toString() {
        return "Generator{" +
                ", one=" + one +
                ", other=" + other +
                ", result=" + result +
                '}';
    }
}
