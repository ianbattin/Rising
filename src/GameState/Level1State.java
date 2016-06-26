package GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.Iterator;

import Entities.Player;
import Entities.Projectile;
import Entities.Walker;
import Entities.SmallStuka;
import Entities.Bomb;
import Entities.Enemy;
import Entities.Jetpacker;
import Entities.MapObject;
import Entities.Pickups;
import Entities.PlaneBoss;
import Main.GamePanel;
import Main.SoundPlayer;
import TileMap.Background;
import TileMap.TileMap;


public class Level1State extends PlayState
{
	private int[][] debrisInfo;
	private int[][] smallDebrisInfo;
	private ArrayList<Color> colors;
	private float timer;
	public static boolean tileStart, debrisAlternator;
	private boolean transition;
	private double transitionDY;
	public boolean hasInited;
	private long introTimer;
	
	public SmallStuka stuka;
	public boolean stukaSpawned = false;
	
	public int bossHeight = 7000;
	public static final double SCROLLSPEED = 1.25;
	
	public Level1State(GameStateManager gsm)
	{
		super();
		this.gsm = gsm;
		try
		{
			bg = new Background("/Backgrounds/battlebackground.gif", 1, true);
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
			debrisInfo[i][0] = (int)(Math.random()*GamePanel.WIDTH);
			debrisInfo[i][1] = -2000 + (int)((Math.random()*2000)-1000);
			debrisInfo[i][2] = (int)(Math.random()*5)+2;
			debrisInfo[i][3] = (int)(Math.random()*130);
			
			smallDebrisInfo[i][0] = (int)(Math.random()*GamePanel.WIDTH);
			smallDebrisInfo[i][1] = -2000 + (int)((Math.random()*2000)-1000);
			smallDebrisInfo[i][2] = (int)(Math.random()*5)+2;
			smallDebrisInfo[i][3] = (int)(Math.random()*130);
		}
		
		tileStart = false;
		bgVectorX = 0;
		bgVectorY = 0;
		debrisVector = 0;
		introTimer = 0;
		
		super.isFadingIn = true;
		super.alphaLevel = 255;
	}

	public void init() 
	{	
		tileMap = new TileMap("level1final.txt", "Level 1", 2, gsm);
		tileMap.setY(-300);
		player = new Player(tileMap, this);
		player.setPosition(375, -100);
		player.setTileMapMoving(true);
		player.setCanMove(false);
		
		((OutroState)gsm.getState(GameStateManager.OUTROSTATE)).setLevel(GameStateManager.LEVEL1STATE);
		
		super.init(); //requires the player to be initiated first
		
		pickups = new Pickups(player, tileMap, new int[]{Pickups.BIRDBOOST, Pickups.HEALBOOST, Pickups.GLIDEBOOST}, 7500000000L, 2500000000L);
		tileStart = false;
		score = 0;
		
		stuka = new SmallStuka(tileMap);
		
		music("Modero.wav");
		
		setBackgroundVector(0, -4);
		setDebrisVectors(3);
		if(!tileStart)
		{
			Timer timer = new Timer();
			timer.schedule(new TimerTask()
			{
				public void run()
				{
					tileStart = true;
					tileMap.setYVector(SCROLLSPEED);		
				}
			}, 10);
		}
		start = true;
	}

