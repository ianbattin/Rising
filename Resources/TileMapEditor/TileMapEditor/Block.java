package TileMapEditor.TileMapEditor;

import java.awt.*;
import java.awt.image.*;
import java.io.File;

import javax.imageio.ImageIO;

public class Block {
	
	BufferedImage image;
	int x;
	int y;
	boolean isInvisible;
	
	public Block(BufferedImage img) {
		image = img;
		isInvisible = true;
		for (int x = 0; x < image.getWidth(); x++)
		{
			for(int y = 0; y < image.getHeight(); y++)
			{
				int a = (image.getRGB(x, y) >> 24) & 0xFF;
				System.out.println(a);
				if(a != 0)	
				{
					isInvisible = false;
				}
			}
		}
	}
	
	public void setPosition(int i1, int i2) {
		x = i1;
		y = i2;
		if(isInvisible && !(x == 0 && y == 0))
		{
			try
			{
				image = ImageIO.read(new File("Resources/TileMapEditor/TileMapEditor/Resources/Error.png"));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void draw(Graphics2D g) {
		g.drawImage(image, x + MyPanel.WIDTH/2, y, null);
	}
	
}