package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
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
	private long explodeTimer;
	
	//sprites loading
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = { 4 };
	
	public Bomb(double x, double y, TileMap tileMap, int type)
	{
		super(tileMap);
		this.x = x;
		this.y = y;
		this.type = type;
		
		dx = Math.random();
		dy = 0;
		
		width = 53;
		height = 53;
		cwidth = 5;
		cheight = 53;
		
		fallSpeed = 0.25;
		maxFallSpeed = 7.0;
		
		remove = false;
		explodeTimer = 1000;
		
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
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(sprites.get(0));
		animation.setDelay(400);
		animation.setDone(true);
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
			g.drawImage(animation.getImage(), (int)x, (int)y, width, height, null);
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
		}
		else
		{
			animation.changeFrames(sprites.get(currentAction));
		}
		
		if(animation.getFrame() == 3 && !exploding) animation.setFrame(2);
		animation.update();
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
		remove = true;
	}
	
	public void explode()
	{
		exploding = true;
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			public void run()
			{	
				if(exploding)
				{
					System.out.println("UHHH");
					tileMap.getExplosions().add(new Explosion(x, y, 2, tileMap));
				}
				remove = true;
				exploding = false;
			}
			
		}, explodeTimer);
	}
}
