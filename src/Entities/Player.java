package Entities;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import GameState.GameStateManager;
import GameState.Level1State;
import GameState.PlayState;
import GameState.GameState;
import Main.GamePanel;
import Main.SoundPlayer;
import TileMap.Tile;
import TileMap.TileMap;


public class Player extends MapObject
{	
	//TileMap
	private PlayState playState;
	
	//Attacks
	private boolean shootUp;
	private boolean shootDown;
	private boolean shootLeft;
	private boolean shootRight;
	private ArrayList<Projectile> bullets;
	private long fireTimer;
	public int fireDelay;
	public boolean firing;
	private boolean mouseHeld;
	private double angle;
	
	//effect variables
	private boolean isUnderEffect;
	private double jumpHeightFactor, birdPos, healthPos;
	private boolean hasJetpack, hasBird, birdActive, hasArmor, slowTime, canGlide;
	private int birdMaxDy, birdMaxDx;
	private double birdX, birdY;
	private Explosion explosions;
	private Enemy chosenEnemy;
	private ArrayList<Integer> charBlurPos;
	private long coolDownTime;
	private Font backupFont, bannerFont;
	
	//score system
	private int heightScore;
	private ArrayList<int[]> bonusScores;

	//character position relative to bottom of tileMap
	private double yFromBottom;
	
	//health
	private int armorBoostHealth;
	private byte blueInc, redInc, greenInc;
	private boolean blueChg, redChg, greenChg;
	private short numOfFramesToAnimHealth, healthAphaVal;
	private boolean hasFlashed, isBlinking, isFlashing, healing;
	private ArrayList<BufferedImage> heartImages;
	
	//animation
	private ArrayList<BufferedImage[]> playerSprites;
	private ArrayList<BufferedImage[]> playerHurtSprites;
	private BufferedImage[] birdPickupSprites;
	private Animation birdAnimation;
	private final int[] numFrames = { 1, 4, 3, 3, 4 };
	private boolean tileMapMoving;
	private boolean canMove;
	
	//animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int LANDING = 4;
	private static final int HOVERING = 5;
	private static final int DOUBLEJUMP = 6;
	
	public Player(TileMap tm, PlayState playState)
	{	
		super(tm);
		this.playState = playState;
		
		bullets = new ArrayList<Projectile>();
		angle = 0.0;
		firing = false;
		fireDelay = 200;
		fireTimer = System.nanoTime();
		
		recoverLength = 3000;
		coolDownTime = 0;
		
		moveSpeedLeft = 0.3;
		moveSpeedRight = 0.3;
		maxSpeed = 5.0;
		maxSpeedLeft = 5.0;
		maxSpeedRight = 5.0;
		stopSpeed = 0.4;
		fallSpeed = 0.25;
		maxFallSpeed = 7.0;
		jumpStart = -5.0;
		
		width = 50;
		height = 70;
		cwidth = 25;
		cheight = 70;

		facingRight = true;
		
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
			
			BufferedImage birdPickupSpritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Tiles/birdSprite.png"));
			birdPickupSprites = new BufferedImage[2];
			for(int i = 0; i < 2; i++)
			{
				birdPickupSprites[i] = (birdPickupSpritesheet.getSubimage(i * 15, 0, 15, 15));
			}
			
			//get sprites for heart. This will be improved if we make the two hearts be on the same spritesheet image
			heartImages = new ArrayList<BufferedImage>();
			BufferedImage h1 = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/fullHeart2.png"));
			BufferedImage h2 = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/emptyHeart2.png"));

			BufferedImage h3 = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/fullHeart2.png"));
			BufferedImage h4 = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/emptyHeart2.png"));
			
			BufferedImage h5 = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/armorHeart.png"));
			
			heartImages.add(h1);
			heartImages.add(h2);
			heartImages.add(h3);
			heartImages.add(h4);
			heartImages.add(h5);
			for (int l = 2; l < 4; l++)
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
						if (g-25 < 0) g = 0;
						else g -= 25;
						if (b-25 < 0) b = 0;
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
		
		//these are always the same; there is no change in the animations of the bird
		birdAnimation = new Animation();
		birdAnimation.setFrames(birdPickupSprites);
		birdAnimation.setDelay(200);
		
		animation = new Animation();
				
		yFromBottom =  GamePanel.HEIGHTSCALED - y;
		
		currentAction = FALLING;
		animation.setFrames(playerSprites.get(FALLING));
		animation.setDelay(200);
		
		falling = true;
		canMove = true;
		
		//health
		health = 5;
		healthAphaVal = 255;
		numOfFramesToAnimHealth = 0;
		healing = isFlashing = isBlinking = hasFlashed = false;
		
		//effects
		hasJetpack = false; 
		hasBird = false; 
		hasArmor = false;
		slowTime = isUnderEffect = hasJetpack  = hasBird = false;
		jumpHeightFactor = 1;
		canGlide = slowTime = isUnderEffect = hasJetpack = birdActive = hasBird = false;
		birdMaxDy = 4;
		birdMaxDx = 4;
		birdX = 0;
		birdY = 0;
		birdPos = 0;
		armorBoostHealth = 0;
		greenInc = blueInc = redInc = 1;
		redChg = blueChg = greenChg = false;
		jumpHeightFactor = 1;
		healthPos = 0;
		heightScore = 0;
		bonusScores = new ArrayList<int[]>();
		charBlurPos = new ArrayList<Integer>();
		backupFont = new Font("Times", Font.PLAIN, 20);
		bannerFont = new Font("Munro", Font.PLAIN, 20);	
	}
	
