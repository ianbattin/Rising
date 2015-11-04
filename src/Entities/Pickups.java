package Entities; 

import Main.GamePanel;
import TileMap.TileMap;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Pickups extends MapObject {	
	
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
	
	private int effectType;
	private long coolDownTime;
	private boolean willDrawPickup, isUnderEffect;
	private double xLoc, yLoc, startingPositionOffset, tmDyPositionOffset, xShift;
	
	public Pickups(Player player, TileMap tileMap)
	{
		super (tileMap);
		
		this.player = player;
		this.tm = tileMap;
		
		coolDownTime = 10000000000L;
		
		tileMapWidth = tileMap.getTileMapWidth()/2;
		
		//set the dimensions of the pickup images
		width = 30;
		height = 40;
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
		else
		{
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
			player.resetEffects();
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
				effectType = (int)(Math.random()*1);
				
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
		if(willDrawPickup)
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
						
			getAnimation();
			
			g.drawImage(animation.getImage(), (int)(xLoc+xShift), (int)yLoc, width, height, null);
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
		}
	}
	
	//checks if the player collided with the pickup
	public void checkCollision()
	{
		if ((player.getX()-(player.getWidth()/2)) < (xLoc+xShift)+35 && (xLoc+xShift)-10 < (player.getX()+(player.getWidth()/2)) && (player.getY()-player.getHeight()/2) < yLoc && yLoc < (player.getY()+player.getHeight()/2))
		{
			effectStart();
			willDrawPickup = false;
			isUnderEffect = true;
			coolDownTime = 10000000000L;
		}
	}
	
	public void effectStart()
	{
		int rand = (int)(Math.random()*0);//increase 0 to increase possibilities
		player.effectStart(rand);
	}

}
