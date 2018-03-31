package com.ubcomp.algendel.stepcounter;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;


public class VisualizationView extends View {

    private Paint drawPaint;
    private ArrayList<Tree> trees;

    private int width, height;

    public VisualizationView(Context context) {
        super(context);

        init(null, null, 0);
    }

    public VisualizationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public VisualizationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {

        Log.e("init", "ERROR init()");
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        this.trees = new ArrayList<Tree>();
    }


    @Override
    public void onDraw(Canvas canvas) {

        for (int j = 0; j < this.trees.size(); j++) {
            Tree tree = this.trees.get(j);
            drawPaint.setColor(tree.getColor());

            for (int i = 0; i < tree.branches.size(); i++) {
                float[] coords = tree.getBranch(i);
                canvas.drawLine(coords[0], coords[1], coords[2], coords[3], drawPaint);
            }
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void reset() {
        this.trees.clear();
        this.invalidate();
    }

    public void newTree() {
        int widthOffset = this.width / 2;
        int heightOffset = this.height-100;

        if (this.trees.size() == 0) {
            Tree tree = new Tree(widthOffset,  this.height, widthOffset, heightOffset);
            this.trees.add(tree);
        } else {
            Random r = new Random();
            int xMin = 80;
            int xMax = this.width - 80;
            int yMin = 400;
            int yMax = this.height;
            int nextx = r.nextInt((xMax - xMin) + 1) + xMin;
            int nexty = r.nextInt((yMax - yMin) + 1) + yMin;
            Tree tree = new Tree(nextx, nexty, nextx, nexty - 100);
            this.trees.add(tree);

        }

    }



    public void addTreeLevel() {
        int numberOfTrees = this.trees.size();
        if (numberOfTrees == 0) {
            newTree();

        } else if (this.trees.get(numberOfTrees - 1).branches.size() >= 500) {

            Tree lastTree = this.trees.get(numberOfTrees - 1);
            lastTree.isFinished = true;

            newTree();
        } else {
            for (int j = 0; j < numberOfTrees; j++) {
                Tree tree = this.trees.get(j);
                if (!tree.isFinished) {
                    for (int i = tree.branches.size() - 1; i >= 0; i--) {
                        if (!tree.branches.get(i).finished) {
                            tree.branches.add(tree.branches.get(i).branchA());
                            tree.branches.add(tree.branches.get(i).branchB());
                        }
                        tree.branches.get(i).finished = true;
                    }
                }
            }
        }
        invalidate();
    }


}
