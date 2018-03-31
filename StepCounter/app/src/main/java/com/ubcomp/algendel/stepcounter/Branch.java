package com.ubcomp.algendel.stepcounter;

import static java.lang.Math.PI;

/* This class is based on the tutorial on object oriented fractal trees that can be cound here
https://www.youtube.com/watch?v=fcdNSZ9IzJM */

public class Branch {
    public Vector begin, end;
    public boolean finished = false;


    public Branch(Vector start, Vector end) {
        this.begin = start;
        this.end = end;
    }


    public Branch branchA() {
        Vector dir = Vector.sub(this.end, this.begin);
        dir.rotateVector(PI / 8.5);
        dir.mul(0.9);
        Vector newEnd = Vector.add(this.end, dir);
        Branch b = new Branch(this.end, newEnd);
        return b;
    }


    public Branch branchB() {
        Vector dir = Vector.sub(this.end, this.begin);
        dir.rotateVector(-PI / 8.5);
        dir.mul(0.9);
        Vector newEnd = Vector.add(this.end, dir);
        Branch b = new Branch(this.end, newEnd);
        return b;
    }

}
