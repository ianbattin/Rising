package Main;

import javafx.scene.media.*;
import java.io.File;

import javax.sound.sampled.*;


public class SoundPlayer implements LineListener, Runnable
{	
	//How many bytes are being put in the playback buffer (for the background music)
	private final static int BUFFER_SIZE = 2048;
	//Indicates whether another instance of SoundPlayer is playing the background sound. If set to false, it will cease playback in other instances
	private static boolean IS_PLAYING;
	private static SourceDataLine AUDIO_LINE;
	
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
	{}
	
	/**
	 * To be used for playing the shooting noise; this is separate of the playClip for optimization reasons
	 */
	public static void playShootingClip()
	{
		if(shootingClip == null)
		{
			try
			{
				shootingClip = new AudioClip("file:Resources/Sound/shoot.wav");
			}
			catch (Exception e)
			{
				System.out.println(e.toString());
			}
		}
		shootingClip.play();
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
			new AudioClip("file:Resources/Sound/" + fileName).play();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	/**
	 * To be used to play short sound clips. This method is static, and does not need a SoundPlayer object.
	 * Allows for a clip to be played at a specified volume
	 * 
	 * @param fileName String representation of the file name (located in the Resources/Sound folder)
	 * @param volume Double representation of the volume the clip is to be played at (volume <= 1.0 && volume >= 0.0)
	 */
	public static void playClipWithVolume(String fileName, double volume)
	{
		try
		{
			AudioClip clip = new AudioClip("file:Resources/Sound/" + fileName);
			clip.setVolume(volume);
			clip.play();
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
		SoundPlayer.AUDIO_LINE.drain();
		SoundPlayer.AUDIO_LINE.close();
		
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
				SoundPlayer.AUDIO_LINE = (SourceDataLine) AudioSystem.getLine(info);
				SoundPlayer.AUDIO_LINE.open(audioFormat);
				SoundPlayer.AUDIO_LINE.start();
				
				byte[] bytesBuffer = new byte[BUFFER_SIZE];
				int bytesRead = -1;
				
				while((bytesRead = audioStream.read(bytesBuffer)) != -1 && IS_PLAYING)
				{
					SoundPlayer.AUDIO_LINE.write(bytesBuffer, 0, bytesRead);
				}
				
				if(!this.willLoopBackgroundMusic)
				{
					IS_PLAYING = false;
				}
				
				SoundPlayer.AUDIO_LINE.drain();
				SoundPlayer.AUDIO_LINE.close();
				audioStream.close();
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