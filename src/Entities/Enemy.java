package Entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import GameState.Level1State;
import Main.GamePanel;
import TileMap.Tile;
import TileMap.TileMap;

public abstract class Enemy extends MapObject
{
	//Relative position to player
	protected double relX;
	protected double relY;
	
	//Line of sight
	protected boolean hasSight;
	
	//TileMap
	protected TileMap tileMap;
	protected Player player;
	
	//Attacks
	protected ArrayList<Projectile> bullets;
	protected long fireTimer;
	protected static int fireDelay;
	protected static boolean firing;
	protected double angle;
	protected double lastAngle;
	protected int gunPosX;
	protected int gunPosY;
	
	//character position relative to bottom of tileMap
	protected double yFromBottom;
	
	//health
	protected byte numOfFramesToAnimHealth;
	protected byte timesToLoop;
	protected boolean isFlashing;
	
	//animation
	protected ArrayList<BufferedImage[]> entitySprites;
	protected ArrayList<BufferedImage[]> entityHurtSprites;
	protected BufferedImage[] gunSprites;
	protected Animation gunAnimation;
	
	//slowdown (when player picks up powerup)
	public static float slowDown;
	
	public Enemy(double x, double y, TileMap tm, Player player)
	{
		super(tm);
		this.x = x;
		this.y = y;
		this.tileMap = tm;
		this.player = player;
		
		angle = 0.0;
		fireTimer = System.nanoTime();
		
		yFromBottom =  GamePanel.HEIGHTSCALED - y;
		
		dx = 0.0;
		dy = 0.0;
		
		numOfFramesToAnimHealth = 0;
		timesToLoop = 0;
		isFlashing = false;
		
		if (slowDown == 0) slowDown = 1;
	}

	public abstract void update();
	public abstract void draw(Graphics2D g);
	public abstract void getAttack();
	public abstract void getMovement();
	public abstract void getAnimation();
	public abstract void collided(int type, Tile t);
	public abstract void collided(MapObject m);	
	public abstract void onDeath();

	protected void getBulletCollision()
	{
		for(int i = 0; i < bullets.size(); i++)
		{
			if(bullets.get(i).getRemove())
			{
				bullets.remove(i);
				i--;
				break;
			}
			bullets.get(i).update();
			if(bullets.get(i).intersects(player))
			{
				bullets.get(i).collided(player);
			}
			if(bullets.get(i).notOnScreen() && bullets.get(i).getLifeTime() > 300) bullets.remove(i);
		}
		
		for(int i = 0; i < player.getBullets().size(); i++)
		{
			if(player.getBullets().get(i).intersects(this))
			{
				player.getBullets().get(i).collided(this);
			}
		}
	}

	protected boolean lineOfSight() 
	{
		hasSight = false;
		for(Tile t: tileMap.getTiles())
		{
			if(t.getRectangle().intersectsLine(x, y, player.getX(), player.getY()))
			{
				hasSight = true;
			}
		}
		return hasSight;
	}
	
	public int getPlayerHealth()
	{
		return health;
	}
	
	public void playerHeal(int amount)
	{
		health += amount;
	}
	
	public void playerHurt(int amount)
	{
		if(recovering || health <= 0)
		{
			long elapsed = (System.nanoTime() - recoverTimer) / 1000000;
			if(recoverLength <= elapsed)
			{
				recovering = false;
			}
		}
		else
		{
			health -= amount;
			numOfFramesToAnimHealth = 10;
			timesToLoop = 5;
			dy = -8.0;
			if(dx >= 0) dx = -8.0;
			else dx = 8.0;
			recovering = true;
			recoverTimer = System.nanoTime();
			if(health <= 0)
			{
				this.onDeath();
			}
		}
	}
	
	public void setFiring(boolean b) 
	{
		firing = b;
	}

	public void setAngle(double atan) 
	{
		angle = atan;
	}

	public double getAngle() 
	{
		return angle;
	}
	
	public static void setSlowDownRate(float slowDown)
	{
		Enemy.slowDown = slowDown;
	}
	
	/**
	 * Draws the gun on top of the enemy and orients it to aim towards the player
	 * Requires that the gunAnimation, gunPosX and gunPosY have been initialized
	 * 
	 * @param g A Graphics2D object
	 */
	protected void drawGun(Graphics2D g)
	{		
		if(angle > lastAngle + 0.2 || angle < lastAngle - 0.2)
			lastAngle = angle;
			
		double angle = Math.toDegrees(lastAngle)+180;
		System.out.println(angle);
		
		//lastAngle = -Math.PI/4;//Math.max(lastAngle, Math.PI/4);
		AffineTransform prev = g.getTransform();
		AffineTransform transform = new AffineTransform();
		
		if (facingRight)
		{
			if(angle < 135)
			{
				angle = 135;
			}
			else if (angle > 225)
			{
				angle = 225;
			}
			//lastAngle = Math.max(-Math.PI/4, Math.min(lastAngle, Math.PI/4));
			transform.rotate(Math.toRadians(angle-180), x + 10 + gunPosX, y + 15 + gunPosY);
			g.transform( transform );
			g.drawImage(gunAnimation.getImage(), (int)(x + xmap) + gunPosX, (int)(y + ymap) + gunPosY, 50, 30, null);
		}
		else
		{
			if(angle > 45 && angle < 180)
			{
				angle = 45;
			}
			else if (angle < 315 && angle > 180)
			{
				angle = 315;
			}

			transform.rotate(Math.toRadians(angle-180), x + 10 + gunPosX, y + 15 + gunPosY);
			g.transform( transform );
			g.drawImage(gunAnimation.getImage(), (int)(x + xmap) + gunPosX, (int)(y + ymap) + gunPosY + 30, 50, -30, null);
		}
		g.setTransform(prev);
	}
}
