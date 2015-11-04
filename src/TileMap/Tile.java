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
	private double x;
	private double y;
	private double dx;
	private double dy;
	
	public double left;
	public double right;
	public double top;
	public double bottom;
	
	private int size;
	
	private int type;
	public static final int AIR = 0; //able to pass through
	
	private BufferedImage spritesheet;
	private BufferedImage image;
	
	public Tile(double x, double y, int type, int size)
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
	}
	
	public void init()
	{
		image = TileMap.getSprite(type);
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
	}
	
	public void draw(Graphics2D g, int type)
	{
		if(-size <= x && x <= GamePanel.WIDTH+size && -size <= y && y <= GamePanel.HEIGHT+size) g.drawImage(image, (int)x, (int)y, size, size, null);
		
		/*g.setColor(Color.RED);
		g.drawLine((int)left, (int)top, (int)right, (int)top);
		g.drawLine((int)right, (int)top, (int)right, (int)bottom);
		g.drawLine((int)right, (int)bottom, (int)left, (int)bottom);
		g.drawLine((int)left, (int)bottom, (int)left, (int)top);*/
	}
	
	public int getType() {	return type;	}
	public double getX(){	return x;	}
	public double getY(){	return y;	}
	public void setX(double x) { this.x = x; }
	public void setY(double y) {this.y = y; }
}