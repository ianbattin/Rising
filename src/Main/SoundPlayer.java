package Main;

import javafx.scene.media.*;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.sound.sampled.*;


public class SoundPlayer implements LineListener, Runnable
{	
	//How many bytes are being put in the playback buffer (for the background music)
	private final static int BUFFER_SIZE = 2048;
	//Indicates whether another instance of SoundPlayer is playing the background sound. If set to false, it will cease playback in other instances
	private static boolean IS_PLAYING, IS_FADING_OUT;
	private static SourceDataLine AUDIO_LINE;
	
	private static FloatControl volumeControl;
	
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
				shootingClip = new AudioClip(SoundPlayer.class.getResource("/Sound/shoot.wav").toExternalForm());
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
			new AudioClip(SoundPlayer.class.getResource("/Sound/" + fileName).toExternalForm()).play();
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
			
			AudioClip clip = new AudioClip(SoundPlayer.class.getResource("/Sound/" + fileName).toExternalForm());
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
		this.backgroundFileName = "/Sound/" + fileName;
		this.willLoopBackgroundMusic = willLoop;

		if(IS_PLAYING)
		{
			stopBackgroundMusic();
		}
		IS_FADING_OUT = false;
		
		backgroundPlaybackThread = new Thread(this);
		backgroundPlaybackThread.start();
	}
	
	/**
	 * Stops the current background music playback
	 */
	public void stopBackgroundMusic()
	{
		IS_PLAYING = false;
		IS_FADING_OUT = false;
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
			while(IS_PLAYING)
			{
				InputStream mediaFile = new BufferedInputStream(SoundPlayer.class.getResourceAsStream(backgroundFileName));
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(mediaFile);
				AudioFormat audioFormat = audioStream.getFormat();
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
				SoundPlayer.AUDIO_LINE = (SourceDataLine) AudioSystem.getLine(info);
				SoundPlayer.AUDIO_LINE.open(audioFormat);
				SoundPlayer.AUDIO_LINE.start();
				
				volumeControl = (FloatControl)SoundPlayer.AUDIO_LINE.getControl(FloatControl.Type.MASTER_GAIN);
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
		
	public static void setVolume(float value)
	{
		if(volumeControl != null)
		{
			volumeControl.setValue(value);
			IS_FADING_OUT = false;
		}
	}
	
	/**
	 * Animate the volume of the music over time.
	 * 
	 * @param end final value of the sound after the animation.
	 */
	public static void animVolume(final float end)
	{
		if(volumeControl != null)
		{
			IS_FADING_OUT = true;
			new Thread(new Runnable() {
			    public void run() {
			        while(volumeControl.getValue() > end && IS_FADING_OUT)
			        {
			        	volumeControl.setValue(volumeControl.getValue() - 0.1f);
			        	try
			        	{
			        		Thread.sleep(25);
			        	}
			        	catch(Exception e)
			        	{
			        		e.printStackTrace();
			        	}
			        }
			    }
			}).start();
			//volumeControl.shift(0, end, millis);
		}
	}
}