	public void update()
	{
		if(super.isFadingIn)
		{
			super.fadeIn(0, Color.BLACK, 4);
		}
		if(start)
		{
			bg.update();
			tileMap.update();
			pickups.update();
			player.update();
			
			checkScript();
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
				
				if(m.intersects(player))
					m.collided(player);
			}
			super.backGroundParallaxUpdate();
			super.aimUpdate();
			super.updateBonusScores();
		}
		while(!PlayState.itemsToSpawn.isEmpty())
		{
			if (PlayState.itemsToSpawn.get(0)[0] == PlayState.SPAWN_BOMB)
			{
				mapObjects.add(new Bomb(PlayState.itemsToSpawn.get(0)[1], -80, tileMap, 0));
			}	
			if (PlayState.itemsToSpawn.get(0)[0] == PlayState.SPAWN_PARACHUTER)
			{
				enemies.add(new Jetpacker(PlayState.itemsToSpawn.get(0)[1], -100, tileMap, player));
			}
			if (PlayState.itemsToSpawn.get(0)[0] == PlayState.SPAWN_WALKER)
			{
				enemies.add(new Walker(PlayState.itemsToSpawn.get(0)[1], PlayState.itemsToSpawn.get(0)[2]-45, tileMap, player));
			}
			if (PlayState.itemsToSpawn.get(0)[0] == PlayState.SPAWN_DEBRIS)
			{
				mapObjects.add(new Projectile((double)PlayState.itemsToSpawn.get(0)[0], (double)PlayState.itemsToSpawn.get(0)[1], Math.random()*3+3, 7, tileMap));
			}
			PlayState.itemsToSpawn.remove(0);
		}
		if(tileMap.getYMove() > 5000 && tileMap.getYMove() < 5500 && !stukaSpawned )
		{
			stuka.init();
			mapObjects.add(stuka);
			stukaSpawned = true;
		}
		else if(tileMap.getYMove() > 5500 && stukaSpawned)
		{
			stukaSpawned = false;
			mapObjects.remove(stuka);
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
				tileStart = false;
				transitionDY = 0;
				//removes all non-boss entities
				for(int i = 0; i < enemies.size(); i++)
				{
					if(!(enemies.get(i) instanceof PlaneBoss))
					{
						enemies.remove(i);
						i--;
					}
				}
				if(enemies.size() == 0) 
				{
					PlaneBoss p = new PlaneBoss(2000, 200, tileMap, player, 1);
					p.setX(1000);
					p.setY(100);
					p.setCockpitCollision(false);
					enemies.add(p);
				}
				else if(enemies.size() == 1 && enemies.get(0) instanceof PlaneBoss && ((PlaneBoss)enemies.get(0)).getMoveComplete() == false)
				{
					((PlaneBoss)enemies.get(0)).setMovement(300, 100, 0.4, 5);
				}
			}
			
			tileMap.setYVector(transitionDY);
			if(player.getY() > GamePanel.HEIGHT)
			{
				player.setPosition(400, 900);
				if(!super.isFadingOut) SoundPlayer.animVolume(-40.0F);
				super.isFadingOut = true;
				super.fadeOut(1000000000.0, Color.WHITE, 20, gsm, GameStateManager.LEVEL1STATE, GameStateManager.TRANSITION_INTERLUDESTATE1);
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
		if(player.hasGun())
			super.drawCrossHair(g);
		super.drawFade(g);
	}
	
	public void checkScript()
	{	
		if (introTimer >= 0)
		{
			if (introTimer < 3800 && tileMap.getYMove() > -GamePanel.HEIGHT/4)
			{
				tileMap.setYVector(0.3);
				if (introTimer < 1800)	
				{
					player.setBannerColor(Color.BLACK);
					player.setPlayerBannerText("\"Christine!\"");
					
				}
				else if (introTimer < 3500)
				{
					player.setBannerColor(Color.WHITE);
					player.setPlayerBannerText("Use "+ KeyEvent.getKeyText(GameStateManager.left) + " and " + KeyEvent.getKeyText(GameStateManager.right) + " to move, " + KeyEvent.getKeyText(GameStateManager.up) + " to jump.");
					player.doIntroFrame(true, false);
				}
				else
				{
					player.doIntroFrame(true, true);
				}
				
				introTimer += GamePanel.getElapsedTime()/1000000;
			} 
			else if (introTimer >= 3800)
			{
				player.setCanMove(true);
				player.hidePlayerBanner();
				player.doIntroFrame(false, false);
				tileMap.setYVector(SCROLLSPEED);
				introTimer = -1;
			}
		}
	}
	
