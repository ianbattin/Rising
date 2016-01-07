package Main;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.URL;

import javax.sound.sampled.*;


public class SoundPlayer implements LineListener, Runnable
{	
	//How many bytes are being read ahead (for the background music)
	private final static int BUFFER_SIZE = 8192;
	//Indicates whether another instance of SoundPlayer is playing the background sound. If set to false, it will cease playback in other instances
	private static boolean IS_PLAYING;
	
	//preload the most used sounds
	private static AudioClip shootingClip;
	
	//thread that handles the background playback
	private Thread backgroundPlaybackThread;
	
	//background playback information
	private String backgroundFileName;
	private boolean willLoopBackgroundMusic;

	
	/**
	 * Initiates the SoundPlayer Object. 
	 */
	public SoundPlayer()
	{
	}
	
	/**
	 * To be used for playing the shooting noise; this is separate of the playClip for optimization reasons
	 */
	public static void playShootingClip()
	{
		if(shootingClip == null)
		{
			try
			{
				shootingClip = Applet.newAudioClip(new URL("file:Resources/Sound/shoot.wav"));
			}
			catch (Exception e)
			{
				System.out.println(e.toString());
			}
		}
		else
		{
			shootingClip.play();
		}
	}
	
	/**
	 * To be used to play short sound clips. This method is static, and does not need a SoundPlayer object.
	 * 
	 * @param fileName String representation of the file name (located in the Resources/Sound folder)
	 */
	public static void playClip(String fileName)
	{
		try
		{
			Applet.newAudioClip(new URL("file:Resources/Sound/" + fileName)).play();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	/**
	 * To be used to play longer music, such as background theme songs, or other longer songs. This spawns a new thread to play in the background.
	 * Note that only one of these background music may be playing at a given instance.
	 * 
	 * @param fileName String representation of the file name (located in the Resources/Sound folder)
	 * @param willLoop Boolean indicating whether or not the music should be looped when it ends playback
	 */
	public void startBackgroundMusic(String fileName, boolean willLoop)
	{
		this.backgroundFileName = "Resources/Sound/" + fileName;
		this.willLoopBackgroundMusic = willLoop;

		if(IS_PLAYING)
		{
			stopBackgroundMusic();
		}
		backgroundPlaybackThread = new Thread(this);
		backgroundPlaybackThread.start();
	}
	
	
	/**
	 * Stops the current background music playback
	 */
	public void stopBackgroundMusic()
	{
		IS_PLAYING = false;
	}

	
	/**
	 * This is the run method for the background playback thread
	 */
	public void run()
	{
		IS_PLAYING = true;
		try
		{
			File mediaFile = new File(this.backgroundFileName);
			while(IS_PLAYING)
			{
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(mediaFile);
				AudioFormat audioFormat = audioStream.getFormat();
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
				SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
				
				audioLine.open(audioFormat);
				audioLine.start();
				
				byte[] bytesBuffer = new byte[BUFFER_SIZE];
				int bytesRead = -1;
				
				while((bytesRead = audioStream.read(bytesBuffer)) != -1 && IS_PLAYING)
				{
					audioLine.write(bytesBuffer, 0, bytesRead);
				}
				
				audioLine.drain();
				audioLine.close();
				audioStream.close();
				
				if(!this.willLoopBackgroundMusic)
					IS_PLAYING = false;
			}
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
		IS_PLAYING = false;
	}
	
	
	/**
	 * Listens to start and end calls of the LineListener.
	 * NOTE: not currently used, LineEvents are not currently being monitored (they are printed out though)
	 * 
	 * @param event The LineEvent event object being passed.
	 */
	public void update(LineEvent event) 
	{
		System.out.println(event.toString());
	}
	
}