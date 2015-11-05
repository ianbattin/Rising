package TileMap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

//import Entities.MapObject;
import GameState.GameStateManager;
//import GameState.PlayState;
import Main.GamePanel;

public class TileMap
{
	private int x;
	private int y;
	private double dx;
	private double dy;
	
	private int width; //total width in tiles
	private int height; //total height in tiles
	private int[][] map; //our 2d array of tiles
	
	private int tileSize; //width and height of individual tiles
	
	public ArrayList<Tile> tiles;
	public static BufferedImage[] sprites;
	public BufferedImage spritesheet;
	
	public TileMap(String s)
	{
		x = 0;
		y = 0;
		
		try
		{
			spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Tiles/tileset4.png"));
			
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
		int minType = 0;
		for(int row = 0; row < height; row++)
		{
			for(int col = 0; col < width; col++)
			{
				int tile = map[row][col];
				if(tile != 0) 
				{
					tiles.add(new Tile(col * tileSize + x, row * tileSize - height * tileSize + GamePanel.HEIGHT, tile, tileSize));
					tiles.add(new Tile(col * tileSize + x + GamePanel.WIDTH, row * tileSize - height * tileSize + GamePanel.HEIGHT + y, tile, tileSize));
					tiles.add(new Tile(col * tileSize + x - GamePanel.WIDTH, row * tileSize - height * tileSize + GamePanel.HEIGHT + y, tile, tileSize));
				}
				if(tile > minType)
				{
					minType = tile;
				}
			}
		}
		
		//load sprites into sprites array
		sprites = new BufferedImage[minType+7];
		for(int i = 0; i < sprites.length; i++)
		{
			if(i < 20) sprites[i] = spritesheet.getSubimage(i * tileSize, 0, tileSize, tileSize);
			else sprites[i] = spritesheet.getSubimage((i-20) * tileSize, tileSize, tileSize, tileSize);
		}
		
		for(Tile t: tiles)
		{
			t.init();
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
			t.draw(g, t.getType());
		}
	}
	
	//Getters & Setters
	public void setVector(double dx, double dy)
	{
		this.dx = dx;
		this.dy = dy;
	}
		
	public static BufferedImage getSprite(int type)
	{
		return sprites[type];
	}

	public void setXVector(double dx) 
	{
		this.dx = dx;
	}
	public void setYVector(double dy) 
	{
		this.dy = dy;
	}
	
	public int getTileSize()
	{
		return tileSize;
	}
	
	public int getType(int row, int col)
	{
		return map[row][col];
	}
	
	public int getTileMapWidth()
	{
		return width*tileSize;
	}
	
	public int getX() {	return x;	}
	public int getY() {	return y;	}
	public double getDX() { return dx; }
	public double getDY() { return dy; }
}
