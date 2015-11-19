package Entities; 

import Main.GamePanel;
import TileMap.Tile;
import TileMap.TileMap;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import GameState.PlayState;

public class Pickups extends MapObject {	
	
	private PlayState playState;
	private Player player;
	private TileMap tm;
	
	private final float tileMapWidth;
	
	//sprites loading
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {
			1
	};
	
	//animation types
	private static final int JUMPBOOST = 0;
	private static final int PLAYERHEAL = 1;
	private static final int LAUNCHERBOOST = 2;
	private static final int BIRDBOOST = 3;
	private static final int ARMORBOOST = 4;
	private static final int TIMEBOOST = 5;
	
	
	private int effectType;
	private long coolDownTime;
	private boolean willDrawPickup, isUnderEffect;
	private double xLoc, yLoc, startingPositionOffset, tmDyPositionOffset, xShift;
	
	public Pickups(Player player, TileMap tileMap, PlayState playState)
	{
		super (tileMap);
		
		this.playState = playState;
		this.player = player;
		this.tm = tileMap;
		
		coolDownTime = 10000000000L;
		
		tileMapWidth = tileMap.getTileMapWidth()/2;
		
		//set the dimensions of the pickup images
		width = 60;
		height = 80;
		try
		{
			//will have to be fixed to get an image from a large sprites image rather than a single image for each pickup
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Tiles/pickups.png"));
			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < 1; i++)
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
			if (yLoc < GamePanel.HEIGHT + 50)
			{
				yLoc += 0.5 + ((tm.getDY() - 2)*0.25);
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
		if (coolDownTime > 0)
		{
			coolDownTime -= GamePanel.getElapsedTime();
		} 
		else if (isUnderEffect)
		{
			resetEffects();
			coolDownTime = 100000000000L;
			init();
		}
		else 
		{
			if ((int)(Math.random()*0) == 0)//edit probability of spawning here
			{
				coolDownTime = 100000000000L;
				willDrawPickup = true;
				//set the pickup type.
				effectType = 3;//(int)(Math.random()*6);
				
				//sets starting points for the spawning of the pickups
				startingPositionOffset = -(Math.random()*GamePanel.HEIGHTSCALED/2);
			}
			else
			{
				coolDownTime = 10000000000L;
			}
		}
	}
	
	//draws the pickup
	public void draw(Graphics2D g)
	{			
		if (willDrawPickup)
		{
			getAnimation();
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
				animation.setFrames(sprites.get(JUMPBOOST));
				animation.setDelay(200);
				break;
			}
			case 1:
			{
				animation.setFrames(sprites.get(JUMPBOOST)); // switch to playerheal when ready
				animation.setDelay(200);
				break;
			}
			case 2:
			{
				animation.setFrames(sprites.get(JUMPBOOST)); //switch to launcherboost
				animation.setDelay(200);
				break;
			}
			case 3: 
			{
				animation.setFrames(sprites.get(JUMPBOOST)); //switch to birdboost
				animation.setDelay(200);
				break;
			}
			case 4:
			{
				animation.setFrames(sprites.get(JUMPBOOST)); //switch to armorboost
				animation.setDelay(200);
				break;
			}
			case 5:
			{			
				animation.setFrames(sprites.get(JUMPBOOST)); //switch to timeboost
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
			coolDownTime = 10000000000L;
		}
	}
	
	public void effectStart()
	{
		if(effectType == 3)
		{
			
		}
		else if(effectType == 5) 
		{
			playState.setBackgroundVector(0, -1);
			playState.setDebrisVectors(0.5);
			playState.setEntitiySpeed(0.2f);
		}
		player.effectStart(effectType);
	}
	
	public void resetEffects()
	{
		if(effectType == 5)
		{
			playState.setBackgroundVector(0, -5);
			playState.setDebrisVectors(1);
			playState.setEntitiySpeed(1);
		}
		player.resetEffects();
	}
}
