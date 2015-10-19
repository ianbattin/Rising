package Entities;

import Main.GamePanel;
import java.awt.Color;
import java.awt.Graphics2D;

public class Pickups {	
	
	private Player player;
	
	private long coolDownTime;
	private boolean willDrawPickup;
	private float xLoc, yLoc;
	private float xVelocity;
	
	public Pickups(Player player)
	{
		this.player = player;
		
		coolDownTime = 10000000000L;
		willDrawPickup = false;
		xLoc = yLoc = 0;
		xVelocity = 0;
	}

	//updates by checking to see if it should spawn a pickup.
	//TODO: move code out of update and into methods
	public void update()
	{	
		if	(!willDrawPickup)
		{
			if (coolDownTime > 0)
			{
				coolDownTime -= GamePanel.getElapsedTime();
			} 
			else 
			{
				if ((int)(Math.random()*0) == 0)//edit probability of spawning here
				{
					coolDownTime = 100000000000L;
					xLoc = (int)(Math.random()*(GamePanel.WIDTH-150))+75;
					if (((int)(Math.random()*3)-1) > 0)
						xVelocity = 1;
					else 
						xVelocity = -1;
						
					willDrawPickup = true;
				}
				else
				{
					coolDownTime = 10000000000L;
				}
			}
		} 
		else
		{
			checkCollision();
		}
	}
	
	//draws the pickup
	public void draw(Graphics2D g)
	{
		if(willDrawPickup)
		{
			g.setColor(new Color(0, 0, 0));
			g.fillRect((int)xLoc, (int)yLoc, 25, 25);
			
			if (yLoc < GamePanel.HEIGHT)
				yLoc += 0.5;
			else 
				willDrawPickup = false;
			xLoc += xVelocity;
			
			if((xLoc+100) > GamePanel.WIDTH && xVelocity > -1)
			{
				xVelocity -= 0.0075;
			} 
			else if(xLoc < 75 && xVelocity < 1)
			{
				xVelocity += 0.0075;
			}
			else if (xVelocity != 1 || xVelocity != -1)
			{
				xVelocity = xVelocity/Math.abs(xVelocity);
			}
		}
	}
	
	//checks if the player collided with the pickup
	public void checkCollision()
	{
		if ((player.getX()-player.getWidth()/2) < xLoc+5 && xLoc-5 < (player.getX()+player.getWidth()/2) && (player.getY()-player.getHeight()/2) < yLoc-5 && yLoc+5 < (player.getY()+player.getHeight()/2))
			willDrawPickup = false;
			
	}
}
