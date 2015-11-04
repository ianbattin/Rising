package TileMap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Entities.Animation;
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
	
	private Animation animation;
	private int frames;
	private boolean animated = false;
	
	private int type;
	private boolean blocked = true;
	public static final int AIR = 0; //able to pass through
	
	private BufferedImage[] images;
	
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
		animation = new Animation();

		if(type < 17) frames = 1;
		else if(type == 17) 
		{
			frames = 3;
			blocked = false;
		}
		else frames = 1;
		if(frames > 1) animated = true;
		
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
		
		animation.update();
	}
	
	public void draw(Graphics2D g, int type)
	{
		if(-size <= x && x <= GamePanel.WIDTH+size && -size <= y && y <= GamePanel.HEIGHT+size)
		{
			g.drawImage(animation.getImage(), (int)x, (int)y, size, size, null);
		}
		
		/*g.setColor(Color.RED);
		g.drawLine((int)left, (int)top, (int)right, (int)top);
		g.drawLine((int)right, (int)top, (int)right, (int)bottom);
		g.drawLine((int)right, (int)bottom, (int)left, (int)bottom);
		g.drawLine((int)left, (int)bottom, (int)left, (int)top);*/
	}
	
	public int getType() {	return type;	}
	public double getX(){	return x;	}
	public double getY(){	return y;	}
	public boolean getAnimated() { return animated; }
	public boolean getBlocked() { return blocked; }
	public void setX(double x) { this.x = x; }
	public void setY(double y) {this.y = y; }
}