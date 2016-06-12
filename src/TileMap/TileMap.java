package TileMap;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Entities.Explosion;
//import Entities.MapObject;
import GameState.GameStateManager;
//import GameState.PlayState;
import Main.GamePanel;

public class TileMap
{	
	GameStateManager gsm;
	
	private int x;
	private int y;
	private double xMove;
	private double yMove;
	private double dx;
	private double dy;
	private boolean moving;
	
	private String path;
	private String levelName;
	
	private boolean showCollisonBox = false;
	
	private int width; //total width in tiles
	private int height; //total height in tiles
	private int totalWidth;
	private int[][] map; //our 2d array of tiles
	
	private int tileSize; //width and height of individual tiles
	
	public ArrayList<Tile> tiles;
	public static BufferedImage[] sprites;
	public BufferedImage spritesheet;
	
	private ArrayList<Explosion> explosions;
	
	public TileMap(String s, String levelName, int extraWidth, GameStateManager gsm)
	{
		this.gsm = gsm;
		x = 0;
		y = 0;
		dx = 0;
		dy = 0;
		xMove = 0;
		yMove = 0;
		moving = false;
		this.levelName = levelName;
		
		try
		{
			path = "/Sprites/Tiles/FullTileSet.png";
			spritesheet = ImageIO.read(getClass().getResourceAsStream(path));
			
			
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Maps/" + s)));
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
			
			totalWidth = width*tileSize + ((width*tileSize)*extraWidth);
			br.close();
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
					tiles.add(new Tile(col * tileSize + x, row * tileSize - height * tileSize + GamePanel.HEIGHT, tile, tileSize, this));
					for(int i = 1; i <= extraWidth; i++)
					{
						tiles.add(new Tile(col * tileSize + x + GamePanel.WIDTH*i, row * tileSize - height * tileSize + GamePanel.HEIGHT + y, tile, tileSize, this));
						tiles.add(new Tile(col * tileSize + x - GamePanel.WIDTH*i, row * tileSize - height * tileSize + GamePanel.HEIGHT + y, tile, tileSize, this));
					}
				}
				if(tile > minType)
				{
					minType = tile;
				}
			}
		}
		
		//load sprites into sprites array
		sprites = new BufferedImage[spritesheet.getHeight()/25 * spritesheet.getWidth()/25];
		int count = 0;
		for(int row = 0; row < spritesheet.getHeight() / tileSize; row++)
		{
			for(int col = 0; col < spritesheet.getWidth() / tileSize; col++)
			{
				sprites[count] = spritesheet.getSubimage(col * tileSize, row * tileSize, tileSize, tileSize);
				count++;
			}
		}
		
		for(Tile t: tiles)
		{
			t.init();
		}
		
		explosions = new ArrayList<Explosion>();
	}
	
	public void update()
	{	
		xMove += dx;
		yMove += dy;
		
		for(int i = 0; i < tiles.size(); i++)
		{
			if(tiles.get(i).pastBottom())
			{
				tiles.remove(i);
				i--;
			}
			else
				tiles.get(i).update(dx, dy);
		}
		
		for(int i = 0; i < explosions.size(); i++)
		{
			if(explosions.get(i).getType() == 0)
				explosions.remove(i);
			else
				explosions.get(i).update();
		}
	}
	
	public void draw(Graphics2D g)
	{
		//instead of drawing the tiles in this class, each tile draws itself
		for(Tile t: tiles)
		{
			t.draw(g, t.getType());
		}
		
		for(int i = 0; i < explosions.size(); i++)
		{
			explosions.get(i).draw(g);
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
	
	public String getSpriteSheet()
	{
		return path;
	}

	public void setXVector(double dx) 
	{
		this.dx = dx;
	}
	public void setYVector(double dy) 
	{
		this.dy = dy;
		if(dy != 0) moving = true;
		else
			moving = false;
	}
	
	public boolean getMoving()
	{
		return moving;
	}
	
	public int getTileSize()
	{
		return tileSize;
	}
	
	public int getType(int row, int col)
	{
		return map[row][col];
	}
	
	public int getTileMapHeight()
	{
		return height*tileSize;
	}
	
	public int getTileMapWidth()
	{
		return width*tileSize;
	}
	
	public ArrayList<Tile> getTiles()
	{
		return tiles;
	}
	
	public ArrayList<Explosion> getExplosions()
	{
		return explosions;
	}
	
	public void setShowCollisonBox(boolean b)
	{
		showCollisonBox = b;
	}
	
	public boolean getShowCollisonBox()
	{
		return showCollisonBox;
	}
	
	public void setX(int x) 
	{	
		xMove = x;
		for(Tile t: tiles)
		{
			t.setX(t.getX() + x);
		}
	}
	
	public void setY( int y) 
	{	
		yMove = y;
		for(Tile t: tiles)
		{
			t.setY(t.getY() + y);
		}
	}
	public int getX() {	return x;	}
	public int getY() {	return y;	}
	public double getXMove() {	return xMove;	}
	public double getYMove() {	return yMove;	}
	public double getDX() { return dx; }
	public double getDY() { return dy; }
	public String getLevelName() { return this.levelName; }

	public int getTotalWidth() 
	{
		return totalWidth;
	}
	
	public GameStateManager getGSM()
	{
		return gsm;
	}
	
	public void setTiles(int[][] changedTiles)
	{
		for(int i = 0; i < changedTiles.length; i++)
		{
			for(Tile tile: this.getTiles())
			{
				if(new Rectangle(changedTiles[i][0] * tileSize - tileSize, changedTiles[i][1] * tileSize - tileSize, 24, 24).intersects(tile.getRectangle()))
				{
					tile.setType(changedTiles[i][2]);
					tile.init();
				}
			}
			//tiles.add(new Tile(changedTiles[i][0] * tileSize + x, changedTiles[i][1] * tileSize + y, changedTiles[i][2], tileSize, this));
			//tiles.get(tiles.size()-1).init();
		}
	}
}
