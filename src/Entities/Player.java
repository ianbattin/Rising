package Entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import GameState.GameStateManager;
import GameState.Level1State;
import GameState.PlayState;
import Main.GamePanel;
import Main.Main;
import Main.SoundPlayer;
import TileMap.Tile;
import TileMap.TileMap;


public class Player extends MapObject
{	
	public boolean ending = false;
	
	//TileMap
	private PlayState playState;
	
	//Attacks
	private ArrayList<Projectile> bullets;
	private long fireTimer;
	private boolean firing;
	private boolean mouseHeld;
	private double angle;
	private boolean canDoubleJump;
	
	//effect variables
	private boolean isUnderEffect;
	private double jumpHeightFactor, birdPos, healthPos;
	private boolean hasBird, birdActive, hasArmor, slowTime, canGlide, hasGun;
	private int ammoCount;
	private double birdX, birdY;
	private Explosion explosions;
	private Enemy chosenEnemy;
	private ArrayList<Integer> charBlurPos;
	private long coolDownTime;
	private Font backupFont, bannerFont;
	private BufferedImage gunImage;
	private boolean displayMessage;
	private String banner;
	
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
	private boolean hasFlashed, isBlinking, isFlashing, wasFlashing, healing;
	private ArrayList<BufferedImage> heartImages;
	
	//animation
	private ArrayList<BufferedImage[]> playerSprites;
	private ArrayList<BufferedImage[]> playerHurtSprites;
	private BufferedImage[] birdPickupSprites;
	private Animation birdAnimation;
	private final int[] numFrames = { 4, 8, 3, 3, 3, 4, 7, 4 };
	private final int[] numShootingFrames = { 4, 4, 4, 4, 4 };
	//private final int jumpDelay = 200;
	private boolean introFrames, showLastIntroFrame;
	private Color bannerColor;

	private boolean tileMapMoving;
	private boolean canMove;

	private boolean fired;

	private boolean midAir;

	private long timer;
	
