package Entities; 

import Main.GamePanel;
import TileMap.TileMap;
import java.awt.Color;
import java.awt.Graphics2D;

public class Pickups {	
	
	private Player player;
	private TileMap tileMap;
	
	private final float tileMapWidth;
	
	private long coolDownTime;
	private boolean willDrawPickup, isUnderEffect;
	private double xLoc, yLoc, startingPositionOffset, tmDyPositionOffset, xShift;
	
	public Pickups(Player player, TileMap tileMap)
	{
		this.player = player;
		this.tileMap = tileMap;
		
		coolDownTime = 10000000000L;
		
		tileMapWidth = tileMap.getTileMapWidth()/2;
		
		init();
	}
	
	public void init()
	{
		//reset values
		willDrawPickup = false;
		isUnderEffect = false;	
		yLoc = -100;
		xLoc = 0;
		tmDyPositionOffset = 0;
		xShift = 0;
	}

	//updates by checking to see if it should spawn a pickup.
	public void update()
	{	
		if	(!willDrawPickup)
		{
			checkPickups();
		}
		else
		{
			checkCollision();
		}
	}
	
	//check to see if should spawn a pickup
	public void checkPickups()
	{
		if (coolDownTime > 0)
		{
			coolDownTime -= GamePanel.getElapsedTime();
		} 
		else if (isUnderEffect)
		{
			player.resetEffects();
			coolDownTime = 100000000000L;
			init();
		}
		else 
		{
			if ((int)(Math.random()*0) == 0)//edit probability of spawning here
			{
				coolDownTime = 100000000000L;
				willDrawPickup = true;
				
				//sets starting points for the spawning of the pickups
				startingPositionOffset = -(Math.random()*GamePanel.HEIGHTSCALED/2);
			}
			else
			{
				coolDownTime = 10000000000L;
			}
		}
	}
	
	//draws the pickup
	public void draw(Graphics2D g)
	{
		if(willDrawPickup)
		{									
			if (yLoc < GamePanel.HEIGHT + 50)
			{
				yLoc += 0.5 + ((tileMap.getDY() - 2)*0.25);
				if (tileMap.getDY() > 2){
					tmDyPositionOffset += ((tileMap.getDY() - 2)*0.25);
				}
			}
			else 
			{
				init();
			}
			xLoc = tileMapWidth + (Math.sin((yLoc-startingPositionOffset-tmDyPositionOffset)/100))*200;
			xShift += tileMap.getDX();
						
			g.setColor(new Color(0, 0, 0));
			g.fillRect((int)(xLoc+xShift), (int)(yLoc), 25, 25);
		}
	}
	
	//checks if the player collided with the pickup
	public void checkCollision()
	{
		if ((player.getX()-(player.getWidth()/2)) < (xLoc+xShift)+35 && (xLoc+xShift)-10 < (player.getX()+(player.getWidth()/2)) && (player.getY()-player.getHeight()/2) < yLoc && yLoc < (player.getY()+player.getHeight()/2))
		{
			effectStart();
			willDrawPickup = false;
			isUnderEffect = true;
			coolDownTime = 10000000000L;
		}
	}
	
	public void effectStart()
	{
		int rand = (int)(Math.random()*0);//increase 0 to increase possibilities
		player.effectStart(rand);
	}
	
}