	//update and draw the debris
	public void debris(Graphics2D g)
	{
		int highestLoc = debrisInfo[0][1];
		for(int[] array : debrisInfo)
		{
			if(array[1] > 0 && array[1] < GamePanel.HEIGHT)
			{
				int xLoc = (int)((player.getX()+player.getWidth()/2));
				int yLoc = (int)((player.getY()+player.getCHeight()+1));
				if ((xLoc > array[0] + array[2]+10 || xLoc < array[0]-10) || (yLoc > array[1] + (int)(array[2]*2.25) + 10 || yLoc < array[1] - 10))
				{
					g.setColor(colors.get(array[3]));
					g.fillRect(array[0], array[1], array[2], (int)(array[2]*2.25));
				}
				array[1] += array[2]*debrisVector;
			}
			else
			{	
				array[1] += 2*debrisVector;
			}
			
			if(array[1] < highestLoc) highestLoc = array[1];
		}
		if (highestLoc > GamePanel.HEIGHT)
		{
			debrisAlternator = !debrisAlternator;
			for (int[] array : debrisInfo)
			{
				array[0] = (int)(Math.random()*GamePanel.WIDTH);
				array[1] = -2000 + (int)((Math.random()*2000)-1000);
				array[2] = (int)(Math.random()*5)+2;
				array[3] = (int)(Math.random()*130);
			}
		}
	}
	
	public void smallDebris(Graphics2D g)
	{
		int highestLoc = smallDebrisInfo[0][1];
		for(int array[] : smallDebrisInfo)
		{
			if(array[1] > 0 && array[1] < GamePanel.HEIGHT)
			{
				int xLoc = (int)((player.getX()+player.getWidth()/2));
				int yLoc = (int)((player.getY()+player.getCHeight()+1));
				if ((xLoc > array[0] + (int)(array[2]*0.75) + 10 || xLoc < array[0]-10) || (yLoc > array[1] + (int)(array[2]*2.25) + 10 || yLoc < array[1] - 10))
				{
					g.setColor(colors.get(array[3]));
					g.fillRect(array[0], array[1], (int)(array[2]*0.75), (int)(array[2]*2.25));
				}
				array[1] += array[2]*debrisVector;
			}
			else
			{
				array[1] += debrisVector;
			}
			
			if(array[1] < highestLoc) highestLoc = array[1];
		}

		if (highestLoc > GamePanel.HEIGHT)
		{
			debrisAlternator = !debrisAlternator;
			for(int[] array : smallDebrisInfo)
			{
				array[0] = (int)(Math.random()*GamePanel.WIDTH);
				array[1] = -2000 + (int)((Math.random()*2000)-1000);
				array[2] = (int)(Math.random()*5)+2;
				array[3] = (int)(Math.random()*130);
			}
		}
	}
	
	public void slowTimeStart()
	{
		setBackgroundVector(0, -1);
		setDebrisVectors(0.75);
		setEntitiySpeed(0.2f);
	}
	
	public void slowTimeEnd()
	{
		setBackgroundVector(0, -4);
		setDebrisVectors(3);
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
			if(!start)
			{	
				start = true;
				player.getAnimation();
			}
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
		if(k == GameStateManager.reset && !start)
		{
			gsm.setState(GameStateManager.MENUSTATE);
			gsm.resetState(GameStateManager.LEVEL1STATE);
		}
		if(k == GameStateManager.pause)
		{
			start = false;
		}
		/*
		if(k == KeyEvent.VK_N)
		{
			gsm.setState(GameStateManager.BOSS1STATE);
			gsm.resetState(GameStateManager.LEVEL1STATE);
		}	
		if(k == KeyEvent.VK_L)
		{
			mapObjects.add(new Projectile(400, 100, 45, 7, tileMap));
		}
		*/
	}

	public void keyReleased(int k) 
	{
		player.keyReleased(k);
	}
}
