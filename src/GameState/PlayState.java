package GameState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Entities.Bomb;
import Entities.Enemy;
import Entities.Jetpacker;
import Entities.MapObject;
import Entities.Pickups;
import Entities.Player;
import Entities.Projectile;
import Main.GamePanel;
import Main.Main;
import TileMap.Background;
import TileMap.TileMap;

public abstract class PlayState extends GameState
{
	protected TileMap tileMap;
	protected double bgVectorX, bgVectorY;
	protected double debrisVector;
	protected static Player player;
	protected Pickups pickups;
	protected ArrayList<Enemy> enemies;
	protected ArrayList<MapObject> mapObjects;
	protected boolean start;
	private ArrayList<int[]> bonusScores;
	protected String[] notStarted;
	protected Font bonusScoreFont, scoreFont, backupFont;
	
	public final static int SPAWN_BOMB = 0;
	public final static int SPAWN_PARACHUTER = 1;
	public final static int SPAWN_WALKER = 2;
	public final static int SPAWN_DEBRIS = 3;
	protected static ArrayList<int[]> itemsToSpawn;
	
	//Mouse
	protected int mouseX;
	protected int mouseY;
	protected int relX;
	protected int relY;
	protected boolean mouseUpdate;
	
	public PlayState()
	{
		super();
		
		itemsToSpawn = new ArrayList<int[]>();
		
		backupFont = new Font("Times", Font.PLAIN, 24);
		scoreFont = new Font("Munro", Font.PLAIN, 24);				
		bonusScoreFont = new Font("Munro", Font.BOLD, 35);
	}
	
	public void init()
	{
		bonusScores = player.getBonusScores();
		
		enemies = new ArrayList<Enemy>();
		mapObjects = new ArrayList<MapObject>();
		
		//create the pause text
		notStarted = new String[4];
		notStarted[0] = "GAME PAUSED";
		notStarted[1] = " ";
		notStarted[2] = "Press " + KeyEvent.getKeyText(GameStateManager.select) + " to resume";
		notStarted[3] = "Press "+ KeyEvent.getKeyText(GameStateManager.reset) +" to return to the menu" ;
	}
	
	public void setBackgroundVector(double vectorX, double vectorY)
	{
		bgVectorX = vectorX;
		bgVectorY = vectorY;
		bg.setVector(bgVectorX, bgVectorY);
	}
	
	public void setBackgroundPosition(double x, double y)
	{
		bg.setPosition(x, y);
	}
	
	public void setBackgroundXVector(double vectorX) 
	{
		bgVectorX = vectorX;
		bg.setXVector(bgVectorX);
	}
	
	public void setDebrisVectors(double vector)
	{
		debrisVector = vector;
	}
	
	public void setEntitiySpeed(float speed)
	{
		Enemy.setSlowDownRate(speed);
		Projectile.setSlowTime(speed);
	}

	public void backGroundParallaxUpdate()
	{
		if(tileMap.getMoving())
		{
			if(player.getY() < 300) 
			{
				if(Level1State.tileStart && tileMap.getMoving())
				{
					if (player.getDY() >= 0)
					{
						bg.setYVector(bgVectorY);
					}
					else
					{
						bg.setYVector(bgVectorY - player.getDY()/5);
					}
				}
			}
		}
		if (player.getDX() >= 0)
		{
			bg.setXVector(bgVectorX + player.getDX()/5);
		}
		else
		{
			bg.setXVector(bgVectorX - player.getDX()/5);
		}
	}
	
	public void aimUpdate()
	{
		if(mouseUpdate)
		{
			mouseX = (int) (((MouseInfo.getPointerInfo().getLocation().getX() - Main.window.getLocation().getX()) - 3)/GamePanel.scaleWidth);
			mouseY = (int) (((MouseInfo.getPointerInfo().getLocation().getY() - Main.window.getLocation().getY()) - 25)/GamePanel.scaleHeight);
			relX = mouseX - (int)player.getX() - player.getWidth()/2;
			relY = mouseY - (int)player.getY() - player.getHeight()/2;
			player.setAngle(Math.atan2(relY, relX));
		}
	}
	
	public void updateBonusScores()
	{
		for(int i = 0; i < bonusScores.size(); i++)
		{
			if(bonusScores.get(i)[1] > 0)
			{
				bonusScores.get(i)[1]--;
			}
			else
			{
				bonusScores.remove(i);
				i--;
			}
		}
	}
	
	public void drawCrossHair(Graphics2D g) 
	{
		mouseX = (int) (((MouseInfo.getPointerInfo().getLocation().getX() - Main.window.getLocation().getX()) - 3)/GamePanel.scaleWidth);
		mouseY = (int) (((MouseInfo.getPointerInfo().getLocation().getY() - Main.window.getLocation().getY()) - 25)/GamePanel.scaleHeight);
		
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2));
		g.drawLine(mouseX - 5, mouseY, mouseX + 5, mouseY);
		g.drawLine(mouseX, mouseY - 5, mouseX, mouseY + 5);
	}
	
	public void drawBonusScores(Graphics2D g)
	{
		if(!bonusScores.isEmpty())
		{
			for(int i = 0; i < bonusScores.size(); i++)
			{
				g.setColor(new Color(85, 213, 10, bonusScores.get(i)[1]));
				g.setFont(bonusScoreFont);
				g.drawString("+" + bonusScores.get(i)[0], centerStringX("+" + bonusScores.get(i)[0], 0, GamePanel.WIDTH, g), 35 + (255-bonusScores.get(i)[1])/2);
			}
		}
	}
	
	public void drawPause(Graphics2D g)
	{
		for(int i = 0; i < notStarted.length; i++)
		{
			int offSet = 0;
			for(int j = 0; j < notStarted[i].length(); j++)
			{
				if(!scoreFont.canDisplay(notStarted[i].charAt(j)))
					g.setFont(backupFont);
				else
					g.setFont(scoreFont);
				g.drawChars(notStarted[i].toCharArray(), j, 1, centerStringX(notStarted[i], 0, GamePanel.WIDTH, g) + offSet, 400 + (40 *i));
				offSet += g.getFontMetrics().charWidth(notStarted[i].charAt(j));
			}
		}
	}
	
	public Player getPlayer()
	{
		return player;
	}

	public ArrayList<Enemy> getEnemies() 
	{
		return enemies;
	}
	
	public ArrayList<MapObject> getMapObjects() 
	{
		return mapObjects;
	}
	
	abstract public void slowTimeStart();
	abstract public void slowTimeEnd();
	
	public static void spawnObject(int type, int xLoc, int yLoc)
	{
		int[] item = {type, xLoc, yLoc};
		itemsToSpawn.add(item);
	}

	
	public void mouseClicked(MouseEvent e)
	{
		mouseUpdate = true;
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
		mouseUpdate = true;
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
		mouseUpdate = false;
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		mouseUpdate = true;
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			player.setFiring(true);
			player.setMouseHeld(true);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		mouseUpdate = true;
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			player.setFiring(false);
			player.setMouseHeld(false);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		mouseUpdate = true;
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
		mouseUpdate = true;
	}
}
