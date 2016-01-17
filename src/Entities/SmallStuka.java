package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Main.GamePanel;
import Main.SoundPlayer;
import TileMap.Tile;
import TileMap.TileMap;

public class SmallStuka extends MapObject
{
	private boolean setMovement;
	private boolean moveComplete;
	private int typeAttack;
	
	//animation
	private ArrayList<BufferedImage[]> playerSprites;
	private ArrayList<BufferedImage[]> playerHurtSprites;
	private final int[] numFrames = { 1 };
	
	public SmallStuka(TileMap tm) 
	{
		super(tm);
		
		x = -150;
		y = 400;
		dx = 0;
		dy = 0;
		
		setMovement = false;

		moveSpeed = 1.0;
		maxSpeedY = 3.0;
		maxSpeedX = 6.0;
		
		width = 50;
		height = 20;
		cwidth = 50;
		cheight = 20;

		facingRight = false;
		
		try
		{
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/StukaFarAwayColor.png"));
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
			BufferedImage playerHurtSpritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/StukaFarAwayColor.png"));
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
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = 0;
		animation.setFrames(playerSprites.get(0));
		animation.setDelay(200);
	}

	//Do anything before starting to show the stuka on the screen
	public void init()
	{
		SoundPlayer.playClipWithVolume("stukaDiving.wav", 1);
	}
	
	@Override
	public void collided(int type, Tile t) 
	{
		
	}

	@Override
	public void collided(MapObject m) 
	{
	}

	@Override
	public void update() 
	{
		getMovement();

		//x += tileMap.getDX();
		//y += tileMap.getDY();

		getAnimation();
	}

	@Override
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

	public void getMovement() 
	{
		if(x > GamePanel.WIDTH + 200) facingRight = true;
		else if(x < -200) facingRight = false;
		
		if(!facingRight)
			dx = maxSpeedX;
		//else
			//dx = -maxSpeedX;
		
		dy = -1.5;
		
		x += dx;
		y += dy;
	}
	
	public void getAnimation() 
	{
		if (recovering) animation.changeFrames(playerHurtSprites.get(currentAction));
		else animation.changeFrames(playerSprites.get(currentAction));
		
		animation.update();	
	}
}
