package Entities;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import GameState.GameStateManager;
import GameState.PlayState;
import Main.GamePanel;
import TileMap.Tile;
import TileMap.TileMap;


public class Player extends MapObject
{	
	//TileMap
	private TileMap tm;
	
	//Attacks
	private ArrayList<Projectile> bullets;
	private long fireTimer;
	public static int fireDelay;
	public static boolean firing;
	private double angle;
	private boolean shootUp;
	private boolean shootDown;
	private boolean shootLeft;
	private boolean shootRight;
	
	//effect variables - @Ian you can edit this how you want when you get to cleaning this class up
	private boolean isUnderEffect;
	private double jumpHeightFactor;
	
	//score system
	private int points;
	int heightScore;

	//character position relative to bottom of tileMap
	private double yFromBottom;
	
	//health
	private byte numOfFramesToAnimHealth;
	private byte timesToLoop;
<<<<<<< HEAD
=======
	private boolean isFlashing;
	
>>>>>>> origin/master
	private ArrayList<BufferedImage> heartImages;
	
	//animation
	private ArrayList<BufferedImage[]> playerSprites;
	private ArrayList<BufferedImage[]> playerHurtSprites;
	private final int[] numFrames = { 1, 4, 3, 3, 4 };
	
	//animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int LANDING = 4;
	private static final int HOVERING = 5;
	private static final int DOUBLEJUMP = 6;
	
	public Player(TileMap tm)
	{	
		super(tm);
		this.tm = tm;
		
		bullets = new ArrayList<Projectile>();
		angle = 0.0;
		firing = false;
		fireDelay = 250;
		fireTimer = System.nanoTime();
		
		moveSpeed = 0.3;
		maxSpeed = 5.0;
		stopSpeed = 0.4;
		fallSpeed = 0.25;
		maxFallSpeed = 7.0;
		jumpStart = -3.0;
		
		width = 50;
		height = 70;
		cwidth = 50;
		cheight = 70;

		facingRight = true;
		
		points = 0;
		
		try
		{
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/MainCharacterSpriteSheet.png"));
			playerSprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < numFrames.length; i++)
			{
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++)
				{
					bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
				}
				playerSprites.add(bi);
			}
			
