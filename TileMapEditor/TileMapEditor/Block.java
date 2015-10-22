package TileMapEditor;

import java.awt.*;
import java.awt.image.*;

public class Block {
	
	BufferedImage image;
	int x;
	int y;
	
	public Block(BufferedImage b) {
		image = b;
	}
	
	public void setPosition(int i1, int i2) {
		x = i1;
		y = i2;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void draw(Graphics2D g) {
		g.drawImage(image, x, y, null);
	}
	
}