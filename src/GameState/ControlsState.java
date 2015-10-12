package GameState;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import Main.GamePanel;
import TileMap.Background;

public class ControlsState extends GameState {
	
	private Background bg;	
	private GameStateManager gsm;

	private Color titleColor;
	private Font titleFont;
	private Font optionsFont;
	
	private String[] movementTypes = {"Jump", "Drop", "Right", "Left", "Glide"};
	private String[] movementKeys = {"W", "S", "D", "A", "Space"};
	
	public ControlsState(GameStateManager gsm)
	{
		this.gsm = gsm;
		
		bg = new Background("/Backgrounds/menubackground.gif", 1);
		bg.setVector(0, -10.0); //moves the background
		
		titleColor = new Color(255, 60 ,0);
		titleFont = new Font("RussellSquare", Font.BOLD, 50);
		optionsFont = new Font("RusselSquare", Font.PLAIN, 24);
		
		
	}
	
	//nothing to init
	public void init() {
		// TODO Auto-generated method stub

	}

	//nothing to update
	public void update() {
		// TODO Auto-generated method stub
		
		bg.update();
		
	}

	//draw the controls. Will create the way you can actually edit the controls later.
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		
		bg.draw(g); //draw the background
		
		//set the style of the title of the page
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("CONTROLS", GamePanel.WIDTH/4, GamePanel.HEIGHT/4);
		
		g.setColor(Color.WHITE);
		g.setFont(optionsFont);
		
		for (int i = 0; i < movementTypes.length; i++){
			
			g.drawString(movementTypes[i], GamePanel.WIDTH/2-60, 50+GamePanel.HEIGHT/2-(i*40));
			g.drawString(movementKeys[i],  GamePanel.WIDTH/2+20, 50+GamePanel.HEIGHT/2-(i*40));
			
		}
		
		
	}

	@Override
	public void keyPressed(int k) {
		// TODO Auto-generated method stub
		
		//click enter to escape the controls
		if(k == KeyEvent.VK_ENTER)
		{
			gsm.setState(GameStateManager.MENUSTATE);
		}
		
	}

	@Override
	public void keyReleased(int k) {
		// TODO Auto-generated method stub

	}

}
