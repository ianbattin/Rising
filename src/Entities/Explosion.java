package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import GameState.PlayState;
import Main.GamePanel;
import Main.SoundPlayer;
import TileMap.Tile;
import TileMap.TileMap;

public class Explosion extends MapObject
{
	public static final int NORMAL_EXPLOSION = 1;
	public static final int BOMB_EXPLOSION = 2;
	public static final int NORMAL_EXPLOSION_NO_TILE_DAMAGE = 3;
	public static final int BOMB_EXPLOSION_BOSS_FIGHT = 4;
	public static final int NORMAL_EXPLOSION_BIRD = 5;
	public static final int NORMAL_EXPLOSION_PROPGUN = 6;
	
	private int type;
	
	private boolean remove;
	private boolean willDestroyBlocks;
	private int playerDamage;

	//Animation
	private ArrayList<BufferedImage[]> sprites, bombExplosionSprites;
	private final int[] numFrames = { 4 };
	private final int[] bombExplosionNumFrames = { 3 };
	
	//private AffineTransform transform;
	private AffineTransformOp op;
	private double rotation;
	
	private int[][] explosionArea = new int[10][10];
	
	public Explosion(double x, double y, int type, int damage, TileMap tm) 
	{
		super(tm);
		
		this.x = x;
		this.y = y;
		this.type = type;
		this.playerDamage = damage;
		
		remove = false;
		
		switch(type)
		{
			case Explosion.NORMAL_EXPLOSION:
			{
				width = 50;
				height = 50;
				cwidth = width*3;
				cheight = height*3;
				willDestroyBlocks = true;
				SoundPlayer.playClip("bombexplosion.wav");
				init();
				break;
			}
			case Explosion.BOMB_EXPLOSION:
			{
				width = 50;
				height = 50;
				cwidth = height*4;
				cheight = height*4;
				willDestroyBlocks = true;
				SoundPlayer.playClip("bombexplosion.wav");
				init();
				break;
			}
			case Explosion.NORMAL_EXPLOSION_NO_TILE_DAMAGE:
			{
				//same as case 1, except it wont destroy tiles
				width = 50;
				height = 50;
				cwidth = width*2;
				cheight = height*2;
				willDestroyBlocks = false;
				SoundPlayer.playClip("bombexplosion.wav");
				init();
				break;
			}
			case Explosion.BOMB_EXPLOSION_BOSS_FIGHT: //used in the boss fight to destroy a minimal amount of tiles and spawn several fires
			{
				width = 50;
				height = 50;
				cwidth = width*2;
				cheight = height*1;
				willDestroyBlocks= true;
				SoundPlayer.playClip("bombexplosion.wav");
				init();
				break;
			}
			case Explosion.NORMAL_EXPLOSION_BIRD:
			{
				width = 50;
				height = 50;
				cwidth = width*3;
				cheight = height*3;
				willDestroyBlocks  = false;
				SoundPlayer.playClip("chirp.wav");
				init();
				break;
			}
			case Explosion.NORMAL_EXPLOSION_PROPGUN:
			{
				width = 50;
				height = 50;
				cwidth = width*3;
				cheight = height*3;
				willDestroyBlocks  = false;
				SoundPlayer.playClip("gunblast.wav");
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
		if (type == Explosion.NORMAL_EXPLOSION || type == Explosion.NORMAL_EXPLOSION_NO_TILE_DAMAGE || type == Explosion.NORMAL_EXPLOSION_BIRD || type == Explosion.NORMAL_EXPLOSION_PROPGUN)
		{
			animation.setFrames(sprites.get(0));
			animation.setDelay(50);
		} 
		else if(type == Explosion.BOMB_EXPLOSION || type == Explosion.BOMB_EXPLOSION_BOSS_FIGHT)
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
						if (type != Explosion.BOMB_EXPLOSION_BOSS_FIGHT)
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
			if(this.intersects(PlayState.getPlayer()) && !(this.type == Explosion.NORMAL_EXPLOSION_NO_TILE_DAMAGE || this.type == Explosion.NORMAL_EXPLOSION_BIRD || this.type == Explosion.NORMAL_EXPLOSION_PROPGUN))
			{
				PlayState.getPlayer().playerHurt(this.playerDamage);
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
			if(type == Explosion.NORMAL_EXPLOSION || type == Explosion.NORMAL_EXPLOSION_NO_TILE_DAMAGE || type == Explosion.NORMAL_EXPLOSION_BIRD || type == Explosion.NORMAL_EXPLOSION_PROPGUN)
			{
				// Drawing the rotated image at the required drawing locations
				g.drawImage(op.filter(animation.getImage(), null), (int)(x + xmap), (int)(y + ymap), null);

				if(tileMap.getShowCollisonBox())
				{
					g.setColor(Color.RED);
					g.draw(this.getRectangle());
				}
			}
			else if(type == Explosion.BOMB_EXPLOSION || type == Explosion.BOMB_EXPLOSION_BOSS_FIGHT)
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
		m.playerHurt(20, false);
	}
}
