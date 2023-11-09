/**
 * FILENAME:    JFXArena.java
 * PURPOSE:     Represents the Arena and handles the logic relating to the
 *              Arena component of the GUI.
 * @author      George Devean Sinining (20165484),
 *              [Code provided by] David Cooper
 */

package edu.curtin.saed.assignment1.controller;

import edu.curtin.saed.assignment1.App;
import edu.curtin.saed.assignment1.model.*;

import javafx.scene.canvas.*;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.io.*;
import java.util.*;

/**
 * A JavaFX GUI element that displays a grid on which you can draw images, text and lines.
 */
public class JFXArena extends Pane
{
    // Stores all Wall and Robot elements
    private List<Wall> walls = new LinkedList<>();
    private List<Robot> robots = new LinkedList<>();

    private boolean finish = false;
    private int deathCount = 0;

    private App app;

    // Represents an image to draw, retrieved as a project resource.
    private static final String ROBOT_FILE = "1554047213.png";
    private static final String UNDAMAGEDWALL_FILE = "181478.png";
    private static final String DAMAGEDWALL_FILE = "181479.png";
    private static final String CITADEL_FILE = "rg1024-isometric-tower.png";
    private static final String CROSS_FILE = "cross.png";
    private Image robot1 = getImage(ROBOT_FILE);
    private Image undamagedWall = getImage(UNDAMAGEDWALL_FILE);
    private Image damagedWall = getImage(DAMAGEDWALL_FILE);
    private Image citadel = getImage(CITADEL_FILE);
    private Image cross = getImage(CROSS_FILE);
    
    // The following values are arbitrary, and you may need to modify them according to the 
    // requirements of your application.
    private int gridWidth = 9;
    private int gridHeight = 9;
    
    private double gridSquareSize; // Auto-calculated
    private Canvas canvas; // Used to provide a 'drawing surface'.

    private List<ArenaListener> listeners = null;
    
    /**
     * Creates a new arena object, loading the robot image and initialising a drawing surface.
     */
    public JFXArena(App inApp)
    {  
        this.app = inApp;

        canvas = new Canvas();
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        getChildren().add(canvas);
    }
    
    public int getGridWidth() { return gridWidth; }
    public int getGridHeight() { return gridHeight; }
    public int getDeathCount() { return this.deathCount; }
    public int getWallCount() { return walls.size(); }
    /** Retrieves Image from given filename. **/
    public Image getImage(String filename)
    {           
        Image image;

        try(InputStream is = getClass().getClassLoader().getResourceAsStream(filename))
        {
            if(is == null)
            {
                throw new AssertionError("Cannot find image file " + filename);
            }
            image = new Image(is);
        }
        catch(IOException e)
        {
            throw new AssertionError("Cannot load image file " + filename, e);
        }
        return image;
    }


    /**
     * Simply updates the GUI with the 'end' screen
     * and notify app that the game has ended.
     */
    public void citadelReached()
    {
        finish = true;
        requestLayout();
        app.endGame();
    }


    /**
     * Deals with updating the Wall and Robot when impacted.
     */
    public void impact(Robot robot)
    {
        Wall impactedWall = null;
        for(Wall wall : walls)
        {
            if((wall.getPosition().getX() == robot.getPosition().getX()) && (wall.getPosition().getY() == robot.getPosition().getY()))
            {
                impactedWall = wall;
                break;
            }
        }

        if(impactedWall.getIsDamaged() == false)
        {
            impactedWall.setIsDamaged(true);
        }
        else
        {
            walls.remove(impactedWall);
        }
        
        app.log("Robot " + robot.getId() + " impacted wall at " + impactedWall.getPosition().getX() + ", " + impactedWall.getPosition().getY() + ".\n");
        robots.remove(robot);
        app.trackElimination();
        this.deathCount += 1;
    }




    /**
     * Simply checks if a given position is a spawn location.
     */
    public boolean isSpawnLocation(Point position)
    {
        if(position.getX() == 0 && position.getY() == 0)
        {
            return true;
        }
        else if(position.getX() == gridWidth-1 && position.getY() == 0)
        {
            return true;
        }
        else if(position.getX() == 0 && position.getY() == gridHeight-1)
        {
            return true;
        }
        else if(position.getX() == gridWidth-1 && position.getY() == gridHeight-1)
        {
            return true;
        }
        return false;
    }


