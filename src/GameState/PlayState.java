package GameState;

import java.awt.Graphics2D;
import java.io.File;
import java.lang.Object.*;

//import Entities.Player;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;

public class PlayState extends GameState
{
	private Background bg;
	public TileMap tileMap;
	public int scroll;
	
	public PlayState(GameStateManager gsm)
	{
		init();
		this.gsm = gsm;
		try
		{
			bg = new Background("/Backgrounds/menubackground.gif", 0.0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void init() 
	{
		tileMap = new TileMap("level1.txt", 10);
		//player = new Player(tileMap);
		//player.setPosition(GamePanel.WIDTH/2, GamePanel.HEIGHT/2);
	}

	public void update()
	{
		tileMap.update();
		//player.update();
		//bg.setVector(player.getX()*100, 0);
		bg.update();
	}

	public void draw(Graphics2D g)
	{
		bg.draw(g);
		tileMap.draw(g);
		//player.draw(g);
	}
	
	public void keyPressed(int k) 
	{
		//player.keyPressed(k);
	}

	public void keyReleased(int k) 
	{
		//player.keyReleased(k);
	}
}
