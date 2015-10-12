package GameState;

import java.awt.Graphics2D;

import Main.GamePanel;
import TileMap.Background;

public class SplashState extends GameState 
{
	private Background bg;
	long elapsedTime = 0;
	
	public SplashState(GameStateManager gsm)
	{
		this.gsm = gsm;
		try
		{
			bg = new Background("/BackGrounds/splashscreen.jpg", 1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void init() 
	{
		
	}


	public void update() 
	{
		
		if(GamePanel.getElapsedTime() < 800000000.0) 
		{
			bg.update(); 
		}
		else
			gsm.setState(GameStateManager.MENUSTATE);
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
