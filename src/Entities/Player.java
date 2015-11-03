package Entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import GameState.GameStateManager;
import GameState.PlayState;
import Main.GamePanel;
import TileMap.Tile;
import TileMap.TileMap;


public class Player extends MapObject
{	
	private TileMap tm;
	
	//effect variables - @Ian you can edit this how you want when you get to cleaning this class up
	private boolean isUnderEffect;
	private double jumpHeightFactor;
	
	//character position relative to bottom of tileMap
	private double character_y;
		
	
	//animation
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {
			2, 4, 2, 1, 2, 4
	};
	
	//animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int HOVERING = 4;
	private static final int DOUBLEJUMP = 5;
	
	public Player(TileMap tm)
	{	
		super(tm);
		this.tm = tm;
		
		moveSpeed = 0.3;
		maxSpeed = 5.0;
		stopSpeed = 0.4;
		fallSpeed = 0.25;
		maxFallSpeed = 7.0;
		jumpStart = -3.0;
		
		width = 20;
		height = 40;
		cwidth = 20;
		cheight = 40;

		facingRight = true;
		
		try
		{
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/RisingBasicSpriteSheet.png"));
			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < 6; i++)
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

		x = GamePanel.WIDTH/2;
		y = GamePanel.HEIGHT/2;
				
		character_y =  GamePanel.heightScaled - y;
		
		dx = 0.0;
		dy = 0.0;
		
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
		
		falling = true;
		
		//effects
		isUnderEffect = false;
		jumpHeightFactor = 1;
	}
	
	public void update()
	{	
		if(idle) dy = dy + 1;
		getMovement();
		myCheckCollision();
		getAnimation();

		//Camera left and right movement (Player always stays centered)
		if(x < GamePanel.WIDTH/2)
		{
			tm.setXVector(-dx);
		}
		if(x > GamePanel.WIDTH/2)
		{
			tm.setXVector(-dx);
		}
		else
		{
			tm.setXVector(0);
			x += dx;
		}

		if(y < 300) 
		{
			if(PlayState.tileStart) tm.setYVector(-dy + 2);
			if(dy >= 0 && PlayState.tileStart) tm.setYVector(2.0);
			if(PlayState.tileStart)
			{
				if(dy > 0) y += (dy);
			}
			else
			{
				y += dy;
			}
			
		}
		else 
		{
			if(PlayState.tileStart) tm.setYVector(2.0);
			y += dy;
		}
		
		character_y += (-dy + tm.getDY());

		
		if(x - width/2 < 0) x = width/2;
		if(x + width/2 > GamePanel.WIDTH) x = GamePanel.WIDTH - width/2;
		if(y  + cheight/2 > GamePanel.HEIGHT)
		{
			//y = GamePanel.HEIGHT - cheight/2;
		}
	}
	
	public void draw(Graphics2D g) 
	{
		setMapPosition();

		if(facingRight)
		{
			g.drawImage(animation.getImage(), (int)(x + xmap - width / 2), (int)(y + ymap - height / 2), width, height, null);
		}
		else
		{
			g.drawImage(animation.getImage(), (int)(x + xmap - width / 2 + width), (int)(y + ymap - height / 2), -width, height, null);
		}
	}
	
