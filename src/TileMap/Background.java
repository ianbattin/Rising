package TileMap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Main.GamePanel;

public class Background 
{
	private BufferedImage image;
	private static ArrayList<Integer> pixelColors;
	public String path;
	
	private double x; //xPosition
	private double y; //yPosition
	private double dx; //xMovement
	private double dy; //yMovement
	
	private double moveScale;
	
	//Constructor reads the image into the image variable
	public Background(String path, double ms, boolean addToPixelColors)
	{
		try
		{
			this.path = path;
			image = ImageIO.read(getClass().getResourceAsStream(path));
			
			if(pixelColors == null) pixelColors = new ArrayList<Integer>();
			if(addToPixelColors)
			{
				for(int i = 1; i < image.getWidth(); i++)
				{
					for(int j = 1; j < image.getHeight(); j++)
					{
						int color = image.getRGB(i, j);
						if(!pixelColors.contains(color))
							pixelColors.add(color);
					}
				}
			}
			addCustomColors(new int[] {
				(new Color(66, 76, 81).getRGB()),
				(new Color(26, 40, 50).getRGB()),
				(new Color(50, 64, 75).getRGB()),
				(new Color(67, 82, 93).getRGB()),
				(new Color(58, 83, 99).getRGB())
			});
			moveScale = ms;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addCustomColors(int[] colors)
	{
		for(int i = 0; i < colors.length; i++)
			pixelColors.add(colors[i]);
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
	
	//Change the image of the background
	public void setNewImage(String s)
	{
		try
		{
			image = ImageIO.read(getClass().getResourceAsStream(s));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
		if(x < 0 && y < 0)
		{
			g.drawImage(image, (int)x + GamePanel.WIDTH, (int)y + GamePanel.HEIGHT, null);
			this.x = (x * moveScale) % GamePanel.WIDTH;
			this.y = (y * moveScale) % GamePanel.HEIGHT;
		}
		if(x > 0 && y < 0)
		{
			g.drawImage(image, (int)x - GamePanel.WIDTH, (int)y + GamePanel.HEIGHT, null);
			this.x = (x * moveScale) % GamePanel.WIDTH;
			this.y = (y * moveScale) % GamePanel.HEIGHT;
		}
		if(x < 0 && y > 0)
		{
			g.drawImage(image, (int)x + GamePanel.WIDTH, (int)y - GamePanel.HEIGHT, null);
			this.x = (x * moveScale) % GamePanel.WIDTH;
			this.y = (y * moveScale) % GamePanel.HEIGHT;
		}
		if(x > 0 && y > 0)
		{
			g.drawImage(image, (int)x - GamePanel.WIDTH, (int)y - GamePanel.HEIGHT, null);
			this.x = (x * moveScale) % GamePanel.WIDTH;
			this.y = (y * moveScale) % GamePanel.HEIGHT;
		}
	}

	public String getPath()
	{
		return path;
	}
	
	public ArrayList<Integer> getPixelColors()
	{
		return pixelColors;
	}
	
	public double getYPosition() 
	{
		return y;
	}

	public void setXVector(double bgVectorX) 
	{
		this.dx = bgVectorX;
	}
	
	public void setYVector(double bgVectorY) 
	{
		this.dy = bgVectorY;
	}
}