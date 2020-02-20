/*
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich
 * All rights reserved.
 *
 */

package ch.fhnw.util.math;

/**
 * 2D vector for basic vector algebra. Instances are immutable.
 *
 * @author radar
 */
public final class Vec2 {
  public static final Vec2 ZERO = new Vec2(0, 0);
  public static final Vec2 ONE = new Vec2(1, 1);
  public static final Vec2 X = new Vec2(1, 0);
  public static final Vec2 Y = new Vec2(0, 1);
  public static final Vec2 X_NEG = new Vec2(-1, 0);
  public static final Vec2 Y_NEG = new Vec2(0, -1);

  public final float x;
  public final float y;

  public Vec2(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Vec2(double x, double y) {
    this((float) x, (float) y);
  }

  public float length() {
    return MathUtil.length(x, y);
  }

  public float distance(Vec2 v) {
    return (float) Math.sqrt((v.x - x) * (v.x - x) + (v.y - y) * (v.y - y));
  }

  public Vec2 add(Vec2 v) {
    return new Vec2(x + v.x, y + v.y);
  }

  public Vec2 subtract(Vec2 v) {
    return new Vec2(x - v.x, y - v.y);
  }

  public Vec2 scale(float s) {
    return new Vec2(x * s, y * s);
  }

  public Vec2 negate() {
    return scale(-1);
  }

  public Vec2 normalize() {
    float l = length();
    if (MathUtil.isZero(l) || l == 1)
      return this;
    return new Vec2(x / l, y / l);
  }

  public float dot(Vec2 a) {
    return MathUtil.dot(x, y, a.x, a.y);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof Vec2) {
      final Vec2 v = (Vec2) obj;
      return (x == v.x) && (y == v.y);
    }
    return false;
  }


  public float[] toArray() {
    return new float[] { x, y };
  }

  @Override
  public String toString() {
    return "[" + x + ", " + y + "]";
  }

}
