package TileMap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Entities.Animation;
import GameState.Level1State;
import GameState.PlayState;
import Main.GamePanel;

public class Tile 
{
	private double x;
	private double y;
	private double dx;
	private double dy;
	
	public double left;
	public double right;
	public double top;
	public double bottom;
	
	private int size;
	
	private TileMap tm;
	
	private Animation animation;
	private int frames;
	private boolean animated = false;
	
	private boolean bulletCollision;
	private boolean isVisible;
	
	private int type;
	private boolean blocked = true;
	public static final int AIR = 0; //able to pass through
	
	private BufferedImage[] images;
	
	public Tile(double x, double y, int type, int size, TileMap tm)
	{
		this.x = x;
		this.y = y;
		
		dx = 0;
		dy = 0;
		
		//these are for collision
		left = x;
		right = x + size;
		top = y;
		bottom = y + size;
		
		this.tm = tm;
		
		this.type = type;
		this.size = size;
		animation = new Animation();
		isVisible = true;

		if(type < 17)
		{
			frames = 1;
			bulletCollision = true;
		}
		else if(type == 17) 
		{
			frames = 3;
			blocked = false;
			animated = true;
			bulletCollision = true;
		}
		else if(type == 20)
		{
			frames = 4;
			blocked = true;
			animated = false;
			bulletCollision = false;
		}
		else if(type >= 446 && type <= 449)
		{
			frames = 1;
			blocked = false;
			animated = false;
			bulletCollision = false;
			isVisible = false;
		}
		else
		{
			bulletCollision = true;
			frames = 1;
		}
		
		images = new BufferedImage[frames];
	}
	
	public void init()
	{
		for(int i = 0; i < frames; i++)
		{
			images[i] = TileMap.getSprite(type + i);
		}
		animation.setFrames(images);
		animation.setDelay(100);
	}
	
	public void update(double dx, double dy)
	{
		//for when we have to move the tiles as the player ascends
		this.x += dx;
		this.y += dy;
		
		left = x;
		right = x + size;
		top = y;
		bottom = y + size;
		
		if(animated) 
		{ 
			animation.update(); 
		}
		
		if(type == 0)
		{
			setX(-100);
			setY(100000);
		}
		//these are the special blocks, each will have their own special function 
		else if(type == 446 && y > -10 && y < 0 && -100 <= x && x <= GamePanel.WIDTH+100)
		{
			PlayState.spawnObject(PlayState.SPAWN_DEBRIS, (int)x, (int)y);
			type = 0;
		}
		else if(type == 447 && y > -100 && y < -75 && -100 <= x && x <= GamePanel.WIDTH+100)
		{
			PlayState.spawnObject(PlayState.SPAWN_WALKER, (int)x, (int)y);
			type = 0;
		}
		else if(type == 448 && y > -100 && y < -75 && -100 <= x && x <= GamePanel.WIDTH+100)
		{
			PlayState.spawnObject(PlayState.SPAWN_PARACHUTER, (int)x, 0);
			type = 0;
		}
		else if(type == 449 && y > -100 && y < -75 && -100 <= x && x <= GamePanel.WIDTH+100)
		{
			PlayState.spawnObject(PlayState.SPAWN_BOMB, (int)x, 0);
			type = 0;
		}
	}
	
	public void draw(Graphics2D g, int type)
	{
		if(onScreen() && isVisible)
		{
			g.drawImage(animation.getImage(), (int)x, (int)y, size+1, size, null);
		}
		
		if(tm.getShowCollisonBox())
		{
			g.setColor(Color.RED);
			g.drawLine((int)left, (int)top, (int)right, (int)top);
			g.drawLine((int)right, (int)top, (int)right, (int)bottom);
			g.drawLine((int)right, (int)bottom, (int)left, (int)bottom);
			g.drawLine((int)left, (int)bottom, (int)left, (int)top);
		}
	}
	
	public boolean onScreen()
	{
		return (-size <= x && x <= GamePanel.WIDTH+size && -size <= y && y <= GamePanel.HEIGHT+size);
	}
	
	public boolean pastBottom()
	{
		return (y > GamePanel.HEIGHT + 200);
	}
		
	public void setType(int type, int frames, boolean animate)
	{
		this.type = type;
		this.animated = animate;
		this.images = new BufferedImage[frames];
		for(int i = 0; i < frames; i++)
		{
			images[i] = TileMap.getSprite(type + i);
		}
		animation.setFrames(images);
		animation.setDelay(100);
	}
	public void setType(int type) 
	{
		this.type = type;	
	}
	public int getType() {	return type;	}
	public double getX(){	return x;	}
	public double getY(){	return y;	}
	public int getSize() { return size; }
	public void setAnimated(boolean b) { animated = b; }
	public boolean getAnimated() { return animated; }
	public boolean getBlocked() { return blocked; }
	public void setX(double x) { this.x = x; }
	public void setY(double y) {this.y = y; }
	
	public void setBulletCollision(boolean b)
	{
		bulletCollision = b;
	}
	
	public boolean getBulletCollision()
	{
		return bulletCollision;
	}

	public Rectangle getRectangle() 
	{
		return new Rectangle((int)x - size, (int)y - size, size, size);
	}

	public void setBlocked(boolean b)
	{
		blocked = b;
	}
}