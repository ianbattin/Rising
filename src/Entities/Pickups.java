package Entities;

import Main.GamePanel;
import java.awt.Color;
import java.awt.Graphics2D;

public class Pickups {	
	
	private Player player;
	
	private long coolDownTime;
	private boolean willDrawPickup, isUnderEffect;
	private float xLoc, yLoc, offSet;
	
	public Pickups(Player player)
	{
		this.player = player;
		
		coolDownTime = 10000000000L;
		
		init();
	}
	
	public void init()
	{
		willDrawPickup = false;
		isUnderEffect = false;
		
		//sets starting points for the spawning of the pickups
		xLoc = GamePanel.WIDTH/2;
		yLoc = -50;
		offSet = (float)(Math.random()*GamePanel.HEIGHT);
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
				init();
			xLoc = (float)(Math.sin((yLoc-offSet)/(GamePanel.HEIGHT/4))*(GamePanel.WIDTH/2-50))+GamePanel.WIDTH/2;
		}
	}
	
	//checks if the player collided with the pickup
	public void checkCollision()
	{
		if ((player.getX()-(player.getWidth()/2)) < xLoc+35 && xLoc-10 < (player.getX()+(player.getWidth()/2)) && (player.getY()-player.getHeight()/2) < yLoc && yLoc < (player.getY()+player.getHeight()/2))
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
