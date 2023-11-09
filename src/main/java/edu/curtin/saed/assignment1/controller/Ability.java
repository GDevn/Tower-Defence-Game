/**
 * FILENAME:    Ability.java
 * PURPOSE:     Represents the task of Wall-placing, and handles the logic
 *              for wall placing on the Arena.
 * @author      George Devean Sinining (20165484)
 */

package edu.curtin.saed.assignment1.controller;

import edu.curtin.saed.assignment1.model.Wall;
import edu.curtin.saed.assignment1.model.Point;
import java.util.concurrent.*;

public class Ability
{   
    private Thread abilityThread = null;
    private JFXArena arena;

    private BlockingQueue<Wall> queue = new LinkedBlockingQueue<>();

    /**
     * Constructor that stores and/or initialises class fields.
     */
    public Ability(JFXArena inArena)
    {
        this.arena = inArena;
    }


    /**
     * Task ran by Ability:
     *      - Continually takes from a command from a blocking queue, if there
     *      are less than 10 Walls already in the Arena
     *      - Sleep for 2000 milliseconds before attempting to take from queue again
     * Acts like a 'Consumer'
     */
    public void start()
    {
        Runnable consumerTask = () ->
        {
            try
            {
                while(true)
                {
                    Wall command = queue.take();
                    arena.addWall(command);
                    Thread.sleep(2000L);
                }
            }
            catch(InterruptedException e)
            {
                System.out.println("Ability-thread ended.");
            }
        };

        abilityThread = new Thread(consumerTask, "ability-thread");
        abilityThread.start();
    }


    /**
     * Allows 'Producers' to queue a Wall-placement command.
     */
    public boolean queueCommand(Point position)
    {
        boolean isSuccess = false;

        if(arena.getWallCount() < 10)
        {
            // Check if placing on spawn location & Unoccupied by Buildings and Robots
            if(arena.isSpawnLocation(position) == false && arena.isOccupied(true, position.getX(), position.getY()) == false)
            {
                
                isSuccess = queue.offer(new Wall(position));
            }   
        }
        return isSuccess;
    }


    /**
     * Deals with gracefully ending the thread.
     */
    public void stop()
    {
        if(abilityThread == null)
        {
            throw new IllegalStateException("Ability-Thread is null");
        }

        queue = null;
        abilityThread.interrupt();
        abilityThread = null;
    }


    /**
     * Helper method which enables other threads the ability to get the 
     * number of elements in queue.
     */
    public int getQueueCount()
    {
        return queue.size();
    }
}
