package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.Object.*;
import java.util.Timer;
import java.util.TimerTask;

import Entities.Player;
import Entities.Pickups;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;


public class PlayState extends GameState
{
	private Background bg;
	private TileMap tileMap;
	private Player player;
	private Pickups pickups;
	private boolean start;
	public static boolean tileStart;
	
	public PlayState(GameStateManager gsm)
	{
		init();
		player.setPosition(300, -100);
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
		tileMap = new TileMap("Resources/Maps/level2.txt");
		player = new Player(tileMap);
		pickups = new Pickups(player);
		tileStart = false;
	}

	public void update()
	{
		if(start)
		{
			bg.update();
			tileMap.update();
			pickups.update();
			player.update();
		}
	}

	public void draw(Graphics2D g)
	{
		bg.draw(g);
		tileMap.draw(g);
		pickups.draw(g);
		player.draw(g);
		g.setColor(Color.WHITE);
		g.setFont(new Font("RusselSquare", Font.PLAIN, 24));
		if(!start) 
		{
			String[] notStarted = {"PRESS ENTER TO START", "PRESS BACKSPACE TO RETURN TO MENU" };
			for(int i = 0; i < notStarted.length; i++)
				g.drawString(notStarted[i], centerStringX(notStarted[i], 0, 600, g), 400  + (40 * i));
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
						tileStart = true;
						tileMap.setVector(0, 2.0);		
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
