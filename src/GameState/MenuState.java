package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import Main.GamePanel;
import TileMap.Background;

public class MenuState extends GameState 
{
	private Background bg;
	
	private String[] options = {"Play", "Controls", "Credits", "Quit" };
	private int currentChoice = 0;
	
	private Color titleColor;
	private Font titleFont;
	private Font optionsFont;
	
	public MenuState(GameStateManager gsm)
	{
		this.gsm = gsm;
		
		titleColor = new Color(255, 60 ,0);
		titleFont = new Font("RussellSquare", Font.BOLD, 60);
		optionsFont = new Font("RusselSquare", Font.PLAIN, 24);
		
		//This is going to try to set the background from a certain file path
		try
		{
			bg = new Background("/Backgrounds/menubackground.gif", 1);
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
	}


	public void draw(Graphics2D g)
	{
		bg.draw(g);
		
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("RISING", GamePanel.centerStringX("RISING", 0, 600), GamePanel.HEIGHT/4); //This probably shouldn't be coded, but instead part of the background or an actual image

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
			g.drawString(options[i], GamePanel.centerStringX(options[i], 0, 600), GamePanel.HEIGHT/2 + 40 + i * 25); //uses the i variable from the for loop to correctly position options on top of eachother
		}
	}

	//Selects the current game state
	private void select()
	{
		if(currentChoice == 0)
		{
			gsm.setCurrentState(GameStateManager.PLAYSTATE);
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
		if(k == KeyEvent.VK_ENTER)
		{
			select();
		}
		
		//If you press the up key, the selected option go up
		if(k == GameStateManager.up)
		{
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
}
