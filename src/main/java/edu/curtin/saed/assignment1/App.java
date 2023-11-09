/**
 * FILENAME:    App.java
 * PURPOSE:     Class that represents the 'Application'. Keeps track of necessary
 *              information and threads (Ability, Spawner, ScoreTracker, GUI).
 *              This also calls the necessary methods that perform the 
 *              functionalities of the program.
 */

package edu.curtin.saed.assignment1;

import edu.curtin.saed.assignment1.controller.*;
import edu.curtin.saed.assignment1.model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application 
{
    private Label score = null;
    private Label queuedWalls = null;
    private TextArea logger = null;

    private Ability ability = null;
    private Spawner spawner = null;
    private ScoreTracker scoreTracker = null;

    public static void main(String[] args) 
    {
        launch();        
    }
    
    @Override
    public void start(Stage stage) 
    {
        stage.setTitle("Assignment 1");

        // Toolbar & Elements
        ToolBar toolbar = new ToolBar();
        Button exitButton = new Button("End threads");
        score = new Label("Score: 0");
        queuedWalls = new Label("Queued walls: 0");
        toolbar.getItems().addAll(score, queuedWalls, exitButton);

        // Logger
        logger = new TextArea();

        JFXArena arena = new JFXArena(this);

        // Split Pane
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(arena, logger);
        arena.setMinWidth(300.0);
        
        // Content Pane
        BorderPane contentPane = new BorderPane();
        contentPane.setTop(toolbar);
        contentPane.setCenter(splitPane);

        // Scene
        Scene scene = new Scene(contentPane, 800, 800);
        stage.setScene(scene);
        stage.show();

        // Threads
        scoreTracker = new ScoreTracker(this);
        scoreTracker.start();
        ability = new Ability(arena);
        ability.start();
        spawner = new Spawner(arena);
        spawner.start();

        // Arena Listener
        arena.addListener((x, y) ->
        {
            // If command was successfully queued, update Queue label
            if(ability.queueCommand(new Point(x, y)) == true)
            {
                setQueueLabel();
            }
        });

        // Exit-button Listener
        exitButton.setOnAction((event) ->
        {
            endGame();
        });
    }


    /**
     * Ends all the threads gracefully.
     */
    public void endGame()
    {
        ability.stop();
        spawner.stop();
        scoreTracker.stop();
    }


    /**
     * When called, accesses the GUI thread and sets the text
     * of the 'Score' label to the given score.
     */
    public void setScoreLabel(int inScore)
    {
        Platform.runLater(() ->
        {
            score.setText("Score: " + String.valueOf(inScore));
        });
    }


    /**
     * When called, accesses the GUI thread and sets the text
     * of the 'Queued Walls' label to the count received from
     * the ability thread.
     */
    public void setQueueLabel()
    {
        Platform.runLater(() ->
        {
            queuedWalls.setText("Queued: " + String.valueOf(ability.getQueueCount()));
        });
    }


    /**
     * When called, accesses the GUI thread and appends a
     * log message to the Logger.
     */
    public void log(String message)
    {
        Platform.runLater(() ->
        {
            logger.appendText(message);
        });
    }


    /**
     * Method which is called by instance of JFXArena,
     * to allow it to notify the ScoreTracker thread
     * that a robot has been eliminated.
     */
    public void trackElimination()
    {
        scoreTracker.robotEliminated();
    }
}