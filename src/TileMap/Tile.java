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
	public static final int BASICBLOCKED = 1; //collision enabled
	public static final int WINGBROKE = 2;
	public static final int WINGMIDDLE = 3;
	public static final int WINGTURBINE = 4;
	public static final int WINGEND = 5;
	//public static final int ANIMATED = 2;
	
	private BufferedImage spritesheet;
	private BufferedImage image;
	
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
		if(type == 1)
		{
			g.setColor(Color.BLACK);
			g.fillRect(x, y, size, size);
			g.setColor(Color.RED);
			g.drawLine(left, top, right, top);
			g.drawLine(right, top, right, bottom);
			g.drawLine(right, bottom, left, bottom);
			g.drawLine(left, bottom, left, top);
		}
		else
			g.drawImage(image, x, y, size, size, null);
	}
	
	public BufferedImage getImage(int type)
	{
		try
		{ 
			spritesheet = ImageIO.read(getClass().getResourceAsStream("/Tiles/AirplaneWingTileSet/WingTileSet1.png"));	
			switch(type)
			{
				case WINGBROKE:
					image = spritesheet.getSubimage(3 * size, 0, size, size);
					break;
				case WINGMIDDLE:
					image = spritesheet.getSubimage(2 * size, 0, size, size);
					break;
				case WINGTURBINE:
					image = spritesheet.getSubimage(1 * size, 0, size, size);
					break;
				case WINGEND:
					image = spritesheet.getSubimage(0 * size, 0, size, size);
					break;
				default:
					image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Error.png"));
			}
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
	public double getX(){	return x;	}
	public double getY(){	return y;	}
}