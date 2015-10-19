package GameState;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics2D;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.awt.Color;

import Main.GamePanel;
import sun.audio.*;

//Every game state will have each of these things. 
//Abstraction makes it more organized
public abstract class GameState
{
	protected GameStateManager gsm;
	protected boolean isFadingOut, isFadingIn;
	protected int alphaLevel;
	private float timeKeeper = 0;
	
	public abstract void init();
	public abstract void update();
	public abstract void draw(Graphics2D g);
	public abstract void keyPressed(int k);
	public abstract void keyReleased(int k);
	
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
	protected void fadeIn(double timeToWait)
	{
		timeKeeper += GamePanel.getElapsedTime();
		
		if(timeKeeper > timeToWait)
		{
			alphaLevel -= 5;
			if (alphaLevel == 0){
				isFadingIn = false;
				timeKeeper = 0; 
			}	
		}
	}
	
	//input: time to wait after animation ends, current gamestatemanager, state that needs reset, and state to initiate
	protected void fadeOut(double timeToWait, GameStateManager currGsm, int stateToReset, int stateToSet)
	{	
		if (alphaLevel < 255){
			alphaLevel += 5;
		} 
		else
		{
			timeKeeper += GamePanel.getElapsedTime();
		}
		
		if(timeKeeper > timeToWait)
		{
			isFadingOut = false;
			currGsm.resetState(stateToReset);
			currGsm.setState(stateToSet);
		}
	 }
	
	//draws the rectangle on top of the game to make the fading appearance
	protected void drawFade(Graphics2D g)
	{
		g.setColor(new Color(0, 0, 0, alphaLevel));
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
	}
	
		
	
	public void music() 
    {       
        AudioPlayer MGP = AudioPlayer.player;
        AudioStream BGM;
        AudioData MD;

        ContinuousAudioDataStream loop = null;

        try
        {
            InputStream music = new FileInputStream("Resources/Sound/Cybernator_-_Fully_Set_Up_For_Penetration.wav");
            BGM = new AudioStream(music);
            AudioPlayer.player.start(BGM);
            MD = BGM.getData();
            loop = new ContinuousAudioDataStream(MD);
        }
        catch(FileNotFoundException e){
            System.out.print(e.toString());
        }
        catch(IOException error)
        {
            System.out.print(error.toString());
        }
        MGP.start(loop);
    }
	
	public void playSound(String fileName)
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
