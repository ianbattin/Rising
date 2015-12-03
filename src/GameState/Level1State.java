package GameState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.Object.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

import Entities.Player;
import Entities.Rifleman;
import Entities.Enemy;
import Entities.Jetpacker;
import Entities.Pickups;
import Entities.PlaneBoss;
import Main.GamePanel;
import Main.Main;
import TileMap.Background;
import TileMap.TileMap;


public class Level1State extends PlayState
{
	private int debrisLoop;
	
	private int[][] debrisInfo;
	private ArrayList<Color> colors;
	private boolean start, isStillAlive;
	private float timer;
	public static boolean tileStart;
	private TileMap tileMap;
	
	private boolean transition;
	private double transitionDY;
	
	public boolean hasInited;
	
	public Level1State(GameStateManager gsm)
	{
		this.gsm = gsm;
		start = false;
		try
		{
			bg = new Background("/Backgrounds/battlebackground.gif", 1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		timer = 0;
		//create & stores all the necessary colors (avoid creating too many color objects)
		colors = new ArrayList<Color>();
		for(int i = 25; i < 195; i++)
		{
			colors.add(new Color(i, i, i));
		}
		//create the initial debris
		debrisInfo = new int[200][4];
		for(int i = 0; i < debrisInfo.length; i++)
		{
			for(int j = 0; j < debrisInfo[i].length; j++)
			{
				debrisInfo[i][0] = (int)(Math.random()*GamePanel.WIDTH);
				debrisInfo[i][1] = -2000 + (int)((Math.random()*2000)-1000);
				debrisInfo[i][2] = (int)(Math.random()*5)+2;
				debrisInfo[i][3] = (int)(Math.random()*170);
			}
		}
		tileStart = false;
		bgVectorX = 0;
		bgVectorY = 0;
		debrisVector = 0;
		debrisLoop = 0;
	}

	public void init() 
	{
		tileMap = new TileMap("Resources/Maps/level5.txt");
		player = new Player(tileMap, this);
		player.setPosition(375, -100);
		player.setTileMapMoving(true);
		
		enemies = new ArrayList<Enemy>();
		int[] pickupsToSpawn = {Pickups.BIRDBOOST, Pickups.HEALBOOST, Pickups.GLIDEBOOST};
		pickups = new Pickups(player, tileMap, this, pickupsToSpawn);
		tileStart = false;
		
		hasInited = true;
	}

	public void update()
	{
		if(start)
		{
			bg.update();
			tileMap.update();
			pickups.update();
			player.update();
			for(Enemy e: enemies)
				e.update();
			aimUpdate();
			
			//Camera left and right movement (Player always stays centered)
			tileMap.setXVector(-player.getDX());
		}
		if(player.getPoints() > 1000 && enemies.size() == 0)
		{
			enemies.add(new Jetpacker(-100, 300, tileMap, player));
		}
		if(player.getPlayerHealth() < 1 && timer > 1500000000.0)
		{
			super.isFadingOut = true;
			super.fadeOut(500000000, Color.BLACK, 5, gsm, GameStateManager.LEVEL1STATE, GameStateManager.OUTROSTATE);
		}
		else if (player.getPlayerHealth() < 1)
		{
			timer += GamePanel.getElapsedTime();
		}
		
		if(tileMap.getYMove() >= tileMap.getTileMapHeight() - 700)
		{	
			if(!transition)
			{
				transitionDY = tileMap.getDY();
				transition = true;
			}
			if(transitionDY > 0) transitionDY -= 0.1;
			else
			{
				transitionDY = 0;
				//removes all non-boss entities
				for(int i = 0; i < enemies.size(); i++)
				{
					if(!(enemies.get(i) instanceof PlaneBoss))
					{
						enemies.get(i).playerHurt(50);
					}
				}
				if(enemies.size() <= 1)
				{
					enemies.add(new PlaneBoss(2000, -200, tileMap, player));
				}
			}
			tileMap.setYVector(transitionDY);
			if(player.getY() > GamePanel.HEIGHT)
			{
				player.setPosition(400, 900);
				super.isFadingOut = true;
				super.fadeOut(1000000000.0, Color.WHITE, 20, gsm, GameStateManager.LEVEL1STATE, GameStateManager.BOSS1STATE);
			}
		}
		
		//save data (in this case the points)
		super.data = Integer.toString(player.getPoints());
	}

	public void draw(Graphics2D g)
	{
		bg.draw(g);
		player.draw(g);
		tileMap.draw(g);
		pickups.draw(g);
		for(Enemy e: enemies)
			e.draw(g);
		debris(g);
		
		drawCrossHair(g);
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("RusselSquare", Font.PLAIN, 24));
		if(!start) 
		{
			String[] notStarted = {"PRESS ENTER TO START", "PRESS BACKSPACE TO RETURN TO MENU" };
			for(int i = 0; i < notStarted.length; i++)
				g.drawString(notStarted[i], centerStringX(notStarted[i], 0, GamePanel.WIDTH, g), 400  + (40 * i));
		}
		else
			g.drawString("Score: " + player.getPoints(), centerStringX("Score: " + player.getPoints(), 0, GamePanel.WIDTH, g), 30);
		
		super.drawFade(g);
	}
	
	public Player getPlayer()
	{
		return player;
	}

	//update and draw the debris
	public void debris(Graphics2D g)
	{
		int highestLoc = debrisInfo[0][1];
		for(int i = 0; i < debrisInfo.length; i++)
		{
			for(int j = 0; j < debrisInfo[i].length; j++)
			{
				g.setColor(colors.get(debrisInfo[i][3]));
				g.fillRect(debrisInfo[i][0], debrisInfo[i][1], debrisInfo[i][2], debrisInfo[i][2]);
				if(debrisInfo[i][1] < 0) debrisInfo[i][1] += 2*debrisVector;
				else debrisInfo[i][1] += debrisInfo[i][2]*debrisVector;
				
				if(debrisInfo[i][1] < highestLoc) highestLoc = debrisInfo[i][1];
			}
		}
		if (highestLoc > GamePanel.HEIGHT)
		{
			for(int i = 0; i < debrisInfo.length; i++)
			{
				for(int j = 0; j < debrisInfo[i].length; j++)
				{
			
					debrisInfo[i][0] = (int)(Math.random()*GamePanel.WIDTH);
					debrisInfo[i][1] = -2000 + (int)((Math.random()*2000)-1000);
					debrisInfo[i][2] = (int)(Math.random()*5)+2;
					debrisInfo[i][3] = (int)(Math.random()*170);
				}
			}
		}
	}
	
	public ArrayList<Enemy> getEnemies()
	{
		return enemies;
	}
	
	public void keyPressed(int k) 
	{
		player.keyPressed(k);
		if(k == GameStateManager.select)
		{
			if(!start)
			{
				start = true;
				setBackgroundVector(0, -5.0);
				setDebrisVectors(1);
				if(!tileStart)
				{
					Timer timer = new Timer();
					timer.schedule(new TimerTask()
					{
						public void run()
						{
							tileStart = true;
							tileMap.setYVector(2.0);		
						}
						
					}, 4000);
				}
			}
		}
		if(k == GameStateManager.reset)
		{
			gsm.setState(GameStateManager.MENUSTATE);
			gsm.resetState(GameStateManager.LEVEL1STATE);
		}
		if(k == GameStateManager.pause)
		{
			start = false;
		}
		if(k == KeyEvent.VK_P)
		{
			enemies.add(new PlaneBoss(1000, 100, tileMap, player));
		}
		if(k == KeyEvent.VK_N)
		{
			gsm.setState(GameStateManager.BOSS1STATE);
			gsm.resetState(GameStateManager.LEVEL1STATE);
		}
	}

	public void keyReleased(int k) 
	{
		player.keyReleased(k);
	}
}
