package com.ubcomp.algendel.stepcounter;

public class StepDetector {

    public static int ACCEL_WINDOW_SIZE = 40;
    public static int VEL_WINDOW_SIZE = 10;

    private int accelWindowCounter = 0;
    private float[][] accelWindow = new float[3][ACCEL_WINDOW_SIZE];
    private float[] accelRunningTotal = new float[3];

    private int velWindowCounter = 0;
    private float[] velWindow = new float[VEL_WINDOW_SIZE];


    public float[] getSmoothedValues(float[] currentAccel) {
        currentAccel = removeGravity((currentAccel));
        int index = accelWindowCounter % ACCEL_WINDOW_SIZE;
        accelWindowCounter++;
        float[] curAccelAvg = new float[3]; //averages
        for (int i = 0; i <= 2; i++) {
            accelWindow[i][index] = currentAccel[i];
            curAccelAvg[i] = MathFunctions.sum(accelWindow[i]) / Math.min(accelWindowCounter, ACCEL_WINDOW_SIZE);
        }

        return curAccelAvg;
    }

    // from https://developer.android.com/reference/android/hardware/SensorEvent.html
    public float[] removeGravity(float[] values) {
        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate

        final float alpha = 0.8f;
        float[] gravity = new float[3];
        float[] la = new float[3];

        for (int i = 0; i <= 2; i++) {
            gravity[i] = alpha * gravity[i] + (1 - alpha) * values[i];
            la[i] = values[i] - gravity[i];
        }

        return la;
    }

    /* This was the code provided in Jon's example. It's equivalent to getSmothedValues() function
    but seems a little less intuitive to me. It is not used.
     */
    public float[] getSmoothedValues2(float[] currentAccel) {

        // First step is to update our guess of where the global z vector is.
        int index = accelWindowCounter % ACCEL_WINDOW_SIZE;
        float[] curAccelAvg = new float[3]; //averages
        for (int i = 0; i <= 2; i++) {
            //remove the last value from total
            accelRunningTotal[i] = accelRunningTotal[i] - accelWindow[i][index];
            //add current value to window
            accelWindow[i][index] = currentAccel[i];
            // add current value to total instead
            accelRunningTotal[i] = accelRunningTotal[i] + accelWindow[i][index];
            //get average
            if (accelWindowCounter == 0) {
                curAccelAvg[i] = accelRunningTotal[i];
            } else {

                curAccelAvg[i] = accelRunningTotal[i] / Math.min(accelWindowCounter, ACCEL_WINDOW_SIZE);
            }
            accelWindowCounter++;
        }


        return curAccelAvg;
    }


    /* It seems like I was getting somewhat better results squaring the smoothed and raw velocity values (averaging them?)
    rather just the smoothed ones
     */

    public float getMagnitude(float[] smoothedValues, float[] rawValues) {
        float magnitudeAtPoint = MathFunctions.magnitude(smoothedValues, rawValues);


        return magnitudeAtPoint;
    }

    public float getSmoothedMagnitude(float currentVelocity) {


        velWindow[velWindowCounter % VEL_WINDOW_SIZE] = currentVelocity;
        velWindowCounter++;

        float smoothedMagnitude = MathFunctions.sum(velWindow) / Math.min(velWindowCounter, VEL_WINDOW_SIZE);
        return smoothedMagnitude;
    }


}
