package Entities;

import Main.GamePanel;
import java.awt.Color;
import java.awt.Graphics2D;

public class Pickups {	
	
	private Player player;
	
	private long coolDownTime;
	private boolean willDrawPickup;
	private float xLoc, yLoc, offSet;
	
	public Pickups(Player player)
	{
		this.player = player;
		
		coolDownTime = 10000000000L;
		willDrawPickup = false;
		xLoc = GamePanel.WIDTH/2;
		yLoc = -50;
		offSet = (float)(Math.random()*GamePanel.HEIGHT);
		System.out.println(offSet);
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
		else 
		{
			if ((int)(Math.random()*0) == 0)//edit probability of spawning here
			{
				coolDownTime = 100000000000L;
				willDrawPickup = true;
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
			g.setColor(new Color(0, 0, 0));
			g.fillRect((int)xLoc, (int)yLoc, 25, 25);
			
			if (yLoc < GamePanel.HEIGHT)
				yLoc += 0.5;
			else 
				willDrawPickup = false;
			xLoc = (float)(Math.sin((yLoc-offSet)/(GamePanel.HEIGHT/4))*(GamePanel.WIDTH/2-50))+GamePanel.WIDTH/2;
		}
	}
	
	//checks if the player collided with the pickup
	public void checkCollision()
	{
		if ((player.getX()-(player.getWidth()/2)) < xLoc+10 && xLoc-10 < (player.getX()+(player.getWidth()/2)) && (player.getY()-player.getHeight()/2) < yLoc+10 && yLoc-10 < (player.getY()+player.getHeight()/2))
			willDrawPickup = false;
	}
}
