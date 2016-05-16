package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import GameState.GameState;
import Main.GamePanel;
import Main.SoundPlayer;
import TileMap.Tile;
import TileMap.TileMap;

public class Projectile extends MapObject
{
	
	public static final int PROJECTILE_1_SIZE = 180;
	
	private double direction;
	private double angle;
	private int damage;
	private int type;
	private int ricochetCount;
	private long ricochetTimer;
	private int ricochetDelay;
	
	
	private boolean remove;
	private boolean playerCollide;
	private boolean onScreen = false;
	
	private long lifeTime;
	
	//timeslow variable
	private static double slowTime;
	
	//sprites loading
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = { 1, 1 };
	
	public Projectile(double x, double y, double direction, int type, TileMap tm) 
	{
		super(tm);
		
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.type = type;
		
		slowTime = 1;
		
		lifeTime = 0;
		
		remove = false;

		switch (this.type)
		{
			//Players
			case 1: 
			{
				moveSpeed = 0;
				width = Projectile.PROJECTILE_1_SIZE;
				height = Projectile.PROJECTILE_1_SIZE;
				damage = 1;
				playerCollide = true;
				SoundPlayer.playClip("propgun.wav");
				break;
			}
			//Slow moving
			case 2:
			{
				moveSpeed = 4.0;
				width = 6;
				height = 6;
				damage = 1;
				playerCollide = true;
				SoundPlayer.playShootingClip();
				break;
			}
			//Normal enemies
			case 3:
			{
				moveSpeed = 25.0;
				width = 6;
				height = 6;
				damage = 0;
				playerCollide = false;
				SoundPlayer.playShootingClip();
				break;
			}
			//Fire
			case 4:
			{
				moveSpeed = 16.0;
				width = 6;
				height = 6;
				damage = 0;
				playerCollide = false;
				SoundPlayer.playShootingClip();
				break;
			}
			//Small explosion
			case 5:
			{
				moveSpeed = 40.0;
				width = 10;
				height = 10;
				damage = 0;
				playerCollide = true;
				SoundPlayer.playShootingClip();
				break;
			}
			//Large explosion
			case 6:
			{
				moveSpeed = 8.0;
				width = 30;
				height = 30;
				damage = 0;
				playerCollide = true;
				SoundPlayer.playShootingClip();
				break;
			}
			//Ricochete
			case 7:
			{
				ricochetCount = 0;
				ricochetDelay = 200;
				ricochetTimer = System.nanoTime();
				moveSpeed = 8.5;
				dx = moveSpeed;
				dy = moveSpeed;
				angle = Math.toDegrees(Math.atan(dy/dx));
				width = 75;
				height = 75;
				cwidth = 75;
				cheight = 75;
				damage = 2;
				playerCollide = true;
				
				try
				{
					//will have to be fixed to get an image from a large sprites image rather than a single image for each pickup
					BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/Debris2.png"));
					sprites = new ArrayList<BufferedImage[]>();
					for(int i = 0; i < numFrames.length; i++)
					{
						BufferedImage[] bi = new BufferedImage[numFrames[i]];
						for(int j = 0; j < numFrames[i]; j++)
						{
							bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
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
				animation.setDelay(200);
				
				break;
			}
		}
		
		cwidth = width;
		cheight = height/2;
	}

	public void update() 
	{
		this.myCheckCollision();
		if(this.type == 1)
		{
			tileMap.getExplosions().add(new Explosion(x + width/2-25, y + height/2-25, 3, tileMap));
			this.remove = true;
		}
		if(this.type != 7)
		{
			dx = Math.cos(direction) * (moveSpeed);
			dy = Math.sin(direction) * (moveSpeed);
			
			dx += tileMap.getDX();
			dy += tileMap.getDY();
		}
		else
		{
			x += tileMap.getDX();
			y += tileMap.getDY();
			
			if(0 < x && x < GamePanel.WIDTH && 0 < y && y < GamePanel.HEIGHT) onScreen = true;
			if(x < 0)
			{
				x += 5;
				dx = -dx;
			}
			else if(x > GamePanel.WIDTH)
			{
				x -= 5;
				dx = -dx;
			}
			if(onScreen)
			{
				if(y < 0)
				{
					dy = -dy;
					y += 5;
				}
				else if(y > GamePanel.HEIGHT)
				{
					dy = -dy;
					y -= 5;
				}
			}
			
			if(Math.abs(dy) < 4) dy = moveSpeed;
			angle = Math.toDegrees(Math.atan(dy/dx));
		}
			
		if(type != 1)
		{
			x += dx*slowTime;
			y += dy*slowTime;
		}
		else
		{
			x += dx;
			y += dy;
		}
		
		lifeTime++;
	}

	public void draw(Graphics2D g) 
	{
		if(!remove)
		{
			if(this.type == 7)
			{
				getAnimation();

				if(dx >= 0)
					g.drawImage(GameState.rotateImage(animation.getImage(), (int)angle), (int)(x), (int)(y), width, height, null);
				else
					g.drawImage(GameState.rotateImage(animation.getImage(), (int)angle), (int)(x + width), (int)(y + height), -width, -height, null);
			}
			else
			{
				if (this.type != 1)
				{
					g.setColor(Color.BLACK);
					g.fillOval((int)x, (int)y, width, height);
				}
			
				if(tileMap.getShowCollisonBox())
				{
					g.setColor(Color.RED);
					g.draw(this.getRectangle());
				}
			}
		}
	}
	
	public void getAnimation()
	{
		animation.setFrames(sprites.get(1));
		animation.update();
	}
	
	@Override
	public void collided(int type, Tile t) 
	{
		if(t.getBulletCollision() && !remove)
		{
			if(t.getType() == 17 && this.type != 7)
			{
				t.setType(0);
				if (this.type != 1)	{remove = true;}
			}
			else if(this.type == 3)
			{
				t.setType(0);
				remove = true;
			}
			else if(this.type == 4)
			{
				remove = true;
				final Tile tile = new Tile(t.getX(), t.getY() - 25, 17, t.getSize(), tileMap);
				getTiles().add(tile);
				getTiles().get(getTiles().size()-1).init();
				new Timer().schedule(new TimerTask()
				{
					public void run()
					{	
						tile.setType(0);
					}
					
				}, (int)(Math.random()*7000+15000));
			}
			else if(this.type == 5)
			{
				remove = true;
				tileMap.getExplosions().add(new Explosion(x, y, 1, tileMap));
			}
			else if(this.type == 6)
			{
				remove = true;
				tileMap.getExplosions().add(new Explosion(x, y, 2, tileMap));
			}
			else if(this.type == 7)
			{
				long elapsed= (System.nanoTime() - ricochetTimer) / 1000000;
				if(ricochetDelay <= elapsed)
				{
					dy = -dy;
					ricochetCount++;
					ricochetTimer = System.nanoTime();
				}
				if(ricochetCount > 5) 
				{
					tileMap.getExplosions().add(new Explosion(x, y, 1, tileMap));
					remove = true;
				}
			}
			else if (this.type != 1)
			{
				remove = true;
			}
		}
	}

	@Override
	public void collided(MapObject m) 
	{
		if(playerCollide)
		{
			if(m instanceof Player)
			{
				((Player) m).playerHurt(damage);
				remove = true;
			}

			if(m instanceof Enemy)
			{
				((Enemy) m).playerHurt(damage, false);
				remove = true;
			}

		    if(this.type == 5)
			{
				remove = true;
				tileMap.getExplosions().add(new Explosion(x, y, 1, tileMap));
				tileMap.getExplosions().get(tileMap.getExplosions().size()-1).collided(m);
			}
		}
	}
	
	public int getType()
	{
		return type;
	}
	
	public boolean getRemove()
	{
		return remove;
	}
	
	public static void setSlowTime(double slowTime)
	{
		Projectile.slowTime = slowTime;
	}

	public long getLifeTime() 
	{
		return lifeTime;
	}
}
