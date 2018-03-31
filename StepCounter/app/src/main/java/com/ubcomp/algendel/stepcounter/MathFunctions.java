package com.ubcomp.algendel.stepcounter;

public class MathFunctions {

    private MathFunctions() {
    }

    public static float sum(float[] array) {
        float result = 0;
        for (int i = 0; i < array.length; i++) {
            result  += array[i];
        }
        return result;
    }

    public static float magnitude(float[] a, float[] b) {
        float retval = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
        return (float)Math.sqrt(retval);

    }


}