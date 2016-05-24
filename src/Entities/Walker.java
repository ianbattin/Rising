package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Main.GamePanel;
import TileMap.Tile;
import TileMap.TileMap;

public class Walker extends Enemy
{
	//animation
	private final int[] numFrames = {1, 3};
	
	private Tile currentTile;
	
	private double windSpeedX;
	private double windSpeedY;
	private boolean resetWindOnLand;
	
	//animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	

	public Walker(double x, double y, TileMap tm, Player player) 
	{
		super(x, y, tm, player);
		
		bullets = new ArrayList<Projectile>();
		recoverLength = 100;
		
		moveSpeed = 0.3;
		moveSpeedLeft = 0.3;
		moveSpeedRight = 0.3;
		maxSpeed = 2.0;
		maxSpeedLeft = 2.5;
		maxSpeedRight = 2.5;
		stopSpeed = 0.4;
		fallSpeed = 0.25;
		maxFallSpeed = 7.0;
		windSpeedX = 0;
		windSpeedY = 0;
		resetWindOnLand = false;
		
		width = 50;
		height = 70;
		cwidth = 50;
		cheight = 70;

		facingRight = true;
		
		try
		{			
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

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(entitySprites.get(IDLE));
		animation.setDelay(200);
		
		falling = true;
		
		//health
		health = 1;
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
		
		for(int i = 0; i < bullets.size(); i++)
		{	
			if(bullets.get(i).getRemove())
			{
				bullets.remove(i);
				i--;
				break;
			}
			bullets.get(i).update();
			if(bullets.get(i).notOnScreen()) bullets.remove(i);
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
	}
	
	public void getAttack()
	{
		relX = (int) (this.x - (int)player.getX());
		relY = (int) (this.y - (int)player.getY());
		this.setAngle(Math.atan2(-relY, -relX));
		this.setAngle(angle + Math.random()*Math.PI/12 - Math.PI/12);
		
		if (player.getX() < this.getX()+ 30 && player.getX() > this.getX()-30 && player.getY() < this.getY()+100 && player.getY() > this.getY()-30)
		{
			player.playerHurt(1);
		}
		
	}

	public void getMovement()
	{
		if(player.getX() > this.getX())
		{
			this.facingRight = true;
		}
		else
		{
			this.facingRight = false;
		}
		//MOVING LEFT AND RIGHT
		
		if (player.getY() < this.getY()+150 && player.getY() > this.getY()-100 && !(player.getX() < this.getX()+ 20 && player.getX() > this.getX()-20))
		{
			right = canGoRight() && this.facingRight;
			left = canGoLeft() && !this.facingRight;
			idle = false;
		}
		else
		{
			right = left = false;
			idle = true;
		}
		if(left)
		{
			dx -= moveSpeed;
			if(dx < -maxSpeed) dx = -maxSpeed;
		}

		if(right)
		{
			dx += moveSpeed;
			if(dx > maxSpeed) dx = maxSpeed;
		}
	
		if(!left && !right)
		{
			dx =  0;
		}
		/*
		//JUMPING AND FALLING
		if(jump)
		{
			if(!jumped)
			{
				jumpHeight = yFromBottom + (100); //edited to be "effectable"
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
		if(doubleJump)
		{
			if(!doubleJumped)
			{
				jumpHeight = yFromBottom + (50.0); //edited to be "effectable"
				doubleJumped = true;
			}
			if(jumped)
			{
				if(yFromBottom < jumpHeight) dy = jumpStart*2; //edited to be "effectable"
				if(yFromBottom >= jumpHeight) 
				{
					jumpHeight = -9000; //arbitrary number, just has to be way below the player so they are always above jumpHeight at this point
				}
			}
			falling = true;
		}
		*/
		if(falling)
		{
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
		
		x += dx*Enemy.slowDown + windSpeedX;
		y += dy*Enemy.slowDown + windSpeedY;
	}
	
	public void getAnimation()
	{
		if(idle)
		{
			if(currentAction != IDLE)
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
		/*
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
		*/
		if (isFlashing) animation.changeFrames(entityHurtSprites.get(currentAction));
		else animation.changeFrames(entitySprites.get(currentAction));
		
		animation.update();		
	}
	
	private boolean canGoRight()
	{
		if (currentTile == null) return false;
		for(int i = 0; i < tileMap.tiles.size(); i++)
		{
			Tile t = tileMap.tiles.get(i);
			double top = t.top;
			double left = t.left;
			if (top == currentTile.top && left >= currentTile.right-4 && left <= currentTile.right+4)
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean canGoLeft()
	{
		if (currentTile == null) return false;
		for(int i = 0; i < tileMap.tiles.size(); i++)
		{
			Tile t = tileMap.tiles.get(i);
			double top = t.top;
			double right = t.right;
			if (top == currentTile.top && right >= currentTile.left-4 && right <= currentTile.left+4)
			{
				return true;
			}
		}
		return false;
	}
	
	public void setWind(double xWind, double yWind, boolean resetOnLand)
	{
		this.windSpeedX = xWind;
		this.windSpeedY = yWind;
		this.resetWindOnLand = resetOnLand;
	}
	
	public void collided(int type, Tile t) 
	{
		currentTile = t;
		if(resetWindOnLand)
		{
			this.windSpeedX = 0;
			this.windSpeedY = 0;
			this.resetWindOnLand = false;
		}
	}

	public void collided(MapObject m) 
	{

	}
	
	public void onDeath()
	{
		player.increasePoints(500);
	}
}