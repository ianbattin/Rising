package TileMap;

import java.awt.Color;
import java.awt.Graphics2D;

import GameState.PlayState;
import Main.GamePanel;

public class Tile 
{
	private int x;
	private int y;
	private int dx;
	private int dy;
	
	private int left;
	private int right;
	private int top;
	private int bottom;
	
	private int size;
	
	private int type;
	private static final int NORMAL = 1; //able to pass through
	private static final int BLOCKED = 0; //collision enabled
	
	public Tile(int x, int y, int type, int size)
	{
		this.x = x;
		this.y = y;
		
		//these are for collision
		left = x;
		right = x + size;
		top = y;
		bottom = y + size;
		
		this.type = type;
		this.size = size;
	}
	
	public void update()
	{
		//for when we have to move the tiles as the player ascends
		x += dx;
		y += dy;
		
		left = x;
		right = x + size;
		top = y;
		bottom = y + size;
	}
	
	public void draw(Graphics2D g)
	{
		if(type == 0)
		{
			//TODO Set an image, not just a color, from an image sprite sheet
			g.setColor(Color.BLACK);
			g.fillRect(x, y, size, size);
			g.setColor(Color.RED);
			g.drawLine(left, top, right, top);
			g.drawLine(right, top, right, bottom);
			g.drawLine(right, bottom, left, bottom);
			g.drawLine(left, bottom, left, top);
		}
	}
	
	//moves the tilemap in a direction
	public void setVector(int dx, int dy)
	{
		this.dx = dx;
		this.dy = dy;
	}
	
	public int getType() {	return type;	}
	public double getX(){	return x;	}
	public double getY(){	return y;	}
}