package GameState;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import Entities.Enemy;
import Entities.Player;

import java.awt.Color;

import Main.GamePanel;
import TileMap.Background;
import sun.audio.*;

//Every game state will have each of these things. 
//Abstraction makes it more organized
public abstract class GameState
{
	protected GameStateManager gsm;
	protected Background bg;
	
	//store data from each state
	protected String data;
	protected boolean isFadingOut, isFadingIn;
	protected int alphaLevel;
	protected float timeKeeper = 0;
	private int fadeRed, fadeBlue, fadeGreen;
	
	public abstract void init();
	public abstract void update();
	public abstract void draw(Graphics2D g);
	public abstract void keyPressed(int k);
	public abstract void keyReleased(int k);
	public abstract void mouseClicked(MouseEvent e);
	public abstract void mouseEntered(MouseEvent e);
	public abstract void mouseExited(MouseEvent e);
	public abstract void mousePressed(MouseEvent e);
	public abstract void mouseReleased(MouseEvent e);
	public abstract void mouseDragged(MouseEvent e);
	public abstract void mouseMoved(MouseEvent e);
	
	//return the saved data
	public String saveState()
	{
		return data;
	}
		
	//Centers string between the xPos and endPos x coordinates
	public static int centerStringX(String s, int xPos, int endPos, Graphics2D g)
	{
        int stringLen = (int)g.getFontMetrics().getStringBounds(s, g).getWidth();
        int width = endPos - xPos;
        int start = width/2 - stringLen/2;
        return start + xPos;
	}
	
	//Fading methods
	//input: time before the animation starts
	protected void fadeIn(double timeToWait, Color myColor, int speed)
	{
		fadeRed = myColor.getRed();
		fadeGreen = myColor.getGreen();
		fadeBlue = myColor.getBlue();
		
		timeKeeper += GamePanel.getElapsedTime();
		
		if(timeKeeper > timeToWait)
		{
			alphaLevel -= speed;
			if (alphaLevel <= 0){
				alphaLevel = 0;
				isFadingIn = false;
				timeKeeper = 0; 
			}	
		}
	}
	
	//input: time to wait after animation ends, current gamestatemanager, state that needs reset, and state to initiate
	protected void fadeOut(double timeToWait, Color myColor, int speed, GameStateManager currGsm, int stateToReset, int stateToSet)
	{	
		fadeRed = myColor.getRed();
		fadeGreen = myColor.getGreen();
		fadeBlue = myColor.getBlue();
		if (alphaLevel+speed < 255){
			alphaLevel += speed;
		} 
		else
		{
			alphaLevel = 255;
			timeKeeper += GamePanel.getElapsedTime();
		}
		
		if(timeKeeper > timeToWait)
		{
			isFadingOut = false;
			currGsm.setState(stateToSet);
			currGsm.resetState(stateToReset);
		}
	 }
	
	//draws the rectangle on top of the game to make the fading appearance
	protected void drawFade(Graphics2D g)
	{
		if(isFadingIn || isFadingOut)
		{
			g.setColor(new Color(fadeRed, fadeGreen, fadeBlue, alphaLevel));
			g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		}
	}
		
	
	public void music(String fileName) 
    {       
        AudioPlayer MGP = AudioPlayer.player;
        AudioStream BGM;
        AudioData MD;

        ContinuousAudioDataStream loop = null;

        try
        {
            InputStream music = new FileInputStream(fileName);
            BGM = new AudioStream(music);
            AudioPlayer.player.start(BGM);
            MD = BGM.getData();
            loop = new ContinuousAudioDataStream(MD);
        }
        catch(FileNotFoundException e)
        {
            System.out.print(e.toString());
        }
        catch(IOException error)
        {
            System.out.print(error.toString());
        }
        MGP.start(loop);
    }
	
	//getting an error on eclipse here (but not other machines... its weird). setting final to the parameter should solve the error. (runs the same)
	public void playSound(final String fileName)
	{
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{
				try 
				{
					AudioClip clip = Applet.newAudioClip(new URL("file:Resources/Sound/" + fileName));
					clip.play();
				} 
				catch (MalformedURLException murle)
				{
					System.out.println(murle);
				}
			}
		});
		thread.start();
	}
}
