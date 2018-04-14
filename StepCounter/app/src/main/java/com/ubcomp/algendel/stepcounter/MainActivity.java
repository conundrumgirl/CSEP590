package com.ubcomp.algendel.stepcounter;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivity extends Activity implements SensorEventListener {
    private TextView stepCount, androidStepCount;
    private StepDetector stepDetector;
    private SensorManager _sensorManager;
    private Sensor _accelSensor, _stepSensor ;
    private LineGraphSeries series_raw_x, series_raw_y, series_raw_z, series_smoothed_x, series_smoothed_y, series_smoothed_z;
    GraphView graph_raw, graph_smoothed;
    private VisualizationView drawView;
    private long lastUpdate = 0;
    private float androidCounterOffset = 0;

    private static final String TEXT_NUM_STEPS = "";
    private int STEPS_PER_LEVEL = 10;
    private static  float STEP_THRESHOLD = 9.1f;

    private static final int STEP_DELAY_NS = 250000000; //.25 seconds

    private float[] lastEvents = new float[3];


    //
    //The algorithm keeps track of three consecutive values in a vector called Start Vector,
    // if the three values getting increased, then a peak might be a head.
    // Then the first value represents the start point of the peak.
    // If the value surpasses a step threshold which is defined in advance,
    // then all the values above the threshold will be stored in a vector called Peak Vector.
    // The algorithm utilizes the peak vector to detect the real peak values as well as the time when they are taken place.
    // Since the peak vector contains the values that fall above the step threshold, then the maximum value should represent the real
    // peak.


    private long lastStepTimeNs = 0;
    private int numSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stepDetector = new StepDetector();
        initializeViews();
        initializeSensors();
        initializeGraphs();

    }

    public void initializeViews() {
        stepCount = (TextView) findViewById(R.id.stepView);
        drawView = (VisualizationView) findViewById(R.id.drawView);
        androidStepCount = (TextView) findViewById(R.id.stepViewAndroid);
        Button btnReset = (Button) this.findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReset();

            }
        });
        ((Button) this.findViewById(R.id.btnDoStep)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doStep();

            }
        });

    }

    private void setConstsFromConfig() {
        STEPS_PER_LEVEL =  Integer.parseInt(((EditText)this.findViewById(R.id.txtStepsPerLevel)).getText().toString());
        StepDetector.ACCEL_WINDOW_SIZE = Integer.parseInt(((EditText)this.findViewById(R.id.txtDebugAcsSmoothingWindow)).getText().toString());
        StepDetector.VEL_WINDOW_SIZE = Integer.parseInt(((EditText)this.findViewById(R.id.txtDebugVctSmoothingWindow)).getText().toString());
        MainActivity.STEP_THRESHOLD= Float.parseFloat(((EditText)this.findViewById(R.id.txtDebugThrshold)).getText().toString());
    }


    public void initializeSensors() {
        _sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        _accelSensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        _stepSensor = _sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

    }

    @Override
    public void onResume() {
        super.onResume();
        _sensorManager.registerListener(this, _accelSensor, SensorManager.SENSOR_DELAY_GAME);
        _sensorManager.registerListener(this, _stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void onReset() {
        numSteps = 0;
        androidCounterOffset = 0;
        setConstsFromConfig();
        stepDetector = new StepDetector();
        stepCount.setText(TEXT_NUM_STEPS + numSteps);
        ((TextView) findViewById(R.id.txtVelocityAtPoint)).setText("");
        this.drawView.reset();
    }

    @Override
    public void onPause() {
        super.onPause();
        _sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER: {
                long timestamp = event.timestamp;
                float[] valArray = {event.values[0], event.values[1], event.values[2]};
                float[] smoothedValues = stepDetector.getSmoothedValues(valArray);

                float magnitudeAtPoint = stepDetector.getMagnitude(smoothedValues, valArray);
                float magnitude = stepDetector.getSmoothedMagnitude(magnitudeAtPoint);

                TextView vAtPoint = (TextView) findViewById(R.id.txtVelocityAtPoint);
                TextView vsmView = (TextView) findViewById(R.id.txtVelocityAvg);
                vAtPoint.setText("Magnitude:" + magnitude);
                if (magnitude > STEP_THRESHOLD) {
                    vsmView.setText("Magnitude above threshhold: " + magnitude);
                }

                long curTime = System.currentTimeMillis();

                // sampling rate for the graph -- don't need to update on every change
                if ((curTime - lastUpdate) > 100) {
                    lastUpdate = curTime;
                    plotToGraph(timestamp, event.values, smoothedValues);
                }

                //technically we don't know if it's the peak until the next value comes in
                if (wasPreviousPeak(magnitude, timestamp)) {
                    doStep();
                }

                break;

            }
            case Sensor.TYPE_STEP_COUNTER: {
                if (androidCounterOffset == 0) {
                    androidCounterOffset = event.values[0];
                }
                androidStepCount.setText("Per Android Step Counter: " + Float.toString(event.values[0] - androidCounterOffset + 1) + " steps" );
                break;
            }
        }

    }

    // If the value is greater than the previous but less than next it's a peak.
    // For this I also cheat by requiring that it's larger than a threshhold value
    // Also to remove some of false positives if a peak is too close to a determined peak i ignore it.
    public boolean wasPreviousPeak(float velocity, long timeNs) {

        boolean result = false;
        lastEvents[0] = lastEvents[1];
        lastEvents[1] = lastEvents[2];
        lastEvents[2] = velocity;

        boolean hadIncrease = lastEvents[1] > lastEvents[0];
        boolean thenDecrease = lastEvents[1] > lastEvents[2];
        boolean isGreaterThanThreshold = lastEvents[1] > STEP_THRESHOLD;
        boolean hasDelayPassed = timeNs - lastStepTimeNs > STEP_DELAY_NS;

        if (hadIncrease && thenDecrease && isGreaterThanThreshold && hasDelayPassed) {
            result = true;
            lastStepTimeNs = timeNs;
        }

        return result;
    }

    public void doStep() {
        numSteps++;
        stepCount.setText(TEXT_NUM_STEPS + numSteps);
        if (numSteps % STEPS_PER_LEVEL == 0) {
            this.drawView.addTreeLevel();
        }


    }

    // private utility functions

    private void initializeGraphs() {
        //should have used arrays here - but too tired.
        graph_raw = (GraphView) findViewById(R.id.graph_raw);
        graph_smoothed = (GraphView) findViewById(R.id.graph_smooth);


        GraphView[] graphs = {graph_raw, graph_smoothed};


        for (int i = 0; i < graphs.length; i++) {
            graphs[i].getViewport().setYAxisBoundsManual(true);
            graphs[i].getViewport().setMinY(0);
            graphs[i].getViewport().setMaxY(12);

            graphs[i].getViewport().setMinX(0);
            graphs[i].getViewport().setMaxX(40);
        }




        series_raw_x = new LineGraphSeries<>();
        series_raw_x.setColor(Color.GREEN);
        series_raw_x.setDrawDataPoints(false);
        graph_raw.addSeries(series_raw_x);
        series_raw_x.setTitle("x");


        series_raw_y = new LineGraphSeries<>();
        series_raw_y.setColor(Color.BLUE);
        series_raw_y.setDrawDataPoints(false);
        graph_raw.addSeries(series_raw_y);
        series_raw_y.setTitle("y");


        series_raw_z = new LineGraphSeries<>();
        series_raw_z.setColor(Color.RED);
        series_raw_z.setDrawDataPoints(false);
        graph_raw.addSeries(series_raw_z);
        series_raw_z.setTitle("z");


        series_smoothed_x = new LineGraphSeries<>();
        series_smoothed_x.setColor(Color.GREEN);
        series_smoothed_x.setDrawDataPoints(false);
        graph_smoothed.addSeries(series_smoothed_x);
        series_smoothed_x.setTitle("x");

        series_smoothed_y = new LineGraphSeries<>();
        series_smoothed_y.setColor(Color.BLUE);
        series_smoothed_y.setDrawDataPoints(false);
        graph_smoothed.addSeries(series_smoothed_y);
        series_smoothed_y.setTitle("y");


        series_smoothed_z = new LineGraphSeries<>();
        series_smoothed_z.setColor(Color.RED);
        series_smoothed_z.setDrawDataPoints(false);
        graph_smoothed.addSeries(series_smoothed_z);
        series_smoothed_z.setTitle("z");

        graph_raw.getLegendRenderer().setVisible(true);
        graph_raw.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        graph_smoothed.getLegendRenderer().setVisible(true);
        graph_smoothed.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        graph_raw.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX)
                    return "";
                else
                    return super.formatLabel(value, isValueX);
            }
        });

        graph_smoothed.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX)
                    return "";
                else
                    return super.formatLabel(value, isValueX);
            }
        });

    }

    /* This function plots two time series to graphs for smoothed and for raw values */
    private void plotToGraph(long timestamp, float[] rawValues, float[] smoothedValues) {
        series_raw_x.appendData(new DataPoint(timestamp, rawValues[0]), false, 100);
        series_raw_y.appendData(new DataPoint(timestamp, rawValues[1]), false, 100);
        series_raw_z.appendData(new DataPoint(timestamp, rawValues[2]), false, 100);
        series_smoothed_x.appendData(new DataPoint(timestamp, smoothedValues[0]), false, 100);
        series_smoothed_y.appendData(new DataPoint(timestamp, smoothedValues[1]), false, 100);
        series_smoothed_z.appendData(new DataPoint(timestamp, smoothedValues[2]), false, 100);
    }

}
