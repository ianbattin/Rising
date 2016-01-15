package Main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;

import javax.swing.JPanel;

import GameState.GameStateManager;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener
{
	//window size TODO: Allow the user to edit the SCALEWIDTH and SCALEHEIGHT
	public static double scaleWidth = Math.min(((Toolkit.getDefaultToolkit().getScreenSize().height-50.0)/GamePanel.HEIGHT), 1);
	public static double scaleHeight = Math.min(((Toolkit.getDefaultToolkit().getScreenSize().height-50.0)/GamePanel.HEIGHT), 1);
	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;
	public static final int WIDTHSCALED = (int)(WIDTH * scaleWidth);
	public static final int HEIGHTSCALED = (int)(HEIGHT * scaleHeight);
	public static boolean modifiedDimensions = false;
	public static int dimensions;
	
	//run
	private Thread thread;
	private boolean running = false;
	private static long totalTime;
	private static long elapsedTime;
	
	private final int FPS = 60; //game will update 60 times per second
	private static double averageFPS;
	private boolean displayFPS = false;
	
	//image
	private static BufferedImage image;
	private static Graphics2D g;
	
	//game state (Level 1 State, Menu State, etc.)
	private GameStateManager gsm;
	
	//GamePanel constructor
	public GamePanel()
	{
		super(); //used since it extends/implements JPanel, Runnable, and KeyListener so it will be able to access their methods
		setPreferredSize(new Dimension(WIDTHSCALED, HEIGHTSCALED)); //sets the size of the GamePanel
		setFocusable(true); //like when you click the window, allows for key inputs
		requestFocus(); //does the above
	}
	
	//Used to constantly run our game for frames - Runnable
	public void addNotify()
	{
		super.addNotify();
		if(thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}
	}
	
	//initialize the game
	public void init()
	{
		image = new BufferedImage(WIDTHSCALED, HEIGHTSCALED, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics(); //do graphics stuff
		g.scale(scaleWidth, scaleHeight);
		gsm = new GameStateManager(); //create new GameStateManager
		addKeyListener(this); //add the listeners for the mouse & keyboard actions
		addMouseListener(this);
		addMouseMotionListener(this);
		running = true; //game initialized, so running is true
	}
	
	public void run()
	{
		init(); //initialize
		
		long startTime;
		long URDTimeMillis;
		long waitTime;
		totalTime = 0;
		elapsedTime = 0;
		
		int frameCount = 0;
		
		/* Game Loop
		 * Basically what this whole chunk of code does is first,
		 * check the time at the start, then trys to run every
		 * line of code to update/draw and if it does that faster than
		 * 60 FPS, it tells the program to just pause for some time
		 * so we get consistent FPS, or however fast we can get.
		 */
		while(running)
		{
			startTime = System.nanoTime(); //gets current time in nano seconds
			
			//every time this runs, the game will do these three things
			gameUpdate(); //
			gameRender();
			gameDrawToScreen();

			int maxFrameCount = FPS;
			long targetTime = 1000 / FPS;
			URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
			waitTime = targetTime - URDTimeMillis;
			
			try
			{
				if(waitTime < 0) waitTime = 0;
				Thread.sleep(waitTime);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			totalTime += System.nanoTime() - startTime;
			elapsedTime += System.nanoTime() - startTime - elapsedTime;
			frameCount++;
			if(frameCount == maxFrameCount)
			{
				averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000.0);
				frameCount = 0;
				totalTime = 0;
			}
			
			if (modifiedDimensions)
			{
				Main.window.setSize(dimensions, dimensions);
				setPreferredSize(new Dimension(dimensions, dimensions));
				scaleWidth = scaleHeight = dimensions/800.0;
				removeKeyListener(this);
				removeMouseListener(this);
				removeMouseMotionListener(this);
				//SoundPlayer.stopBackgroundMusic();
				init();
				modifiedDimensions = false;
			}
			
		}
	}
	
	//updates from GameStateManager which will then update for each game state
	public void gameUpdate()
	{
		gsm.update();
	}
	
	//draws from GameStateManager which will then draws for each game state
	public void gameRender()
	{
		gsm.draw(g);
	}
	
	//takes the drawing code and actually displays it
	public void gameDrawToScreen()
	{
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0 , 0, WIDTHSCALED, HEIGHTSCALED, null);
		if(displayFPS) g2.drawString("" + (int)getAverageFPS(), 2, 10);
		g2.dispose();
	}

	public static double getAverageFPS()
	{
		return averageFPS;
	}
	
	public static long getTotalTime()
	{
		return totalTime;
	}
	
	public static long getElapsedTime()
	{
		return elapsedTime;
	}
	
	public static BufferedImage getImage()
	{
		return image;
	}
	
	public static void setNewSize(int dimension)
	{
		modifiedDimensions = true;
		dimensions = dimension;
	}

	//processes key presses
	public void keyPressed(KeyEvent key) 
	{
		if(key.getKeyCode() == KeyEvent.VK_F1)
		{
			if(displayFPS) displayFPS = false;
			else displayFPS = true;
		}
		gsm.keyPressed(key.getKeyCode());
	}

	public void keyReleased(KeyEvent key) 
	{
		gsm.keyReleased(key.getKeyCode());
	}

	public void keyTyped(KeyEvent key) 
	{
		
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		gsm.mouseClicked(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
		gsm.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
		gsm.mouseExited(e);
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		gsm.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		gsm.mouseReleased(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		gsm.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
		gsm.mouseMoved(e);
	}
}
