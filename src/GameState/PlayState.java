package GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.Object.*;
import java.util.Timer;
import java.util.TimerTask;

import Entities.Player;
//import Entities.Player;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;


public class PlayState extends GameState
{
	private Background bg;
	private TileMap tileMap;
	Player player;
	private boolean start;
	
	public PlayState(GameStateManager gsm)
	{
		init();
		player.setPosition(300, 700);
		this.gsm = gsm;
		start = false;
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
		player = new Player(tileMap);
	}

	public void update()
	{
		if(start)
		{
			bg.update();
			tileMap.update();
			player.update();
		}
	}

	public void draw(Graphics2D g)
	{
		bg.draw(g);
		tileMap.draw(g);
		player.draw(g);
		g.setColor(Color.WHITE);
		if(!start) 
		{
			String[] notStarted = {"PRESS ENTER TO START", "PRESS BACKSPACE TO RETURN TO MENU" };
			for(int i = 0; i < notStarted.length; i++)
				g.drawString(notStarted[i], GamePanel.centerStringX(notStarted[i], 0, 600), 400  + (40 * i));
		}
	}
	
	public void keyPressed(int k) 
	{
		player.keyPressed(k);
		if(k == GameStateManager.select)
		{
			if(!start)
			{
				start = true;
				bg.setVector(0, -5.0);
				Timer timer = new Timer();
				timer.schedule(new TimerTask()
				{
					public void run()
					{
						tileMap.setVector(0, 1);		
					}
					
				}, 3000);
			}
		}
		if(k == GameStateManager.reset)
		{
			gsm.resetState(GameStateManager.PLAYSTATE);
		}
	}

	public void keyReleased(int k) 
	{
		player.keyReleased(k);
	}
}
