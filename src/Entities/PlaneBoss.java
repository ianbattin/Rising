package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import TileMap.Tile;
import TileMap.TileMap;

public class PlaneBoss extends Enemy {

	public PlaneBoss(int x, int y, TileMap tm, Player player) 
	{
		super(x, y, tm, player);
		
		bullets = new ArrayList<Projectile>();
		firing = false;
		fireDelay = 10;
		
		recoverLength = 100;
		
		moveSpeed = 1.0;
		maxSpeedY = 3.0;
		maxSpeedX = 7.0;
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
		health = 10;
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

		yFromBottom += (-dy + tm.getDY());

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
		if(tileMap.getSpriteSheet().equals("/Sprites/Tiles/FullTileSet.png"))
		{
			for(Tile t: tileMap.getTiles())
			{
				if(t.onScreen() && t.getType() != 0)
				{
					relX = (int) (this.x - (int)t.getX());
					relY = (int) (this.y - (int)t.getY());
					this.setAngle(Math.atan2(-relY, -relX));
					if(angle > 3.5) angle = 3.5;
					if(angle < 2.5) angle = 2.5;
					this.setAngle(angle + Math.random()*Math.PI/58 - Math.PI/58);

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
		else
		{
			relX = (int) (this.x - (int)player.getX());
			relY = (int) (this.y - (int)player.getY());
			this.setAngle(Math.atan2(-relY, -relX));
			this.setAngle(angle + Math.random()*Math.PI/12 - Math.PI/12);

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
	}

	@Override
	public void getMovement() 
	{
		if(tileMap.getSpriteSheet().equals("/Sprites/Tiles/FullTileSet.png"))
		{
			dx -= moveSpeed;
			if(dx < -(maxSpeedX*super.slowDown)) dx = -(maxSpeedX*super.slowDown);
		}
		
		x += dx;
		y += dy;
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

}
