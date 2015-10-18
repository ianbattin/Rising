package GameState;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import Main.GamePanel;
import TileMap.Background;

public class MenuState extends GameState 
{
	private Background bg;
	private BufferedImage title;
	
	private String[] options = {"Play", "Controls", "Credits", "Quit" };
	private int currentChoice = 0;
	
	private Font titleFont;
	private Font optionsFont;
	private boolean isFadingOut;
	private int alphaLevel;
	private int titleAlphaLevel;
	private float timeKeeper;
	
	public MenuState(GameStateManager gsm)
	{
		this.gsm = gsm;

		isFadingOut = false;
		alphaLevel = 0;
		timeKeeper = 0;
		titleAlphaLevel = 255;
		titleFont = new Font("RussellSquare", Font.BOLD, 60);
		optionsFont = new Font("RusselSquare", Font.PLAIN, 24);
		
		//This is going to try to set the background from a certain file path
		try
		{
			bg = new Background("/Backgrounds/menubackground.gif", 1);
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

	}

	//Only thing being updated is the background for movement
	public void update()
	{
		bg.update();
		
		timeKeeper += GamePanel.getElapsedTime();
		
		if (isFadingOut)
		{
			fadeOut(4000000000.0);
			if (titleAlphaLevel > 0)
			{
				titleAlphaLevel -= 1;
			}
		}
	}


	public void draw(Graphics2D g)
	{
		bg.draw(g);
			
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
			g.drawString(options[i], centerStringX(options[i], 0, 600, g), GamePanel.HEIGHT/2 + 40 + i * 25); //uses the i variable from the for loop to correctly position options on top of eachother
		
			drawFade(g);
			
			//g.setColor(new Color(255, 60, 0, titleAlphaLevel));
			//g.setFont(titleFont);
			//g.drawString("RISING", centerStringX("RISING", 0, 600, g), GamePanel.HEIGHT/4); //This probably shouldn't be coded, but instead part of the background or an actual image
			g.drawImage(title, GamePanel.WIDTH/2 - 175/2, GamePanel.HEIGHT/4, 175, 100, null);
		}
	}

	//Selects the current game state
	private void select()
	{
		playSound("select.wav");
	    if(currentChoice == 0)
		{
			isFadingOut = true;
		}
		if(currentChoice == 1)
		{
			gsm.setCurrentState(GameStateManager.CONTROLSTATE);
		}
		if(currentChoice == 2)
		{
			gsm.setCurrentState(GameStateManager.CREDITSTATE);
		}
		if(currentChoice == 3)
		{
			System.exit(0);
		}
	}
	
	public void keyPressed(int k) 
	{
		if(k == GameStateManager.select && !isFadingOut)
		{
			select();
		} 
		else if (k == GameStateManager.select && isFadingOut)
		{
			gsm.resetState(GameStateManager.MENUSTATE);
			gsm.setCurrentState(GameStateManager.PLAYSTATE);
		}
		
		if (k == GameStateManager.reset && isFadingOut)
		{
			gsm.resetState(GameStateManager.MENUSTATE);
		}
		
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


	public void keyReleased(int k) 
	{

	}
	
	//Fading methods
	private void fadeOut(double timeToWait)
	{
		if (alphaLevel < 255){
			alphaLevel += 5;
			timeKeeper = 0;
		} 
		else if(timeKeeper > timeToWait)
		{
			isFadingOut = false;
			gsm.resetState(GameStateManager.MENUSTATE);
			gsm.setState(GameStateManager.INTROSTATE);
		}
	}
	
	private void drawFade(Graphics2D g)
	{
		g.setColor(new Color(0, 0, 0, alphaLevel));
		g.fillRect(0, 0, 600, 800);
	}

}
