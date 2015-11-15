package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import TileMap.Tile;
import TileMap.TileMap;

public class Projectile extends MapObject
{
	private TileMap tm;
	private double direction;
	private double damage;
	
	private boolean remove;
	
	public Projectile(double x, double y, double direction, int type, TileMap tm) 
	{
		super(tm);
		this.tm = tm;
		
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
				break;
			}
			case 2:
			{
				moveSpeed = 7.0;
				width = 14;
				height = 14;
				damage = 2;
				break;
			}
		}
	}

	public void update() 
	{
		this.myCheckCollision(tileMap);
		dx = Math.cos(direction) * moveSpeed;
		dy = Math.sin(direction) * moveSpeed + tileMap.getDY();
		dx += tm.getDX();
		dy += tm.getDY();
			
		x += dx;
		y += dy;
	}

	public void draw(Graphics2D g) 
	{
		if(!remove)
		{
			g.setColor(Color.BLACK);
			g.fillOval((int)x, (int)y, width, height);
		}
	}
	
	@Override
	public void collided(int type, Tile t) 
	{
		if(t.getType() == 17)
		{
			t.setType(0);
			remove = true;
		}
		else
		{
			remove = true;
		}
	}

	@Override
	public void collided(MapObject m) 
	{
		if(m instanceof Player)
		{
			((Player) m).playerHurt(1);
			remove = true;
		}
		
		if(m instanceof Enemy)
		{
			((Enemy) m).playerHurt(1);
			remove = true;
		}
	}
}
