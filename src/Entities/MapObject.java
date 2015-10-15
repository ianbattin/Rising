package Entities;

import java.awt.Rectangle;
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
	
	//animation
	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingRight;
	
	//movement
	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;
	protected boolean jumping;
	protected boolean falling;
	protected boolean jumped;
	
	//movement attributes
	protected double moveSpeed;
	protected double maxSpeed;
	protected double stopSpeed;
	protected double fallSpeed;
	protected double maxFallSpeed;
	protected double jumpHeight;
	protected double stopJump;
	
	public MapObject(TileMap tm)
	{
		tileMap = tm;
		tiles = tm.tiles;
		tileSize = tm.getTileSize();
	}
	
	public boolean intersects(MapObject other)
	{
		Rectangle r1 = getRectangle();
		Rectangle r2 = other.getRectangle();
		return r1.intersects(r2);
	}
	
	public Rectangle getRectangle()
	{
		return new Rectangle((int)x - cwidth, (int)y - cheight, cwidth, cheight);
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
		
		topLeft = t1 == Tile.BLOCKED;
		topRight = tr == Tile.BLOCKED;
		bottomLeft = bl == Tile.BLOCKED;
		bottomRight = br == Tile.BLOCKED;
		
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

	public double getX() {	return x;	}
	public double getY() {	return y;	}
	public int getWidth() {	return width;	}
	public int getHeight() {	return height;	}
	public int getCWidth() {	return cwidth;	}
	public int getCHeight() {	return cheight;	}
	
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
	
	public void setLeft(boolean b) { 	left = b;	}
	public void setRight(boolean b) { 	right = b;	}
	public void setUp(boolean b) { 	up = b;	}
	public void setDown(boolean b) { 	down = b;	}
	public void setJumping(boolean b) { 	jumping = b;	}
	
	public boolean notOnScreen()
	{
		return x + xmap + width < 0 || x + xmap - width > GamePanel.WIDTH || 
				y + ymap + height < 0 || y + ymap - height > GamePanel.HEIGHT;
	}
}