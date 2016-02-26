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
import java.util.Iterator;

import Entities.Player;
import Entities.Rifleman;
import Entities.Walker;
import Entities.SmallStuka;
import Entities.Bomb;
import Entities.Enemy;
import Entities.Explosion;
import Entities.Jetpacker;
import Entities.MapObject;
import Entities.Pickups;
import Entities.PlaneBoss;
import Main.GamePanel;
import Main.Main;
import TileMap.Background;
import TileMap.TileMap;


public class Level1State extends PlayState
{
	private int[][] debrisInfo;
	private int[][] smallDebrisInfo;
	private ArrayList<Color> colors;
	private ArrayList<int[]> bonusScores;
	private boolean isStillAlive;
	private float timer;
	public static boolean tileStart, debrisAlternator;
	private boolean transition;
	private double transitionDY;
	public boolean hasInited;
	
	public SmallStuka stuka;
	public boolean stukaSpawned = false;
	
	public int bossHeight = 7000;
	
	public Level1State(GameStateManager gsm)
	{
		super();
		this.gsm = gsm;
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
		for(int i = 65; i < 195; i++)
		{
			colors.add(new Color(i, i, i));
		}
		//create the initial debris
		debrisAlternator = false;
		
		debrisInfo = new int[200][4];
		smallDebrisInfo = new int[debrisInfo.length][4];
		for(int i = 0; i < debrisInfo.length; i++)
		{
			for(int j = 0; j < debrisInfo[i].length; j++)
			{
				debrisInfo[i][0] = (int)(Math.random()*GamePanel.WIDTH);
				debrisInfo[i][1] = -2000 + (int)((Math.random()*2000)-1000);
				debrisInfo[i][2] = (int)(Math.random()*5)+2;
				debrisInfo[i][3] = (int)(Math.random()*130);
				
				smallDebrisInfo[i][0] = (int)(Math.random()*GamePanel.WIDTH);
				smallDebrisInfo[i][1] = -2000 + (int)((Math.random()*2000)-1000);
				smallDebrisInfo[i][2] = (int)(Math.random()*5)+2;
				smallDebrisInfo[i][3] = (int)(Math.random()*130);
			}
		}
		
		tileStart = false;
		bgVectorX = 0;
		bgVectorY = 0;
		debrisVector = 0;
		
		super.isFadingIn = true;
		super.alphaLevel = 255;
	}

