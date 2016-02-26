package GameState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.Object.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

import Entities.Player;
import Entities.Projectile;
import Entities.Rifleman;
import Entities.Enemy;
import Entities.Jetpacker;
import Entities.Pickups;
import Entities.PlaneBoss;
import Main.GamePanel;
import Main.Main;
import TileMap.Background;
import TileMap.Tile;
import TileMap.TileMap;


public class Boss1State extends PlayState
{
	//Mouse
	private int mouseX;
	private int mouseY;
	private int relX;
	private int relY;
	private boolean mouseUpdate;
	private MouseEvent mouse;

	private boolean isStillAlive;
	private float deathTimer;
	public static boolean tileStart;
	
	private long timer;
	
	private boolean setUp = false;
	private boolean done;
	private int stage;
	private int step;
	private int count;
	
	private double planeX, planeY;

	private double debrisVector;
	private boolean drawBossHealth;

	public Boss1State(GameStateManager gsm)
	{
		super();
		stage = 0;
		step = 0;
		count = 0;
		done = false;
		
		this.gsm = gsm;
		start = true;
		deathTimer = 0;

		super.isFadingIn = true;
		super.alphaLevel = 255;
	}

	public void init() 
	{
		tileMap = new TileMap("Resources/Maps/boss1.txt", 0, gsm);
		tileMap.setVector(0, 0);
		tileMap.setY(tileMap.getY() + 175);
		player.setTileMapMoving(false);
		player.setTileMap(tileMap);
		player.setPlayState(this);
		
		super.init(); //requires the player to be inited first
		
		int[] pickupsToSpawn = {Pickups.ARMORBOOST, Pickups.HEALBOOST, Pickups.SLOWTIMEBOOST, Pickups.BIRDBOOST};
		pickups = new Pickups(player, tileMap, this, pickupsToSpawn, 50000000000L);
		tileStart = false;
		try
		{
			bg = new Background("/Backgrounds/battlebackground.gif", 1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setBackgroundVector(0, 5.0);
		setDebrisVectors(1);
		
		if(enemies.isEmpty())
		{
			enemies.add(new PlaneBoss(-2000, 200, tileMap, player, 0));
		}
		
		for(Tile t: tileMap.getTiles())
		{
			if(t.getY() <= 850)
			{
				t.setBulletCollision(false);
				t.setBlocked(false);
			}
		}
		System.out.println("Left: " + player.getMoveSpeedLeft() + " Right: " + player.getMoveSpeedRight() + " MaxLeft: " + player.getMaxSpeedLeft() + " MaxRight: " + player.getMaxSpeedRight());
	}

	public void update()
	{
		if(super.isFadingIn)
		{
			super.fadeIn(500000000.0, Color.WHITE, 2);
		}
		if(start)
		{
			basicChecks();
			script();
		}
	}

	public void draw(Graphics2D g)
	{
		bg.draw(g);
		tileMap.draw(g);
		player.draw(g);
		pickups.draw(g);
		for(Enemy e: enemies)
			e.draw(g);
		
		if(drawBossHealth) drawBossHealth(g);
		
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
	
	private void drawBossHealth(Graphics2D g) 
	{
		g.setColor(Color.white);
		g.fillRect(197, 97, 406, 36);
		g.setColor(Color.BLACK);
		g.fillRect(200, 100, 400, 30);
		g.setColor(Color.RED);
		g.fillRect(200, 100, enemies.get(0).getHealth()*4, 30);
	}

	private void basicChecks() 
	{
		if(!setUp)
		{
			player.setPosition(400, -300);
			setUp = true;
		}
		
		bg.update();
		tileMap.update();
		pickups.update();
		player.update();
		super.aimUpdate();
		super.backGroundParallaxUpdate();
		super.updateBonusScores();
		
		for(int i = 0; i < enemies.size(); i++)
		{
			enemies.get(i).update();
			if (enemies.get(i).getRemove()) 
			{
				enemies.remove(i);
				i--;
			}
		}

		if(player.getPlayerHealth() < 1 && deathTimer > 1500000000.0)
		{
			super.isFadingOut = true;
			super.fadeOut(500000000, Color.BLACK, 5, gsm, GameStateManager.BOSS1STATE, GameStateManager.OUTROSTATE);
		}
		else if (player.getPlayerHealth() < 1)
		{
			deathTimer += GamePanel.getElapsedTime();
		}
		
		for(Enemy e: enemies)
		{
			e.setMoveSpeedLeft(0.3);//player.getMoveSpeedLeft()/2);
			e.setMaxSpeedLeft(2.5);//player.getMaxSpeedLeft()*2);
			e.setMoveSpeedRight(0.4);//player.getMoveSpeedRight()/2);
			e.setMaxSpeedRight(6);//player.getMaxSpeedRight()*2);
		}
		player.setMoveSpeedLeft(0.3);//player.getMoveSpeedLeft()/2);
		player.setMaxSpeedLeft(2.5);//player.getMaxSpeedLeft()*2);
		player.setMoveSpeedRight(0.4);//player.getMoveSpeedRight()/2);
		player.setMaxSpeedRight(6);//player.getMaxSpeedRight()*2);
	}
	
	private void script()
	{		
		if (!enemies.isEmpty() && enemies.get(0) instanceof PlaneBoss)
		{
			planeX = ((PlaneBoss) enemies.get(0)).getX();
			planeY = ((PlaneBoss) enemies.get(0)).getY();
		
			//Player falling and being saved by plane
			if(stage == 0)
			{
				switch(step)
				{
					case 0:
					{
						if(player.getDY() <= 0)
						{
							if(tileMap.getYMove() > 0)
							{
								if(bg.getYPosition() != 0)
								{
									if(bg.getYPosition() <= 400) setBackgroundVector(10.0, -1);
									else setBackgroundVector(10.0, 1);
								}
								setBackgroundXVector(10.0);
								tileMap.setYVector(-1.0);
							}
							else
							{
								tileMap.setYVector(0);
								enemies.get(0).setX(-2000);
								enemies.get(0).setY(200);
								step = 1;
							}
						}
						break;
					}
					case 1:
					{
						step = 0;
						stage = 1;
						break;
					}
				}
			}
			//Plane gets spawned, doesnt attack yet, but flies past to the right, then comes back from the right and flies past left (shooting but not hurting)
			else if(stage == 1)
			{
				switch(step)
				{
					//Plane flies left to right at 1 speed
					case 0:
					{
						if(((PlaneBoss) enemies.get(0)).getMoveComplete() == false)
						{
							((PlaneBoss) enemies.get(0)).setMovement(-2000, 200, 1200, 200, 2, 0);
						}
						else
						{
							((PlaneBoss) enemies.get(0)).setMoveComplete(false);
							step = 1;
						}
						break;
					}
						
					//Plane flies right to middle of screen and waits for 4 seconds at half speed
					case 1:
					{
						if(((PlaneBoss) enemies.get(0)).getMoveComplete() == false)
						{
							((PlaneBoss) enemies.get(0)).setMovement(1200, 200, 400, 200, 0.5, 0);
						}
						else
						{
							if(!done)
							{
								timer = System.nanoTime();
								done = true;
							}
							long elapsed = (System.nanoTime() - timer) / 1000000;
							if(2000 <= elapsed)
							{
								((PlaneBoss) enemies.get(0)).setMoveComplete(false);
								done = false;
								step = 2;
								timer = System.nanoTime();
							}
						}
						
						//enable the double jumping & display the banner
						player.setDoubleJump(true);
						player.setPlayerBannerText("Press "+ KeyEvent.getKeyText(GameStateManager.up)+ " twice to somersault!");
						
						break;
					}
						
					//Plane flies from middle of screen to left at 1 speed
					case 2:
					{
						((PlaneBoss) enemies.get(0)).setHealth(100);
						drawBossHealth = true;
						if(((PlaneBoss) enemies.get(0)).getMoveComplete() == false)
						{
							((PlaneBoss) enemies.get(0)).setMovement(400, 200, -1500, 200, 1, 0);
						}
						else
						{
							step = 0;
							stage = 2;
							((PlaneBoss) enemies.get(0)).setMoveComplete(false);
						}
						
						//hide the banner
						player.hidePlayerBanner();
						break;
					}
				}
			}
			
			//Attacking stage at 80-100 health;
			else if(stage == 2)
			{
				if(enemies.get(0).getHealth() > 0)
				{
					switch(step)
					{
						//Plane flies left to right at 1 speed and drops off 3 paratroopers
						case 0:
						{
							if(((PlaneBoss) enemies.get(0)).getMoveComplete() == false)
							{
								((PlaneBoss) enemies.get(0)).setMovement(-1500, 200, 1500, 200, 1, 0);
							}
							
							if(count == 0)
							{
								if(enemies.size() == 1 && (180 < planeX  && planeX < 220))
								{
									enemies.add(new Jetpacker(planeX, planeY, tileMap, player));
								}
								else if(enemies.size() == 2 && (380 < planeX && planeX < 420))
								{
									enemies.add(new Jetpacker(planeX, planeY, tileMap, player));
								}
								else if(enemies.size() == 3 && (580 < planeX && planeX < 620))
								{
									enemies.add(new Jetpacker(planeX, planeY, tileMap, player));
								}
								else if(enemies.size() == 1 && planeX > 800)
								{
									count++;
									if(count % 3 == 0)
										step = 2;
									else
										step = 1;
									((PlaneBoss) enemies.get(0)).setMoveComplete(false);
								}	
							}
							else if(count == 2)
							{
								if(enemies.size() == 1 && (180 < planeX  && planeX < 220))
								{
									enemies.add(new Rifleman(planeX, planeY, tileMap, player));
								}
								else if(enemies.size() == 2 && (580 < planeX && planeX < 620))
								{
									enemies.add(new Rifleman(planeX, planeY, tileMap, player));
								}
								else if(enemies.size() == 1 && planeX > 800)
								{
									count++;
									if(count % 3 == 0)
										step = 2;
									else
										step = 1;
									((PlaneBoss) enemies.get(0)).setMoveComplete(false);
								}	
							}
							break;
						}
	
						//Plane flies left and shoots fire bullets
						case 1:
						{
							if(((PlaneBoss) enemies.get(0)).getMoveComplete() == false)
							{
								((PlaneBoss) enemies.get(0)).setMovement(1500, 200, -1500, 200, 1, 3);
							}
							else
							{
								count++;
								if(count % 3 == 0)
									step = 2;
								else
									step = 0;
								stage = 2;
								((PlaneBoss) enemies.get(0)).setMoveComplete(false);
							}
							break;
						}
						
						//Plane flies from right to middle low enough for player to jump on cockpit
						case 2:
						{
							if(((PlaneBoss) enemies.get(0)).getMoveComplete() == false)
							{
								((PlaneBoss) enemies.get(0)).setMovement(1500, 200, 400, 500, 1, 0);
							}
							else
							{
								if(!done)
								{
									timer = System.nanoTime();
									done = true;
								}
								long elapsed = (System.nanoTime() - timer) / 1000000;
								if(4000 <= elapsed)
								{
									count = 0;
									done = false;
									step = 3;
									stage = 2;
									timer = System.nanoTime();
									((PlaneBoss) enemies.get(0)).setMoveComplete(false);
								}
							}
							break;
						}
						
						//Plane flies from middle to left to drop off troops again
						case 3:
						{
							if(((PlaneBoss) enemies.get(0)).getMoveComplete() == false)
							{
								((PlaneBoss) enemies.get(0)).setMovement(400, 500, -1500, 200, 1, 0);
							}
							else
							{
								step = 0;
								stage = 2;
								((PlaneBoss) enemies.get(0)).setMoveComplete(false);
							}
							break;
						}
					}
				}
				else
				{
					drawBossHealth = false;
					for (Enemy e: enemies)
					{
						if (!(e instanceof PlaneBoss)) e.playerHurt(500);
					}
				}
			}
		}
		else
		{
			System.out.println("WAHHHHHH");
		}
	}
	
	public void slowTimeStart()
	{
		setBackgroundVector(2, -0.2);
		setEntitiySpeed(0.2f);
	}
	
	public void slowTimeEnd()
	{
		setBackgroundVector(10, -1.0);
		setEntitiySpeed(1);
	}
	
	public void keyPressed(int k) 
	{
		player.keyPressed(k);

		if(k == GameStateManager.reset)
		{
			gsm.setState(GameStateManager.MENUSTATE);
			gsm.resetState(GameStateManager.BOSS1STATE);
		}
		if(k == GameStateManager.pause)
		{
			start = false;
		}
		if(k == GameStateManager.select)
		{
			start = true;
		}
	}

	public void keyReleased(int k) 
	{
		player.keyReleased(k);
	}
}
