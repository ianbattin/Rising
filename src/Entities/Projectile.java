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
	private int damage;
	private int type;
	
	private boolean remove;
	private boolean playerCollide;
	
	private long lifeTime;
	
	//timeslow variable
	private static double slowTime;
	
	public Projectile(double x, double y, double direction, int type, TileMap tm) 
	{
		super(tm);
		this.tm = tm;
		
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.type = type;
		
		slowTime = 1;
		
		lifeTime = 0;
		
		remove = false;

		switch (this.type)
		{
			case 1: 
			{
				moveSpeed = 15.0;
				width = 7;
				height = 7;
				damage = 1;
				playerCollide = true;
				break;
			}
			case 2:
			{
				moveSpeed = 7.0;
				width = 14;
				height = 14;
				damage = 2;
				playerCollide = true;
				break;
			}
			case 3:
			{
				moveSpeed = 25.0;
				width = 10;
				height = 10;
				damage = 0;
				playerCollide = false;
				break;
			}
			case 4:
			{
				moveSpeed = 8.0;
				width = 15;
				height = 15;
				damage = 0;
				playerCollide = false;
				break;
			}
		}
	}

	public void update() 
	{
		this.myCheckCollision(tileMap);
		dx = Math.cos(direction) * (moveSpeed*slowTime);
		dy = Math.sin(direction) * (moveSpeed*slowTime);
		dx += tm.getDX();
		dy += tm.getDY();
			
		x += dx;
		y += dy;
		
		lifeTime++;
		
		//y += tm.getDY();
		//x += tm.getDX();
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
		if(t.getBulletCollision())
		{
			if(t.getType() == 17)
			{
				t.setType(0);
				remove = true;
			}
			else if(this.type == 3)
			{
				t.setType(0);
				remove = true;
			}
			else if(this.type == 4)
			{
				getTiles().add(new Tile(t.getX(), t.getY() - 25, 17, t.getSize(), tileMap));
				getTiles().get(getTiles().size()-1).init();
				remove = true;
			}
			else
				remove = true;
		}
	}

	@Override
	public void collided(MapObject m) 
	{
		if(playerCollide)
		{
			if(m instanceof Player)
			{
				((Player) m).playerHurt(damage);
				remove = true;
			}

			if(m instanceof Enemy)
			{
				((Enemy) m).playerHurt(damage);
				remove = true;
			}
			
			if(m instanceof PlaneBoss)
			{
				((PlaneBoss) m).playerHurt(damage);
				remove = true;
			}
		}
	}
	
	public boolean getRemove()
	{
		return remove;
	}
	
	public void setSlowTime(double slowTime)
	{
		this.slowTime = slowTime;
	}

	public long getLifeTime() 
	{
		return lifeTime;
	}
}
