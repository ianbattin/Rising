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
	private static final int NORMAL = 1;
	private static final int BLOCKED = 0;
	
	public Tile(int x, int y, int left, int right, int top, int bottom, int type)
	{
		this.x = x;
		this.y = y;
		
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		
		this.type = type;
		
		if(type == NORMAL || type == BLOCKED)
		{
			size = 10;
		}
	}
	
	public void update()
	{
		x += dx;
		y += dy;
	}
	
	public void draw(Graphics2D g)
	{
		if(type == 0)
		{
			g.setColor(Color.BLACK);
			g.fillRect(x, y, size, size);
		}
	}
	
	public int getType() {	return type;	}
	public double getX(){	return x;	}
	public double getY(){	return y;	}
}