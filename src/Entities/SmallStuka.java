package Entities;

import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Main.GamePanel;
import Main.SoundPlayer;
import TileMap.Tile;
import TileMap.TileMap;

public class SmallStuka extends MapObject
{
	//animation
	private ArrayList<BufferedImage[]> stukaSprites;
	private final int[] numFrames = { 2 };
	//private long timer;
	
	public SmallStuka(TileMap tm) 
	{
		super(tm);
		
		x = -150;
		y = 400;
		dx = 0;
		dy = 0;

		moveSpeed = 1.0;
		maxSpeedY = 3.0;
		maxSpeedX = 6.0;
		
		width = 60;
		height = 20;
		cwidth = 60;
		cheight = 20;

		facingRight = false;
		
		try
		{
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/StukaFarAway.png"));
			stukaSprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < numFrames.length; i++)
			{
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++)
				{
					bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
				}
				BufferedImage temp = bi[0];
				bi[0] = bi[1];
				bi[1] = temp;
				stukaSprites.add(bi);
			}
			
			//make the spritesheet for when the player is blinking red
			BufferedImage playerHurtSpritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/StukaFarAway.png"));
			for (int i = 0; i < playerHurtSpritesheet.getWidth(); i++)
			{
				for (int j = 0; j < playerHurtSpritesheet.getHeight(); j++)
				{
					int rgb = playerHurtSpritesheet.getRGB(i, j);
					int a = (rgb >> 24) & 0xFF;
					int r = (rgb >> 16) & 0xFF;
					int g = (rgb >> 8) & 0xFF;
					int b = rgb & 0xFF;
					if (r+150 > 255) r = 255;
					else r += 150;
					playerHurtSpritesheet.setRGB(i, j, (a*16777216)+(r*65536)+(g*256)+b);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = 0;
		animation.setFrames(stukaSprites.get(0));
		animation.setDelay(1500);
		//timer = System.currentTimeMillis();
	}

	//Do anything before starting to show the stuka on the screen
	public void init()
	{
		SoundPlayer.playClip("Stukadiving.wav");
	}
	
	@Override
	public void collided(int type, Tile t) 
	{
		
	}

	@Override
	public void collided(MapObject m) 
	{
	}

	@Override
	public void update() 
	{
		getMovement();

		//x += tileMap.getDX();
		//y += tileMap.getDY();

		getAnimation();
	}

	@Override
	public void draw(Graphics2D g) 
	{
		setMapPosition();

		if(tileMap.getShowCollisonBox())
		{
			g.setColor(Color.RED);
			g.draw(this.getRectangle());
		}
		
		if(facingRight)
		{
			g.drawImage(animation.getImage(), (int)(x + xmap), (int)(y + ymap), width, height, null);
		}
		else
		{
			g.drawImage(animation.getImage(), (int)(x + xmap) + width, (int)(y + ymap), -width, height, null);
		}
	}

	public void getMovement() 
	{
		facingRight = false;
		
		dx = maxSpeedX;
		if(x >= GamePanel.WIDTH/2)
		{
			dy = 5.0;
		}
		else
			dy = 2.0;
		
		x += dx;
		y += dy;
	}
	
	public void getAnimation() 
	{
		animation.update();	
	}
}
