package Entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Main.GamePanel;
import TileMap.Tile;
import TileMap.TileMap;

//A MapObject is any collision enabled character that moves (Player, Enemies...)
public abstract class MapObject 
{
	//tile stuff
	protected ArrayList<Tile> tiles;
	protected TileMap tileMap;
	protected int tileSize;
	protected double xmap;
	protected double ymap;
	
	//position and vector
	protected double x;
	protected double y;
	protected double dx;
	protected double dy;
	
	//character position relative to bottom of tileMap
	protected double yFromBottom;
	
	//dimensions
	protected int width;
	protected int height;
	
	//collision box
	protected int cwidth;
	protected int cheight;
	
	//collision
	protected int currRow;
	protected int currCol;
	protected double xdest;
	protected double ydest;
	protected double xtemp;
	protected double ytemp;
	protected boolean topLeft;
	protected boolean topRight;
	protected boolean bottomLeft;
	protected boolean bottomRight;
	
	//health
	protected int health;
	protected boolean recovering;
	protected long recoverTimer;
	protected int recoverLength;
	
	//animation
	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingRight;
	
	//movement
	protected boolean idle;
	protected boolean left;
	protected boolean right;
	protected boolean jump;
	protected boolean jumped;
	protected boolean doubleJump;
	protected boolean doubleJumped;
	protected boolean doubleJumpable; //Prevents you from double-jumping in the same key press you initially jumped with (Player must tap jump twice basically)
	protected boolean falling;
	protected boolean fallingAnim;
	protected boolean landing;
	protected boolean gliding;
	protected boolean drop;
	
	//movement attributes
	protected double moveSpeed;
	protected double maxSpeed;
	protected double maxSpeedY;
	protected double maxSpeedX;
	protected double stopSpeed;
	protected double fallSpeed;
	protected double maxFallSpeed;
	protected double jumpHeight;
	protected double stopJump;
	protected double jumpStart;
	
	public MapObject(TileMap tm)
	{
		tileMap = tm;
		tiles = tm.tiles;
		tileSize = tm.getTileSize();
	}
	
	public abstract void collided(int type, Tile t);
	public abstract void collided(MapObject m);
	public abstract void update();
	public abstract void draw(Graphics2D g);
	
	public boolean intersects(MapObject other)
	{
		Rectangle r1 = getRectangle();
		Rectangle r2 = other.getRectangle();
		return r1.intersects(r2);
	}
	
	public boolean intersects(Tile other)
	{
		Rectangle r1 = getRectangle();
		Rectangle r2 = other.getRectangle();
		return r1.intersects(r2);
	}
	
	public Rectangle getRectangle()
	{
		return new Rectangle((int)x, (int)y, width, height);
	}
	
	public void calculateCorners(double x, double y)
	{
		int leftTile = (int)(x - cwidth / 2) / tileSize;
		int rightTile = (int)(x + cwidth / 2 - 1) / tileSize;
		int topTile = (int)(y - cheight / 2) / tileSize;
		int bottomTile = (int)(y + cwidth / 2 - 1) / tileSize;
		
		int t1 = tileMap.getType(topTile, leftTile);
		int tr = tileMap.getType(topTile, rightTile);
		int bl = tileMap.getType(bottomTile, leftTile);
		int br = tileMap.getType(bottomTile, rightTile);
		
		topLeft = t1 != Tile.AIR;
		topRight = tr == Tile.AIR;
		bottomLeft = bl == Tile.AIR;
		bottomRight = br == Tile.AIR;
		
	}
	
