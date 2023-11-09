/**
 * FILENAME:    Robot.java
 * PURPOSE:     Class which implements Runnable. Represents a Robot element in
 *              the Arena. Contains information needed to display a Robot onto
 *              the GUI.
 * @author      George Devean Sinining (20165484)
 */

package edu.curtin.saed.assignment1.controller;

import edu.curtin.saed.assignment1.model.Point;
import java.util.concurrent.ThreadLocalRandom;

public class Robot implements Runnable
{
    private Spawner producer;

    private int id;
    private long delay;
    private boolean isDead;
    private Point position;
    private Point destination;

    /**
     * Constructor that stores and/or initialises class fields.
     * 
     * @param inProducer    The producer-thread
     * @param inId          Id, given by producer-thread
     * @param inPosition    Position (Spawn location), given by producer-thread
     */
    public Robot(Spawner inProducer, int inId, Point inPosition)
    {
        this.producer = inProducer;

        this.id = inId;
        this.delay = ThreadLocalRandom.current().nextLong(500L, 2000L);
        this.isDead = false;
        this.position = inPosition;
        this.destination = null;
    }

    // Generic Accessors
    public int getId() { return id; }
    public Point getPosition() { return position; }
    public Point getDestination() { return destination; }

    // Generic Mutators
    public void setPosition(Point inPosition) { this.position = inPosition; }
    public void setId(int id) { this.id = id; }
    public void setDestination(Point destination) { this.destination = destination; }

    
    /**
     * Task ran by Robot:
     *      - Will continually look for a destination to go, and
     *      move if possible. 
     *      - Thread will end if Robot reached a Wall or 'game-end'
     *      condition reached.
     */
    @Override
    public void run()
    {
        try
        {
            while(!this.isDead)
            {
                Thread.sleep(delay);
                Point location = producer.getDestination(this.position);

                if(location != null)
                {
                    this.destination = location;

                    // move() returns boolean, whether Robot should end thread.
                    this.isDead = producer.move(this);
                }
            }
        }
        catch(InterruptedException ex)
        {
            System.out.println("Robot " + this.id + " thread ended.");
        }
    }
}
