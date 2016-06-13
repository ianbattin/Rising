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
	public static final int AMMOBOOST = 2;
	public static final int BIRDBOOST = 3;
	public static final int ARMORBOOST = 4;
	public static final int SLOWTIMEBOOST = 5;
	
	private int effectType;
	private int[] pickupsToSpawn;
	private long coolDownTime, coolDown;
	private boolean willDrawPickup, isUnderEffect;
	private double startingPositionOffset, tmDyPositionOffset, xShift, initialXShift;
	private float windX;
	private boolean willWeave;
	
	
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
		
		cwidth = 60;
		cheight = 80;
		
		windX = 0;
		initialXShift = 0;
		
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
		
		willWeave = true;
		init();
	}
	
	public void init()
	{
		//reset values
		willDrawPickup = false;
		isUnderEffect = false;	
		y = -100;
		x = 0;
		tmDyPositionOffset = 0;
		xShift = initialXShift;
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
			if (y < GamePanel.HEIGHT + 50)
			{
				y += 0.5 + ((tm.getDY() - 2)*0.25);
				if (tm.getDY() == 0)
				{
					y += 0.5;
				}
				if (tm.getDY() > 2){
					tmDyPositionOffset += ((tm.getDY() - 2)*0.25);
				}
			}
			else 
			{
				init();
			}
			if(willWeave)
				x = tileMapWidth + (Math.sin((y-startingPositionOffset-tmDyPositionOffset)/100))*200;
			xShift += tm.getDX() + windX;

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
			g.drawImage(animation.getImage(), (int)(x+xShift), (int)y, (int)(width), (int)(height), null);
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
				animation.setFrames(sprites.get(AMMOBOOST)); //switch to ammoboost
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
		if ((player.getX()-(player.getWidth()/2)) < (x+xShift+width) && (x+xShift) < (player.getX()+(player.getWidth()/2)) && (player.getY()-player.getHeight()/2) < (y+height) && (y) < (player.getY()+player.getHeight()/2))
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
	
	public void setWind(float windX, double xShift, boolean willWeave)
	{
		this.windX = windX; 
		this.initialXShift = xShift;
		if(!willDrawPickup)
		{
			this.xShift = xShift;
		}
		this.willWeave = willWeave;
	}
	
	/**
	 * Spawns indicated pickup. Will only spawn, if there currently isn't a pickup.
	 * @param type Integer representing the pickup type to spawn.
	 */
	public void spawnPickup(int type)
	{
		if(!willDrawPickup)
		{
			coolDown = coolDownTime;
			willDrawPickup = true;
			//set the pickup type.
			effectType = type;
			//get the animation of the pickup
			getAnimation();
			//sets starting points for the spawning of the pickups
			startingPositionOffset = -(Math.random()*GamePanel.HEIGHTSCALED/2);
		}
	}
}
