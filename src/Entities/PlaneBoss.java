package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import Main.GamePanel;
import Main.SoundPlayer;
import TileMap.Tile;
import TileMap.TileMap;

public class PlaneBoss extends Enemy {

	public static final int COCKPIT = 0;
	public static final int BOMBDROP = 1;
	
	private boolean setMovement;
	private boolean moveComplete;
	private int typeAttack;
	
	//animation
	private ArrayList<BufferedImage[]> playerSprites;
	private ArrayList<BufferedImage[]> playerHurtSprites;
	private BufferedImage arrowImage;
	private BufferedImage bombImage;
	private final int[] numFrames = { 4 };
	
	private Rectangle cockpit;
	private Rectangle bombArea;
	private int cockpitX;
	private int cockpitY;
	private int arrowLoc;
	private int arrowAnimator;
	private boolean attacking;
	private boolean evading;
	private boolean drawArrow;
	private boolean bombAttack;
	private long bombTimer;
	private ArrayList<MapObject> mapObjects;
	
	private int evadeX;
	private int evadeY;
	private double bobLeftX;
	private double bobRightX;
	private double bobUpY;
	private double bobDownY;
	private double bobSpeedX;
	private double bobSpeedY;

	public PlaneBoss(int x, int y, TileMap tm, Player player, int typeAttack) 
	{
		super(x, y, tm, player);
		
		attacking = false;
		
		bullets = new ArrayList<Projectile>();
		mapObjects = new ArrayList<MapObject>();
		firing = false;
		fireDelay = 20;
		this.typeAttack = typeAttack;
		
		setMovement = false;
		
		recoverLength = 40;
		
		moveSpeed = 5.0;
		maxSpeedY = 3.0;
		maxSpeedX = 7.0;
		maxSpeed = 10.0;
		stopSpeed = 0.4;
		fallSpeed = 0.25;
		maxFallSpeed = 7.0;
		jumpStart = -3.0;
		bobSpeedX = -0.25;
		bobSpeedY = 0.5;
		
		width = 307;
		height = 114;
		cwidth = 200;
		cheight = 75;

		facingRight = false;
		
		evadeX = evadeY = 0;
		evading = false;
		drawArrow = false;
		
		try
		{
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/StukaSprites.png"));
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
			BufferedImage playerHurtSpritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/StukaSprites.png"));
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
			
			arrowImage = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/arrow.png"));
			bombImage = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemy/bomb.png")).getSubimage(0, 0, 53, 53);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = 0;
		animation.setFrames(playerSprites.get(0));
		animation.setDelay(2);
		
		cockpitX = x + 90;
		cockpitY = y + 10;
		cockpit = new Rectangle(cockpitX, cockpitY, 80, 25);
		
		bombArea = new Rectangle((int)(this.x+cwidth/2+15), (int)(this.y+cheight-15), 53, 53);
		
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
			if(!attacking || bombAttack) getAttack();
		}
		else
		{
			dy = 10.0;
			y += dy;
		}

		x += tileMap.getDX();
		y += tileMap.getDY();
		
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
		
		for(int i = 0; i < mapObjects.size(); i++)
		{
			if(mapObjects.get(i).getRemove())
			{
				mapObjects.remove(i);
				i--;
			}
			else
			{
				mapObjects.get(i).update();
			}
		}
		for(int i = 0; i < bullets.size(); i++)
		{	
			if(bullets.get(i).getRemove())
			{
				bullets.remove(i);
				i--;
			}
			else
			{
				bullets.get(i).update();
				if(bullets.get(i).getY() > GamePanel.HEIGHT) bullets.remove(i);
			}
		}
	}

