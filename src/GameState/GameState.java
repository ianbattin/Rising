package GameState;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Entities.Enemy;
import Entities.Player;

import java.awt.Color;
import java.awt.Font;

import Main.GamePanel;
import Main.SoundPlayer;
import TileMap.Background;

//Every game state will have each of these things. 
//Abstraction makes it more organized
public abstract class GameState
{
	protected GameStateManager gsm;
	protected Background bg;
	protected SoundPlayer soundPlayer;
	
	protected boolean isFadingOut, isFadingIn;
	protected int alphaLevel;
	protected float timeKeeper = 0;
	private int fadeRed, fadeBlue, fadeGreen;
	protected static int score; 
	
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
	
	public GameState()
	{
		try
		{
			InputStream myStream = new BufferedInputStream(new FileInputStream("Resources/Text/Fonts/Munro.ttf"));
		    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, myStream));
		} 
		catch (Exception e) 
		{
		     e.printStackTrace();
		}
		soundPlayer = new SoundPlayer();
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
		
		if(timeToWait > timeKeeper)
		{
			timeKeeper += GamePanel.getElapsedTime();
		}
		else
		{
			alphaLevel -= speed;
			if (alphaLevel <= 0)
			{
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
		
		if (alphaLevel+speed < 255)
		{
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
	
	public Background getBackground()
	{
		return bg;
	}
	
	public void music(String fileName) 
    {   
		soundPlayer.startBackgroundMusic(fileName, true);
		/*
		SoundPlayer sound = new SoundPlayer();
		
		AudioPlayer mediaPlayer = AudioPlayer.player;
        AudioStream mediaStream;
        AudioDataStream mediaData;
        
        try
        {
            InputStream music = new FileInputStream(fileName);
            mediaStream = new AudioStream(music);
            
            mediaPlayer.start(mediaStream); 
        }
        catch(FileNotFoundException e)
        {
            System.out.print(e.toString());
        }
        catch(IOException e)
        {
            System.out.print(e.toString());
        }
        */
    }
	
	/*public void playSound(String fileName)
	{
		SoundPlayer.playClip(fileName);
	}
	*/
	
	public static <E> boolean containsInstance(List<E> list, Class<? extends E> clazz) {
	    for (E e : list) {
	        if (clazz.isInstance(e)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void setScore(int i)
	{
		score = i;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public static BufferedImage rotateImage(BufferedImage image, int angle)
	{
		AffineTransform tx = new AffineTransform();
		tx.rotate(Math.toRadians(angle-65), image.getWidth() / 2, image.getHeight() / 2);

		AffineTransformOp op = new AffineTransformOp(tx,
				AffineTransformOp.TYPE_BILINEAR);
		image = op.filter(image, null);
		
		return image;
	}
}
