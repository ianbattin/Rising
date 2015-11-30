package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import TileMap.Tile;
import TileMap.TileMap;

public class PlaneBoss extends Enemy {

	private boolean setMovement;
	private boolean moveComplete;
	private int typeAttack;
	
	//animation
	private ArrayList<BufferedImage[]> playerSprites;
	private ArrayList<BufferedImage[]> playerHurtSprites;
	private final int[] numFrames = { 1 };
	
	private Rectangle cockpit;
	private int cockpitX;
	private int cockpitY;

	public PlaneBoss(int x, int y, TileMap tm, Player player) 
	{
		super(x, y, tm, player);
		
		bullets = new ArrayList<Projectile>();
		firing = false;
		fireDelay = 10;
		typeAttack = 1;
		
		setMovement = false;
		
		recoverLength = 40;
		
		moveSpeed = 10.0;
		maxSpeedY = 3.0;
		maxSpeedX = 7.0;
		maxSpeed = 10.0;
		stopSpeed = 0.4;
		fallSpeed = 0.25;
		maxFallSpeed = 7.0;
		jumpStart = -3.0;
		
		width = 307;
		height = 114;
		cwidth = 200;
		cheight = 75;

		facingRight = false;
		
		try
		{
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Tiles/Stuka.png"));
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
			BufferedImage playerHurtSpritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Tiles/Stuka.png"));
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
		
		cockpitX = x + 90;
		cockpitY = y + 10;
		cockpit = new Rectangle(cockpitX, cockpitY, 80, 25);
		
		//health
		health = 100;
	}

	@Override
	public void update() 
	{
		if (health > 0)
		{
			getMovement();
			getAttack();
		}
		else
		{
			dy = 10.0;
			y += dy;
		}

		x += tm.getDX();
		y += tm.getDY();
		
		getBulletCollision();
		getAnimation();

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
		
		checkPlayerCollision();
	}

	@Override
	public void draw(Graphics2D g) 
	{
		setMapPosition();

		if(tm.getShowCollisonBox())
		{
			g.setColor(Color.RED);
			g.draw(cockpit);
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
		
		for(Projectile p: bullets)
		{
			p.draw(g);
		}
	}

	public void checkPlayerCollision()
	{
		if(!facingRight)
		{
			cockpitX = (int) (x + 90);
			cockpitY = (int) (y + 10);
		}
		else
		{
			cockpitX = (int) (x + 140);
			cockpitY = (int) (y + 10);
		}
		cockpit.setLocation(cockpitX, cockpitY);
		
		if(cockpit.intersects(player.getRectangle()) && player.getDY() > 0)
		{
			playerHurt(20);
			player.setYVector(-10.0);
		}
		else if(this.intersects(player))
		{
			player.playerHurt(1);
		}
	}
	
	@Override
	public void getAttack() 
	{
		if(typeAttack == 1)
		{
			for(Tile t: tileMap.getTiles())
			{
				if(t.onScreen() && t.getType() != 0)
				{
					relX = (int) (this.x - (int)t.getX());
					relY = (int) (this.y - (int)t.getY());
					angle = Math.atan2(-relY, -relX);
					if(angle > 3.5) angle = 3.5;
					if(angle < 2.5) angle = 2.5;
					angle +=  Math.random()*Math.PI/58 - Math.PI/58;

					if(relX < 1000)
					{
						firing = true;
					}
					else
						firing = false;

					if(firing)
					{

						long elapsed= (System.nanoTime() - fireTimer) / 1000000;
						if(fireDelay <= elapsed*(0.5*super.slowDown))
						{
							bullets.add(new Projectile(x + width/2, y+height, angle, 3, tm));
							fireTimer = System.nanoTime();
						}
					}
				}
			}
		}
		else if(typeAttack == 2)
		{
			relX = (int) (this.x - (int)player.getX());
			relY = (int) (this.y - (int)player.getY());
			angle = Math.atan2(-relY, -relX);
			angle += Math.random()*Math.PI/2 - Math.PI/2;

			firing = true;

			if(firing)
			{

				long elapsed= (System.nanoTime() - fireTimer) / 1000000;
				if(fireDelay <= elapsed*(0.5*super.slowDown))
				{
					bullets.add(new Projectile(x + width/2, y+height, angle, 2, tm));
					fireTimer = System.nanoTime();
				}
			}
		}
		else if(typeAttack == 3)
		{
			relX = (int) (this.x - (int)player.getX());
			relY = (int) (this.y - (int)player.getY());
			angle = 2.5;

			fireDelay = 100;
			firing = true;

			if(firing)
			{

				long elapsed= (System.nanoTime() - fireTimer) / 1000000;
				if(fireDelay <= elapsed*(0.5*super.slowDown))
				{
					bullets.add(new Projectile(x+width/2, y+height, angle, 4, tm));
					fireTimer = System.nanoTime();
				}
			}
		}
	}

	@Override
	public void getMovement() 
	{
		if(tileMap.getSpriteSheet().equals("/Sprites/Tiles/FullTileSet.png") && !setMovement)
		{
			dx -= moveSpeed;
			if(dx < -(maxSpeedX*super.slowDown)) dx = -(maxSpeedX*super.slowDown);
		}
		
		x += dx;
		y += dy;
	}
	
	public void setMovement(double startX, double startY, double endX, double endY, double speed, int typeAttack)
	{
		this.typeAttack = typeAttack;
		moveComplete = false;
		setMovement = true;
		
		double differenceX = endX - startX;
		double differenceY = endY - startY;
		
		if(differenceX < 0)
		{
			moveSpeed = -maxSpeed;
		}
		else
			moveSpeed = maxSpeed;
			
		if((endX - 10 < x && x < endX + 10))
		{
			dx = 0;
		}
		else
		{
			dx = moveSpeed / (1/speed);
		}
		
		if((endY - 10 < y && y < endY + 10))
		{
			dy = 0;
		}
		else
		{
			dy = dx * (differenceY / differenceX);
		}
		
		if((dx == 0 && dy == 0) || (dx == -0 && dy == -0) || (dx == 0 && dy == -0) || (dx == -0 && dy == 0))
		{
			moveComplete = true;
		}
	}

	@Override
	public void playerHurt(int amount)
	{
		if(recovering || health <= 0)
		{
			Timer timer = new Timer();
			timer.schedule(new TimerTask()
			{
				public void run()
				{
					recovering = false;		
				}
				
			}, 10);
		}
		else
		{
			health -= amount;
			numOfFramesToAnimHealth = 10;
			timesToLoop = 5;
			recovering = true;
			recoverTimer = System.nanoTime();
		}
	}
	
	public boolean getMoveComplete()
	{
		return moveComplete;
	}
	
	@Override
	public void getAnimation() 
	{
		if(dx > 0) facingRight = true;
		else facingRight = false;
		
		if (recovering) animation.changeFrames(playerHurtSprites.get(currentAction));
		else animation.changeFrames(playerSprites.get(currentAction));
		
		animation.update();	
	}

	@Override
	public void collided(int type, Tile t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void collided(MapObject m) 
	{
		// TODO Auto-generated method stub

	}

	public void setMoveComplete(boolean b) 
	{
		moveComplete = b;
	}

}
