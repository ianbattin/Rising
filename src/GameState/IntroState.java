package GameState;

import java.awt.Graphics2D;

import Main.GamePanel;
import TileMap.Background;


public class IntroState extends GameState {

	private Background bg;
	private GameStateManager gsm;
	
	private float timeKeeper;
	private int currFrame, totalFrames;
	
	public IntroState(GameStateManager gsm)
	{
		this.gsm = gsm;
		
		timeKeeper = 0;
		currFrame = 1; //set the starting frame number
		totalFrames = 4; //set last frame number
		
		try
		{
			bg = new Background("/Intro/frame" + currFrame + ".gif", 1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	//nothing to init
	public void init() 
	{	
		
	}

	//update bgrnd
	public void update() 
	{
		if(timeKeeper > 300000000000.0)
		{
			if (currFrame < totalFrames)
			{
				currFrame++;
				timeKeeper = 0;
				try
				{
					bg.setNewImage("/Intro/frame" + currFrame + ".gif");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				gsm.setCurrentState(GameStateManager.PLAYSTATE);
			}
		}
		else
		{
			timeKeeper += GamePanel.getElapsedTime();
		}
		bg.update();
	}

	public void draw(Graphics2D g) 
	{
		bg.draw(g);
	}

	public void keyPressed(int k) 
	{

	}

	public void keyReleased(int k) 
	{

	}

}