	public void init() 
	{	
		tileMap = new TileMap("Resources/Maps/level5.txt", 2, gsm);
		tileMap.setY(-300);
		player = new Player(tileMap, this);
		player.setPosition(375, -100);
		player.setTileMapMoving(true);
		
		super.init(); //requires the player to be inited first
		
		int[] pickupsToSpawn = {Pickups.BIRDBOOST, Pickups.HEALBOOST, Pickups.GLIDEBOOST};
		pickups = new Pickups(player, tileMap, this, pickupsToSpawn, 10000000000L);
		tileStart = false;
		score = 0;
		
		stuka = new SmallStuka(tileMap);
		
		setBackgroundVector(0, -4);
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
				
			}, 10);
		}
		start = true;
	}

	public void update()
	{
		if(super.isFadingIn)
		{
			super.fadeIn(0, Color.BLACK, 2);
		}
		if(start)
		{
			bg.update();
			tileMap.update();
			pickups.update();
			player.update();
			for(int i = 0; i < enemies.size(); i++)
			{
				enemies.get(i).update();
				if (enemies.get(i).getRemove()) 
				{
					enemies.remove(i);
					i--;
				}
			}
			
			Iterator<MapObject> iter = mapObjects.iterator();
			while (iter.hasNext()) 
			{
				MapObject m = iter.next();
				if(m.getRemove())
					iter.remove();
				else
					m.update();
			}
			super.backGroundParallaxUpdate();
			super.aimUpdate();
			super.updateBonusScores();
		}
		while(!PlayState.itemsToSpawn.isEmpty())
		{
			if (PlayState.itemsToSpawn.get(0)[0] == 0)
			{
				mapObjects.add(new Bomb(PlayState.itemsToSpawn.get(0)[1], -80, tileMap, 1));
			}	
			if (PlayState.itemsToSpawn.get(0)[0] == 1)
			{
				enemies.add(new Jetpacker(PlayState.itemsToSpawn.get(0)[1], -100, tileMap, player));
			}
			if (PlayState.itemsToSpawn.get(0)[0] == 2)
			{
				enemies.add(new Walker(PlayState.itemsToSpawn.get(0)[1], PlayState.itemsToSpawn.get(0)[2]-45, tileMap, player));
			}
			PlayState.itemsToSpawn.remove(0);
		}
		if(player.getPoints() > 6000 && !stukaSpawned )
		{
			stuka.init();
			mapObjects.add(stuka);
			stukaSpawned = true;
		}
		else if(player.getPoints() > 7000)
			mapObjects.remove(stuka);
			
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
				if(enemies.size() < 1)
				{
					enemies.add(new PlaneBoss(2000, 100, tileMap, player, 1));
					System.out.println("STOP PLAYER MOVEMENT... SO WE CAN GET A NICE CUTSCREEN EVERY TIME");
				}
			}
			tileMap.setYVector(transitionDY);
			if(player.getY() > GamePanel.HEIGHT && !enemies.isEmpty() && enemies.get(0).getX() < GamePanel.WIDTH && player.getPoints() > bossHeight)
			{
				System.out.println("NOT DEAD");
				player.setPosition(400, 900);
				super.isFadingOut = true;
				super.fadeOut(1000000000.0, Color.WHITE, 20, gsm, GameStateManager.LEVEL1STATE, GameStateManager.BOSS1STATE);
			}
		}
	}

	public void draw(Graphics2D g)
	{
		bg.draw(g);
		for(MapObject m: mapObjects)
			m.draw(g);
		if (debrisAlternator)
			smallDebris(g);
		player.draw(g);
		tileMap.draw(g);
		pickups.draw(g);
		for(Enemy e: enemies)
			e.draw(g);
		if(!debrisAlternator)
			debris(g);
		
		g.setFont(scoreFont);
		g.setColor(Color.WHITE);
		if(!start) 
		{
			super.drawPause(g);
		}
		g.drawString("Score: " + player.getPoints(), centerStringX("Score: " + player.getPoints(), 0, GamePanel.WIDTH, g), 30);
		
		super.drawBonusScores(g);
		super.drawCrossHair(g);
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
				if(debrisInfo[i][1] > 0 && debrisInfo[i][1] < GamePanel.HEIGHT)
				{
					g.setColor(colors.get(debrisInfo[i][3]));
					g.fillRect(debrisInfo[i][0], debrisInfo[i][1], debrisInfo[i][2], debrisInfo[i][2]);
					debrisInfo[i][1] += debrisInfo[i][2]*debrisVector;
				}
				else
				{	
					debrisInfo[i][1] += 2*debrisVector;
				}
				
				if(debrisInfo[i][1] < highestLoc) highestLoc = debrisInfo[i][1];
			}
		}
		if (highestLoc > GamePanel.HEIGHT)
		{
			debrisAlternator = !debrisAlternator;
			for(int i = 0; i < debrisInfo.length; i++)
			{
				for(int j = 0; j < debrisInfo[i].length; j++)
				{
					debrisInfo[i][0] = (int)(Math.random()*GamePanel.WIDTH);
					debrisInfo[i][1] = -2000 + (int)((Math.random()*2000)-1000);
					debrisInfo[i][2] = (int)(Math.random()*5)+2;
					debrisInfo[i][3] = (int)(Math.random()*130);
				}
			}
		}
	}
	
	public void smallDebris(Graphics2D g)
	{
		int highestLoc = smallDebrisInfo[0][1];
		for(int i = 0; i < smallDebrisInfo.length; i++)
		{
			for(int j = 0; j < smallDebrisInfo[i].length; j++)
			{	
				if(smallDebrisInfo[i][1] > 0 && smallDebrisInfo[i][1] < GamePanel.HEIGHT)
				{
					g.setColor(colors.get(smallDebrisInfo[i][3]));
					g.fillRect(smallDebrisInfo[i][0], smallDebrisInfo[i][1], (int)(smallDebrisInfo[i][2]*0.75), (int)(smallDebrisInfo[i][2]*0.75));
					smallDebrisInfo[i][1] += smallDebrisInfo[i][2]*debrisVector;
				}
				else
				{
					smallDebrisInfo[i][1] += debrisVector;
				}
				
				if(smallDebrisInfo[i][1] < highestLoc) highestLoc = smallDebrisInfo[i][1];
			}
		}
		if (highestLoc > GamePanel.HEIGHT)
		{
			debrisAlternator = !debrisAlternator;
			for(int i = 0; i < smallDebrisInfo.length; i++)
			{
				for(int j = 0; j < smallDebrisInfo[i].length; j++)
				{
					smallDebrisInfo[i][0] = (int)(Math.random()*GamePanel.WIDTH);
					smallDebrisInfo[i][1] = -2000 + (int)((Math.random()*2000)-1000);
					smallDebrisInfo[i][2] = (int)(Math.random()*5)+2;
					smallDebrisInfo[i][3] = (int)(Math.random()*130);
				}
			}
		}
	}
	
	public void slowTimeStart()
	{
		setBackgroundVector(0, -1);
		setDebrisVectors(0.5);
		setEntitiySpeed(0.2f);
	}
	
	public void slowTimeEnd()
	{
		setBackgroundVector(0, -4);
		setDebrisVectors(1);
		setEntitiySpeed(1);
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
			start = true;
			/*if(!start)
			{
				start = true;
				setBackgroundVector(0, -4);
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
			}*/
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
			enemies.add(new PlaneBoss(1000, 100, tileMap, player, 1));
		}
		if(k == KeyEvent.VK_N)
		{
			gsm.setState(GameStateManager.BOSS1STATE);
			gsm.resetState(GameStateManager.LEVEL1STATE);
		}
		if(k == KeyEvent.VK_E)
		{
			tileMap.getExplosions().add(new Explosion(mouseX, mouseY, 2, tileMap));
		}
		
	}

	public void keyReleased(int k) 
	{
		player.keyReleased(k);
		
		/*
		if(k == KeyEvent.VK_B)
		{
			addBomb = false;
		}
		*/
	}
}
