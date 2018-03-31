package com.ubcomp.algendel.stepcounter;
import android.graphics.Color;
import java.util.Random;
import java.util.ArrayList;

public class Tree {

    //This is a tree class. It creates random colored trees

    private Branch root;
    public ArrayList<Branch> branches;
    private int paintColor;
    public boolean isFinished = false;


    public Tree(int x1, int y1, int x2, int y2) {

        Vector start = new Vector(x1, y1);
        Vector end = new Vector(x2, y2);
        Branch root = new Branch(start, end);
        this.root = root;
        this.branches = new ArrayList<Branch>();
        Random rand = new Random();
        int r = rand.nextInt();
        int g = rand.nextInt();
        int b = rand.nextInt();
        paintColor = Color.rgb(r, g, b);
        Color.rgb(r, g, b);
        this.branches.add(root);

    }

    public int getColor() {
        return this.paintColor;
    }

    public float[] getBranch(int branchIndex) {

        float[] coords = new float[4];
        coords[0] = this.branches.get(branchIndex).begin.x;
        coords[1] = this.branches.get(branchIndex).begin.y;
        coords[2] = this.branches.get(branchIndex).end.x;
        coords[3] = this.branches.get(branchIndex).end.y;

        return coords;
    }


}