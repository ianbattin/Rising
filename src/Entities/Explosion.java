package Entities;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Main.GamePanel;
import Main.SoundPlayer;
import TileMap.Tile;
import TileMap.TileMap;

public class Explosion extends MapObject
{
	private double direction;
	private int damage;
	private int type;
	
	private boolean remove;
	private boolean playerCollide;
	private boolean willDestroyBlocks;
	
	private long lifeTime;
	
	//timeslow variable
	private static double slowTime;
	
	//Animation
	private ArrayList<BufferedImage[]> sprites, bombExplosionSprites;
	private final int[] numFrames = { 4 };
	private final int[] bombExplosionNumFrames = { 3 };
	
	//private AffineTransform transform;
	private AffineTransformOp op;
	private double rotation;
	
	private int[][] explosionArea = new int[10][10];
	
	public Explosion(double x, double y, int type, TileMap tm) 
	{
		super(tm);
		
		this.x = x;
		this.y = y;
		this.type = type;
		
		slowTime = 1;
		lifeTime = 0;
		remove = false;
		
		switch(type)
		{
			case 1:
			{
				width = 50;
				height = 50;
				cwidth = width*2;
				cheight = height*2;
				willDestroyBlocks  = true;
				init();
				break;
			}
			case 2:
			{
				width = 50;
				height = 50;
				cwidth = height*4;
				cheight = height*4;
				willDestroyBlocks = true;
				init();
				break;
			}
			case 3:
			{
				//same as case 1, except it wont destroy tiles
				width = 50;
				height = 50;
				cwidth = width*2;
				cheight = height*2;
				willDestroyBlocks = false;
				init();
				break;
			}
			case 4: //used in the boss fight to destroy a minimal amount of tiles and spawn several fires
			{
				width = 50;
				height = 50;
				cwidth = width*2;
				cheight = height*1;
				willDestroyBlocks= true;
				init();
				break;
			}
		}
		
		try
		{
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/FX/explosion2.png"));
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
			
			BufferedImage bombExplosionSpriteSheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/FX/bombexplosion.png"));
			bombExplosionSprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < bombExplosionNumFrames.length; i++)
			{
				BufferedImage[] bi = new BufferedImage[bombExplosionNumFrames[i]];
				for(int j = 0; j < bombExplosionNumFrames[i]; j++)
				{
					bi[j] = bombExplosionSpriteSheet.getSubimage(j * 95, i * 90, 95, 90);
				}
				bombExplosionSprites.add(bi);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		animation = new Animation();
				
		yFromBottom =  GamePanel.HEIGHTSCALED - y;
		
		currentAction = 0;
		if (type == 1 || type == 3)
		{
			animation.setFrames(sprites.get(0));
			animation.setDelay(50);
		} 
		else if(type == 2 || type == 4)
		{
			animation.setFrames(bombExplosionSprites.get(0));
			animation.setDelay(75);
		}
		animation.setDone(true);
		
		//transform = new AffineTransform();
		rotation = Math.random()*360;
		op = new AffineTransformOp(AffineTransform.getRotateInstance(rotation, width/2, height/2), AffineTransformOp.TYPE_BILINEAR);
	}

	public void init()
	{
		for(int i = 0; i < explosionArea.length; i++)
		{
			for(int j = 0; j < explosionArea[i].length; j++)
			{
				explosionArea[i][j] = (int)(Math.random()*2);
			}
		}
		
		SoundPlayer.playClip("bombexplosion.wav");
	}
	
	@Override
	public void update() 
	{
		if(!remove)
		{
			this.myCheckCollision();
			getAnimation();

			x += dx;
			y += dy;
			x += tileMap.getDX();
			y += tileMap.getDY();
			
			if(willDestroyBlocks)
			{
				for(Tile tile: tileMap.getTiles())
				{
					if(this.intersects(tile))
					{
						if (type != 4)
						{
							tile.setType(0);
						}
						else
						{
							boolean flag = false;
							for(Tile tileBelow : tileMap.getTiles())
							{
								if((int)tileBelow.getX() == (int)tile.getX() && (int)tileBelow.getY() - super.tileMap.getTileSize() == (int)tile.getY() && !this.intersects(tileBelow))
								{
									tile.setType(17, 3, true);
									flag = true;
									break;
								}
							}
							if (!flag)
							{
								tile.setType(0);
							}	
						}
					}
				}
			}
		}
		else
			type = 0;
	}

	@Override
	public void draw(Graphics2D g) 
	{
		if(!remove)
		{
			if(type == 1 || type == 3)
			{
				// Drawing the rotated image at the required drawing locations
				g.drawImage(op.filter(animation.getImage(), null), (int)(x + xmap), (int)(y + ymap), null);

				if(tileMap.getShowCollisonBox())
				{
					g.setColor(Color.RED);
					g.draw(this.getRectangle());
				}
			}
			else if(type == 2 || type == 4)
			{
				g.drawImage(animation.getImage(), (int)x, (int)y - 10, animation.getImage().getWidth(), animation.getImage().getHeight(), null);
				/*
				for(int i = 0; i < explosionArea.length; i++)
				{
					for(int j = 0; j < explosionArea[i].length; j++)
					{
						if(explosionArea[i][j] == 1)
						{
							g.drawImage(op.filter(animation.getImage(), null), (int)(x - cwidth/2 + xmap + i*20), (int)(y - cheight/2 + ymap + j*20), null);
							rotation = Math.random()*360;
							op = new AffineTransformOp(AffineTransform.getRotateInstance(rotation, width/2, height/2), AffineTransformOp.TYPE_BILINEAR);
						}
					}
				}*/

				if(tileMap.getShowCollisonBox())
				{
					g.setColor(Color.RED);
					g.draw(this.getRectangle());
				}
			}
		}
	}
	private void getAnimation() 
	{
		if(animation.hasPlayedOnce())
		{
			remove = true;
		}
		else
		{
			animation.update();
		}
		/*
		if(animation.getFrame() == 3)
		{
			animation.update();
			remove = true;
		}
		else
			animation.update();
			*/
	}
	
	public int getType()
	{
		return type;
	}
	
	public boolean getRemove()
	{
		return remove;
	}
	
	@Override
	public void collided(int type, Tile t) 
	{
		
	}

	@Override
	public void collided(MapObject m) 
	{
		m.playerHurt(20);
	}
}
