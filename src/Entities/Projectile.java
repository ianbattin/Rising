package Entities;

import java.awt.Color;
import java.awt.Graphics2D;

import TileMap.Tile;
import TileMap.TileMap;

public class Projectile extends MapObject
{
	private double direction;
	private double damage;
	
	private boolean remove;
	
	public Projectile(double x, double y, double direction, int type, TileMap tm) 
	{
		super(tm);
		
		this.x = x;
		this.y = y;
		this.direction = direction;
		
		remove = false;

		switch (type)
		{
			case 1: 
			{
				moveSpeed = 15.0;
				width = 7;
				height = 7;
				damage = 1;
			}
		}
	}

	public void update() 
	{
		if(!remove)
		{
			this.myCheckCollision(tileMap);
			dx = Math.cos(direction) * moveSpeed;
			dy = Math.sin(direction) * moveSpeed + tileMap.getDY();
			
			x += dx;
			y += dy;
		}
		
	}

	public void draw(Graphics2D g) 
	{
		g.setColor(Color.BLACK);
		g.fillOval((int)x, (int)y, width, height);
	}

	public void collided(int type, Tile t, MapObject m) 
	{
		if(m instanceof Player)
		{
			m.setHealth(m.getHealth()-1);
			remove = true;
		}
		
	}

	@Override
	public void collided(int type, Tile t) 
	{
		System.out.println("Kinda Working");
		if(t.getType() == 17)
		{
			System.out.println("Working");
			t.setType(0);
		}
	}
}
