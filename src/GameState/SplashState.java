package GameState;

import java.awt.Graphics2D;

import Main.GamePanel;
import TileMap.Background;

public class SplashState extends GameState 
{
	private Background bg;
	long elapsedTime;
	
	public SplashState(GameStateManager gsm)
	{
		init();
		this.gsm = gsm;
		
		elapsedTime = 0;
		try
		{
			bg = new Background("/Backgrounds/splashscreen.jpg", 1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void init() 
	{
		music();
	}


	public void update() 
	{
		elapsedTime += GamePanel.getElapsedTime();
		if(elapsedTime < 800000000.0) 
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
