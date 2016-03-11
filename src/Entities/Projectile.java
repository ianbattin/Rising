package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import GameState.GameState;
import Main.GamePanel;
import Main.SoundPlayer;
import TileMap.Tile;
import TileMap.TileMap;

public class Projectile extends MapObject
{
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
			case 1: 
			{
				moveSpeed = 50.0;
				width = 15;
				height = 15;
				damage = 1;
				playerCollide = true;
				SoundPlayer.playShootingClip();
				break;
			}
			case 2:
			{
				moveSpeed = 7.0;
				width = 14;
				height = 14;
				damage = 1;
				playerCollide = true;
				SoundPlayer.playShootingClip();
				break;
			}
			case 3:
			{
				moveSpeed = 25.0;
				width = 10;
				height = 10;
				damage = 0;
				playerCollide = false;
				SoundPlayer.playShootingClip();
				break;
			}
			case 4:
			{
				moveSpeed = 16.0;
				width = 5;
				height = 5;
				damage = 0;
				playerCollide = false;
				SoundPlayer.playShootingClip();
				break;
			}
			case 5:
			{
				moveSpeed = 25.0;
				width = 10;
				height = 10;
				damage = 0;
				playerCollide = true;
				SoundPlayer.playShootingClip();
				break;
			}
			case 6:
			{
				moveSpeed = 10.0;
				width = 30;
				height = 30;
				damage = 0;
				playerCollide = true;
				SoundPlayer.playShootingClip();
				break;
			}
			case 7:
			{
				ricochetCount = 0;
				ricochetDelay = 200;
				ricochetTimer = System.nanoTime();
				moveSpeed = 10.0;
				dx = moveSpeed;
				dy = moveSpeed;
				angle = Math.toDegrees(Math.atan(dy/dx));
				width = 75;
				height = 75;
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
		
		cwidth = width/2;
		cheight = height/2;
	}

	public void update() 
	{
		this.myCheckCollision();
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
					g.drawImage(GameState.rotateImage(animation.getImage(), (int)angle), (int)(x + xmap), (int)(y + ymap), width, height, null);
				else
					g.drawImage(GameState.rotateImage(animation.getImage(), (int)angle), (int)(x + xmap), (int)(y + ymap), -width, -height, null);
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
		if(true)
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
				remove = true;
			}
			else if(this.type == 1)
			{
				tileMap.getExplosions().add(new Explosion(x, y, 3, tileMap));
				remove = true;
			}
			else if(this.type == 3)
			{
				t.setType(0);
				remove = true;
			}
			else if(this.type == 4)
			{
				remove = true;
				getTiles().add(new Tile(t.getX(), t.getY() - 25, 17, t.getSize(), tileMap));
				getTiles().get(getTiles().size()-1).init();
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
			else
				remove = true;
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
				((Enemy) m).playerHurt(damage);
				remove = true;
			}
			
			if(m instanceof PlaneBoss)
			{
				((PlaneBoss) m).playerHurt(damage);
				remove = true;
			}
			
			if(this.type == 1)
			{
				remove = true;
				tileMap.getExplosions().add(new Explosion(x, y, 3, tileMap));
				tileMap.getExplosions().get(tileMap.getExplosions().size()-1).collided(m);
			}
			else if(this.type == 5)
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