	@Override
	public void draw(Graphics2D g) 
	{
		setMapPosition();

		if(tileMap.getShowCollisonBox())
		{
			g.setColor(Color.RED);
			g.draw(cockpit);
			g.draw(this.getRectangle());
		}
		
		if(facingRight)
		{
			g.drawImage(animation.getImage(), (int)(x + xmap), (int)(y + ymap), width, height, null);
			if(drawArrow)
			{
				if(arrowLoc == PlaneBoss.COCKPIT)
				{
					arrowAnimator++;
					g.drawImage(arrowImage, (int)(cockpitX + cockpit.getWidth()/2 - arrowImage.getWidth()/2), (int)(this.y - (arrowImage.getHeight() + 25) + (Math.sin(arrowAnimator/8.0)*25)), (int)(arrowImage.getWidth()), (int)(arrowImage.getHeight()), null);
				}
				else
				{
					arrowAnimator++;
					g.drawImage(arrowImage, (int)(this.x + this.cwidth/2 + 23), (int)(this.y + this.height + (arrowImage.getHeight() + 10) - (Math.sin(arrowAnimator/8.0)*25)), (int)(arrowImage.getWidth()), -(int)(arrowImage.getHeight()), null);
				}
			}
			if (bombAttack)
			{
				g.drawImage(bombImage, (int)(this.x+cwidth/2+15) + 53, (int)(this.y+cheight-15), -53, 53, null);
			}
		}
		else
		{
			g.drawImage(animation.getImage(), (int)(x + xmap) + width, (int)(y + ymap), -width, height, null);
			if(drawArrow)
			{
				if(arrowLoc == PlaneBoss.COCKPIT)
				{
					arrowAnimator++;
					g.drawImage(arrowImage, (int)(cockpitX + cockpit.getWidth()/2 - arrowImage.getWidth()/2), (int)(this.y - (arrowImage.getHeight() + 25) + (Math.sin(arrowAnimator/8.0)*25)), (int)(arrowImage.getWidth()), (int)(arrowImage.getHeight()), null);
				}
				else
				{
					arrowAnimator++;
					g.drawImage(arrowImage, (int)(this.x + this.cwidth/2 + 23), (int)(this.y + this.height + (arrowImage.getHeight() + 10) - (Math.sin(arrowAnimator/8.0)*25)), (int)(arrowImage.getWidth()), -(int)(arrowImage.getHeight()), null);
				}
			}
			if (bombAttack)
			{
				g.drawImage(bombImage, (int)(this.x+cwidth/2+15), (int)(this.y+cheight-15), 53, 53, null);
			}
		}
		
		for(Projectile p: bullets)
		{
			p.draw(g);
		}
		for(MapObject m : mapObjects)
		{
			m.draw(g);
		}
	}

	public void checkPlayerCollision()
	{
		if(!facingRight)
		{
			cockpitX = (int) (x + 90);
			cockpitY = (int) (y + 10);
			
			if (bombAttack)
			{
				bombArea.x = (int)(this.x+cwidth/2+15);
				bombArea.y = (int)(this.y+cheight-15);
			}
		}
		else
		{
			cockpitX = (int) (x + 140);
			cockpitY = (int) (y + 10);
			
			if (bombAttack)
			{
				bombArea.x = (int)(this.x+cwidth/2+15);
				bombArea.y = (int)(this.y+cheight-15);
			}
		}
		cockpit.setLocation(cockpitX, cockpitY);
		
		
		
		if(cockpit.intersects(player.getRectangle()) && player.getDY() > 0)
		{
			playerHurt(10);
			player.setYVector(-10.0);
			player.resetDoubleJump();
			if (getMoveComplete())
			{
				evading = true;
				int dist;
				do 
				{
					evadeX = (int)(Math.random()*500 + 100);
					evadeY = (int)(Math.random()*500 + 100);
					dist = (int)Math.hypot(evadeX - x, evadeY - y);
				} 
				while (dist < 100 && dist > 400); //ensure that the plane moves long enough of a distance
				setMoveComplete(false);
			}
			if (arrowLoc == PlaneBoss.COCKPIT)	drawArrow = false;
		}
		else if(this.intersects(player) && !evading)
		{
			player.playerHurt(1);
		}
	}
	
