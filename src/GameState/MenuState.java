package GameState;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Entities.MapObject;
import Entities.Explosion;
import Entities.PlaneBoss;
import Entities.Projectile;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;

public class MenuState extends GameState 
{
	private Background bg;
	private BufferedImage title;
	
	private String[] options = {"Play", "Controls", "Credits", "Quit" };
	private int currentChoice = 0;
	
	private Font titleFont;
	private Font optionsFont;
	private int titleAlphaLevel;
	
	private TileMap tm  = new TileMap("Resources/Maps/level5.txt");
	private ArrayList<MapObject> entities;
	
	private long timer;
	
	public MenuState(GameStateManager gsm)
	{
		super();
		init();
		this.gsm = gsm;
		timer = System.nanoTime();;

		super.isFadingOut = false;
		super.alphaLevel = 0;
		
		titleAlphaLevel = 0;
		//titleFont = new Font("RussellSquare", Font.BOLD, 60);
		optionsFont = new Font("Munro", Font.PLAIN, 24);
		
		//This is going to try to set the background from a certain file path
		try
		{
			bg = new Background("/Backgrounds/MenuBackground.png", 0);
			title = ImageIO.read(getClass().getResourceAsStream("/Text/TitlePlaceholder2.png"));
			bg.setVector(0, -5.0); //moves the background
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	//Don't need to initialize anything here really...
	public void init() 
	{
		entities = new ArrayList<MapObject>();
		
	}

	//Only thing being updated is the background for movement
	public void update()
	{
		bg.update();
		
		/*long elapsed = (System.nanoTime() - timer) / 1000000;
		if(100 <= elapsed)
		{
			int randomNumber = (int)(Math.random()*10 + 1);
			if(randomNumber < 10)
				entities.add(new Explosion((int)(Math.random()*800), (int)(Math.random()*800), 1, tm));
			else
				entities.add(new Explosion((int)(Math.random()*800), (int)(Math.random()*800), 2, tm));
			timer = System.nanoTime();
		}*/
		
		if (super.isFadingOut)
		{
			super.fadeOut(5000000000.0, Color.BLACK, 5, gsm, GameStateManager.MENUSTATE, GameStateManager.TRANSITION_INTROSTATE);
			//title fading
			if (titleAlphaLevel < 255)
			{
				titleAlphaLevel += 1;
			}
		}
		
		for(MapObject mo: entities)
		{
			mo.update();
		}
	}


	public void draw(Graphics2D g)
	{	
		bg.draw(g);
		for(MapObject mo: entities)
		{
			mo.draw(g);
		}
		
		//Draws out our options menu
		for(int i = 0; i < options.length; i++)
		{
			if(i == currentChoice)
			{
				g.setColor(new Color(255,150,0));
			}
			else
			{
				g.setColor(Color.WHITE);
			}
			g.setFont(optionsFont);
			g.drawString(options[i], centerStringX(options[i], 0, GamePanel.WIDTH, g), GamePanel.HEIGHT/2 + 40 + i * 25); //uses the i variable from the for loop to correctly position options on top of eachother
		
			super.drawFade(g);
			
			//g.drawImage(title, GamePanel.WIDTH/2 - 175/2, GamePanel.HEIGHT/4, 175, 100, null);
			//title fading
			//g.setColor(new Color(0,0,0,titleAlphaLevel));
			//g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		}
	}

	//Selects the current game state
	private void select()
	{
		playSound("select.wav");
	    if(currentChoice == 0)
		{
			super.isFadingOut = true;
		}
		if(currentChoice == 1)
		{
			gsm.setState(GameStateManager.CONTROLSTATE);
		}
		if(currentChoice == 2)
		{
			gsm.setState(GameStateManager.CREDITSTATE);
		}
		if(currentChoice == 3)
		{
			System.exit(0);
		}
	}
	
	public void keyPressed(int k) 
	{
		
		if(k == GameStateManager.select && !super.isFadingOut)
		{
			select();
		} 
		else if (k == GameStateManager.select)
		{
			gsm.setState(GameStateManager.LEVEL1STATE);
			gsm.resetState(GameStateManager.MENUSTATE);
		}
		
		if (k == GameStateManager.reset)
		{
			gsm.resetState(GameStateManager.MENUSTATE);
		}
		
		//prevents other changes/movements once play has been selected
		if(!super.isFadingOut)
		{
			//If you press the up key, the selected option go up
			if(k == GameStateManager.up)
			{
				playSound("changeselection.wav");
				currentChoice--;
				//Unless you reach the top in which case it loops back to the bottom
				if(currentChoice == -1)
				{
					currentChoice = options.length - 1;
				}
			}
			
			//If you press the down key, the selected option goes down
			if(k == GameStateManager.down)
			{
				playSound("changeselection.wav");
				currentChoice++;
				//Unless you reach the bottom in which case it loops back to the top
				if(currentChoice == options.length)
				{
					currentChoice = 0;
				}
			}
		}
	}


	public void keyReleased(int k) 
	{

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
