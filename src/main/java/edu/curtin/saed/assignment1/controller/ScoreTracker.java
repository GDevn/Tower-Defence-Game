/**
 * FILENAME:    ScoreTracker.java
 * PURPOSE:     Represents the task of managing the Score of the game.
 * @author      George Devean Sinining (20165484)
 */

package edu.curtin.saed.assignment1.controller;

import edu.curtin.saed.assignment1.App;
import java.util.concurrent.LinkedBlockingQueue;

public class ScoreTracker 
{
    private Thread scoreTrackerThread;
    private Thread passiveScoreThread;
    private App app;
    
    private int score = 0;
    private LinkedBlockingQueue<Integer> list = new LinkedBlockingQueue<>();

    /**
     * Constructor that stores and/or initialises class fields.
     * 
     * @param inApp     Reference to App.
     */
    public ScoreTracker(App inApp)
    {
        this.app = inApp;
    }


    /**
     * Task ran by ScoreTracker:
     *      - For every 1 second, score is incremented by 10 points and 
     *      calls method in App thread to update Score label
     *      - Takes value from blocking queue, which represents a Robot
     *      being destroyed
     *          - If queue empty, proceed as per usual
     */
    public void start()
    {
        Runnable passiveScoreTask = () ->
        {
            try
            {
                while(true)
                {
                    Thread.sleep(1000L);
                    list.put(10);
                }
            }
            catch(InterruptedException e)
            {
                System.out.println("Robot eliminated method.");
            }
        };

        Runnable consumerTask = () ->
        {
            try
            {
                while(true)
                {
                    score += list.take();
                    app.setScoreLabel(score);
                }
            }
            catch(InterruptedException e)
            {
                System.out.println("Score Tracker thread ended");
            }
        };

        scoreTrackerThread = new Thread(consumerTask, "score_tracker-thread");
        scoreTrackerThread.start();

        passiveScoreThread = new Thread(passiveScoreTask, "passive_score-thread");
        passiveScoreThread.start();
    }


    /**
     * When called, adds 100 points to queue.
     */
    public void robotEliminated()
    {
        try
        {
            list.put(100);
        }
        catch(InterruptedException e)
        {
            System.out.println("Robot eliminated method.");
        }
    }


    /**
     * Deals with gracefully ending the thread.
     */
    public void stop()
    {
        if(scoreTrackerThread == null)
        {
            throw new IllegalStateException("Score-tracker thread is null");
        }

        scoreTrackerThread.interrupt();
        scoreTrackerThread = null;
        passiveScoreThread.interrupt();
        passiveScoreThread = null;
    }
}
