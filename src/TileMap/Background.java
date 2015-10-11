package TileMap;

import java.awt.Graphics2D;
import java.awt.image.*;

import javax.imageio.ImageIO;

import Main.GamePanel;

public class Background 
{
	private BufferedImage image;
	
	private double x; //xPosition
	private double y; //yPosition
	private double dx; //xMovement
	private double dy; //yMovement
	
	private double moveScale;
	
	//Constructor reads the image into the image variable
	public Background(String s, double ms)
	{
		try
		{
			image = ImageIO.read(getClass().getResourceAsStream(s));
			moveScale = ms;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setPosition(double x, double y)
	{
		this.x = (x * moveScale) % GamePanel.WIDTH;
		this.y = (y * moveScale) % GamePanel.HEIGHT;
	}
	
	//Sets the movement in a certain direction
	public void setVector(double dx, double dy)
	{
		this.dx = dx;
		this.dy = dy;
	}
	
	//Moves the background
	public void update()
	{
		x += (dx * moveScale) % GamePanel.WIDTH;
		y += (dy * moveScale) % GamePanel.HEIGHT;
	}
	
	/* Draws the background in new locations
	 * if the x/y position is off the screen to the left, right, up, or down, it
	 * copys and draws a second background to the right, left, up or down
	 */
	public void draw(Graphics2D g)
	{
		g.drawImage(image, (int)x, (int)y, null);
		if(x < 0)
		{
			g.drawImage(image, (int)x + GamePanel.WIDTH, (int)y, null);
			this.x = (x * moveScale) % GamePanel.WIDTH;
			this.y = (y * moveScale) % GamePanel.HEIGHT;
		}
		if(x > 0)
		{
			g.drawImage(image, (int)x - GamePanel.WIDTH, (int)y, null);
			this.x = (x * moveScale) % GamePanel.WIDTH;
			this.y = (y * moveScale) % GamePanel.HEIGHT;
		}
		if(y < 0)
		{
			g.drawImage(image, (int)x, (int)y + GamePanel.HEIGHT, null);
			this.x = (x * moveScale) % GamePanel.WIDTH;
			this.y = (y * moveScale) % GamePanel.HEIGHT;
		}
		if(y > 0)
		{
			g.drawImage(image, (int)x, (int)y - GamePanel.HEIGHT, null);
			this.x = (x * moveScale) % GamePanel.WIDTH;
			this.y = (y * moveScale) % GamePanel.HEIGHT;
		}
	}
}