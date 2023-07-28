package lib;

import java.util.Random;
import java.lang.Math;

class SBMath {
    public static int random(Integer min, Integer max) {
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

    public static int modulo(Integer a, Integer b) {
        return a % b;
    }

    public static int floor(Double a) {
        Double floored = Math.floor(a);
        return floored.intValue();
    }

    public static int floor(Integer a) {
        return a;
    }
}
