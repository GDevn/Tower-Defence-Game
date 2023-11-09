/**
 * FILENAME:    Wall.java
 * PURPOSE:     Represents a Wall element in the Arena.
 *              Contains information needed to display a Wall onto the GUI.
 * @author      George Devean Sinining (20165484)
 */

package edu.curtin.saed.assignment1.model;

public class Wall
{
    private Point position;
    private boolean isDamaged;

    /**
     * Constructor that stores and/or initialises class fields.
     * 
     * @param inPosition    Position Wall occupies
     */
    public Wall(Point inPosition)
    {
        this.position = inPosition;     
        this.isDamaged = false;
    }

    // Generic Accessors
    public Point getPosition() { return this.position; }
    public boolean getIsDamaged() { return this.isDamaged; }
    
    // Generic Mutators
    public void setPosition(Point inPosition) { this.position = inPosition; }
    public void setIsDamaged(boolean inIsDamaged) { this.isDamaged = inIsDamaged; }
}
