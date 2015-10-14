package GameState;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import Main.GamePanel;
import TileMap.Background;

public class CreditState extends GameState 
{
	private GameStateManager gsm;
	private Background bg;
	private Color titleColor;
	private Font titleFont, optionsFont, subTextFont;
	private String[] creditsNames = {"Ian Battin - Progammer", "Maxence Weyrich - Programmer", "Xavier Graham - Progammer", 
									"Sarah MacDougall - Programmer", "Rhea Bae - Art and Design", "Taage Storey - Design and Storyline"};
	
	public CreditState(GameStateManager gsm)
	{
		this.gsm = gsm;
		bg = new Background("/Backgrounds/menubackground.gif", 1);
		bg.setVector(0, -5.0); //moves the background
		titleColor = new Color(255, 60 ,0);
		titleFont = new Font("RussellSquare", Font.BOLD, 40);
		optionsFont = new Font("RusselSquare", Font.PLAIN, 20);
		subTextFont = new Font("RusselSquare", Font.PLAIN, 17);
	}
	
	//nothing to init
	public void init() 
	{
	}

	//update bgrnd
	public void update() 
	{
		bg.update();	
	}

	//draw the text
	public void draw(Graphics2D g) 
	{
		bg.draw(g);

		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("CREDITS", GamePanel.centerStringX("CREDITS", 0, 600), GamePanel.HEIGHT/4); //This probably shouldn't be coded, but instead part of the background or an actual image

		g.setColor(Color.WHITE);
		g.setFont(optionsFont);
		for (int i = 0; i < creditsNames.length; i++)
		{
			g.drawString(creditsNames[i], GamePanel.centerStringX(creditsNames[i], 0, 600), GamePanel.HEIGHT/2+(i*30)-75);
		}
		
		g.setFont(subTextFont);
		g.drawString("Press ENTER to return to Main Menu", GamePanel.centerStringX("Press ENTER to return to Main Menu", 0, 600), GamePanel.HEIGHT-100);
	}

	//handle press of key
	public void keyPressed(int k) 
	{
		if (k == KeyEvent.VK_ENTER)
		{
			gsm.setState(GameStateManager.MENUSTATE);		
		}	
	}

	@Override
	public void keyReleased(int k) 
	{
	}

}
