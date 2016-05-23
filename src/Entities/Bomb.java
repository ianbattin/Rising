package Entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import Main.GamePanel;
import Main.SoundPlayer;
import TileMap.Tile;
import TileMap.TileMap;

public class Bomb extends MapObject
{
	public int type;
	private boolean collided = false;
	
	private boolean exploding = false;
	private boolean flashing = false;
	private long explodeTimer, flashTimer;
	
	//sprites loading
	private ArrayList<BufferedImage[]> sprites;
	private ArrayList<BufferedImage[]> flashingSprites;
	private final int[] numFrames = { 4 };

	public Bomb(double x, double y, TileMap tileMap, int type)
	{
		super(tileMap);
		this.x = x;
		this.y = y;
		this.type = type;
		
		dx = 0;
		dy = 0;
		
		width = 53;
		height = 53;
		cwidth = 53;
		cheight = 53;
		
		fallSpeed = 0.25;
		maxFallSpeed = 7.0;
		
		remove = false;
		if(type == 1) explodeTimer = 250;
		else explodeTimer = 1000;
		flashTimer = 100000000;
		
		try
		{
			//will have to be fixed to get an image from a large sprites image rather than a single image for each pickup
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/bomb.png"));
			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < numFrames.length; i++)
			{
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++)
				{
					bi[j] = spritesheet.getSubimage(i * width, j * height, width, height);
				}
				sprites.add(bi);
			}

			BufferedImage flashingSpritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/bomb.png"));
			flashingSprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < numFrames.length; i++)
			{
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++)
				{
					bi[j] = flashingSpritesheet.getSubimage(i * width, j * height, width, height);
					for (int picX = 0; picX < bi[j].getWidth(); picX++)
					{
						for (int picY = 0; picY < bi[j].getHeight(); picY++)
						{
							int rgb = bi[j].getRGB(picX, picY);
							int a = (rgb >> 24) & 0xFF;
							int r = (rgb >> 16) & 0xFF;
							int g = (rgb >> 8) & 0xFF;
							int b = rgb & 0xFF;
							if (r+150 > 255) r = 255;
							else r += 150;
							bi[j].setRGB(picX, picY, (a*16777216)+(r*65536)+(g*256)+b);
						}
					}
				}
				flashingSprites.add(bi);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(sprites.get(0));
		animation.setDelay(400);
		animation.setDone(true);
		
		SoundPlayer.playClip("fallingbomb.wav");
	}

	@Override
	public void update() 
	{
		myCheckCollision();
		getMovement();
		getAnimation();
		
		if(!collided)
		{
			x += dx;
			y += dy;
		}
		
		x += tileMap.getDX();
		y += tileMap.getDY();
	}

	@Override
	public void draw(Graphics2D g) 
	{
		if(!remove)
		{
			g.drawImage(animation.getImage(), (int)x, (int)y, width, height, null);
		}
	}
	
	public void getMovement()
	{
		if(dy < maxFallSpeed) dy += fallSpeed;
		else dy = maxFallSpeed;
	}
	
	public void getAnimation()
	{
		if(exploding)
		{
			animation.setFrame(3);
			animation.setDelay(100);
			
			if (flashTimer > 0)
			{
				flashTimer -= GamePanel.getElapsedTime();
			}
			else
			{
				flashing = !flashing;
				flashTimer = 100000000;
				
				if (flashing)
					animation.changeFrames(flashingSprites.get(currentAction));
				else
					animation.changeFrames(sprites.get(currentAction));
			}
		}
		else
		{
			animation.changeFrames(sprites.get(currentAction));
		}
				
		animation.update();
		if(animation.getFrame() == 3 && !exploding) animation.setFrame(2);
	}
	
	@Override
	public void collided(int type, Tile t) 
	{
		if(t.getBlocked())
		{
			dy = 0;
			dx = 0;
			y = t.getY() - height;
			collided = true;
		
			if(!exploding) explode();
		}
	}

	@Override
	public void collided(MapObject m) 
	{
		//remove = true;
	}
	
	public void explode()
	{
		exploding = true;
		
		new Timer().schedule(new TimerTask()
		{
			public void run()
			{	
				if(type == 0)
					tileMap.getExplosions().add(new Explosion(x, y, 2, 1, tileMap));
				else if(type == 1)
					tileMap.getExplosions().add(new Explosion(x, y, 4, 1, tileMap));
				exploding = false;
				remove = true;
			}
			
		}, explodeTimer);
	}
}
