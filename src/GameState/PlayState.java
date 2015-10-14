package GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.Object.*;

//import Entities.Player;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;


public class PlayState extends GameState
{
	private Background bg;
	private TileMap tileMap;
	private boolean start = false;
	
	public PlayState(GameStateManager gsm)
	{
		init();
		this.gsm = gsm;
		try
		{
			bg = new Background("/Backgrounds/menubackground.gif", 1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void init() 
	{
		tileMap = new TileMap("level1.txt");
	}

	public void update()
	{
		if(start)
		{
			bg.update();
			tileMap.update();
		}
	}

	public void draw(Graphics2D g)
	{
		bg.draw(g);
		tileMap.draw(g);
		g.setColor(Color.WHITE);
		if(!start) g.drawString("PRESS ENTER TO START", GamePanel.centerStringX("PRESS ENTER TO START", 0, 600), 400);
	}
	
	public void keyPressed(int k) 
	{
		if(k == KeyEvent.VK_ENTER)
		{
			if(!start)
			{
				start = true;
				tileMap.setVector(0, 1);
				bg.setVector(0, -5.0);
			}
		}
	}

	public void keyReleased(int k) 
	{
		//player.keyReleased(k);
	}
}
