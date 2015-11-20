package GameState;

import java.util.ArrayList;

import Entities.Enemy;
import Entities.Pickups;
import Entities.Player;
import TileMap.Background;
import TileMap.TileMap;

public abstract class PlayState extends GameState
{
	protected Background bg;
	protected double bgVectorX, bgVectorY;
	protected double debrisVector;
	protected Player player;
	protected Pickups pickups;
	protected ArrayList<Enemy> enemies;
	
	public void setBackgroundVector(double vectorX, double vectorY)
	{
		bgVectorX = vectorX;
		bgVectorY = vectorY;
		bg.setVector(bgVectorX, bgVectorY);
	}
	
	public void setDebrisVectors(double vector)
	{
		debrisVector = vector;
	}
	
	public void setEntitiySpeed(float speed)
	{
		for(Enemy e: enemies)
			e.setSlowDownRate(speed);
	}
	
	public Player getPlayer()
	{
		return player;
	}

	public ArrayList<Enemy> getEnemies() 
	{
		return enemies;
	}
}
