package TileMap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

//import Entities.MapObject;
import GameState.GameStateManager;
//import GameState.PlayState;
import Main.GamePanel;

public class TileMap
{
	public int x;
	public int y;
	
	public static int width;
	public static int height;
	public static int[][] map;
	
	public static int tileSize;
	
	public ArrayList<Tile> tiles;
	
	public TileMap(String s, int tileSize)
	{
		this.tileSize = tileSize;
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(s));
		
			width = Integer.parseInt(br.readLine());
			height = Integer.parseInt(br.readLine());
		
			map = new int[height][width];
			tiles = new ArrayList<Tile>();
			
			String delimeters = " ";
			
			for(int row = 0; row < height; row++)
			{
				String line = br.readLine();
				String[] wholeRow = line.split(delimeters);
				for(int col = 0; col < width; col++)
				{
					map[row][col] = Integer.parseInt(wholeRow[col]);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		tiles = new ArrayList<Tile>();

		for(int row = 0; row < height; row++)
		{
			for(int col = 0; col < width; col++)
			{
				int tile = map[row][col];
				if(tile == 0) 
				{
					tiles.add(new Tile(col * tileSize, row * tileSize, (col * tileSize) - tileSize, (col * tileSize) + tileSize, 
							(row * tileSize) - tileSize, (row * tileSize) + tileSize, tile));
				}
			}
		}
	}
	
	public void update()
	{	
	}
	
	public void draw(Graphics2D g)
	{
		for(Tile t: tiles)
		{
			t.draw(g);
			t.update();
		}
	}
	
	public int getType(int row, int col)
	{
		return map[row][col];
	}
	
	public int getX() {	return x;	}
	public int getY() {	return y;	}
}
