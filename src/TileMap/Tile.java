package TileMap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import GameState.PlayState;
import Main.GamePanel;

public class Tile 
{
	private int x;
	private int y;
	private int dx;
	private int dy;
	
	public int left;
	public int right;
	public int top;
	public int bottom;
	
	private int size;
	
	private int type;
	public static final int AIR = 0; //able to pass through
	
	private BufferedImage spritesheet;
	private BufferedImage image;
	
	public Tile(int x, int y, int type, int size)
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
		
		this.type = type;
		this.size = size;
		
		image = this.getImage(type);
	}
	
	public void update(int dx, int dy)
	{
		//for when we have to move the tiles as the player ascends
		x += dx;
		y += dy;
		
		left = x;
		right = x + size;
		top = y;
		bottom = y + size;
	}
	
	public void draw(Graphics2D g, int type)
	{
		g.drawImage(image, x, y, size, size, null);
	}
	
	public BufferedImage getImage(int type)
	{
		try
		{ 
			spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Tiles/tileset.png"));	
			image = spritesheet.getSubimage(type * size, 0, size, size);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return image;
	}
	
	//moves the tilemap in a direction
	public void setVector(int dx, int dy)
	{
		this.dx = dx;
		this.dy = dy;
	}
	
	public int getType() {	return type;	}
	public int getX(){	return x;	}
	public int getY(){	return y;	}
	public void setX(int x) { this.x = x; }
	public void setY(int y) {this.y = y; }
}