	public void update()
	{	
		if (health > 0)
		{
			myCheckCollision();
			//if(onScreen()) checkPixelColorCollision(tileMap);
			getAttack();
		}
		else
		{
			falling = true;
			gliding = false;
		}
		getMovement();
		getAnimation();
		
		if(hasBird)
		{
			birdAnimation.update();
		}
		
		if(explosions != null && !explosions.getRemove())
		{
			if(explosions.getRemove())
				explosions = null;
			else
				explosions.update();
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
		
		yFromBottom += (-dy + tileMap.getDY());
		
		if(x - width/2 < 0) x = width/2;
		if(x + width/2 > GamePanel.WIDTH) x = GamePanel.WIDTH - width/2;
		
		if(dy < 0 && heightScore <= yFromBottom)
		{
			heightScore = (int)yFromBottom;
			playState.setScore((int) (playState.getScore() + -dy));
		}
		
		if (y > GamePanel.HEIGHT + 75 + height)
		{
			playerHurt(50);
		}
		
		if (recovering)
		{
			long elapsed = (System.nanoTime() - recoverTimer) / 1000000;
			if(recoverLength <= elapsed)
			{
				recovering = false;
			}
			if (!hasFlashed && numOfFramesToAnimHealth < 30)
			{
				isFlashing = true;
				numOfFramesToAnimHealth++;
			}
			else if (!hasFlashed && !(numOfFramesToAnimHealth < 30))
			{
				isFlashing = false;
				hasFlashed = true;
				numOfFramesToAnimHealth = 0;
			}
			
			if(hasFlashed && numOfFramesToAnimHealth < 20)
			{
				if(numOfFramesToAnimHealth < 5)
					isBlinking = true;
				else
					isBlinking = false;
				numOfFramesToAnimHealth++;
			}
			else if(hasFlashed && !(numOfFramesToAnimHealth < 20))
			{
				numOfFramesToAnimHealth = 0;
			}
		}
		else
		{
			isBlinking = false;
			isFlashing = false;
			hasFlashed = false;
		}

		if(hasBird && ++birdPos > 720) birdPos = 0;
		if(healing) healthPos += 5;
	}
	
	public void draw(Graphics2D g) 
	{
		if(tileMap.getShowCollisonBox())
		{
			g.setColor(Color.RED);
			g.draw(this.getRectangle());
		}
		
		if(explosions != null)
		{
			explosions.draw(g);
		}
		
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
				if(hasArmor && i < armorBoostHealth)
				{
					g.drawImage(heartImages.get(2), 10 + (i*40), 52 , null);
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
				
				if(hasArmor && i < armorBoostHealth)
				{
					for (int x = 0; x < heartImages.get(4).getWidth(); x++)
					{
						for (int y = 0; y < heartImages.get(4).getHeight(); y++)
						{
							int rgb = heartImages.get(4).getRGB(x, y);
							int alpha = (rgb >> 24) & 0xFF;
							int red = (rgb >> 16) & 0xFF;
							int green = (rgb >> 8) & 0xFF;
							int blue = rgb & 0xFF;
							
							if (!redChg && (red+redInc > 255 || red+redInc < 10)) 
							{
								redChg = true;
							}
							else if(!(red+redInc > 255 || red+redInc < 10)) red += redInc;
							if (!greenChg && (green+greenInc > 250 || green+greenInc < 10))
							{
								greenChg = true;
							}
							else if(!(green+greenInc > 250 || green+greenInc < 10)) green += greenInc;
							if (!blueChg && (blue+blueInc > 250 || blue+blueInc < 10)) 
							{
								blueChg = true;
							}
							else if(!(blue+blueInc > 250 || blue+blueInc < 10)) blue += blueInc;					
							
							heartImages.get(4).setRGB(x, y, (alpha*16777216)+(red*65536)+(green*256)+blue);
						}
					}
					if(redChg) redInc*=-1;
					if(greenChg) greenInc *= -1;
					if(blueChg) blueInc *= -1;
					redChg = blueChg = greenChg = false;
					
					g.drawImage(heartImages.get(4), 10 + (i*40), 52 , null);
				}
			}
		}
		
		setMapPosition();
		drawEffects(g);

		for(Projectile p: bullets)
		{
			p.draw(g);
		}	
	}
	
