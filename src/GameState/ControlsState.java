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
	private Font titleFont, optionsFont, subTextFont;
	
	private int selection;
	
	private boolean isListeningToKey; //true if next key press will set controls
	
	private String[] movementTypes = {"Jump", "Drop", "Right", "Left", "Glide"};
	private String[] movementKeys = {"W", "S", "D", "A", "Space"};
	
	public ControlsState(GameStateManager gsm)
	{
		this.gsm = gsm;
		
		bg = new Background("/Backgrounds/menubackground.gif", 1);
		bg.setVector(0, -5.0); //moves the background
		
		selection = 0;
		isListeningToKey = false;
		
		titleColor = new Color(255, 60 ,0);
		titleFont = new Font("RussellSquare", Font.BOLD, 50);
		optionsFont = new Font("RusselSquare", Font.PLAIN, 24);
		subTextFont = new Font("RusselSquare", Font.PLAIN, 20);
		
		
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
			
			if (i == selection){
				
				g.setColor(new Color(255,150,0));
				
			} else {
				
				g.setColor(Color.WHITE);
				
			}
			
			g.drawString(movementKeys[i],  GamePanel.WIDTH/2, GamePanel.HEIGHT/2+(i*40)-75);
			g.setColor(Color.WHITE);
			g.drawString(movementTypes[i], GamePanel.WIDTH/2-80, GamePanel.HEIGHT/2+(i*40)-75);
			
			
		}
		
		if (selection == movementTypes.length){
			g.setColor(new Color(255,150,0));
		} else {
			g.setColor(Color.WHITE);
		}
		g.setFont(subTextFont);
		g.drawString("Return to Menu", GamePanel.WIDTH/3+20, GamePanel.HEIGHT-100);
		
		
	}
	
	//when you select something, it changes to press so that you can set the controls
	public void selection(){
		
		if (selection == movementTypes.length){
			gsm.setState(GameStateManager.MENUSTATE);
		} else {
			for (int i = 0; i < movementTypes.length; i++){
				
				if (selection == i && !movementKeys[i].equals(">>> Press <<<")){
					movementKeys[i] = ">>> Press <<<";
					isListeningToKey = true;
				} 
				
			}
			
		}
		
	}
	
	//when the program is listening to the next keystroke, it will run this to set the key
	public void setKey(int k){
		
		for (int i = 0; i < movementTypes.length; i++){
			
			if (selection == i && movementKeys[i].equals(">>> Press <<<")){
				movementKeys[i] = KeyEvent.getKeyText(k);
				isListeningToKey = false;
				if (i == 0) GameStateManager.UP = k;
				else if (i == 1) GameStateManager.DOWN = k;
				else if (i == 2) GameStateManager.RIGHT = k;
				else if (i == 3) GameStateManager.LEFT = k;
				else if (i == 4) GameStateManager.GLIDE = k;
				
			} 
			
		}
		
	}

	@Override
	public void keyPressed(int k) {
		// TODO Auto-generated method stub
		
		if(!isListeningToKey){
			
			if(k == KeyEvent.VK_ENTER)
			{
				this.selection();
				
			} else if (k == GameStateManager.UP){
				
				selection--;
				
				if (selection < 0){
					
					selection = movementTypes.length;
					
				}
				
			} else if (k == GameStateManager.DOWN){
				
				selection++;
				
				if (selection == movementTypes.length+1){
					
					selection = 0;
					
				}
				
			}
		} else {
			
			this.setKey(k);
			
		}
		
		
	}

	@Override
	public void keyReleased(int k) {
		// TODO Auto-generated method stub

	}

}