			//make the spritesheet for when the player is blinking red
			BufferedImage playerHurtSpritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/MainCharacterSpriteSheet.png"));
			for (int x = 0; x < playerHurtSpritesheet.getWidth(); x++)
			{
				for (int y = 0; y < playerHurtSpritesheet.getHeight(); y++)
				{
					int rgb = playerHurtSpritesheet.getRGB(x, y);
					int a = (rgb >> 24) & 0xFF;
					int r = (rgb >> 16) & 0xFF;
					int g = (rgb >> 8) & 0xFF;
					int b = rgb & 0xFF;
					if (r+150 > 255) r = 255;
					else r += 150;
					playerHurtSpritesheet.setRGB(x, y, (a*16777216)+(r*65536)+(g*256)+b);
				}
			}
			playerHurtSprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < numFrames.length; i++)
			{
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++)
				{
					bi[j] = playerHurtSpritesheet.getSubimage(j * width, i * height, width, height);
				}
				//sprites.add(bi);
				playerHurtSprites.add(bi);
			}
			
			//get sprites for heart. This will be improved if we make the two hearts be on the same spritesheet image
			heartImages = new ArrayList<BufferedImage>();
			BufferedImage h1 = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/fullHeart2.png"));
			BufferedImage h2 = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/emptyHeart2.png"));

			BufferedImage h3 = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/fullHeart2.png"));
			BufferedImage h4 = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/emptyHeart2.png"));

			
			heartImages.add(h1);
			heartImages.add(h2);
			heartImages.add(h3);
			heartImages.add(h4);
			for (int l = 2; l < heartImages.size(); l++)
			{
				for (int x = 0; x < heartImages.get(l).getWidth(); x++)
				{
					for (int y = 0; y < heartImages.get(l).getHeight(); y++)
					{
						int rgb = heartImages.get(l).getRGB(x, y);
						int a = (rgb >> 24) & 0xFF;
						int r = (rgb >> 16) & 0xFF;
						int g = (rgb >> 8) & 0xFF;
						int b = rgb & 0xFF;
						if (r+150 > 255) r = 255;
						else r += 150;
						if (g-25 < 0) r = 0;
						else g -= 25;
						if (b-25 < 0) r = 0;
						else b -= 25;
						heartImages.get(l).setRGB(x, y, (a*16777216)+(r*65536)+(g*256)+b);
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		animation = new Animation();

		x = GamePanel.WIDTH/2 - 20;
		y = -100;
				
		yFromBottom =  GamePanel.HEIGHTSCALED - y;
		
		dx = 0.0;
		dy = 0.0;
		
		currentAction = FALLING;
		animation.setFrames(playerSprites.get(FALLING));
		animation.setDelay(200);
		
		falling = true;
		
		//health
		health = 5;
		
		numOfFramesToAnimHealth = 0;
		timesToLoop = 0;
		isFlashing = false;
		
		//effects
		isUnderEffect = false;
		jumpHeightFactor = 1;
		heightScore = 0;
	}
	
	public void update()
	{	
		if(idle) dy = dy + 1;
		getMovement();
		if (health > 0)
		{
			myCheckCollision(tm);
		}
		else
		{
			falling = true;
		}
		getAnimation();
		getAttack();
		
		for(Projectile p: bullets)
		{
			p.update();
		}

		//Camera left and right movement (Player always stays centered)
		tm.setXVector(-dx);

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
		
		yFromBottom += (-dy + tm.getDY());
		
		if(x - width/2 < 0) x = width/2;
		if(x + width/2 > GamePanel.WIDTH) x = GamePanel.WIDTH - width/2;
		if(y  + cheight/2 > GamePanel.HEIGHT)
		{
			//y = GamePanel.HEIGHT - cheight/2;
		}
		
		if(dy < 0 && heightScore <= yFromBottom)
		{
			heightScore = (int)yFromBottom;
			points += -dy;
		}
		
		if (y > GamePanel.HEIGHT + 50 + height)
		{
			playerHurt(1);
		}
		
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
		if (isFlashing)
		{
			for (int i = 0; i < 5; i++)
			{
				if (i < health)
				{
					g.drawImage(heartImages.get(2), 10 + (i*40), 10 , null);
				}
				else
				{
					g.drawImage(heartImages.get(3), 10 + (i*40), 10 , null);
				}
			}
		}
		else
		{
			for (int i = 0; i < 5; i++)
			{
				if (i < health)
				{
					g.drawImage(heartImages.get(0), 10 + (i*40), 10 , null);
				}
				else
				{
					g.drawImage(heartImages.get(1), 10 + (i*40), 10 , null);
				}
			}
		}
		
		setMapPosition();

		if(facingRight)
		{
			g.drawImage(animation.getImage(), (int)(x + xmap - width / 2), (int)(y + ymap - height / 2), width, height, null);
		}
		else
		{
			g.drawImage(animation.getImage(), (int)(x + xmap - width / 2 + width), (int)(y + ymap - height / 2), -width, height, null);
		}
		
		for(Projectile p: bullets)
		{
			p.draw(g);
		}
	}
	
	public void collided(int type, Tile t)
	{
		if(recovering)
		{
			long elapsed = (System.nanoTime() - recoverLength) / 1000000;
			if(3000 <= elapsed)
			{
				recovering = false;
			}
		}
		else if(type == 17) 
		{
			playerHurt(1);
			dy = -8.0;
			if(dx >= 0) dx = -8.0;
			else dx = 8.0;
			recovering = true;
			recoverLength = System.nanoTime();
		}
		else if(type == 20)
		{
			falling = true;
			dy = -20.0;
			t.setAnimated(true);
		}
	}
	
	public void getAttack()
	{
		if(shootUp) angle = Math.toRadians(270);
		if(shootDown) angle = Math.toRadians(90);
		if(shootLeft) angle = Math.toRadians(180);
		if(shootRight) angle = Math.toRadians(0);
		if(shootUp && shootRight) angle = Math.toRadians(315);
		if(shootUp && shootLeft) angle = Math.toRadians(225);
		if(shootDown && shootLeft) angle = Math.toRadians(135);
		if(shootDown && shootRight) angle = Math.toRadians(45);
		
		if(!shootUp && !shootDown && !shootLeft && !shootRight) firing = false;
		
		if(firing)
		{
			long elapsed= (System.nanoTime() - fireTimer) / 1000000;
			if(fireDelay <= elapsed)
			{
				bullets.add(new Projectile(x, y, angle, 1, tm));
				fireTimer = System.nanoTime();
			}
		}
	}
	
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
		yFromBottom = GamePanel.HEIGHTSCALED - y;
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
		if(jump)
		{
			if(!jumped)
			{
				jumpHeight = yFromBottom + (100*jumpHeightFactor); //edited to be "effectable"
				jumped = true;
			}
			if(jumped)
			{
				if(yFromBottom < jumpHeight) dy = jumpStart*3*jumpHeightFactor;
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
				jumpHeight = yFromBottom + (50.0*jumpHeightFactor); //edited to be "effectable"
				doubleJumped = true;
			}
			if(jumped)
			{
				if(yFromBottom < jumpHeight) dy = jumpStart*2*jumpHeightFactor; //edited to be "effectable"
				if(yFromBottom >= jumpHeight) 
				{
					jumpHeight = -9000; //arbitrary number, just has to be way below the player so they are always above jumpHeight at this point
				}
			}
			falling = true;
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
			fallingAnim = false;
	}
	
	public void getAnimation()
	{
		if(idle)
		{
			if(currentAction != IDLE && currentAction != WALKING)
			{
				currentAction = IDLE;
				animation.setFrames(playerSprites.get(IDLE));
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
				animation.setFrames(playerSprites.get(WALKING));
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
				animation.setFrames(playerSprites.get(JUMPING));
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
				animation.setFrames(playerSprites.get(FALLING));
				animation.setDelay(200);
				width = 50;
				height = 70;
			}
		}
		/*else if(landing)
		{
			if(currentAction != LANDING)
			{
				currentAction = LANDING;
				animation.setFrames(sprites.get(LANDING));
				animation.setDelay(200);
				width = 50;
				height = 70;
			}
		}
		else if(gliding)
		{
			if(currentAction != HOVERING)
			{
				currentAction = HOVERING;
				animation.setFrames(sprites.get(HOVERING));
				animation.setDelay(200);
				width = 50;
				height = 70;
			}
		}
		else if(doubleJump)
		{
			if(currentAction != DOUBLEJUMP)
			{
				currentAction = DOUBLEJUMP;
				animation.setFrames(sprites.get(DOUBLEJUMP));
				animation.setDelay(50);
				width = 50;
				height = 70;
			}
		}*/
		
		if (isFlashing) animation.changeFrames(playerHurtSprites.get(currentAction));
		else animation.changeFrames(playerSprites.get(currentAction));
		
		animation.update();
			
	}
	
	public double getCharacterY()
	{
		return this.yFromBottom;
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
		health -= amount;
		numOfFramesToAnimHealth = 10;
		timesToLoop = 5;
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
			case 1:
			{
				playerHeal(1);
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
	
	public int getPoints()
	{
		return points;
	}
	
	public void keyPressed(int k)
	{
		if(k == GameStateManager.up)
		{
			if(!jumped)
			{
				jump = true;
				doubleJumpable = false;
				idle = false;
			}
			/*if(jumped && !doubleJump && doubleJumpable)
			{
				falling = false;
				jump = false;
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
			idle = false;
		}
		if(k == GameStateManager.right)
		{
			right = true;
			idle = false;
		}
		if(k == GameStateManager.glide)
		{
			gliding = true;
			idle = false;
		}	
		if(k == GameStateManager.shootUp)
		{
			shootUp = true;
			firing = true;
		}
		if(k == GameStateManager.shootDown)
		{
			shootDown = true;
			firing = true;
		}
		if(k == GameStateManager.shootLeft)
		{
			shootLeft = true;
			firing = true;
		}
		if(k == GameStateManager.shootRight)
		{
			shootRight = true;
			firing = true;
		}
	}
	
	public void keyReleased(int k)
	{
		if(k == GameStateManager.up)
		{
			falling = true;
			jump = false;
			idle = true;
			doubleJumpable = true;
			
			if(doubleJump)
			{
				doubleJump = false;
				jump = false;
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
		if(k == GameStateManager.shootUp)
		{
			shootUp = false;
		}
		if(k == GameStateManager.shootDown)
		{
			shootDown = false;
		}
		if(k == GameStateManager.shootLeft)
		{
			shootLeft = false;
		}
		if(k == GameStateManager.shootRight)
		{
			shootRight = false;
		}
	}

	@Override
	public void collided(int type, Tile t, MapObject m) {
		// TODO Auto-generated method stub
		
	}
}