	public void collided(int type, Tile t)
	{
		if(type == 17) 
		{
			playerHurt(1);
		}
		if(type == 20)
		{
			falling = true;
			//set the animation to be jumping
			currentAction = JUMPING;
			animation.setFrames(playerSprites.get(JUMPING));
			animation.setDelay(150);
			animation.setDone(true);
			width = 50;
			height = 70;

			dy = -20.0;
			t.setAnimated(true);
		}
		if(type == 400)
		{
			this.effectStart(3);
			t.setType(0);
		}
		/*if(type == 666)
		{
			t.setType(0);
			falling = true;
			dy = -60.0;
		}*/
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
		
		if(!shootUp && !shootDown && !shootLeft && !shootRight && !mouseHeld) firing = false;
		
	    
		if(firing)
		{
			
			long elapsed= (System.nanoTime() - fireTimer) / 1000000;
			if(fireDelay <= elapsed)
			{
				bullets.add(new Projectile(x + width/2, y + height/2, angle, 1, tileMap));
				fireTimer = System.nanoTime();
			}
		}
	}
	
	public void setMouseHeld(boolean b)
	{
		mouseHeld = b;
	}
	
	public boolean getMouseHeld()
	{
		return mouseHeld;
	}
	
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
		yFromBottom = GamePanel.HEIGHTSCALED - y;
	}
	
	public void setCanMove(boolean val)
	{
		canMove = val;
	}
	
	public void getMovement()
	{
		//MOVING LEFT AND RIGHT
		if(!canMove)
		{
			left = false;
			right = false;
			if(!jumped) jump = false;
		}
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
			if(!jump && !falling) idle = true;
		}

		//JUMPING AND FALLING
		if(jump)
		{
			if(!jumped)
			{
				Timer timer = new Timer();
				timer.schedule(new TimerTask()
				{
					public void run()
					{	
						jumpHeight = yFromBottom + (100*jumpHeightFactor);
						if(!jumped) SoundPlayer.playClip("jump.wav");
						jumped = true;
					}
					
				}, 200);
			}
			if(jumped)
			{
				if(yFromBottom < jumpHeight) dy = jumpStart*jumpHeightFactor;
				if(yFromBottom >= jumpHeight) 
				{
					jumpHeight = -9000; //arbitrary number, just has to be way below the player so they are always above jumpHeight at this point
					falling = true;
				}
			}
		}
	
		/*if(doubleJump)
		{
			if(!doubleJumped)
			{
				jumpHeight = yFromBottom + (50.0*jumpHeightFactor);
				doubleJumped = true;
			}
			if(jumped)
			{
				if(yFromBottom < jumpHeight) dy = jumpStart*2*jumpHeightFactor;
				if(yFromBottom >= jumpHeight) 
				{
					jumpHeight = -9000; //arbitrary number, just has to be way below the player so they are always above jumpHeight at this point
				}
			}
			falling = true;
		}*/
		
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
		
		moveTileMapX();
		if(tileMapMoving)
		{
			if(y < 300) 
			{
				if(Level1State.tileStart && tileMap.getMoving())
				{

					if (dy >= 0) tileMap.setYVector(2.0);
					else tileMap.setYVector(-dy + 2);
					if(dy > 0) y += (dy);
				}
				else
				{
					y += dy;
				}
			}
			else 
			{	
				if(slowTime) tileMap.setYVector(0.5);
				else if(Level1State.tileStart) tileMap.setYVector(2.0);
				y += dy;
			}
		}
		else
			y += dy;
	}
	
	public void getAnimation()
	{
		if(idle)
		{
			if(currentAction != IDLE && (!left || !right))
			{
				currentAction = IDLE;
				animation.setFrames(playerSprites.get(IDLE));
				animation.setDelay(50);
				width = 50;
				height = 70;
			}
		}
		
		if(left || right)
		{
			if(right) facingRight = true;
			else facingRight = false;
			if(currentAction != WALKING && !fallingAnim && currentAction != JUMPING)
			{
				animation.setDone(false);
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
		else if(gliding)
		{
			if(currentAction != HOVERING)
			{
				currentAction = HOVERING;
				//animation.setFrames(sprites.get(HOVERING));
				animation.setDelay(200);
				width = 50;
				height = 70;
			}
		}
		else if(doubleJump)
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
		
		if (isFlashing)	animation.changeFrames(playerHurtSprites.get(currentAction));
		else animation.changeFrames(playerSprites.get(currentAction));
		
		animation.update();
	}
	
	public void moveTileMapX()
	{
		if(tileMap.getTotalWidth() == 800)
		{
			x += (dx + tileMap.getDX());
			tileMap.setXVector(0);
			tileMap.setX(0);
		}
		else if(-tileMap.getXMove() <= -tileMap.getTotalWidth() + GamePanel.WIDTH || -tileMap.getXMove() >= tileMap.getTotalWidth() - GamePanel.WIDTH)
		{
			x += dx;
			tileMap.setXVector(0);
			
			if(-tileMap.getXMove() <= -tileMap.getTotalWidth() + GamePanel.WIDTH && x > GamePanel.WIDTH/2)
			{
				tileMap.setXVector(-this.getDX());
				x += (dx + tileMap.getDX());
			}
			else if(-tileMap.getXMove() >= tileMap.getTotalWidth() - GamePanel.WIDTH && x < GamePanel.WIDTH/2)
			{
				tileMap.setXVector(-this.getDX());
				x += (dx + tileMap.getDX());
			}
		}
		else
		{
			tileMap.setXVector(-this.getDX());
			x += (dx + tileMap.getDX());
		}
	}
	
	public double getCharacterY()
	{
		return this.yFromBottom;
	}
	
	public void setTileMapMoving(boolean b)
	{
		tileMapMoving = b;
	}

	public int getPoints()
	{
		return playState.getScore();
	}
	
	public void increasePoints(int amount)
	{
		int[] data = {amount, 255};
		bonusScores.add(data);
		playState.setScore(playState.getScore() + amount);	
	}
	
	public ArrayList<int[]> getBonusScores()
	{
		return bonusScores;
	}
	
	public int getPlayerHealth()
	{
		return health;
	}
	public void playerHeal(int amount)
	{
		health += amount;
		healing = true;
	}
	public void playerHurt(int amount)
	{
		if(recovering)
		{
			long elapsed = (System.nanoTime() - recoverTimer) / 1000000;
			if(recoverLength <= elapsed)
			{
				recovering = false;
			}
		}
		else
		{
			if (hasArmor) 
			{
				armorBoostHealth -= amount;
				if(armorBoostHealth < 0)
				{
					health += armorBoostHealth;
				}
				recovering = true;
				recoverTimer = System.nanoTime();
				numOfFramesToAnimHealth = 0;
				if(armorBoostHealth == 0)
					hasArmor = false;
			}
			else
			{
				health -= amount;
				dy = -2.0;
				if(dx >= 0) dx = -2.0;
				else dx = 2.0;
				recovering = true;
				numOfFramesToAnimHealth = 0;
				if(health > 0)
					recoverTimer = System.nanoTime();
				else
				{
					numOfFramesToAnimHealth = -500;
					recoverTimer = System.nanoTime();
				}
			}
			if(health > 0)
				SoundPlayer.playClip("hurt.wav");
			else
				SoundPlayer.playClip("dies.wav");
		}
	}
		
	//starts effects, added to enable the pickups
	public void effectStart(int effect)
	{
		switch (effect)
		{
			case 0: 
			{
				canGlide = true;
				jumpHeightFactor = 1.5;
				break;
			}
			case 1:
			{
				playerHeal(1);
				break;
			}
			case 2:
			{
				dy = -60;
				hasJetpack = true;
				jump = true;
				break;
			}
			case 3:
			{
				//bird
				hasBird = true;
				break;
			}
			case 4:
			{
				//armor
				hasArmor = true;
				armorBoostHealth = 5;
				break;
			}
			case 5:
			{
				slowTime = true;
				playState.slowTimeStart();
				jumpHeightFactor = 1.2;
				break;
			}
		}
		coolDownTime = 10000000000L;
		isUnderEffect = true;
		System.out.println("Effect started: " + effect);
	}
	
	//resets the effects
	public void resetEffects()
	{
		playState.slowTimeEnd();
		isUnderEffect = false;
		hasJetpack = false;
		slowTime = false;
		canGlide = gliding = false;
		charBlurPos = new ArrayList<Integer>();
		jumpHeightFactor = 1;
		healthPos = 0;
		System.out.println("Effect ended");
	}
	
	public void drawEffects(Graphics2D g)
	{
		if (!isBlinking)
		{
			//creates the "blur" effect  - to occur only when time is slowed
			if(slowTime)
			{
				float[] scales = { 1f, 1f, 1f, 1f };
			    float[] offsets = new float[4];
			
			    scales[3] = 0.15f;

				RescaleOp rop = new RescaleOp(scales, offsets, null);
				BufferedImage fadIm = rop.filter(animation.getImage(), null);
				
				for(int i = 0; i < charBlurPos.size(); i+=2)
				{
					if(facingRight)
						g.drawImage(fadIm, (int)(charBlurPos.get(i)), (int)(charBlurPos.get(i+1)), width, height, null);
					else
						g.drawImage(fadIm, (int)(charBlurPos.get(i) + width), (int)(charBlurPos.get(i+1)), -width, height, null);
					
					charBlurPos.set(i, charBlurPos.get(i)-(int)dx);
					charBlurPos.set(i+1, charBlurPos.get(i+1)-(int)dy/2);
				}
				if(charBlurPos.size()>10)
				{
					charBlurPos.remove(0);
					charBlurPos.remove(0);
				}
				
				charBlurPos.add((int)(x));
				charBlurPos.add((int)(y));
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
	
		if(hasBird && !birdActive)
		{
			birdX = (x+(50*Math.cos(Math.toRadians(birdPos))));
			birdY = (y-50-(5*Math.sin(Math.toRadians(birdPos))));
			if((-50*Math.sin(Math.toRadians(birdPos)) > 0))
			{
				g.drawImage(birdAnimation.getImage(), (int)birdX, (int)birdY, 15, 15, null);
			}
			else
			{
				g.drawImage(birdAnimation.getImage(), (int)birdX+15, (int)birdY, -15, 15, null);
			}
			boolean displayMessage = false;
			ArrayList<Enemy> enemies = playState.getEnemies();
			if(enemies != null && enemies.size() > 0)
			{
				for(Enemy e: enemies)
				{
					if(e.getHealth() > 0 && !(e instanceof PlaneBoss))
					{
						displayMessage = true;
					}
				}
			}
			if(displayMessage)
			{
				g.setColor(Color.WHITE);
				String banner = "Press "+ KeyEvent.getKeyText(GameStateManager.action) + " to lauch Clara!";
				int offSet = 0;
				for(int j = 0; j < banner.length(); j++)
				{
					if(!bannerFont.canDisplay(banner.charAt(j)))
						g.setFont(backupFont);
					else
						g.setFont(bannerFont);
	
					g.drawChars(banner.toCharArray(), j, 1, (int)(x + super.width/2 - g.getFontMetrics().getStringBounds(banner, g).getWidth()/2 + offSet), (int)(y + super.height + 80));
					offSet += g.getFontMetrics().charWidth(banner.charAt(j));
				}
			}
		}
		else if(hasBird && birdActive)
		{
		    double slope = (chosenEnemy.getY()-birdY)/(chosenEnemy.getX()-birdX);
		    double angle = Math.atan((double)slope);
		    boolean enemyIsRight = false;
			if(chosenEnemy.getX() < birdX && chosenEnemy.getX() > 0)
			{
				birdX = birdX -(8*Math.cos(angle));
				enemyIsRight = false;
			}
			else if(chosenEnemy.getX() > birdX && chosenEnemy.getX() > 0)
			{
				birdX = birdX + (8*Math.cos(angle));
				enemyIsRight = true;
			}
			
			if(chosenEnemy.getY() < birdY && chosenEnemy.getY() > 0)
			{
				birdY = birdY - Math.abs((8*Math.sin(angle)));
			}
			else if(chosenEnemy.getY() > birdY && chosenEnemy.getY() > 0)
			{
				birdY = birdY + Math.abs((8*Math.sin(angle)));
			}

			if(birdY+10 >= chosenEnemy.getY() && birdY-10 <= chosenEnemy.getY() && birdX+10 >= chosenEnemy.getX() && birdX-10 <= chosenEnemy.getX()) 
			{
				chosenEnemy.playerHurt(50);
				this.explosions = new Explosion(birdX, birdY, 3, tileMap);
				hasBird = false;
				birdActive = false;
			}
			
			if(enemyIsRight)
			{
				g.drawImage(birdAnimation.getImage(), (int)birdX, (int)birdY, 15, 15, null);
			}
			else
			{
				g.drawImage(birdAnimation.getImage(), (int)birdX+15, (int)birdY, -15, 15, null);
			}
			//g.fillRect((int)birdX, (int)birdY, 10, 10);
		}
		
		if(healing)
		{
			float[] scales = { 1f, 1f, 1f, 1f };
		    float[] offsets = new float[4];
		
		    scales[3] = healthAphaVal/255f;

			RescaleOp rop = new RescaleOp(scales, offsets, null);
			BufferedImage fadIm = rop.filter(heartImages.get(2), null);
			
			g.drawImage(fadIm, (int)(x+(50*Math.cos(Math.toRadians(healthPos)))), (int)(y-40-(healthPos/4)), 20, 20, null);

			healthAphaVal-=2;
			if (healthAphaVal <= 0)
			{
				healthAphaVal = 255;
				healing = false;
			}
		}
		
		if (coolDownTime > 0)
		{
			coolDownTime -= GamePanel.getElapsedTime();
		} 
		else if (isUnderEffect)
		{
			resetEffects();
			coolDownTime = 10000000000L;
		}
	}
	
	public void keyPressed(int k)
	{
		if(health > 0)
		{
			if(k == GameStateManager.up)
			{
				if(!jumped)
				{
					idle = false;
					jump = true;
					doubleJumpable = false;
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
			if(k == GameStateManager.glide && canGlide)
			{
				gliding = true;
				idle = false;
			}
			if(k == GameStateManager.action && hasBird)
			{
				ArrayList<Enemy> enemies = playState.getEnemies();
				if(enemies != null && enemies.size() > 0)
				{
					ArrayList<Enemy> possibleEnemies = new ArrayList<Enemy>();
					for(Enemy e: enemies)
					{
						if(e.getHealth() > 0 && !(e instanceof PlaneBoss))
						{
							possibleEnemies.add(e);
						}
					}
					if(!possibleEnemies.isEmpty())
					{
						this.chosenEnemy = possibleEnemies.get((int)(Math.random()*possibleEnemies.size()));
						birdActive = true;
					}
				}
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
			if(k == KeyEvent.VK_T)
			{
				if(tileMap.getShowCollisonBox())
				{
					tileMap.setShowCollisonBox(false);
				}
				else
					tileMap.setShowCollisonBox(true);
			}
		}
	}
	
	public void keyReleased(int k)
	{
		if(k == GameStateManager.up)
		{
			if(jumped)
			{
				jump = false;
				falling = true;
				doubleJumpable = true;
			}
			
			if(doubleJump)
			{
				doubleJump = false;
				jump = false;
				falling = true;
			}
		}
		if(k == GameStateManager.down)
		{
			falling = true;
			drop = false;
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

	public void collided(MapObject m) 
	{

	}
	
	public boolean getRecovering()
	{
		return recovering;
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
	
	public ArrayList<Projectile> getBullets()
	{
		return bullets;
	}

	public double getTotalHeight() 
	{
		return yFromBottom;
	}
	
	public boolean onScreen()
	{
		return (-width <= x && x <= GamePanel.WIDTH+width && -height <= y && y <= GamePanel.HEIGHT+height);
	}
	
	public void setPlayState(PlayState playState)
	{
		this.playState = playState;
	}
}