	//animation actions
	public static final int IDLE = 0;
	public static final int WALKING = 1;
	public static final int JUMPING = 2;
	public static final int FALLING = 3;
	public static final int LANDING = 4;
	public static final int LOOKING_UP = 5;
	public static final int DOUBLEJUMP = 6;
	public static final int RUNNING = 7;
	public static final int SHOOTING = 8;
	public static final int SHOOTING_FALLING = 9;
	public static final int SHOOTING_WALKING = 12;
	public static final int SHOOTING_WALKING_BKWDS = 9;
	public static final int SHOOTING_FALLING_BKWDS = 10;

	
	public Player(TileMap tm, PlayState playState)
	{	
		super(tm);
		this.playState = playState;
		
		bullets = new ArrayList<Projectile>();
		angle = 0.0;
		firing = false;
		fireTimer = System.currentTimeMillis();
		
		recoverLength = 3000;
		coolDownTime = 0;
		
		moveSpeedLeft = 0.3;
		moveSpeedRight = 0.3;
		maxSpeed = 5.0;
		maxSpeedLeft = 5.0;
		maxSpeedRight = 5.0;
		stopSpeed = 0.4;
		fallSpeed = 0.50;
		maxFallSpeed = 7.0;
		jumpStart = -8.0;
		
		width = 50;
		height = 70;
		cwidth = 25;
		cheight = 70;

		facingRight = true;
		bannerColor = Color.WHITE;
		
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
			//make the sprites for the player when he shoots the gun
			for(int i = numFrames.length; i < numShootingFrames.length + numFrames.length; i++)
			{
				BufferedImage[] bi = new BufferedImage[numShootingFrames[i - numFrames.length]];
				for(int j = 0; j < numShootingFrames[i - numFrames.length]; j++)
				{
					bi[j] = spritesheet.getSubimage(j * 77, i * 70, 77, 70);
				}
				playerSprites.add(bi);
			}
			
			//add player colors to "background" colors for collision
			for(int x = 0; x < playerSprites.get(0)[0].getWidth(); x++)
			{
				for(int y = 0; y < playerSprites.get(0)[0].getHeight(); y++)
				{
					int color = playerSprites.get(0)[0].getRGB(x, y);
					if(!bgColors.contains(color))
						bgColors.add(color);
				}
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
				playerHurtSprites.add(bi);
			}
			//make the sprites for the player when he shoots the gun and takes damage
			for(int i = numFrames.length; i < numShootingFrames.length + numFrames.length; i++)
			{
				BufferedImage[] bi = new BufferedImage[numShootingFrames[i - numFrames.length]];
				for(int j = 0; j < numShootingFrames[i - numFrames.length]; j++)
				{
					bi[j] = playerHurtSpritesheet.getSubimage(j * 77, i * 70, 77, 70);
				}
				playerHurtSprites.add(bi);
			}
			
			
			//make the sprites for the flying bird
			BufferedImage birdPickupSpritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Tiles/birdSprite.png"));
			birdPickupSprites = new BufferedImage[2];
			for(int i = 0; i < 2; i++)
			{
				birdPickupSprites[i] = (birdPickupSpritesheet.getSubimage(i * 15, 0, 15, 15));
			}
			
			//get sprites for heart. This will be improved if we make the two hearts be on the same spritesheet image
			heartImages = new ArrayList<BufferedImage>();
			heartImages.add(ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/fullHeart2.png")));
			heartImages.add(ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/emptyHeart2.png")));
			heartImages.add(ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/fullHeart2.png")));
			heartImages.add(ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/emptyHeart2.png")));
			heartImages.add(ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/armorHeart.png")));
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
			gunImage = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/gunIcon.png"));
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
		introFrames = showLastIntroFrame = false;
		
		falling = true;
		midAir = true;
		canDoubleJump = false;
		canMove = true;
		
		//health
		health = 5;
		healthAphaVal = 255;
		numOfFramesToAnimHealth = 0;
		healing = wasFlashing = isFlashing = isBlinking = hasFlashed = false;
		
		//effects 
		banner = null;
		displayMessage = false;
		hasBird = false; 
		hasArmor = false;
		jumpHeightFactor = 1;
		canGlide = slowTime = isUnderEffect = hasGun = birdActive = hasBird = false;
		birdX = 0;
		birdY = 0;
		birdPos = 0;
		ammoCount = 0;
		hasGun = false;
		
		armorBoostHealth = 0;
		greenInc = blueInc = redInc = 1;
		redChg = blueChg = greenChg = false;
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
			try
			{
				if(onScreen() && (y+height < GamePanel.HEIGHT - 10)) checkPixelColorCollision(tileMap);
				else myCheckCollision();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				myCheckCollision();
			}
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
			if(bullets.get(i).notOnScreen() && bullets.get(i).getType() != 7) bullets.remove(i);
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
			g.drawOval((int)(x + width/2), (int)(y + cheight), 5, 5);
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
		
		if (hasGun)
		{
			g.setFont(bannerFont);
			g.setColor(Color.darkGray);
			if(hasGun && ammoCount <= 0)
			{
				g.setFont(new Font("Munro", Font.BOLD, 30));
				g.setColor(new Color(230, 120, 120));
			}
			g.drawImage(gunImage, null, GamePanel.WIDTH-10-gunImage.getWidth(), 10);
			g.drawString(Integer.toString(ammoCount), GamePanel.WIDTH-gunImage.getWidth()-20 , 40);
		}
		setMapPosition();
		drawEffects(g);

		for(int i = 0; i < bullets.size(); i++)
		{
			bullets.get(i).draw(g);
		}	
	}
	
	public void collided(int type, Tile t)
	{
		midAir = false;
		timer = 0;
		if(type == 17) 
		{
			playerHurt(1);
		}
		else if(type == 20)
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
		else if(type == 478)
		{
			healing = true;
			playerHeal(1);
			t.setType(0);
			SoundPlayer.playClip("pickup.wav");
		}
		else if(type == 479)
		{
			if(!hasGun) 
			{
				this.setPlayerBannerText("Move crosshair with mouse and click to fire!");
				new Timer().schedule(new TimerTask()
				{
					public void run()
					{	
						hidePlayerBanner();
					}
				}, 6000);
				hasGun = true;
			}
			ammoCount += 6;
			t.setType(0);
		}
		/*else if(type == 358 || type == 359 || type == 388 || type == 389 || type == 418 || type == 419)
		{
			this.dy = -35;
			this.jump = true;
			if (System.currentTimeMillis() - launchTimer > 500)
			{
				ArrayList<Tile> tiles = tileMap.getTiles();
				for (int i = 0; i < tiles.size(); i++)
				{
					if (tiles.get(i).getType() == 358)
					{
						this.explosions = new Explosion(tiles.get(i).getX(), tiles.get(i).getY()-40, 3, tileMap);
						launchTimer = System.currentTimeMillis();
						break;
					}
				}
			}
		}*/
	}
	
	public void getAttack()
	{		
		if(!mouseHeld) firing = false;
		final int mouseX = (int) (((MouseInfo.getPointerInfo().getLocation().getX() - Main.window.getLocation().getX()) - 3)/GamePanel.scaleWidth) - Projectile.PROJECTILE_1_SIZE/2;
		final int mouseY = (int) (((MouseInfo.getPointerInfo().getLocation().getY() - Main.window.getLocation().getY()) - 25)/GamePanel.scaleHeight) - Projectile.PROJECTILE_1_SIZE/2;
	    
		if(firing && hasGun)
		{
			firing = false;
			new Timer().schedule(new TimerTask()
			{
				public void run()
				{	
					bullets.add(new Projectile(mouseX, mouseY, 0, 1, tileMap));
					ammoCount--;
					//if(ammoCount <= 0) hasGun = false;
				}
				
			}, 150);
			new Timer().schedule(new TimerTask()
			{
				public void run()
				{	
					fired = false;
				}
				
			}, 600);
			/*
			long elapsed = (System.nanoTime() - fireTimer) / 1000000;
			if(fireDelay <= elapsed)
			{
				bullets.add(new Projectile(x + width/2, y + height/2, angle, 1, tileMap));
				ammoCount--;
				if(ammoCount <= 0) hasGun = false;
				fireTimer = System.nanoTime();
			}
			*/
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
			if(!jump && !falling && !doubleJump) idle = true;
		}

		//JUMPING AND FALLING
		if(jump && !midAir)
		{
			/*if(!jumping && !jumped)
			{
				jumping = true;
				new Timer().schedule(new TimerTask()
				{
					public void run()
					{	
						y-=5;
						falling = false;
						jumpHeight = yFromBottom + (100*jumpHeightFactor);
						if(!jumped) SoundPlayer.playClip("jump.wav");
						jumped = true;
						jumping = false;
					}
					
				}, jumpDelay);
			}
			if(jumped)
			{
				if(yFromBottom < jumpHeight) dy = jumpStart*jumpHeightFactor;
				if(yFromBottom >= jumpHeight) 
				{
					jumpHeight = -9000; //arbitrary number, just has to be way below the player so they are always above jumpHeight at this point
					falling = true;
				}
			}*/
			if(!jumped)
			{
				y-=5;
				falling = false;
				jumpHeight = yFromBottom + (100*jumpHeightFactor);
				if(!jumped) SoundPlayer.playClip("jump.wav");
				jumped = true;
				//jumping = false;
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
	
		if(doubleJump)
		{
			if(!doubleJumped)
			{
				jumpHeight = yFromBottom + (50.0*jumpHeightFactor);
				doubleJumped = true;
				SoundPlayer.playClip("doublejump.wav");
			}
			if(doubleJumped)
			{
				if(yFromBottom < jumpHeight) dy = jumpStart*jumpHeightFactor;
				if(yFromBottom >= jumpHeight) 
				{
					jumpHeight = -9000; //arbitrary number, just has to be way below the player so they are always above jumpHeight at this point
					falling = true;
				}
			}
		}
		
		if(falling)
		{		
			long elapsed = (System.nanoTime() - timer) / 1000000;
			if(100 <= elapsed)
			{
				midAir = true;
				timer = System.nanoTime();
				
				if(!fallingAnim && dy > 3) fallingAnim = true;
				
				jump = false;
				doubleJump = false;
			}
			
			if(dy > 0.0 && gliding)
			{
				dy = 1;
			}
			else if(dy < maxFallSpeed) dy += fallSpeed;
			else dy = maxFallSpeed;
		}
		else
		{
			fallingAnim = false;
			midAir = false;
			timer = System.nanoTime();
		}
		
		moveTileMapX();
		if(tileMapMoving)
		{
			if(y < 300) 
			{
				if(Level1State.tileStart && tileMap.getMoving())
				{

					if (dy >= 0) tileMap.setYVector(Level1State.SCROLLSPEED);
					else tileMap.setYVector(-dy + Level1State.SCROLLSPEED);
					if(dy > 0) y += (dy);
				}
				else
				{
					y += dy;
				}
			}
			else 
			{	
				if(slowTime) tileMap.setYVector(Level1State.SCROLLSPEED/4);
				else if(Level1State.tileStart) tileMap.setYVector(Level1State.SCROLLSPEED);
				y += dy;
			}
		}
		else
			y += dy;
	}
	
	public void getAnimation()
	{		
				
		if(right) facingRight = true;
		else if(left) facingRight = false;

		if(!fired)
		{		
			if(idle)
			{
				if (introFrames)
				{
					if (currentAction != LOOKING_UP || showLastIntroFrame)
					{
						currentAction = LOOKING_UP;
						if (showLastIntroFrame) 
						{
							animation.setFrames(Arrays.copyOfRange(playerSprites.get(LOOKING_UP), 3, 4));
						}
						else 
						{
							animation.setFrames(Arrays.copyOfRange(playerSprites.get(LOOKING_UP), 0, 3));
						}
						animation.setDelay(200);
						animation.setDone(false);
						width = 50;
						height = 70;
					} 
				} 
				else if(currentAction != IDLE && (!left || !right))
				{
					currentAction = IDLE;
					animation.setFrames(playerSprites.get(IDLE));
					animation.setDelay(200);
					animation.setDone(false);
					width = 50;
					height = 70;
				}
			}
			
			if(left || right)
			{
				if(!ending)
				{
					if(currentAction != WALKING  && !(fallingAnim || midAir) && currentAction != JUMPING && currentAction != DOUBLEJUMP)
					{
						currentAction = WALKING;
						animation.setFrames(playerSprites.get(WALKING));
						width = 50;
						height = 70;
						animation.setDone(false);
						animation.setDelay(150);
					}
				}
				else
				{
					if(currentAction != RUNNING  && !(fallingAnim || midAir) && currentAction != JUMPING && currentAction != DOUBLEJUMP)
					{
						currentAction = RUNNING;
						animation.setFrames(playerSprites.get(RUNNING));
						width = 50;
						height = 70;
						animation.setDone(false);
						animation.setDelay(150);
					}
				}
			}
			if(jump)
			{
				if(currentAction != JUMPING)
				{
					currentAction = JUMPING;
					animation.setFrames(playerSprites.get(JUMPING));
					animation.setDelay(100);
					animation.setDone(true);
					width = 50;
					height = 70;
				}
			}
			if(gliding)
			{
				if(currentAction != DOUBLEJUMP)
				{
					currentAction = DOUBLEJUMP;
					animation.setFrames(playerSprites.get(DOUBLEJUMP));
					animation.setDelay(200);
					animation.setDone(true);
					width = 50;
					height = 70;
				}
			}
			if(fallingAnim && midAir)
			{
				if(currentAction != FALLING)
				{
					currentAction = FALLING;
					animation.setFrames(playerSprites.get(FALLING));
					width = 50;
					height = 70;
					animation.setDelay(200);
				}
			}
			if(doubleJump)
			{
				if(currentAction != DOUBLEJUMP)
				{
					currentAction = DOUBLEJUMP;
					animation.setFrames(playerSprites.get(DOUBLEJUMP));
					animation.setDelay(100);
					animation.setDone(true);
					width = 50;
					height = 70;
				}
			}
		}
		else
		{
			if ((currentAction == IDLE) && currentAction != SHOOTING)
			{
				currentAction = SHOOTING;
				animation.setFrames(playerSprites.get(SHOOTING));
				width = 77;
				height = 70;
				animation.setDone(true);
				animation.setDelay(150);
			}
			else if ((currentAction == WALKING) && currentAction != SHOOTING_WALKING )
			{
				currentAction = SHOOTING_WALKING;
				animation.setFrames(playerSprites.get(SHOOTING_WALKING));
				width = 77;
				height = 70;
				animation.setDone(true);
				animation.setDelay(150);
			}
			else if ((currentAction == FALLING || currentAction == JUMPING) && currentAction != SHOOTING_FALLING )
			{
				currentAction = SHOOTING_FALLING;
				animation.setFrames(playerSprites.get(SHOOTING_FALLING));
				width = 77;
				height = 70;
				animation.setDone(true);
				animation.setDelay(150);
			}
			
		}
		
		if (isFlashing)	
		{
			animation.changeFrames(playerHurtSprites.get(currentAction));
			fired = false;
			wasFlashing = true;
		}
		else if (wasFlashing)
		{
			animation.changeFrames(playerSprites.get(currentAction));
			fired = false;
			wasFlashing = false;
		}
		
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
			case Pickups.GLIDEBOOST: 
			{
				canGlide = true;
				jumpHeightFactor = 1.5;
				break;
			}
			case Pickups.HEALBOOST:
			{
				playerHeal(1);
				break;
			}
			case Pickups.AMMOBOOST:
			{
				this.ammoCount += 6;
				this.hasGun = true;
				break;
			}
			case Pickups.BIRDBOOST:
			{
				hasBird = true;
				break;
			}
			case Pickups.ARMORBOOST:
			{
				hasArmor = true;
				armorBoostHealth = 5;
				break;
			}
			case Pickups.SLOWTIMEBOOST:
			{
				slowTime = true;
				playState.slowTimeStart();
				jumpHeightFactor = 1.2;
				break;
			}
		}
		coolDownTime = 10000000000L;
		isUnderEffect = true;
	}
	
	//resets the effects
	public void resetEffects()
	{
		playState.slowTimeEnd();
		isUnderEffect = false;
		slowTime = false;
		canGlide = gliding = false;
		charBlurPos = new ArrayList<Integer>();
		jumpHeightFactor = 1;
		healthPos = 0;
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
				
				playState.getBackground().addCustomColors(fadIm.getRGB(0, 0, fadIm.getWidth(), fadIm.getHeight(), null, 0, fadIm.getWidth()));
				
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
				g.drawImage(animation.getImage(), (int)(x + xmap) + width - width%50, (int)(y + ymap), -width, height, null);
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
			boolean willDisplay = false;
			ArrayList<Enemy> enemies = playState.getEnemies();
			if(enemies != null && enemies.size() > 0)
			{
				for(Enemy e: enemies)
				{
					if(e.getHealth() > 0 && !(e instanceof PlaneBoss) && !e.notOnScreen())
					{
						willDisplay = true;
					}
				}
			}
			if (willDisplay)
			{
				this.setPlayerBannerText("Press "+ KeyEvent.getKeyText(GameStateManager.action) + " to lauch Clara!");
			}
			else
			{
				this.hidePlayerBanner();
			}
		}
		else if(hasBird && birdActive)
		{
			this.hidePlayerBanner();
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
				chosenEnemy.playerHurt(50, true);
				this.explosions = new Explosion(birdX, birdY, Explosion.NORMAL_EXPLOSION_BIRD, 0, tileMap);
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
				resetEffects();
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
		
		if(displayMessage && banner != null)
		{
			g.setColor(bannerColor);
			int offSet = 0;
			for(int j = 0; j < banner.length(); j++)
			{
				if(!bannerFont.canDisplay(banner.charAt(j)))
					g.setFont(backupFont);
				else
					g.setFont(bannerFont);

				g.drawChars(banner.toCharArray(), j, 1, (int)(x + super.width/2 - g.getFontMetrics().getStringBounds(banner, g).getWidth()/2 + offSet), (int)(y + super.height - 80));
				offSet += g.getFontMetrics().charWidth(banner.charAt(j));
			}
		}
	}
	
	public void setSlowTime(boolean b)
	{
		slowTime = b;
		playState.slowTimeStart();
	}
	
	public void keyPressed(int k)
	{
		if(health > 0 && canMove)
		{
			if(k == GameStateManager.up)
			{
				fallSpeed = 0.25;
				if(!jumped)
				{
					idle = false;
					jump = true;
				}
				else if(jumped && !doubleJump && doubleJumpable && canDoubleJump)
				{
					falling = false;
					jump = false;
					doubleJump = true;
					doubleJumpable = false;
					idle = false;
				}
				else
				{
					doubleJump = false;
				}
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
						if(e.getHealth() > 0 && !(e instanceof PlaneBoss) && !e.notOnScreen())
						{
							possibleEnemies.add(e);
						}
					}
					if(!possibleEnemies.isEmpty())
					{
						double dist = Double.MAX_VALUE;
						Enemy chosen = null;
						for(Enemy e : possibleEnemies)
						{
							double temp = Math.hypot(e.getX() - this.getX(), e.getY() - this.getY()); 
							if(temp < dist)
							{
								dist = temp;
								chosen = e;
							}
						}
						this.chosenEnemy = chosen;
						birdActive = true;
					}
				}
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
			fallSpeed = 0.50;
			if(jumped)
			{
				jump = false;
				falling = true;
				doubleJumpable = true;
			}
			if(doubleJumped)
			{
				doubleJumpable = false;
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
	}

	public void collided(MapObject m) 
	{
		if(m instanceof Item)
		{
			if(m.getType() == 0 && m.getDY() > 0)
			{
				ending = true;
			}
		}
	}
	
	public boolean getRecovering()
	{
		return recovering;
	}

	public void setFiring(boolean b) 
	{
		if (b && System.currentTimeMillis() - this.fireTimer >= 800 && this.ammoCount > 0)
		{
			firing = true;
			fired = true;
			this.fireTimer = System.currentTimeMillis();
		}
		else
		{
			firing = false;
		}
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
		return (0 < x+cwidth && x+cwidth < GamePanel.WIDTH && 100 < y+cheight && y+cheight < GamePanel.HEIGHT-1);
	}
	
	public void setPlayState(PlayState playState)
	{
		this.playState = playState;
	}
	
	/**
	 * Determines whether the intro frames are going to be displayed
	 * 
	 * @param b - boolean that indicates whether the intro animations should be played
	 * @param d - boolean that indicates whether only the last of the intro frames should be played
	 */
	public void doIntroFrame(boolean b, boolean d)
	{
		introFrames = b;
		showLastIntroFrame = d;
	}
	
	/**
	 * Set whether the player can double jump or not
	 * @param state: boolean value indicating whether player can (or can't) double jump
	 */
	public void setDoubleJump(boolean state)
	{
		this.canDoubleJump = state;
	}
	
	/**
	 * Sets the text in the message below the player
	 * Also sets makes the text visible. Use hidePlayerBanner() to stop displaying it
	 */
	public void setPlayerBannerText(String text)
	{
		this.banner = text;
		this.displayMessage = true;
	}
	
	public void setBannerColor(Color newColor)
	{
		this.bannerColor = newColor;
	}
	
	public void resetDoubleJump()
	{
		jump = false;
		falling = true;
		doubleJumped = false;
		doubleJumpable = true;
		doubleJump = false;
		jumped = true;
	}
	
	public void hidePlayerBanner()
	{
		this.displayMessage = false;
	}
	
	public void setDY(double dy)
	{
		this.dy = dy;
	}
	
	public void setFired(boolean b)
	{
		fired = b;
	}
	
	public boolean hasGun(){ return hasGun; }
	public void setFacingRight(boolean b) { facingRight = b; }
	

}