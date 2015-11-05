package GameState;

import java.awt.Graphics2D;

import Main.GamePanel;
import TileMap.Background;

public class OutroState extends GameState{

	private Background bg;
	private GameStateManager gsm;
	
	private float timer;
	
	public OutroState(GameStateManager gsm)
	{
		this.gsm = gsm;
		
		bg = new Background("/Outro/gameOver.gif", 1);
	}
	
	@Override
	public void init() 
	{
		super.isFadingIn = true;
		super.alphaLevel = 255;
		
		timer = 0;
	}

	@Override
	public void update() 
	{
		if(super.isFadingIn)
		{
			super.fadeIn(500000000.0);
		}
		else if (timer > 1000000000)
		{
			super.fadeOut(1000000000.0, gsm, GameStateManager.OUTROSTATE, GameStateManager.MENUSTATE);
		}
		else
		{
			timer += GamePanel.getElapsedTime();
		}
	}

	@Override
	public void draw(Graphics2D g) 
	{
		bg.draw(g);
		super.drawFade(g);
	}

	@Override
	public void keyPressed(int k) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(int k) 
	{
		// TODO Auto-generated method stub
		
	}

}