    /**
     * Returns boolean if a given cell in the arena is occupied.
     * Takes in a 'checkBuildings' boolean, to perform an additional
     * check for buildings.
     */
    public boolean isOccupied(boolean checkBuildings, double column, double row)
    {
        // Check within bounds
        if((column < 0 || column > gridWidth-1) || (row < 0 || row > gridHeight-1))
        {
            return true;
        }

        else
        {
            // Check for 'building' elements
            if(checkBuildings)
            {
                // Check if Citadel
                if(column == gridWidth / 2 && row == gridHeight / 2)
                { 
                    return true; 
                }

                // Check if Wall
                for(Wall wall : walls)
                {
                    if(wall.getPosition().getX() == column && wall.getPosition().getY() == row)
                    {
                        return true;
                    }
                }
            }

            // Check if Robot
            for(Robot robot : robots)
            {
                // Check for Robot's destination
                if((robot.getDestination() != null) && (robot.getDestination().getX() == column && robot.getDestination().getY() == row))
                {
                    return true;
                }
                // Check for Robot's current position
                if(robot.getPosition().getX() == column && robot.getPosition().getY() == row)
                {
                    return true;
                }
            }
        }   
        return false;
    }


    /**
     * To simply check if a Wall element occupies a given position.
     */
    public boolean isWall(double col, double row)
    {
        for(Wall wall : walls)
        {
            if(wall.getPosition().getX() == col && wall.getPosition().getY() == row)
            {
                return true;
            }
        }
        return false;
    }


    /**
     * Deals with adding a Wall into the game.
     */
    public void addWall(Wall inWall)
    {
        walls.add(inWall);
        app.log("Wall placed at ( " + inWall.getPosition().getX() + ", " + inWall.getPosition().getY() + ")\n");
        app.setQueueLabel();
        requestLayout();
    }


    /**
     * Deals with adding a Robot into the game.
     */
    public void addRobot(Robot inRobot)
    {
        robots.add(inRobot);
        app.log("Robot " + inRobot.getId() + " spawned at ( " + inRobot.getPosition().getX() + ", " + inRobot.getPosition().getY() + ")\n");
        requestLayout();
    }
 
    
    /**
     * Simply draw all the elements (Citadel, Wall and Robot).
     */
    public void drawElements(GraphicsContext gfx)
    {
        drawImage(gfx, citadel, gridWidth/2, gridHeight/2);

        for(Wall wall : walls)
        {
            if(wall.getIsDamaged())
            {
                drawImage(gfx, damagedWall, wall.getPosition().getX(), wall.getPosition().getY());
            }
            else
            {
                drawImage(gfx, undamagedWall, wall.getPosition().getX(), wall.getPosition().getY());
            }
        }

        for(Robot robot : robots)
        {
            drawImage(gfx, robot1, robot.getPosition().getX(), robot.getPosition().getY());
            drawLabel(gfx, String.valueOf(robot.getId()), robot.getPosition().getX(), robot.getPosition().getY());
        }
    }


