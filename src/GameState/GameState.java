package GameState;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics2D;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import Main.GamePanel;
import sun.audio.*;

//Every game state will have each of these things. 
//Abstraction makes it more organized
public abstract class GameState
{
	protected GameStateManager gsm;
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
	
	public void music() 
    {       
        AudioPlayer MGP = AudioPlayer.player;
        AudioStream BGM;
        AudioData MD;

        ContinuousAudioDataStream loop = null;

        try
        {
            InputStream test = new FileInputStream("Resources/Sound/Cybernator_-_Fully_Set_Up_For_Penetration.wav");
            BGM = new AudioStream(test);
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
					System.out.println("SOUND");
					AudioClip clip = Applet.newAudioClip(new URL("file:Resources/Sound/" + fileName));
					System.out.println("SOUND");
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
