package GameState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
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
import Entities.Enemy;
import Entities.Jetpacker;
import Entities.Pickups;
import Entities.PlaneBoss;
import Main.GamePanel;
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

	private Player player;
	private Player otherPlayer;
	private Pickups pickups;
	private ArrayList<Enemy> enemies;
	private int[][] debrisInfo;
	private ArrayList<Color> colors;
	private boolean start, isStillAlive;
	private float deathTimer;
	public static boolean tileStart;
	private TileMap tileMap;
	private Font healthFont, generalFont;
	
	private long timer;
	
	private boolean setUp = false;
	private boolean done;
	private int stage;
	private int step;
	
	private double planeX, planeY;

	private double bgVectorX, bgVectorY;
	private double debrisVector;
	private boolean drawBossHealth;

	public Boss1State(GameStateManager gsm, Player player)
	{
		init();
		stage = 0;
		step = 0;
		done = false;
		
		otherPlayer = player;
		
		this.gsm = gsm;
		start = false;
		deathTimer = 0;
		//create & stores all the necessary colors (avoid creating too many color objects)
		colors = new ArrayList<Color>();
		for(int i = 25; i < 195; i++)
		{
			colors.add(new Color(i, i, i));
		}
		//create the initial debris
		debrisInfo = new int[30][4];
		for(int i = 0; i < debrisInfo.length; i++)
		{
			for(int j = 0; j < debrisInfo[i].length; j++)
			{
				debrisInfo[i][0] = (int)(Math.random()*GamePanel.WIDTH);
				debrisInfo[i][1] = (int)(Math.random()*GamePanel.HEIGHT);
				debrisInfo[i][2] = (int)(Math.random()*5)+2;
				debrisInfo[i][3] = (int)(Math.random()*170);
			}
		}
		bgVectorX = 0;
		bgVectorY = 0;
		debrisVector = 0;
		
		healthFont = new Font("RusselSquare", Font.PLAIN, (int)(24*(GamePanel.scaleWidth*GamePanel.scaleWidth*GamePanel.scaleWidth)));
		generalFont = new Font("RusselSquare", Font.PLAIN, 24);
		
		super.isFadingIn = true;
		super.alphaLevel = 255;
	}

	public void init() 
	{
		tileMap = new TileMap("Resources/Maps/boss1.txt");
		tileMap.setVector(0, 0);
		tileMap.setY(tileMap.getY() + 175);
		player = new Player(tileMap, this);
		player.setTileMapMoving(false);
		int[] pickupsToSpawn = {Pickups.ARMORBOOST, Pickups.HEALBOOST, Pickups.SLOWTIMEBOOST, Pickups.BIRDBOOST};
		pickups = new Pickups(player, tileMap, this, pickupsToSpawn);
		enemies = new ArrayList<Enemy>();
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
	}

	public void update()
	{
		if(super.isFadingIn)
		{
			super.fadeIn(500000000.0, Color.WHITE, 1);
		}
		basicChecks();
		script();
	}

	public void draw(Graphics2D g)
	{
		bg.draw(g);
		tileMap.draw(g);
		player.draw(g);
		
		pickups.draw(g);
		for(Enemy e: enemies)
			e.draw(g);

		drawCrossHair(g);

		g.setColor(Color.WHITE);
		g.setFont(generalFont);
		g.drawString("Score: " + player.getPoints(), centerStringX("Score: " + player.getPoints(), 0, GamePanel.WIDTH, g), 30);
		
		if(drawBossHealth) drawBossHealth(g);

		super.drawFade(g);
	}
	
	private void drawBossHealth(Graphics2D g) 
	{
		g.setColor(Color.RED);
		g.setFont(healthFont);
		String s = "[";
		for(int i = 0; i < enemies.get(0).getHealth(); i++)
		{
			s += "|";
		}
		for(int i = 0; i < 100-enemies.get(0).getHealth(); i++)
		{
			s += " ";
		}
		s += "]";
		g.drawString(s, centerStringX(s, 0, GamePanel.WIDTH, g), 100);
	}

	private void basicChecks() 
	{
		if(!setUp)
		{
			this.player.setPosition(400, -300);
			//this.player.setHealth(otherPlayer.getHealth());
			setUp = true;
		}
		
		bg.update();
		tileMap.update();
		pickups.update();
		player.update();
		for(int i = 0; i < enemies.size(); i++)
		{
			enemies.get(i).update();
			if(enemies.get(i).getHealth() == 0 && enemies.get(i).getY() > 810)
			{
				enemies.remove(i);
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

		//save data (in this case the points)
		super.data = Integer.toString(player.getPoints());
	}
	
	private void script()
	{
		if(enemies.size() == 0)
		{
			enemies.add(new PlaneBoss(-2000, 400, tileMap, player));
		}
		
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
							step = 1;
						}
					}
					break;
				}
				case 1:
				{
					for(Tile t: tileMap.getTiles())
					{
						if(t.getY() <= 675)
						{
							t.setBulletCollision(false);
						}
					}
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
						((PlaneBoss) enemies.get(0)).setMovement(-2000, 400, 1200, 400, 2, 0);
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
						((PlaneBoss) enemies.get(0)).setMovement(1200, 400, 400, 400, 0.5, 0);
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
							step = 2;
							timer = System.nanoTime();
						}
					}
					break;
				}
					
				//Plane flies from middle of screen to left at 1 speed
				case 2:
				{
					((PlaneBoss) enemies.get(0)).setHealth(100);
					drawBossHealth = true;
					if(((PlaneBoss) enemies.get(0)).getMoveComplete() == false)
					{
						((PlaneBoss) enemies.get(0)).setMovement(400, 400, -1500, 400, 1, 0);
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
							((PlaneBoss) enemies.get(0)).setMovement(-1500, 400, 1500, 400, 1, 0);
						}
						
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
							step = 1;
							((PlaneBoss) enemies.get(0)).setMoveComplete(false);
						}	
						break;
					}

					//Plane flies left and shoots fire bullets
					case 1:
					{
						if(((PlaneBoss) enemies.get(0)).getMoveComplete() == false)
						{
							((PlaneBoss) enemies.get(0)).setMovement(1500, 400, -1500, 400, 1, 3);
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
				System.out.println("WAHHHHHH");
		}
	}
	
	private void drawCrossHair(Graphics2D g) 
	{
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2));
		g.drawLine(mouseX - 5, mouseY, mouseX + 5, mouseY);
		g.drawLine(mouseX, mouseY - 5, mouseX, mouseY + 5);
	}

	//update and draw the debris
	public void debris(Graphics2D g)
	{
		for(int i = 0; i < debrisInfo.length; i++)
		{
			for(int j = 0; j < debrisInfo[i].length; j++)
			{
				g.setColor(colors.get(debrisInfo[i][3]));
				g.fillRect(debrisInfo[i][0], debrisInfo[i][1], debrisInfo[i][2], debrisInfo[i][2]);
				debrisInfo[i][1] += debrisInfo[i][2]*debrisVector;

				if (debrisInfo[i][1] > GamePanel.HEIGHT)
				{
					debrisInfo[i][0] = (int)(Math.random()*GamePanel.WIDTH);
					debrisInfo[i][1] = -20;
					debrisInfo[i][2] = (int)(Math.random()*5)+2;
					debrisInfo[i][3] = (int)(Math.random()*170);
				}
			}
		}
	}

	public void setBackgroundVector(double vectorX, double vectorY)
	{
		bgVectorX = vectorX;
		bgVectorY = vectorY;
		bg.setVector(bgVectorX, bgVectorY);
	}

	public void setDebrisVectors(double vector)
	{
		debrisVector = vector;
	}

	public void setEntitiySpeed(float speed)
	{
		for(Enemy e: enemies)
			e.setSlowDownRate(speed);
	}

	public void keyPressed(int k) 
	{
		player.keyPressed(k);

		if(k == GameStateManager.reset)
		{
			gsm.resetState(GameStateManager.BOSS1STATE);
		}
		if(k == GameStateManager.pause)
		{
			start = false;
		}
	}

	public void keyReleased(int k) 
	{
		player.keyReleased(k);
	}

	@Override

	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
		mouseUpdate = true;
		mouse = e;
	}



	@Override
	public void mouseExited(MouseEvent e) 
	{
		mouseUpdate = false;
		mouse = e;
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			player.setFiring(true);
			player.setMouseHeld(true);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			player.setFiring(false);
			player.setMouseHeld(false);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		mouseX = (int)(e.getX()/GamePanel.scaleWidth);
		mouseY = (int)(e.getY()/GamePanel.scaleHeight);
		relX = mouseX - (int)player.getX();
		relY = mouseY - (int)player.getY();
		player.setAngle(Math.atan2(relY, relX));

	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
		mouseX = (int)(e.getX()/GamePanel.scaleWidth);
		mouseY = (int)(e.getY()/GamePanel.scaleHeight);
		relX = mouseX - (int)player.getX();
		relY = mouseY - (int)player.getY();
		player.setAngle(Math.atan2(relY, relX));
	}
}
