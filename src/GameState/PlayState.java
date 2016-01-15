package GameState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Entities.Enemy;
import Entities.MapObject;
import Entities.Pickups;
import Entities.Player;
import Main.GamePanel;
import Main.Main;
import TileMap.Background;
import TileMap.TileMap;

public abstract class PlayState extends GameState
{
	protected TileMap tileMap;
	protected Background bg;
	protected double bgVectorX, bgVectorY;
	protected double debrisVector;
	protected static Player player;
	protected Pickups pickups;
	protected ArrayList<Enemy> enemies;
	protected ArrayList<MapObject> mapObjects;
	
	//Mouse
	protected int mouseX;
	protected int mouseY;
	protected int relX;
	protected int relY;
	protected boolean mouseUpdate;
	
	public PlayState()
	{
		super();
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
		for(Enemy e: enemies)
			e.setSlowDownRate(speed);
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
	
	public void drawCrossHair(Graphics2D g) 
	{
		mouseX = (int) (((MouseInfo.getPointerInfo().getLocation().getX() - Main.window.getLocation().getX()) - 3)/GamePanel.scaleWidth);
		mouseY = (int) (((MouseInfo.getPointerInfo().getLocation().getY() - Main.window.getLocation().getY()) - 25)/GamePanel.scaleHeight);
		
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2));
		g.drawLine(mouseX - 5, mouseY, mouseX + 5, mouseY);
		g.drawLine(mouseX, mouseY - 5, mouseX, mouseY + 5);
	}
	
	public Player getPlayer()
	{
		return player;
	}

	public ArrayList<Enemy> getEnemies() 
	{
		return enemies;
	}
	
<<<<<<< HEAD
	public ArrayList<MapObject> getMapObjects() 
	{
		return mapObjects;
	}
=======
	abstract public void slowTimeStart();
	abstract public void slowTimeEnd();
>>>>>>> origin/master
	
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