	@Override
	public void getAttack() 
	{
		if(bombAttack)
		{
			long elapsed = System.currentTimeMillis() - bombTimer;
			if (elapsed > 9000)
			{
				mapObjects.add(new Bomb((this.x+cwidth/2+15), (this.y+cheight-15), this.tileMap, 1));
				bombAttack = false;
				drawArrow = false;
			}
		}
		//Lots of exploding bullets
		if(typeAttack == 1)
		{
			attacking = true;
			for(Tile t: tileMap.getTiles())
			{
				if(t.onScreen() == true && t.getType() != 0)
				{
					relX = (int) (x + width/2 - (int)t.getX());
					relY = (int) (y + height - (int)t.getY());
					angle = Math.atan2(-relY, -relX);
					if(angle > 3.75) angle = 3.75;
					if(angle < 1.25) angle = 1.25;
					angle +=  Math.random()*Math.PI/58 - Math.PI/58;

					if(relX < 1000)
					{
						firing = true;
					}
					else
						firing = false;

					if(firing && !super.notOnScreen())
					{

						long elapsed= (System.nanoTime() - fireTimer) / 1000000;
						if(fireDelay <= elapsed*(0.5*Enemy.slowDown))
						{
							bullets.add(new Projectile(x + width/2, y+height, angle, 5, tileMap));
							fireTimer = System.nanoTime();
						}
					}
				}
			}
			attacking = false;
		}
		//Normal attack
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
				if(fireDelay <= elapsed*(0.5*Enemy.slowDown))
				{
					bullets.add(new Projectile(x + width/2, y+height, angle, 2, tileMap));
					fireTimer = System.nanoTime();
				}
			}
		}
		//Fire
		else if(typeAttack == 3)
		{
			if(facingRight)
			{
				relX = -100;
				relY = -100;
			}
			else
			{
				relX = 100;
				relY = -100;
			}
			angle = Math.atan2(-relY, -relX);

			fireDelay = 100;
			firing = true;

			if(firing && !super.notOnScreen())
			{

				long elapsed= (System.nanoTime() - fireTimer) / 1000000;
				if(fireDelay <= elapsed*(0.5*Enemy.slowDown))
				{
					bullets.add(new Projectile(x+width/2, y+height, angle, 4, tileMap));
					fireTimer = System.nanoTime();
				}
			}
		}
		//Three bullets
		else if(typeAttack == 4)
		{
			if(player.getX() <= 200)
			{
				relX = 100;
				relY = 200;
			}
			else if(player.getX() >= 600)
			{
				relX = -100;
				relY = 200;
			}
			else
			{
				relX = 0;
				relY = 200;
			}

			angle = Math.atan2(-relY, -relX);
			firing = true;
			fireDelay = 1000;

			if(firing)
			{
				long elapsed= (System.nanoTime() - fireTimer) / 1000000;
				if(fireDelay <= elapsed*(0.5*Enemy.slowDown))
				{
					System.out.println("FIRING");
					bullets.add(new Projectile(x + width/2, y+20, angle, 2, tileMap));
					bullets.add(new Projectile(x + width/2, y+20, angle-0.35, 2, tileMap));
					bullets.add(new Projectile(x + width/2, y+20, angle+0.35, 2, tileMap));
					fireTimer = System.nanoTime();
				}
			}
		}
	}
	
	public void setAttack(int typeAttack)
	{
		if(typeAttack == 0) attacking = false;
		else attacking = true;
		this.typeAttack = typeAttack;
	}

	@Override
	public void getMovement() 
	{
		if(tileMap.getSpriteSheet().equals("/Sprites/Tiles/FullTileSet.png") && !setMovement)
		{
			dx -= moveSpeed;
			dy = -2.0;
			if(dx < -(maxSpeedX*Enemy.slowDown)) dx = -(maxSpeedX*Enemy.slowDown);
		}
		
		x += dx*Enemy.slowDown;
		y += dy*Enemy.slowDown;
		
		if(moveComplete)
		{
			bobUpDown();
			bobLeftRight();
		}
	}
	
	public void setMovement(double endX, double endY, double speed, int typeAttack)
	{
		this.typeAttack = typeAttack;
		moveComplete = false;
		setMovement = true;
		
		double startX = x;
		double startY = y;
		
		double differenceX = endX - startX;
		double differenceY = endY - startY;
		
		if(differenceX < 0)
			moveSpeed = -maxSpeed;
		else
			moveSpeed = maxSpeed;
			
		if((endX - 10 < x && x < endX + 10))
			dx = 0;
		else
			dx = moveSpeed / (1/speed);
		
		if((endY - 10 < y && y < endY + 10))
			dy = 0;
		else
			dy = dx * (differenceY / differenceX);
		
		if((dx == 0 && dy == 0) || (dx == -0 && dy == -0) || (dx == 0 && dy == -0) || (dx == -0 && dy == 0))
		{
			moveComplete = true;
			bobUpY = y - 10;
			bobDownY = y + 10;
			bobLeftX = x - 10;
			bobRightX = x + 10;
			evading = false;
		}
	}
	
	public void bobUpDown()
	{
		if(y < bobUpY)
		{
			y += 2;
			bobSpeedY = -bobSpeedY;
		}
		else if(y > bobDownY)
		{
			y -= 2;
			bobSpeedY = -bobSpeedY;
		}
		else
			y += bobSpeedY;
	}
	
	public void bobLeftRight()
	{
		if(x > bobRightX)
		{
			x -= 2;
			bobSpeedX = -bobSpeedX;
		}
		else if(x < bobLeftX)
		{
			x += 2;
			bobSpeedX = -bobSpeedX;
		}
		else
			x += bobSpeedX;
	}

	@Override
	public void playerHurt(int amount)
	{
		if(recovering || health <= 0)
		{
			long elapsed = (System.nanoTime() - recoverTimer) / 10000;
			if(recoverLength <= elapsed)
			{
				recovering = false;
			}
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
	
		if (recovering)
		{
			animation.changeFrames(playerHurtSprites.get(currentAction));
			long elapsed = (System.nanoTime() - recoverTimer) / 10000;
			if(recoverLength <= elapsed)
			{
				recovering = false;
			}
		}
		else animation.changeFrames(playerSprites.get(currentAction));
		
		animation.update();	
	}

	public void evadeMove()
	{
		setMovement(evadeX, evadeY, 0.25, 0);
	}
	
	public void getBulletCollision()
	{
		for(int i = 0; i < bullets.size(); i++)
		{
			if(bullets.get(i).intersects(player))
			{
				bullets.get(i).collided(player);
			}
			if(bullets.get(i).notOnScreen() && bullets.get(i).getLifeTime() > 300) bullets.remove(i);
		}
		
		for(int i = 0; i < player.getBullets().size(); i++)
		{
			if(player.getBullets().get(i).intersects(this))
			{
				player.getBullets().get(i).collided(this);
			}
			if(player.getBullets().get(i).getRectangle().intersects(bombArea) && bombAttack)
			{
				System.out.println("SPECIAL");
				bombAttack = false;
				drawArrow = false;
				mapObjects.add(new Explosion((this.x+cwidth/2+15), (this.y+cheight-15), 4, tileMap));
				recovering = false;
				playerHurt(30);
			}
		}
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

	public void onDeath()
	{
		player.increasePoints(5000);
	}
	
	public void setMoveComplete(boolean b) 
	{
		moveComplete = b;
	}
	
	public boolean isEvading() {return evading;}
	public boolean isAttacking() {return attacking;}
	public boolean isBombAttacking() {return bombAttack;}
	
	public void setDrawArrow(boolean willDraw, int location)
	{
		this.drawArrow = willDraw;
		this.arrowLoc = location;
	}
	
	public void startBombAttack()
	{
		this.bombAttack = true;
		bombTimer = System.currentTimeMillis();
	}

}