	public void myCheckCollision(TileMap tm)
	{
		boolean collided = false;
		for(int i = 0; i < tiles.size(); i++)
		{
			Tile t = tiles.get(i);
			double collisionLeft = t.left;
			double collisionRight = t.right;
			double collisionTop = t.top;
			double collisionBottom = t.bottom;

			if((collisionLeft <= x && x < collisionRight) && (collisionTop <= y + height/2 && y + height/2 < collisionBottom) && !drop)
			{
				if(t.getType() < 17 || (32 <= t.getType() && t.getType() <= 57))
				{
					if(dy >= 0)
					{
						y = t.top - cheight/2 - 0.5;
						dy = tm.getDY();
						jumped  = false;
						doubleJumped = false;
						falling = false;
						//landing = true;
						//gliding = false;
						idle = true;
						fallingAnim = false;
					}
					collided(t.getType(), t);
				}
				else
				{
					collided(t.getType(), t);
				}
					
			}
			if(!collided && (collisionLeft <= x && x < collisionRight) && (collisionTop <= y + height/2 + 1 && y + height/2 + 1 < collisionBottom && !drop)) 
			{
				collided = true;
			}
			
		}
		if(!collided && !jumped && !doubleJumped)
		{
			falling = true;
		}
	}
	
	public void checkTileMapCollision()
	{
		currCol = (int)x / tileSize;
		currRow = (int)y / tileSize;
		
		xdest = x + dx;
		ydest = y + dy;
		
		xtemp = x;
		ytemp = y;
		
		calculateCorners(x, ydest);
		if(dy < 0)
		{
			if(topLeft || topRight)
			{
				dy = 0;
				ytemp = currRow * tileSize + cheight / 2;
			}
			else
				ytemp += dy;
		}
		if(dy > 0)
		{
			if(bottomLeft || bottomRight)
			{
				dy = 0;
				falling = false;
				ytemp = (currRow + 1) * tileSize - cheight / 2;
			}
			else
				ytemp += dy;
		}
		calculateCorners(xdest, y);
		if(dx < 0)
		{
			if(topLeft || bottomLeft)
			{
				dx = 0;
				xtemp = currCol * tileSize + cwidth / 2;
			}
			else
				xtemp += dx;
		}
		if(dx > 0)
		{
			if(topRight || bottomRight)
			{
				dx = 0;
				xtemp = (currCol +1) * tileSize - cwidth / 2;
			}
			else
				xtemp += dx;
		}
		
		if(!falling)
		{
			calculateCorners(x, ydest + 1);
			if(!bottomLeft && !bottomRight)
			{
				falling = true;
			}
		}
	}

	public void setX(double x) {	this.x = x;	}
	public void setY(double y) { this.y = y;	}
	public double getX() {	return x;	}
	public double getY() {	return y;	}
	public double getDX() {	return dx; }
	public double getDY() {	return dy;	}
	public int getWidth() {	return width;	}
	public int getHeight() {	return height;	}
	public int getCWidth() {	return cwidth;	}
	public int getCHeight() {	return cheight;	}
	
	public ArrayList<Tile> getTiles()
	{
		return tiles;
	}
	
	public void addTile(Tile t)
	{
		tiles.add(t);
	}
	
	public void setPosition(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public void setVector(double dx, double dy)
	{
		this.dx = dx;
		this.dy = dy;
	}
	
	public void setMapPosition()
	{
		xmap = tileMap.getX();
		ymap = tileMap.getY();
	}
	
	public int getHealth()
	{
		return health;
	}
	
	public void setHealth(int health)
	{
		this.health = health;
	}
	
	public void setXVector(double dx) 
	{
		this.dx = dx;
	}
	public void setYVector(double dy) 
	{
		this.dy = dy;
	}
	
	public void setLeft(boolean b) { 	left = b;	}
	public void setRight(boolean b) { 	right = b;	}
	public void setUp(boolean b) { 	jump = b;	}
	public void setDown(boolean b) { 	drop = b;	}
	public void setJumping(boolean b) { 	jump = b;	}
	
	public boolean notOnScreen()
	{
		return x + xmap + width < 0 || x + xmap - width > GamePanel.WIDTH || 
				y + ymap + height < 0 || y + ymap - height > GamePanel.HEIGHT;
	}
}