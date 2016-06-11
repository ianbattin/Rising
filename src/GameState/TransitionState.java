package GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import Main.GamePanel;
import Main.SoundPlayer;
import TileMap.Background;


public class TransitionState extends GameState {

	public String path;
	
	private long timer, coolDownTimer;
	private int currFrame, totalFrames;
	
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
				timeModifierToUse = new int[] { 5, 7, 5, 5, 7, 5, 4, 5, 6 };
				totalFrames = 9;
				break;
			case "Interlude":
<<<<<<< HEAD
				timeModifierToUse = new int[] { 1, 4, 4, 4, 4 };
=======
				timeModifierToUse = new int[] { 1, 5, 4, 5, 7 };
>>>>>>> origin/master
				totalFrames = 5;
				break;
			case "Outro":
				timeModifierToUse = new int[] { 3, 3, 3, 3 };
				totalFrames = 1;
				break;
			case "WinOutro":
				timeModifierToUse = new int[] { 1, 8};
				totalFrames = 2;
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
		switch(path)
		{
			case "Intro":
				//start the game music
				music("Prelude.wav");
				break;
			case "Interlude":
				music("Prelude2.wav");
				break;
		}
	}

	//update bgrnd
	public void update() 
	{		
		if(super.isFadingIn)
		{
			//super.fadeIn(500000000.0, Color.BLACK, 5);
			
			switch(path)
			{
				case "Intro":
					super.fadeIn(500000000.0, Color.BLACK, 5);
					break;
				case "Interlude":
					super.fadeIn(500000000.0, Color.WHITE, 5);
					break;
				case "Outro":
					super.fadeIn(500000000.0, Color.BLACK, 5);
					break;
				case "WinOutro":
					super.fadeIn(500000000.0, Color.BLACK, 5);
					break;
			}
		}
		else if (super.isFadingOut)
		{
			switch(path)
			{
				case "Intro":
					super.fadeOut(1000000000.0, Color.BLACK, 5, gsm, GameStateManager.INTROSTATE, GameStateManager.LEVEL1STATE);
					break;
				case "Interlude":
					super.fadeOut(1000000000.0, Color.WHITE, 5, gsm, GameStateManager.TRANSITION_INTERLUDESTATE1, GameStateManager.BOSS1STATE);
					break;
			}
		}
		else
		{
			if(timer > (timeModifierToUse[currFrame-1]*1000000000.0))
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
					SoundPlayer.animVolume(-40.0F);
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
		if(this.path.equals("Intro"))
		{
			if(k == GameStateManager.select)
			{ 
				if(System.currentTimeMillis() - coolDownTimer < 250)
				{
					gsm.setState(GameStateManager.LEVEL1STATE);
					gsm.resetState(GameStateManager.INTROSTATE);
				}
				if(currFrame < totalFrames)
				{
					timer += timeModifierToUse[currFrame-1]*1000000000.0;
				}
				else
				{
					gsm.setState(GameStateManager.LEVEL1STATE);
					gsm.resetState(GameStateManager.INTROSTATE);
				}
				coolDownTimer = System.currentTimeMillis();
			} 
			if (k == GameStateManager.reset)
			{
				gsm.setState(GameStateManager.MENUSTATE);
				gsm.resetState(GameStateManager.INTROSTATE);
			}
		}
		else if(this.path.equals("Interlude"))
		{
			if(k == GameStateManager.select)
			{ 
				if(System.currentTimeMillis() - coolDownTimer < 250)
				{
					gsm.setState(GameStateManager.BOSS1STATE);
					gsm.resetState(GameStateManager.TRANSITION_INTERLUDESTATE1);
				}
				if(currFrame < totalFrames)
				{
					timer += timeModifierToUse[currFrame-1]*1000000000.0;
				}
				else
				{
					gsm.setState(GameStateManager.BOSS1STATE);
					gsm.resetState(GameStateManager.TRANSITION_INTERLUDESTATE1);
				}
				coolDownTimer = System.currentTimeMillis();
			} 
			if (k == GameStateManager.reset)
			{
				gsm.setState(GameStateManager.MENUSTATE);
				gsm.resetState(GameStateManager.TRANSITION_INTERLUDESTATE1);
			}
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