	public void myCheckCollision()
	{
		boolean collided = false;
		for(Tile t: tiles)
		{
			int collisionLeft = t.left;
			int collisionRight = t.right;
			int collisionTop = t.top;
			int collisionBottom = t.bottom;

			if(dy > 0 && (collisionLeft <= x && x < collisionRight) && (collisionTop <= y + height/2 && y + height/2 < collisionBottom) && !drop)
			{
				y = t.top - cheight/2;
				dy = tm.getDY();
				jumped  = false;
				doubleJumped = false;
				falling = false;
				gliding = false;
			}
			if(!collided && (collisionLeft <= x && x < collisionRight) && (collisionTop <= y + height/2 + 1 && y + height/2 + 1 < collisionBottom && !drop)) 
			{
				collided = true;
			}
			
		}
		if(!collided && !jumping && !jumped && !doubleJumped)
		{
			falling = true;
		}
	}
	
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
		character_y = GamePanel.heightScaled - y;
	}
	
	public void getMovement()
	{
		//MOVING LEFT AND RIGHT
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
		}

		//JUMPING AND FALLING
		if(jumping)
		{
			if(!jumped)
			{
				jumpHeight = character_y + (100*jumpHeightFactor); //edited to be "effectable"
				jumped = true;
			}
			if(jumped)
			{
				if(character_y < jumpHeight) dy = jumpStart*3*jumpHeightFactor;
				if(character_y >= jumpHeight) 
				{
					jumpHeight = -9000; //arbitrary number, just has to be way below the player so they are always above jumpHeight at this point
					falling = true;
				}
			}
		}

		/*
		if(doubleJump)
		{
			if(!doubleJumped)
			{
				jumpHeight = this.yJump - (50.0*jumpHeightFactor); //edited to be "effectable"
				doubleJumped = true;
			}
			if(jumped)
			{
				if(yJump > jumpHeight) dy = jumpStart*2*jumpHeightFactor; //edited to be "effectable"
				if(yJump <= jumpHeight) 
				{
					jumpHeight = 9000; //arbitrary number, just has to be way below the player so they are always above jumpHeight at this point
				}
			}
			falling = true;
		}
		*/
		if(falling)
		{
			jumping = false;
			if(dy > 0.0 && gliding)
			{
				dy = 1;
			}
			else if(dy < maxFallSpeed) dy += fallSpeed;
			else dy = maxFallSpeed;
		}
	}
	
	public void getAnimation()
	{
		if(left || right)
		{
			if(currentAction != WALKING)
			{
				if(right){ facingRight = true;	}
				else{ facingRight = false;	}
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(200);
				width = 20;
				height = 40;
			}
		}
		if(jumping)
		{
			if(currentAction != JUMPING)
			{
				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(200);
				width = 20;
				height = 40;
			}
		}
		else if(!left && !right)
		{
			if(currentAction != IDLE)
			{
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(200);
				width = 20;
				height = 40;
			}
		}
		if(falling)
		{
			if(currentAction != FALLING)
			{
				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(200);
				width = 20;
				height = 40;
			}
		}
		if(gliding)
		{
			if(currentAction != HOVERING)
			{
				currentAction = HOVERING;
				animation.setFrames(sprites.get(HOVERING));
				animation.setDelay(200);
				width = 20;
				height = 40;
			}
		}
		if(doubleJump)
		{
			if(currentAction != DOUBLEJUMP)
			{
				currentAction = DOUBLEJUMP;
				animation.setFrames(sprites.get(DOUBLEJUMP));
				animation.setDelay(50);
				width = 20;
				height = 40;
			}
		}
		
		animation.update();
			
	}
	
	public void keyPressed(int k)
	{
		if(k == GameStateManager.up)
		{
			if(!jumped)
			{
				jumping = true;
				idle = false;
			}
			/*if(jumped && !doubleJump)
			{
				falling = false;
				jumping = false;
				doubleJump = true;
				idle = false;
			}*/
		}
		if(k == GameStateManager.down)
		{
			falling = true;
			drop = true;
			idle = false;
		}
		if(k == GameStateManager.left)
		{
			left = true;
		}
		if(k == GameStateManager.right)
		{
			right = true;
		}
		if(k == GameStateManager.glide)
		{
			gliding = true;
			idle = false;
		}	
	}
	
	public void keyReleased(int k)
	{
		if(k == GameStateManager.up)
		{
			falling = true;
			jumping = false;
			idle = true;
			
			if(doubleJump)
			{
				doubleJump = false;
				jumping = false;
				idle = true;
			}
		}
		if(k == GameStateManager.down)
		{
			falling = true;
			drop = false;
			idle = true;
		}
		if(k == GameStateManager.left)
		{
			left = false;
		}
		if(k == GameStateManager.right)
		{
			right = false;
		}
		if(k == GameStateManager.glide)
		{
			gliding = false;
			idle = true;
		}
	}
	
	public double getCharacterY()
	{
		return this.character_y;
	}
	
	//starts effects, added to enable the pickups
	public void effectStart(int effect)
	{
		switch (effect)
		{
			case 0: 
			{
				jumpHeightFactor = 2;
				break;
			}
		}
		isUnderEffect = true;
		System.out.println("Effect started");
	}
	
	//resets the effects
	public void resetEffects()
	{
		isUnderEffect = false;
		jumpHeightFactor = 1;
		System.out.println("Effect ended");
	}
	
}