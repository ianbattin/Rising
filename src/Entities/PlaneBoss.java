package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import TileMap.Tile;
import TileMap.TileMap;

public class PlaneBoss extends Enemy {

	private boolean setMovement;
	private boolean moveComplete;
	private int typeAttack;

	public PlaneBoss(int x, int y, TileMap tm, Player player) 
	{
		super(x, y, tm, player);
		
		bullets = new ArrayList<Projectile>();
		firing = false;
		fireDelay = 10;
		typeAttack = 1;
		
		setMovement = false;
		
		recoverLength = 5;
		
		moveSpeed = 10.0;
		maxSpeedY = 3.0;
		maxSpeedX = 7.0;
		maxSpeed = 10.0;
		stopSpeed = 0.4;
		fallSpeed = 0.25;
		maxFallSpeed = 7.0;
		jumpStart = -3.0;
		
		width = 250;
		height = 100;
		cwidth = 250;
		cheight = 100;

		facingRight = false;
		
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
		
		getAnimation();
		getBulletCollision();

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
	}

	@Override
	public void draw(Graphics2D g) 
	{
		setMapPosition();

		g.setColor(Color.BLACK);
		if(recovering) g.setColor(Color.RED);
		g.fillRect((int)x, (int)y, width, height);
//		if(facingRight)
//		{
//			g.drawImage(animation.getImage(), (int)(x + xmap - width / 2), (int)(y + ymap - height / 2), width, height, null);
//		}
//		else
//		{
//			g.drawImage(animation.getImage(), (int)(x + xmap - width / 2 + width), (int)(y + ymap - height / 2), -width, height, null);
//		}
		
		for(Projectile p: bullets)
		{
			p.draw(g);
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
							bullets.add(new Projectile(x, y, angle, 3, tm));
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
			angle += Math.random()*Math.PI/12 - Math.PI/12;

			firing = true;

			if(firing)
			{

				long elapsed= (System.nanoTime() - fireTimer) / 1000000;
				if(fireDelay <= elapsed*(0.5*super.slowDown))
				{
					bullets.add(new Projectile(x, y, angle, 2, tm));
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
					bullets.add(new Projectile(x, y, angle, 4, tm));
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
			long elapsed = (System.nanoTime() - recoverTimer) / 1000000;
			if(recoverLength <= elapsed)
			{
				recovering = false;
			}
		}
		else
		{
			recovering = true;
			recoverTimer = System.nanoTime();
			health -= amount;
			numOfFramesToAnimHealth = 10;
			timesToLoop = 5;
		}
	}
	
	public boolean getMoveComplete()
	{
		return moveComplete;
	}
	
	@Override
	public void getAnimation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void collided(int type, Tile t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void collided(MapObject m) {
		// TODO Auto-generated method stub

	}

	public void setMoveComplete(boolean b) 
	{
		moveComplete = b;
	}

}
