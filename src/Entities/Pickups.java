package Entities; 

import Main.GamePanel;
import Main.SoundPlayer;
import TileMap.Tile;
import TileMap.TileMap;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Pickups extends MapObject 
{		
	private Player player;
	private TileMap tm;
	
	private final float tileMapWidth;
	
	//sprites loading
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {
			2, 2, 2, 2, 2, 2
	};
	
	//effect types
	public static final int GLIDEBOOST = 0;
	public static final int HEALBOOST = 1;
	public static final int LAUNCHERBOOST = 2;
	public static final int BIRDBOOST = 3;
	public static final int ARMORBOOST = 4;
	public static final int SLOWTIMEBOOST = 5;
	
	private int effectType;
	private int[] pickupsToSpawn;
	private long coolDownTime, coolDown;
	private boolean willDrawPickup, isUnderEffect;
	private double xLoc, yLoc, startingPositionOffset, tmDyPositionOffset, xShift;
	
	public Pickups(Player player, TileMap tileMap, int[] avaliablePickups, long initialDelay, long frequency)
	{
		super (tileMap);
		
		this.player = player;
		this.tm = tileMap;
		pickupsToSpawn = avaliablePickups;
		coolDown = initialDelay;
		coolDownTime = frequency;
		
		tileMapWidth = tileMap.getTileMapWidth()/2;
		
		//set the dimensions of the pickup images
		width = 60;
		height = 80;
		try
		{
			//will have to be fixed to get an image from a large sprites image rather than a single image for each pickup
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Pickups/pickupsSpritesheet.png"));
			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < numFrames.length; i++)
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
		
		init();
	}
	
	public void init()
	{
		//reset values
		willDrawPickup = false;
		isUnderEffect = false;	
		yLoc = -100;
		xLoc = 0;
		tmDyPositionOffset = 0;
		xShift = 0;
	}

	//updates by checking to see if it should spawn a pickup.
	public void update()
	{	
		if	(!willDrawPickup)
		{
			checkPickups();
		}
		else if(willDrawPickup)
		{				
			animation.update();
			if (yLoc < GamePanel.HEIGHT + 50)
			{
				yLoc += 0.5 + ((tm.getDY() - 2)*0.25);
				if (tm.getDY() == 0)
				{
					yLoc += 0.5;
				}
				if (tm.getDY() > 2){
					tmDyPositionOffset += ((tm.getDY() - 2)*0.25);
				}
			}
			else 
			{
				init();
			}
			xLoc = tileMapWidth + (Math.sin((yLoc-startingPositionOffset-tmDyPositionOffset)/100))*200;
			xShift += tm.getDX();
			
			checkCollision();
		}
	}
	
	//check to see if should spawn a pickup
	public void checkPickups()
	{
		if (coolDown > 0)
		{
			coolDown -= GamePanel.getElapsedTime();
		} 
		else if (isUnderEffect)
		{
			resetEffects();
			coolDown = 10000000000L;
			init();
		}
		else 
		{
			if ((int)(Math.random()*0) == 0)//edit probability of spawning here. Currently 100% chance of spawning
			{
				coolDown = coolDownTime;
				willDrawPickup = true;
				//set the pickup type.
				effectType = pickupsToSpawn[(int)(Math.random()*pickupsToSpawn.length)];
				//get the animation of the pickup
				getAnimation();
				//sets starting points for the spawning of the pickups
				startingPositionOffset = -(Math.random()*GamePanel.HEIGHTSCALED/2);
			}
			else
			{
				coolDown = 10000000000L;
			}
		}
	}
	
	//draws the pickup
	public void draw(Graphics2D g)
	{			
		if (willDrawPickup)
		{
			g.drawImage(animation.getImage(), (int)(xLoc+xShift), (int)yLoc, (int)(width), (int)(height), null);
		}
	}
	
	//do the animation for the pickup
	public void getAnimation()
	{
		switch (effectType)
		{
			case 0:
			{
				animation.setFrames(sprites.get(GLIDEBOOST));
				animation.setDelay(200);
				break;
			}
			case 1:
			{
				animation.setFrames(sprites.get(HEALBOOST)); //switch to playerheal when ready
				animation.setDelay(200);
				break;
			}
			case 2:
			{
				animation.setFrames(sprites.get(LAUNCHERBOOST)); //switch to launcherboost
				animation.setDelay(200);
				break;
			}
			case 3: 
			{
				animation.setFrames(sprites.get(BIRDBOOST)); //switch to birdboost
				animation.setDelay(200);
				break;
			}
			case 4:
			{
				animation.setFrames(sprites.get(ARMORBOOST)); //switch to armorboost
				animation.setDelay(200);
				break;
			}
			case 5:
			{			
				animation.setFrames(sprites.get(SLOWTIMEBOOST)); //switch to timeboost
				animation.setDelay(200);
				break;
			}
		}
	}
	
	public void collided(int type, Tile t)
	{
		if(type == 17) willDrawPickup = false;
	}
	
	public void collided(MapObject m) 
	{
		
	}
	
	//checks if the player collided with the pickup
	public void checkCollision()
	{
		if ((player.getX()-(player.getWidth()/2)) < (xLoc+xShift+width) && (xLoc+xShift) < (player.getX()+(player.getWidth()/2)) && (player.getY()-player.getHeight()/2) < (yLoc+height) && (yLoc) < (player.getY()+player.getHeight()/2))
		{
			effectStart();
			willDrawPickup = false;
			isUnderEffect = true;
			coolDown = 10000000000L;
		}
	}
	
	public void effectStart()
	{
		SoundPlayer.playClip("pickup.wav");
		player.effectStart(effectType);
	}
	
	public void resetEffects()
	{
		player.resetEffects();
	}
	
	public long getCoolDown()
	{
		return coolDown;
	}
}
