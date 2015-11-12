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

import Entities.Player;
import Entities.Pickups;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;


public class PlayState extends GameState
{
	//Mouse
	private int mouseX;
	private int mouseY;
	private int relX;
	private int relY;
	private boolean mouseUpdate;
	private MouseEvent mouse;
	
	private Background bg;
	private TileMap tileMap;
	private Player player;
	private Pickups pickups;
	private int[][] debrisInfo;
	private boolean start, isStillAlive;
	private float timer;
	public static boolean tileStart;
	
	public PlayState(GameStateManager gsm)
	{
		init();
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
		timer = 0;
		debrisInfo = new int[50][4];
		for(int i = 0; i < debrisInfo.length; i++)
		{
			for(int j = 0; j < debrisInfo[i].length; j++)
			{
				debrisInfo[i][0] = (int)(Math.random()*GamePanel.WIDTH);
				debrisInfo[i][1] = (int)(Math.random()*GamePanel.HEIGHT);
				debrisInfo[i][2] = (int)(Math.random()*5)+2;
				debrisInfo[i][3] = (int)(Math.random()*170)+25;
			}
		}
	}

	public void init() 
	{
		tileMap = new TileMap("Resources/Maps/level5.txt");
		player = new Player(tileMap);
		pickups = new Pickups(player, tileMap);
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
		if(player.getPlayerHealth() < 1 && timer > 1000000000.0)
		{
			super.isFadingOut = true;
			super.fadeOut(500000000, gsm, GameStateManager.PLAYSTATE, GameStateManager.OUTROSTATE);
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
	
	private void drawCrossHair(Graphics2D g) 
	{
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2));
		g.drawLine(mouseX - 5, mouseY, mouseX + 5, mouseY);
		g.drawLine(mouseX, mouseY - 5, mouseX, mouseY + 5);
	}

	public void debris(Graphics2D g)
	{
		for(int i = 0; i < debrisInfo.length; i++)
		{
			for(int j = 0; j < debrisInfo[i].length; j++)
			{
				g.setColor(new Color(debrisInfo[i][3], debrisInfo[i][3], debrisInfo[i][3]));
				g.fillOval(debrisInfo[i][0], debrisInfo[i][1], debrisInfo[i][2], debrisInfo[i][2]);
				debrisInfo[i][1] += debrisInfo[i][2]*.75;
				
				if (debrisInfo[i][1] > GamePanel.HEIGHT)
				{
					debrisInfo[i][0] = (int)(Math.random()*GamePanel.WIDTH);
					debrisInfo[i][1] = -20;
					debrisInfo[i][2] = (int)(Math.random()*5)+2;
					debrisInfo[i][3] = (int)(Math.random()*170)+25;
				}
			}
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
						tileMap.setYVector(2.0);		
					}
					
				}, 4000);
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
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			player.setFiring(false);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		mouseX = e.getX();
		mouseY = e.getY();
		relX = mouseX - (int)player.getX();
		relY = mouseY - (int)player.getY();
		player.setAngle(Math.atan2(relY, relX));
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
		mouseX = e.getX();
		mouseY = e.getY();
		relX = mouseX - (int)player.getX();
		relY = mouseY - (int)player.getY();
		player.setAngle(Math.atan2(relY, relX));
	}
}
