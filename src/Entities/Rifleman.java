package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Main.GamePanel;
import TileMap.Tile;
import TileMap.TileMap;

public class Rifleman extends Enemy
{
	//animation
	private final int[] numFrames = {1, 3};
	
	private int proximity;
	
	//animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 0;
	private static final int FALLING = 0;
	
	private BufferedImage parachute;
	
	public Rifleman(double x, double y, TileMap tm, Player player) 
	{
		super(x, y, tm, player);
		
		bullets = new ArrayList<Projectile>();
		firing = false;
		fireDelay = 600;
		
		recoverLength = 100;
		
		moveSpeed = 0.3;
		moveSpeedLeft = 0.3;
		moveSpeedRight = 0.3;
		maxSpeed = 5.0;
		maxSpeedLeft = 5.0;
		maxSpeedRight = 5.0;
		stopSpeed = 0.4;
		fallSpeed = 0.25;
		maxFallSpeed = 7.0;
		jumpStart = -1.0;
		
		width = 50;
		height = 70;
		cwidth = 50;
		cheight = 70;
		
		proximity = (int) (Math.random()*3*50 + 100);

		facingRight = true;
		
		try
		{
			parachute = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/parachute.png"));
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/enemyWalkingSprite.png"));
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
			BufferedImage playerHurtSpritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/enemyWalkingSprite.png"));
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
		currentAction = FALLING;
		animation.setFrames(entitySprites.get(FALLING));
		animation.setDelay(200);
		
		falling = true;
		
		//create the animation for the gun
		gunAnimation = new Animation();
		gunAnimation.setFrames(gunSprites);
		gunAnimation.setDelay(200);
		
		super.gunPosX = 15;
		super.gunPosY = 20;
		
		//health
		health = 2;
	}
	
	public void update() 
	{
		
		if (health > 0)
		{
			getMovement();
			myCheckCollision();
			getAttack();
		}
		else
		{
			dy = 10.0;
			y += dy;
		}
		
		x += tileMap.getDX();
		y += tileMap.getDY();
		
		getAnimation();
		getBulletCollision();

		yFromBottom += (-dy + tileMap.getDY());
		
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

		if (y > GamePanel.HEIGHT + 400)
		{
			//remove enemy as it is too low
			this.remove = true;
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
		
		if(fallingAnim)
			g.drawImage(parachute, (int)(x+(25-(parachute.getWidth()/2))), (int)(y-33), parachute.getWidth(), parachute.getHeight(), null);
		
		super.drawGun(g);
		
		for(Projectile p: bullets)
		{
			p.draw(g);
		}
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
				bullets.add(new Projectile(x, y, angle, 2, tileMap));
				fireTimer = System.nanoTime();
			}
		}
	}

	public void getMovement()
	{
		if(relX < -proximity)
		{
			right = true;
			left = false;
			idle = false;
		}
		else if(relX > proximity)
		{
			right = false;
			left = true;
			idle = false;
		}
		else
		{
			right = false;
			left = false;
			idle = true;
		}
		if(relY > proximity)
		{
			if(!falling)
			{
				if(!jumped)
				{
					idle = false;
					jump = true;
					drop = false;
				}
			}
			else
				drop = false;
		}
		else if(relY < -30)
		{
			falling = true;
			drop = true;
			idle = false;
		}
		else
		{
			falling = false;
			drop = false;
		}
		
		//MOVING LEFT AND RIGHT
		if(left)
		{
			dx -= moveSpeedLeft;
			if(dx < -maxSpeedLeft) dx = -maxSpeedLeft;
		}

		if(right)
		{
			dx += moveSpeedRight;
			if(dx > maxSpeedRight) dx = maxSpeedRight;
		}

		if(!left && !right)
		{
			if(dx < 0.0) 
			{
				dx += stopSpeed;
				if(dx > 0.0) dx = 0.0;
			}
			if(dx > 0.0) 
			{
				dx -= stopSpeed;
				if(dx < 0.0) dx = 0.0;
			}
			dx = -player.getDX();
		}

		//JUMPING AND FALLING
		if(jump)
		{
			if(!jumped)
			{
				jumpHeight = yFromBottom + ((int)Math.random()*70+30);
				jumped = true;
			}
			if(jumped)
			{
				if(yFromBottom < jumpHeight) dy = jumpStart*3;
				if(yFromBottom >= jumpHeight) 
				{
					jumpHeight = -9000; //arbitrary number, just has to be way below the player so they are always above jumpHeight at this point
					falling = true;
				}
			}
		}
		
		if(falling)
		{
			jump = false;
			if(dy > 0.0 && gliding)
			{
				dy = 1;
			}
			else if(dy < maxFallSpeed) dy += fallSpeed;
			else dy = maxFallSpeed;
			
			if(!fallingAnim && dy > 0) fallingAnim = true;
		}
		else
		{
			fallingAnim = false;
		}
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
			if(currentAction != WALKING && !idle)
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
		player.increasePoints(750);
	}
}