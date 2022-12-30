package com.uroria.kebab.location;

import java.util.Random;

public class Vector implements Cloneable {
    private static final Random RANDOM = new Random();
    private static final double EPSILON = 0.000001;

    protected double x;
    protected double y;
    protected double z;

    public Vector() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector add(Vector vector) {
        this.x += vector.x;
        this.y += vector.y;
        this.z += vector.z;
        return this;
    }

    public Vector subtract(Vector vector) {
        this.x -= vector.x;
        this.y -= vector.y;
        this.z -= vector.z;
        return this;
    }

    public Vector multiply(Vector vector) {
        this.x *= vector.x;
        this.y *= vector.y;
        this.z *= vector.z;
        return this;
    }

    public Vector copy(Vector vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
        return this;
    }

    public double length() {
        return Math.sqrt(NumberCo)
    }
}
