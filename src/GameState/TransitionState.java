package GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import Main.GamePanel;
import TileMap.Background;


public class TransitionState extends PlayState {

	private String path;
	
	private float timer;
	private int currFrame, totalFrames;//, alphaLevel;
	
	public TransitionState(GameStateManager gsm, String path)
	{
		this.gsm = gsm;
		this.path = path;
		
		timer = 0;
		currFrame = 1; //set the starting frame number.

		switch(path)
		{
			case "Intro":
				totalFrames = 8;
				break;
			case "Outro":
				totalFrames = 1;
				break;
		}
		
		super.isFadingOut = false;
		super.isFadingIn = true;
		super.alphaLevel = 255;
		
		try
		{
			bg = new Background("/" + path + "/frame" + currFrame + ".gif", 1);
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
		if(super.isFadingIn)
		{
			super.fadeIn(500000000.0, Color.BLACK, 5);
		}
		else if (super.isFadingOut)
		{
			switch(path)
			{
				case "Intro":
					super.fadeOut(1000000000.0, Color.BLACK, 5, gsm, GameStateManager.INTROSTATE, GameStateManager.LEVEL1STATE);
					break;
				case "Outro":
					super.fadeOut(1000000000.0, Color.BLACK, 5, gsm, GameStateManager.TRANSITION1STATE, GameStateManager.BOSS1STATE);
			}
			
		}
		else
		{
			if(timer > 7000000000.0)
			{
				timer = 0;
				if (currFrame < totalFrames)
				{
					currFrame++;
					bg.setNewImage("/" + path + "/frame" + currFrame + ".gif");
				}
				else
				{
					super.isFadingOut = true;
				}
			}
			timer += GamePanel.getElapsedTime();
			bg.update();
		}
	}

	public void draw(Graphics2D g) 
	{
		bg.draw(g);
		super.drawFade(g);
	}
	
	public void keyPressed(int k) 
	{
		if(k == GameStateManager.select)
		{
			gsm.setState(GameStateManager.LEVEL1STATE);
			gsm.resetState(GameStateManager.INTROSTATE);
		} 
		else if (k == GameStateManager.reset)
		{
			gsm.setState(GameStateManager.MENUSTATE);
			gsm.resetState(GameStateManager.INTROSTATE);
		}
	}

	public void keyReleased(int k) 
	{
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
