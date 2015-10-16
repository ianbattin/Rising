package GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import Main.GamePanel;
import TileMap.Background;


public class IntroState extends GameState {

	private Background bg;
	private GameStateManager gsm;
	
	private float timeKeeper, initTime;
	private int currFrame, totalFrames, alphaLevel, alphaUpdateSpeed;
	private boolean isFadingIn, isFadingOut;

	
	public IntroState(GameStateManager gsm)
	{
		this.gsm = gsm;
		
		initTime = timeKeeper = 0;
		currFrame = 1; //set the starting frame number.
		totalFrames = 4; //set last frame number
		
		isFadingOut = false;
		isFadingIn = true;
		alphaLevel = 255;
		
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
		timeKeeper += GamePanel.getElapsedTime();
					
		if(isFadingIn)
		{
			if(timeKeeper > 500000000.0)
			{
				alphaLevel -= 5;
				if (alphaLevel == 0){
					isFadingIn = false;
					timeKeeper = 0; 
				}	
			}
		}
		else if (isFadingOut)
		{
			if (alphaLevel < 255){
				alphaLevel += 5;
				timeKeeper = 0;
			} 
			else if(timeKeeper > 1000000000.0)
			{
				isFadingOut = false;
				gsm.resetState(GameStateManager.INTROSTATE);
				gsm.setState(GameStateManager.PLAYSTATE);

			}
		}
		else if(timeKeeper > 2000000000.0)
		{
			timeKeeper = 0;
			if (currFrame < totalFrames)
			{
				currFrame++;
				initTime = GamePanel.getElapsedTime();
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
				isFadingOut = true;
			}
		}
		bg.update();
	}

	public void draw(Graphics2D g) 
	{
		bg.draw(g);
		
		g.setColor(new Color(0, 0, 0, alphaLevel));
		g.fillRect(0, 0, 600, 800);
	}
	
	public void keyPressed(int k) 
	{
		if(k == GameStateManager.select)
		{
			gsm.resetState(GameStateManager.INTROSTATE);
			gsm.setState(GameStateManager.PLAYSTATE);
		} 
		else if (k == GameStateManager.reset)
		{
			gsm.resetState(GameStateManager.INTROSTATE);
			gsm.setState(GameStateManager.MENUSTATE);
		}
	}

	public void keyReleased(int k) 
	{
	}

}
