package lib;

import java.util.Random;
import java.lang.Math;

class SBMath {
    public static double Pi() {
        return Math.PI;
    }

    public static int Abs(Integer x) {
        return Math.abs(x);
    }

    public static double Abs(Double x) {
        return Math.abs(x);
    }

    public static int Ceiling(Integer x) {
        return ((Double) Math.ceil(x)).intValue();
    }

    public static int Ceiling(Double x) {
        return ((Double) Math.ceil(x)).intValue();
    }

    public static int Floor(Integer x) {
        return ((Double) Math.floor(x)).intValue();
    }

    public static int Floor(Double x) {
        return ((Double) Math.floor(x)).intValue();
    }

    public static double NaturalLog(Integer x) {
        return Math.log(x);
    }

    public static double NaturalLog(Double x) {
        return Math.log(x);
    }

    public static double Log(Integer x) {
        return Math.log10(x);
    }

    public static double Log(Double x) {
        return Math.log10(x);
    }

    public static double Cos(Integer x) {
        return Math.cos(x);
    }

    public static double Cos(Double x) {
        return Math.cos(x);
    }

    public static double Sin(Integer x) {
        return Math.sin(x);
    }

    public static double Sin(Double x) {
        return Math.sin(x);
    }

    public static double Tan(Integer x) {
        return Math.tan(x);
    }

    public static double Tan(Double x) {
        return Math.tan(x);
    }

    public static double ArcCos(Integer x) {
        return Math.acos(x);
    }

    public static double ArcCos(Double x) {
        return Math.acos(x);
    }

    public static double ArcSin(Integer x) {
        return Math.asin(x);
    }

    public static double ArcSin(Double x) {
        return Math.asin(x);
    }

    public static double ArcTan(Integer x) {
        return Math.atan(x);
    }

    public static double ArcTan(Double x) {
        return Math.atan(x);
    }

    public static double GetDegress(Integer x) {
        return Math.toDegrees(x);
    }

    public static double GetDegress(Double x) {
        return Math.toDegrees(x);
    }

    public static double GetRadians(Integer x) {
        return Math.toRadians(x);
    }

    public static double GetRadians(Double x) {
        return Math.toRadians(x);
    }

    public static double SquareRoot(Integer x) {
        return Math.sqrt(x);
    }

    public static double SquareRoot(Double x) {
        return Math.sqrt(x);
    }

    public static double Power(Integer x, Integer y) {
        return Math.pow(x, y);
    }

    public static double Power(Double x, Double y) {
        return Math.pow(x, y);
    }

    public static double Round(Integer x) {
        return Math.round(x);
    }

    public static double Round(Double x) {
        return Math.round(x);
    }

    public static double Max(Integer x, Integer y) {
        return Math.max(x, y);
    }

    public static double Max(Double x, Double y) {
        return Math.max(x, y);
    }

    public static double Max(Integer x, Double y) {
        return Math.max(x, y);
    }

    public static double Max(Double x, Integer y) {
        return Math.max(x, y);
    }

    public static double Min(Integer x, Integer y) {
        return Math.min(x, y);
    }

    public static double Min(Double x, Double y) {
        return Math.min(x, y);
    }

    public static double Min(Integer x, Double y) {
        return Math.min(x, y);
    }

    public static double Min(Double x, Integer y) {
        return Math.min(x, y);
    }

    public static double Remainder(Integer x, Integer y) {
        return Math.IEEEremainder(x, y);
    }

    public static double Remainder(Double x, Double y) {
        return Math.IEEEremainder(x, y);
    }

    public static double Remainder(Integer x, Double y) {
        return Math.IEEEremainder(x, y);
    }

    public static double Remainder(Double x, Integer y) {
        return Math.IEEEremainder(x, y);
    }

    public static int GetRandomNumber(Integer x) {
        Random rand = new Random();
        return rand.nextInt(x);
    }

    public static int GetRandomNumber(Double x) {
        Random rand = new Random();
        return rand.nextInt(x.intValue());
    }
}
