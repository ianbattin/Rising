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

	private PlaneBoss planeBoss;

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
		tileMap.setY(tileMap.getY() + 225);
		player.setTileMapMoving(false);
		player.setTileMap(tileMap);
		player.updateTileMap(tileMap);
		player.setPlayState(this);

		super.init(); //requires the player to be inited first

		pickups = new Pickups(player, tileMap, this, new int[]{Pickups.HEALBOOST, Pickups.ARMORBOOST, Pickups.BIRDBOOST}, 30000000000L, 5000000000L);
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

		if(!enemies.isEmpty())
		{
			enemies.clear();
		}
		enemies.add(new PlaneBoss(-2000, 200, tileMap, player, 0));
		planeBoss = (PlaneBoss)enemies.get(0);

		for(Tile t: tileMap.getTiles())
		{
			if(t.getY() <= 900)
			{
				t.setBulletCollision(false);
				t.setBlocked(false);
			}
		}

		//reset player from potential overlap
		player.setCanMove(true);
		player.hidePlayerBanner();
		player.doIntroFrame(false, false);

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
		g.setColor(Color.darkGray);
		g.fillRect(200, 100, 400, 30);
		g.setColor(Color.RED);
		g.fillRect(200, 100, planeBoss.getHealth()*4, 30);
	}

	private void basicChecks() 
	{
		if(!setUp)
		{
			player.setPosition(400, -350);
			player.setTileMap(tileMap);
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
		if (!enemies.isEmpty() && enemies.get(0) instanceof PlaneBoss && planeBoss != null)
		{
			planeX = planeBoss.getX();
			planeY = planeBoss.getY();

			System.out.println("Stage: " + stage + "      Step: " + step +  "      Count: "+ count +"      PlaneXY: " + planeBoss.getX() + "   " + planeBoss.getY());
			
			//Player falling and being saved by plane
			if(stage == 0)
			{
				switch(step)
				{
				case 0:
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
							planeBoss.setX(-2000);
							planeBoss.setY(200);
							step = 1;
						}
					}
					break;
				case 1:
					step = 0;
					stage = 1;
					break;
				}
			}
			//Plane gets spawned, flies right while shooting fires
			else if(stage == 1)
			{
				switch(step)
				{
				//Plane flies left to right at 1 speed
				case 0:
				{
					planeBoss.setHealth(100);
					drawBossHealth = true;

					if(planeBoss.getMoveComplete() == false)
					{
						planeBoss.setMovement(2400, 200, 2, 0);
						planeBoss.setHealth(100);

						if(planeBoss.getMoveComplete() == false)
						{
							planeBoss.setMovement(1200, 200, 2, 0);
						}
						else
						{
							planeBoss.setMoveComplete(false);
							step = 1;
							planeBoss.startBombAttack();
							planeBoss.setDrawArrow(true, PlaneBoss.BOMBDROP);
						}
						break;
					}
					else
					{
						planeBoss.setMoveComplete(false);
						step = 1;
						if(planeBoss.getMoveComplete() == false)
						{
							planeBoss.setMovement(400, 200, 0.5, 0);
						}
						else
						{
							if(!done)
							{
								timer = System.nanoTime();
								done = true;
							}
							long elapsed = (System.nanoTime() - timer) / 1000000;
							if(12000 <= elapsed || !planeBoss.isBombAttacking())
							{
								planeBoss.setMoveComplete(false);
								done = false;
								step = 2;
								timer = System.nanoTime();
							}
						}					
						break;
					}
				}

				//Plane flies right to middle of screen and waits for 4 seconds at half speed
				case 1:
				{
					if(planeBoss.getMoveComplete() == false)
					{
						planeBoss.setMovement(400, 200, 1, 3);
					}
					else
					{
						planeBoss.setAttack(0);
						if(!done)
						{
							timer = System.nanoTime();
							done = true;
						}
						long elapsed = (System.nanoTime() - timer) / 1000000;
						if(3000 <= elapsed)
						{
							planeBoss.setMoveComplete(false);
							done = false;
							step = 2;
							timer = System.nanoTime();
						}
					}					
					break;
				}

				//Plane flies from middle of screen to left at 1 speed
				case 2:
					if(planeBoss.getMoveComplete() == false)
					{
						planeBoss.setMovement(-1500, 200, 1, 0);
					}
					else
					{
						step = 0;
						stage = 2;
						planeBoss.setMoveComplete(false);

						//enable the double jumping & display the banner
						player.setDoubleJump(true);
						player.setPlayerBannerText("Press "+ KeyEvent.getKeyText(GameStateManager.up)+ " twice to somersault!");
					}
					break;
				}
			}
			//Attacking stage at 50 - 100 health;
			else if(stage == 2)
			{
				switch(step)
				{
				//Plane flies left to right at 1 speed and drops off 3 paratroopers
				case 0:
					if(count == 0)
					{
						Jetpacker e = new Jetpacker(-200-(int)(Math.random()*100), -150 + (Math.random()*400) - 60, tileMap, player);
						e.setWind(0.1, 0.0);
						enemies.add(e);
						count++;
					}
					else if(count == 1)
					{
						Jetpacker e = new Jetpacker(-300-(int)(Math.random()*100), -150 + (Math.random()*400) - 60, tileMap, player);
						e.setWind(0.1, 0.0);
						enemies.add(e);
						count++;
					}
					else if(count == 2)
					{
						Jetpacker e = new Jetpacker(-400-(int)(Math.random()*100), -150 + (Math.random()*400) - 60, tileMap, player);
						e.setWind(0.1, 0.0);
						enemies.add(e);
						count++;
					}
					
					long elapsed = (System.nanoTime() - timer) / 1000000;
					if(9000 <= elapsed)
					{
						planeBoss.setMoveComplete(false);
						step = 1;
						count = 0;
						player.hidePlayerBanner();
						timer = System.nanoTime();
					}
					break;

					//Plane flies left and shoots fire bullets
				case 1:
					switch(count) 
					{
					case 0:
						if(planeBoss.getMoveComplete() == false)
						{
							planeBoss.setMovement(1500, 200, 2, 0);
						}
						else
						{
							count = 1;
							planeBoss.setMoveComplete(false);
						}
						break;

					case 1:
						if(planeBoss.getMoveComplete() == false)
							planeBoss.setMovement(400-planeBoss.getCWidth()/2, 400, 1, 0);
						else
						{
							count = 2;
							planeBoss.setMoveComplete(false);
							timer = System.nanoTime();
						}
						break;

					case 2:
						elapsed = (System.nanoTime() - timer) / 1000000;
						if(10000 <= elapsed)
						{
							count = 3;
							planeBoss.setMoveComplete(false);
							timer = System.nanoTime();
						}
						else
						{
							planeBoss.setAttack(4);

							if(planeBoss.getMoveComplete() == false)
							{
								if(planeBoss.isEvading())
								{
									planeBoss.evadeMove();
								} 
								else
								{
									planeBoss.setMovement(400-planeBoss.getCWidth()/2, 400, 1, 4);
									planeBoss.setDrawArrow(true, PlaneBoss.COCKPIT);
								}
							}
						}
						break;
						
					case 3:
						if(planeBoss.getMoveComplete() == false)
						{
							planeBoss.setMovement(-1500, 200, 2, 0);
							planeBoss.setDrawArrow(false, PlaneBoss.COCKPIT);
						}
						else
						{
							planeBoss.setAttack(0);
							step = 2;
							count = 0;
						}
						break;
					}
					break;

					//Plane flies from right to middle low enough for player to jump on cockpit
				case 2:
					if(enemies.size() == 1 && count == 0)
					{
						enemies.add(new Rifleman(200, -70, tileMap, player));
						count++;
					}
					else if(enemies.size() == 2 && count == 1)
					{
						enemies.add(new Rifleman(600, -70, tileMap, player));
						count++;
					}
					else if(enemies.size() == 1)
					{
						player.hidePlayerBanner();
						step = 1;
						count = 0;
					}	
					
					elapsed = (System.nanoTime() - timer) / 1000000;
					if(9000 <= elapsed)
					{
						planeBoss.setMoveComplete(false);
						step = 0;
						count = 0;
						if (planeBoss.getHealth() <= 50)
						{
							stage = 3;
							//planeBoss.setX(-1000);
						}
						timer = System.nanoTime();
					}
					break;
				}
			}
			else if (stage == 3) // 50 - 0 health stages. 
			{
				switch(step)
				{
				case 0:
				{
					if(planeBoss.getMoveComplete() == false)
					{
						planeBoss.setMovement(1200, 200, 1, 0);
					}
					else
					{
						planeBoss.setMoveComplete(false);
						step = 1;
						done = false;
						planeBoss.startBombAttack();
						planeBoss.setDrawArrow(true, PlaneBoss.BOMBDROP);
					}
					break;
				}
				case 1:
					if(planeBoss.getMoveComplete() == false)
						planeBoss.setMovement(400-planeBoss.getCWidth()/2, 200, 1, 0);
					else
					{
						//planeBoss.setMoveComplete(false);
						if(!done)
						{
							timer = System.currentTimeMillis();
							done = true;
						}
						long elapsed = System.currentTimeMillis() - timer;
						if(100000 <= elapsed || !planeBoss.isBombAttacking())
						{
							if(planeBoss.isEvading())
							{
								planeBoss.evadeMove();
							} 
							else
							{
								planeBoss.setMovement(400-planeBoss.getCWidth()/2, 500, 1, 4);
								planeBoss.setDrawArrow(true, PlaneBoss.COCKPIT);
							}
							planeBoss.setMoveComplete(false);
							done = false;
							step = 2;
							timer = System.currentTimeMillis();
						}
					}
					break;
				case 2:
					if (planeBoss.getMoveComplete() == false)
					{
						planeBoss.setMovement(-1000, 200, 1, 0);
					}
					else
					{
						//go to next step
						stage = 2;
						step = 0;
						count = 0;
					}
					break;
				}	
			}
			else //plane boss dies; no more health
			{
				drawBossHealth = false;
				for (Enemy e: enemies)
				{
					if (!(e instanceof PlaneBoss)) e.playerHurt(500, true);
				}
			}
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
