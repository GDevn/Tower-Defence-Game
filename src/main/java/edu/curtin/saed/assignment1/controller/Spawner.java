/**
 * FILENAME:    Spawner.java
 * PURPOSE:     Represents the task of Robot-spawning and management for the game.
 * @author      George Devean Sinining (20165484)
 */

package edu.curtin.saed.assignment1.controller;

import edu.curtin.saed.assignment1.model.Point;
import java.util.concurrent.*;

public class Spawner
{
    private Thread spawnerThread;
    private JFXArena arena;

    private ExecutorService threadPool;
    private int nThreads = Runtime.getRuntime().availableProcessors();
    // private int nThreads = 30;

    /**
     * Constructor that stores and/or initialises class fields.
     * 
     * @param inArena   Reference to Arena.
     */
    public Spawner(JFXArena inArena)
    {
        this.arena = inArena;
        this.threadPool = Executors.newFixedThreadPool(nThreads);
    }


    /**
     * Task ran by Spawner:
     *      - Continually spawns a Robot in the game every 1500
     *      milliseconds.
     *      - Before spawning in a Robot, must pass conditions:
     *          - Number of Robots is less than threads
     *          - Unoccupied spawn location
     */
    public void start() 
    {
        Runnable producerTask = () ->
        {
            try
            {
                int count = 1;
                while(true)
                {
                    if(count - arena.getDeathCount() < nThreads)
                    {
                        Point spawnPoint = getSpawnLocation();
                        if(spawnPoint != null)
                        {
                            Robot robot = new Robot(this, count, spawnPoint);
                            arena.addRobot(robot);
                            threadPool.submit(robot);
                            count += 1;
                        }
                    }
                    Thread.sleep(1500L);
                }
            }
            catch(InterruptedException e)
            {
                System.out.println("Spawner thread ended.");
            }
        };

        spawnerThread = new Thread(producerTask, "spawner-thread");
        spawnerThread.start();
    }


    /**
     * To return a Point on the Arena that a Robot can spawn on.
     */
    private Point getSpawnLocation()
    {
        // Top-left
        if(arena.isOccupied(false, 0, 0) == false)
        {
            return new Point(0,0);
        }

        // Top-right
        else if(arena.isOccupied(false, arena.getGridWidth()-1, 0) == false)
        {
            return new Point(arena.getGridWidth()-1, 0);
        }

        // Bottom-left
        else if(arena.isOccupied(false, 0, arena.getGridHeight()-1) == false)
        {
            return new Point(0, arena.getGridHeight()-1);
        }

        // Bottom-right
        else if(arena.isOccupied(false, arena.getGridWidth()-1, arena.getGridHeight()-1) == false)
        {
            return new Point(arena.getGridWidth()-1, arena.getGridHeight()-1);
        }

        // No spawnable locations
        else
        {
            return null;
        }
    }


    /**
     * To return a Point on the Arena that a Robot can move to, given 
     * its current position.
     */
    public Point getDestination(Point currentPos)
    {
        // RIGHT
        if(arena.isOccupied(false, currentPos.getX()+1, currentPos.getY()) == false)
        {
            return new Point((int) currentPos.getX()+1, (int) currentPos.getY());
        }

        // DOWN
        else if(arena.isOccupied(false, currentPos.getX(), currentPos.getY()+1) == false)
        {
            return new Point((int) currentPos.getX(), (int) currentPos.getY()+1);
        }

        // LEFT
        else if(arena.isOccupied(false, currentPos.getX()-1, currentPos.getY()) == false)
        {
            return new Point((int) currentPos.getX()-1, (int) currentPos.getY());
        }

        // UP
        else if(arena.isOccupied(false, currentPos.getX(), currentPos.getY()-1) == false)
        {
            return new Point((int) currentPos.getX(), (int) currentPos.getY()-1);
        }


        else
        {
            return null;
        }
    }

    
    /**
     * Handles the logic of:
     *      - Animating the Robot
     *      - Checking if Robot has moved to a point occupied by:
     *          - Wall
     *          - Citadel
     */
    public boolean move(Robot robot) throws InterruptedException
    {
        String direction = getDirection(robot.getPosition(), robot.getDestination());
        for(int i = 0; i < 4; i++)
        {
            if(direction.equals("Up"))
            {
                robot.getPosition().setY(robot.getPosition().getY() - 0.25);
            }
            else if(direction.equals("Right"))
            {
                robot.getPosition().setX(robot.getPosition().getX() + 0.25);
            }
            else if(direction.equals("Down"))
            {
                robot.getPosition().setY(robot.getPosition().getY() + 0.25);
            }
            else if(direction.equals("Left"))
            {
                robot.getPosition().setX(robot.getPosition().getX() - 0.25);
            }
            arena.requestLayout();
            Thread.sleep(40L);
        }

        if((robot.getPosition().getX() == arena.getGridWidth() / 2) && robot.getPosition().getY() == arena.getGridHeight() / 2)
        {
            arena.citadelReached();
            return true;
        }
        else if(arena.isWall(robot.getPosition().getX(), robot.getPosition().getY()))
        {
            arena.impact(robot);
            return true;
        }
        return false;
    }


    /**
     * Helper method to simply get the direction a Robot is headed based on
     * current position and destination.
     */
    private String getDirection(Point current, Point destination)
    {
        String direction = null;
        if(current.getX() == destination.getX()) // Same column
        {
            if(current.getY() < destination.getY())
            {
                direction = "Down";
            }
            else
            {
                
                direction = "Up";
            }
        }
        else if(current.getY() == destination.getY()) // Same row
        {
            if(current.getX() < destination.getX())
            {
                direction = "Right";
            }
            else
            {
                direction = "Left";
            }
        }
        return direction;
    }


    /**
     * Deals with gracefully ending the thread.
     */
    public void stop()
    {
        if(spawnerThread == null)
        {
            throw new IllegalStateException("Spawner thread is null");
        }
        threadPool.shutdownNow();
        threadPool = null;
        spawnerThread.interrupt();
        spawnerThread = null;
    }
}