    /**
     * Adds a callback for when the user clicks on a grid square within the arena. The callback 
     * (of type ArenaListener) receives the grid (x,y) coordinates as parameters to the 
     * 'squareClicked()' method.
     */
    public void addListener(ArenaListener newListener)
    {
        if(listeners == null)
        {
            listeners = new LinkedList<>();
            setOnMouseClicked(event ->
            {
                int gridX = (int)(event.getX() / gridSquareSize);
                int gridY = (int)(event.getY() / gridSquareSize);
                
                if(gridX < gridWidth && gridY < gridHeight)
                {
                    for(ArenaListener listener : listeners)
                    {   
                        listener.squareClicked(gridX, gridY);
                    }
                }
            });
        }
        listeners.add(newListener);
    }

        
    /**
     * This method is called in order to redraw the screen, either because the user is manipulating 
     * the window, OR because you've called 'requestLayout()'.
     *
     * You will need to modify the last part of this method; specifically the sequence of calls to
     * the other 'draw...()' methods. You shouldn't need to modify anything else about it.
     */
    @Override
    public void layoutChildren()
    {
        super.layoutChildren(); 
        GraphicsContext gfx = canvas.getGraphicsContext2D();
        gfx.clearRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
        
        // First, calculate how big each grid cell should be, in pixels. (We do need to do this
        // every time we repaint the arena, because the size can change.)
        gridSquareSize = Math.min(
            getWidth() / (double) gridWidth,
            getHeight() / (double) gridHeight);
            
        double arenaPixelWidth = gridWidth * gridSquareSize;
        double arenaPixelHeight = gridHeight * gridSquareSize;
            
            
        // Draw the arena grid lines. This may help for debugging purposes, and just generally
        // to see what's going on.
        gfx.setStroke(Color.DARKGREY);
        gfx.strokeRect(0.0, 0.0, arenaPixelWidth - 1.0, arenaPixelHeight - 1.0); // Outer edge

        for(int gridX = 1; gridX < gridWidth; gridX++) // Internal vertical grid lines
        {
            double x = (double) gridX * gridSquareSize;
            gfx.strokeLine(x, 0.0, x, arenaPixelHeight);
        }
        
        for(int gridY = 1; gridY < gridHeight; gridY++) // Internal horizontal grid lines
        {
            double y = (double) gridY * gridSquareSize;
            gfx.strokeLine(0.0, y, arenaPixelWidth, y);
        }

        // Draw as per usual
        if(finish == false)
        {
            drawElements(gfx);
        }

        // Draw 'end' scene
        else
        {
            drawImage(gfx, cross, gridWidth/2, gridHeight/2);
        }
    }
    
    
    /** 
     * Draw an image in a specific grid location. *Only* call this from within layoutChildren(). 
     *
     * Note that the grid location can be fractional, so that (for instance), you can draw an image 
     * at location (3.5,4), and it will appear on the boundary between grid cells (3,4) and (4,4).
     *     
     * You shouldn't need to modify this method.
     */
    private void drawImage(GraphicsContext gfx, Image image, double gridX, double gridY)
    {
        // Get the pixel coordinates representing the centre of where the image is to be drawn. 
        double x = (gridX + 0.5) * gridSquareSize;
        double y = (gridY + 0.5) * gridSquareSize;
        
        // We also need to know how "big" to make the image. The image file has a natural width 
        // and height, but that's not necessarily the size we want to draw it on the screen. We 
        // do, however, want to preserve its aspect ratio.
        double fullSizePixelWidth = robot1.getWidth();
        double fullSizePixelHeight = robot1.getHeight();
        
        double displayedPixelWidth, displayedPixelHeight;
        if(fullSizePixelWidth > fullSizePixelHeight)
        {
            // Here, the image is wider than it is high, so we'll display it such that it's as 
            // wide as a full grid cell, and the height will be set to preserve the aspect 
            // ratio.
            displayedPixelWidth = gridSquareSize;
            displayedPixelHeight = gridSquareSize * fullSizePixelHeight / fullSizePixelWidth;
        }
        else
        {
            // Otherwise, it's the other way around -- full height, and width is set to 
            // preserve the aspect ratio.
            displayedPixelHeight = gridSquareSize;
            displayedPixelWidth = gridSquareSize * fullSizePixelWidth / fullSizePixelHeight;
        }

        // Actually put the image on the screen.
        gfx.drawImage(image,
            x - displayedPixelWidth / 2.0,  // Top-left pixel coordinates.
            y - displayedPixelHeight / 2.0, 
            displayedPixelWidth,              // Size of displayed image.
            displayedPixelHeight);
    }
    
    
    /**
     * Displays a string of text underneath a specific grid location. *Only* call this from within 
     * layoutChildren(). 
     *     
     * You shouldn't need to modify this method.
     */
    private void drawLabel(GraphicsContext gfx, String label, double gridX, double gridY)
    {
        gfx.setTextAlign(TextAlignment.CENTER);
        gfx.setTextBaseline(VPos.TOP);
        gfx.setStroke(Color.BLUE);
        gfx.strokeText(label, (gridX + 0.5) * gridSquareSize, (gridY + 1.0) * gridSquareSize);
    }
    
    /** 
     * George -- Commented out as it results in "UnusedPrivateMethod", and seems
     *           to be never used by provided code.
     * 
     * Draws a (slightly clipped) line between two grid coordinates.
     *     
     * You shouldn't need to modify this method.
     */
    // private void drawLine(GraphicsContext gfx, double gridX1, double gridY1, 
    //                                            double gridX2, double gridY2)
    // {
    //     gfx.setStroke(Color.RED);
        
    //     // Recalculate the starting coordinate to be one unit closer to the destination, so that it
    //     // doesn't overlap with any image appearing in the starting grid cell.
    //     final double radius = 0.5;
    //     double angle = Math.atan2(gridY2 - gridY1, gridX2 - gridX1);
    //     double clippedGridX1 = gridX1 + Math.cos(angle) * radius;
    //     double clippedGridY1 = gridY1 + Math.sin(angle) * radius;
        
    //     gfx.strokeLine((clippedGridX1 + 0.5) * gridSquareSize, 
    //                    (clippedGridY1 + 0.5) * gridSquareSize, 
    //                    (gridX2 + 0.5) * gridSquareSize, 
    //                    (gridY2 + 0.5) * gridSquareSize);
    // }
}
