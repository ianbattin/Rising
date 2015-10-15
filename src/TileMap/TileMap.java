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
	private int x;
	private int y;
	private int dx;
	private int dy;
	
	private int width; //total width in tiles
	private int height; //total height in tiles
	private int[][] map; //our 2d array of tiles
	
	private int tileSize; //width and height of individual tiles
	
	public ArrayList<Tile> tiles;
	
	public TileMap(String s)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(s));
			tileSize = Integer.parseInt(br.readLine());
			width = Integer.parseInt(br.readLine()); //reads line 1 of the file for the width
			height = Integer.parseInt(br.readLine()); //reads line 2 of the files for the height
		
			map = new int[height][width]; //sets map to a new int[][] with width and height
			tiles = new ArrayList<Tile>();
			
			String delimeters = " "; //spaces will be ignored
			
			//reads each line row by row, and assigns the numbers into the 2d array map
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
		
		tiles = new ArrayList<Tile>();//basically map[][] but in ArrayList form cause they're easier

		//takes all of the values in map[][] and adds a new tile with their location to tiles
		for(int row = 0; row < height; row++)
		{
			for(int col = 0; col < width; col++)
			{
				int tile = map[row][col];
				if(tile == 0) 
				{
					tiles.add(new Tile(col * tileSize, row * tileSize - height * tileSize + GamePanel.HEIGHT, tile, tileSize));
				}
			}
		}
	}
	
	public void update()
	{	
		for(Tile t: tiles)
		{
			t.update(dx, dy);
		}
	}
	
	public void draw(Graphics2D g)
	{
		//instead of drawing the tiles in this class, each tile draws itself
		for(Tile t: tiles)
		{
			t.draw(g);
		}
	}
	
	public void setVector(int dx, int dy)
	{
		this.dx = dx;
		this.dy = dy;
		for(Tile t: tiles)
		{
			t.setVector(dx, dy);
		}
	}
	
	public int getTileSize()
	{
		return tileSize;
	}
	
	public int getType(int row, int col)
	{
		return map[row][col];
	}
	
	public int getX() {	return x;	}
	public int getY() {	return y;	}
}
