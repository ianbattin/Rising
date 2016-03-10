package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Main.GamePanel;
import TileMap.Tile;
import TileMap.TileMap;

public class Jetpacker extends Enemy
{
	//animation
	private final int[] numFrames = {1};
	
	//animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	
	//parachute
	private BufferedImage parachute;
	
	public Jetpacker(double x, double y, TileMap tm, Player player) 
	{
		super(x, y, tm, player);
		
		bullets = new ArrayList<Projectile>();
		firing = false;
		fireDelay = 600;
		
		recoverLength = 100;
		
		moveSpeed = 0.1;
		moveSpeedLeft = 0.3;
		moveSpeedRight = 0.3;
		maxSpeedY = 2;
		maxSpeedX = 2.5;
		stopSpeed = 0.4;
		fallSpeed = 0.25;
		maxFallSpeed = 7.0;
		jumpStart = -3.0;
		
		width = 50;
		height = 70;
		cwidth = 50;
		cheight = 70;

		facingRight = true;
		
		try
		{
			parachute = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/parachute.png"));
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/parachuter.png"));
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
			
			//make the spritesheet for when the player is blinking red
			BufferedImage playerHurtSpritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/parachuter.png"));
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
			
			entityHurtSprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < numFrames.length; i++)
			{
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++)
				{
					bi[j] = playerHurtSpritesheet.getSubimage(j * width, i * height, width, height);
				}
				//sprites.add(bi);
				entityHurtSprites.add(bi);
			}
			
			//make the spritesheet for the gun
			BufferedImage gunSpriteSheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/gunSprite.png"));
			gunSprites = new BufferedImage[2];
			for(int i = 0; i < gunSprites.length; i++)
			{
				gunSprites[i] = (gunSpriteSheet.getSubimage(i * 25, 0, 25, 15)); //set the second number to select gun type (intervals of 15)
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(entitySprites.get(IDLE));
		animation.setDelay(200);
		
		//create the animation for the gun
		gunAnimation = new Animation();
		gunAnimation.setFrames(gunSprites);
		gunAnimation.setDelay(200);
		
		super.gunPosX = 10;
		super.gunPosY = 18;
		
		falling = true;
		
		//health
		health = 1;
	}
	
	public void update() 
	{
		if (health > 0)
		{
			getMovement();
			getAttack();
		}
		else
		{
			dy = 10.0;
			y += dy;
		}
		
		x += tileMap.getDX();
		y += tileMap.getDY()/2;
		
		if (y > GamePanel.HEIGHT + 400)
		{
			//remove enemy as it is too low
			this.remove = true;
		}
		
		getAnimation();
		getBulletCollision();

		yFromBottom += (-dy + tileMap.getDY()/2);
		
		if (numOfFramesToAnimHealth  > 0 && timesToLoop%2 == 1)
		{
			isFlashing = true;
			numOfFramesToAnimHealth--;
			if(numOfFramesToAnimHealth == 0 && timesToLoop > 0) 
			{
				timesToLoop--;
				numOfFramesToAnimHealth = 10;
			}
		}
		else if (numOfFramesToAnimHealth > 0 && timesToLoop%2 == 0)
		{
			isFlashing = false;
			numOfFramesToAnimHealth--;
			if(numOfFramesToAnimHealth == 0 && timesToLoop > 0)
			{
				timesToLoop--;
				numOfFramesToAnimHealth = 10;
			}
		}
	}

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
		
		g.drawImage(parachute, (int)(x+(25-(parachute.getWidth()/2))), (int)(y-33), parachute.getWidth(), parachute.getHeight(), null);
		
		for(Projectile p: bullets)
		{
			p.draw(g);
		}
		
		drawGun(g);
	}
	
	public void getAttack()
	{
		relX = (int) (this.x - (int)player.getX());
		relY = (int) (this.y - (int)player.getY());
		this.setAngle(Math.atan2(-relY, -relX));
		this.setAngle(angle + Math.random()*Math.PI/12 - Math.PI/12);
		
		if(!lineOfSight())
		{
			firing = true;
		}
		else
			firing = false;
		
		if(firing)
		{
			
			long elapsed= (System.nanoTime() - fireTimer) / 1000000;
			if(fireDelay <= elapsed*(0.5*Enemy.slowDown))
			{
				bullets.add(new Projectile(x + this.gunPosX, y + this.gunPosY, angle, 2, tileMap));
				fireTimer = System.nanoTime();
			}
		}
	}

	public void getMovement()
	{
		if(relX > 200)
		{
			dx -= moveSpeedLeft;
			if(dx < -(maxSpeedX)) dx = -(maxSpeedX);
		}
		else if(relX < -200)
		{
			dx += moveSpeedRight;
			if(dx > (maxSpeedX)) dx = (maxSpeedX);
		}		
		dy += moveSpeed;
		if(dy > (maxSpeedY)) dy = (maxSpeedY);
		
		if(player.getX() > this.getX())
		{
			this.facingRight = true;
		}
		else
		{
			this.facingRight = false;
		}
		
		x += dx*Enemy.slowDown;
		y += dy*Enemy.slowDown;
	}
	
	public void getAnimation()
	{
		if(idle)
		{
			if(currentAction != IDLE && currentAction != WALKING)
			{
				currentAction = IDLE;
				animation.setFrames(entitySprites.get(IDLE));
				animation.setDelay(200);
				width = 50;
				height = 70;
			}
		}
		if(left || right)
		{
			if(right){ facingRight = true;	}
			else{ facingRight = false;	}
			if(currentAction != WALKING && currentAction != FALLING && currentAction != JUMPING && !idle)
			{
				currentAction = WALKING;
				animation.setFrames(entitySprites.get(WALKING));
				animation.setDelay(200);
				width = 50;
				height = 70;
			}
		}
		if(jump)
		{
			if(currentAction != JUMPING && !fallingAnim)
			{
				currentAction = JUMPING;
				animation.setFrames(entitySprites.get(JUMPING));
				animation.setDelay(200);
				animation.setDone(true);
				width = 50;
				height = 70;
			}
		}
		if(fallingAnim)
		{
			if(currentAction != FALLING)
			{
				currentAction = FALLING;
				animation.setFrames(entitySprites.get(FALLING));
				animation.setDelay(200);
				width = 50;
				height = 70;
			}
		}
		
		if (isFlashing) animation.changeFrames(entityHurtSprites.get(currentAction));
		else animation.changeFrames(entitySprites.get(currentAction));
		
		animation.update();		
	}
	
	public void collided(int type, Tile t) 
	{

	}

	public void collided(MapObject m) 
	{

	}
	
	public void onDeath()
	{
		player.increasePoints(500);
	}
}