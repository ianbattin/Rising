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
	protected TileMap tm;
	protected Player player;
	
	//Attacks
	protected ArrayList<Projectile> bullets;
	protected long fireTimer;
	protected static int fireDelay;
	protected static boolean firing;
	protected double angle;
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
	protected float slowDown;
	
	public Enemy(double x, double y, TileMap tm, Player player)
	{
		super(tm);
		this.x = x;
		this.y = y;
		this.tm = tm;
		this.player = player;
		
		angle = 0.0;
		fireTimer = System.nanoTime();
		
		yFromBottom =  GamePanel.HEIGHTSCALED - y;
		
		dx = 0.0;
		dy = 0.0;
		
		numOfFramesToAnimHealth = 0;
		timesToLoop = 0;
		isFlashing = false;
		
		slowDown = 1; // 1 is the normal speed
	}

	public abstract void update();
	public abstract void draw(Graphics2D g);
	public abstract void getAttack();
	public abstract void getMovement();
	public abstract void getAnimation();
	public abstract void collided(int type, Tile t);
	public abstract void collided(MapObject m);	

	protected void getBulletCollision()
	{
		for(int i = 0; i < bullets.size(); i++)
		{
			bullets.get(i).update();
			if(bullets.get(i).getRemove())
			{
				bullets.remove(i);
				i--;
				break;
			}
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
		for(Tile t: tm.getTiles())
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
	
	public void setSlowDownRate(float slowDown)
	{
		this.slowDown = slowDown;
		for(int i = 0; i < bullets.size(); i++)
		{
			bullets.get(i).setSlowTime(this.slowDown);
		}
	}
	
	/**
	 * Draws the gun on top of the enemy and orients it to aim towards the player
	 * Requires that the gunAnimation, gunPosX and gunPosY have been initialized
	 * 
	 * @param g The Graphics2D object
	 */
	protected void drawGun(Graphics2D g)
	{		
		if(facingRight)
			g.drawImage(new AffineTransformOp(AffineTransform.getRotateInstance(angle, 12.5, 7.5), AffineTransformOp.TYPE_BILINEAR).filter(gunAnimation.getImage(), null), (int)(x + xmap) + gunPosX, (int)(y + ymap) + gunPosY, 50, 30, null);
		else
			g.drawImage(new AffineTransformOp(AffineTransform.getRotateInstance(-angle, 12.5, 7.5), AffineTransformOp.TYPE_BILINEAR).filter(gunAnimation.getImage(), null), (int)(x + xmap) + gunPosX, (int)(y + ymap) + gunPosY + 30, 50, -30, null);
	}
}
