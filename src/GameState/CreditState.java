package GameState;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import Main.GamePanel;
import TileMap.Background;

public class CreditState extends GameState 
{
	private GameStateManager gsm;
	private Background bg;
	private Color titleColor;
	private Font titleFont, optionsFont, subTextFont;
	private String[] creditsNames = {"Ian Battin - Lead Progammer", "Maxence Weyrich - Programmer", 
			"Taage Storey - Game Design and Lead Artist", "Sarah MacDougall - Level Design", "Rhea Bae - Art and Design"};
	
	public CreditState(GameStateManager gsm)
	{
		super();
		this.gsm = gsm;
		bg = new Background("/Backgrounds/menubackgroundNoText.png", 1);
		//bg.setVector(0, -5.0); //moves the background
		titleColor = new Color(255, 60 ,0);
		titleFont = new Font("Munro", Font.BOLD, 40);
		optionsFont = new Font("Munro", Font.PLAIN, 24);
		subTextFont = new Font("Munro", Font.PLAIN, 20);
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
		g.drawString("CREDITS", centerStringX("CREDITS", 0, GamePanel.WIDTH, g), GamePanel.HEIGHT/4); //This probably shouldn't be coded, but instead part of the background or an actual image

		g.setColor(Color.WHITE);
		g.setFont(optionsFont);
		for (int i = 0; i < creditsNames.length; i++)
		{
			g.drawString(creditsNames[i], centerStringX(creditsNames[i], 0, GamePanel.WIDTH, g), GamePanel.HEIGHT/2+(i*30)-75);
		}
		
		//trying to keep it looking & functioning the same as in the control state. Harmony = better esthetics.
		g.setFont(subTextFont);
		g.setColor(new Color(255,150,0));
		g.drawString("Return to Menu", centerStringX("Return to Menu", 0, GamePanel.WIDTH, g), GamePanel.HEIGHT-100);
		/*
		g.drawString("Press " + KeyEvent.getKeyText(GameStateManager.reset) + " to return to Main Menu", 
				GamePanel.centerStringX("Press " + KeyEvent.getKeyText(GameStateManager.reset) + " to return to Main Menu", 0, 600), GamePanel.HEIGHT-100);
		 */
	}

	//handle press of key
	public void keyPressed(int k) 
	{
		//if (k == GameStateManager.reset)
		if (k == GameStateManager.select)
		{
			gsm.setState(GameStateManager.MENUSTATE);		
		}	
	}

	@Override
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
