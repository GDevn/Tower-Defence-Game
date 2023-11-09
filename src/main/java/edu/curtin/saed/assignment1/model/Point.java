/**
 * FILENAME:    Point.java
 * PURPOSE:     Represents a Point in the Arena. Simply contains the
 *              'x' and 'y' coordinates of a point.
 * @author      George Devean Sinining (20165484)
 */

package edu.curtin.saed.assignment1.model;

public class Point 
{
    private double x;
    private double y;

    /**
     * Constructor that stores and/or initialises class fields.
     * 
     * @param x     x coordinate
     * @param y     y coordinate
     */
    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    // Generic Accessors
    public double getX() { return x; }
    public double getY() { return y; }

    // Generic Mutators
    public void setX(double inX) { this.x = inX; }
    public void setY(double inY) { this.y = inY; }
}
