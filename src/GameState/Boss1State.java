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
import Entities.Enemy;
import Entities.Jetpacker;
import Entities.Pickups;
import Main.GamePanel;
import TileMap.Background;
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

	private Background bg;
	private Player player;
	private Player otherPlayer;
	private Pickups pickups;
	private ArrayList<Enemy> enemies;
	private int[][] debrisInfo;
	private ArrayList<Color> colors;
	private boolean start, isStillAlive;
	private float timer;
	public static boolean tileStart;
	private TileMap tileMap;
	
	private boolean setUp = false;

	private double bgVectorX, bgVectorY;
	private double debrisVector;

	public Boss1State(GameStateManager gsm, Player player)
	{
		init();
		
		otherPlayer = player;
		
		this.gsm = gsm;
		start = false;
		timer = 0;
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
	}

	public void init() 
	{
		tileMap = new TileMap("Resources/Maps/boss1.txt");
		tileMap.setVector(0, 0);
		player = new Player(tileMap, this);
		pickups = new Pickups(player, tileMap, this);
		enemies = new ArrayList<Enemy>();
		tileStart = false;
		try
		{
			bg = new Background("/Backgrounds/gamebackground.gif", 1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setBackgroundVector(10.0, 0);
		setDebrisVectors(1);
	}

	public void update()
	{
		if(!setUp)
		{
			this.player.setPosition(otherPlayer.getX(), otherPlayer.getY());
			this.player.setXVector(otherPlayer.getDX());
			this.player.setYVector(otherPlayer.getDY());
			this.player.setHealth(otherPlayer.getHealth());
			setUp = true;
		}
		
		bg.update();
		tileMap.update();
		pickups.update();
		player.update();
		for(Enemy e: enemies)
			e.update();

		if(player.getPlayerHealth() < 1 && timer > 1500000000.0)
		{
			super.isFadingOut = true;
			super.fadeOut(500000000, gsm, GameStateManager.LEVEL1STATE, GameStateManager.OUTROSTATE);
		}
		else if (player.getPlayerHealth() < 1)
		{
			timer += GamePanel.getElapsedTime();
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

		drawCrossHair(g);

		g.setColor(Color.WHITE);
		g.setFont(new Font("RusselSquare", Font.PLAIN, 24));
		g.drawString("Score: " + player.getPoints(), centerStringX("Score: " + player.getPoints(), 0, GamePanel.WIDTH, g), 30);

		super.drawFade(g);
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
