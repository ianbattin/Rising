package GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import Main.GamePanel;
import TileMap.Background;


public class TransitionState extends GameState {

	private String path;
	
	//private static int numberOfFrames;
	
	private long timer, coolDownTimer;
	private int currFrame, totalFrames;

	//these are the arrays that we can use to modify the time for each of the intro frames (each integer value = 1 second). 
	//The arrays must be the length of the total frames
	private final int[] secondsToAdd_IntroFrames = { 4, 14, 3, 4, 4, 4, 4 };
	private final int[] secondsToAdd_OutroFrames = { 4, 4, 4, 4 };
	private int[] timeModifierToUse;
	
	public TransitionState(GameStateManager gsm, String path)
	{
		super();
		this.gsm = gsm;
		this.path = path;
		
		coolDownTimer = 0;
		timer = 0;
		currFrame = 1; //set the starting frame number.

		switch(path)
		{
			case "Intro":
				timeModifierToUse = secondsToAdd_IntroFrames;
				totalFrames = 7;
				break;
			case "Outro":
				timeModifierToUse = secondsToAdd_OutroFrames;
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
					super.fadeOut(1000000000.0, Color.BLACK, 5, gsm, GameStateManager.TRANSITION_INTROSTATE, GameStateManager.LEVEL1STATE);
					break;
				case "Outro":
					super.fadeOut(1000000000.0, Color.BLACK, 5, gsm, GameStateManager.TRANSITION_OUTROSTATE, GameStateManager.MENUSTATE);
					currFrame = 1;
					break;
			}
		}
		else
		{
			if(timer > (timeModifierToUse[currFrame-1]*1000000000.0))
			{
				timer = 0;
				System.out.println(currFrame + "  Total:   " + totalFrames);
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
			if(System.currentTimeMillis() - coolDownTimer < 250)
			{
				gsm.setState(GameStateManager.LEVEL1STATE);
				gsm.resetState(GameStateManager.TRANSITION_INTROSTATE);
			}
			if(currFrame < totalFrames)
			{
				timer += timeModifierToUse[currFrame-1]*1000000000.0;
			}
			else
			{
				gsm.setState(GameStateManager.LEVEL1STATE);
				gsm.resetState(GameStateManager.TRANSITION_INTROSTATE);
			}
			coolDownTimer = System.currentTimeMillis();
		} 
		else if (k == GameStateManager.reset)
		{
			gsm.setState(GameStateManager.MENUSTATE);
			gsm.resetState(GameStateManager.TRANSITION_INTROSTATE);
		}
	}
	
	public void setTotalFrames(int num)
	{
		this.totalFrames = num;
		System.out.println("New total: " + this.totalFrames);
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
