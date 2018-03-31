package com.ubcomp.algendel.stepcounter;

/* A simple utility vector class for drawing trees */
public class Vector {
    int x;
    int y;

    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Vector sub(Vector vector, Vector vector2) {
        return new Vector(vector.x - vector2.x, vector.y - vector2.y);
    }

    public static Vector add(Vector vector, Vector vector2) {
        return new Vector(vector.x + vector2.x, vector.y + vector2.y);
    }


    public void mul(double coeff) {
        this.x *= coeff;
        this.y *= coeff;
    }


    public void rotateVector(double ang) {
        //ang = -ang * (Math.PI/180);
        double cos = Math.cos(ang);
        double sin = Math.sin(ang);
        this.x = (int) Math.round(10000 * (this.x * cos - this.y * sin)) / 10000;
        this.y = (int) Math.round(10000 * (this.x * sin + this.y * cos)) / 10000;
    }

}