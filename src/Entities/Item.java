package Entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Main.GamePanel;
import TileMap.Tile;
import TileMap.TileMap;

public class Item extends MapObject
{
	private boolean physics;

	private ArrayList<BufferedImage[]> entitySprites;
	
	public Item(double x, double y, double dx, double dy, boolean physics, String spritePath, int[] numFrames, int type, TileMap tm) 
	{
		super(tm);
		
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.type = type;
		
		width = 45;
		height = 25;
		cwidth = width;
		cheight = height;
		
		fallSpeed = 0.25;
		maxFallSpeed = 5.0;
		
		this.physics = physics;
		
		try
		{
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream(spritePath));
			entitySprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < numFrames.length; i++)
			{
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++)
				{
					bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
				}
				entitySprites.add(bi);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(entitySprites.get(0));
		animation.setDelay(200);
	}

	@Override
	public void update()
	{	
		myCheckCollision();
		getAttack();
		getMovement();
		getAnimation();
	}
	
	@Override
	public void draw(Graphics2D g) 
	{
		if(facingRight)
		{
			g.drawImage(animation.getImage(), (int)(x + xmap), (int)(y + ymap), width, height, null);
		}
		else
		{
			g.drawImage(animation.getImage(), (int)(x + xmap) + width, (int)(y + ymap), -width, height, null);
		}
	}

	private void getAnimation() 
	{
		animation.update();	
	}

	private void getMovement() 
	{
		if(physics)
		{
			if(dy < maxFallSpeed) dy += fallSpeed;
			else dy = maxFallSpeed;
		}
		
		x += dx*Enemy.slowDown;
		y += dy*Enemy.slowDown;
	}

	private void getAttack() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void collided(int type, Tile t) {
		dx = 0;
		dy = 0;
	}

	@Override
	public void collided(MapObject m) {
		
	}

}
