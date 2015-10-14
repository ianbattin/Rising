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

	private String[] movementTypes = {"Jump:", "Drop:", "Right:", "Left:", "Glide:", "Select:", "Reset:"};
	private String[] movementKeys = {KeyEvent.getKeyText(GameStateManager.up), KeyEvent.getKeyText(GameStateManager.down), KeyEvent.getKeyText(GameStateManager.right), 
			KeyEvent.getKeyText(GameStateManager.left), KeyEvent.getKeyText(GameStateManager.glide), KeyEvent.getKeyText(GameStateManager.select), KeyEvent.getKeyText(GameStateManager.reset)};

	
	public ControlsState(GameStateManager gsm)
	{
		this.gsm = gsm;
		
		bg = new Background("/Backgrounds/menubackground.gif", 1);
		bg.setVector(0, -5.0); //moves the background
		
		selection = 0;
		isListeningToKey = false;
		
		titleColor = new Color(255, 60 ,0);
		titleFont = new Font("RussellSquare", Font.BOLD, 40);
		optionsFont = new Font("RusselSquare", Font.PLAIN, 24);
		subTextFont = new Font("RusselSquare", Font.PLAIN, 20);
	}
	
	//nothing to init
	public void init() 
	{
	}

	//nothing to update
	public void update() 
	{
		bg.update();
	}

	//draw the controls
	public void draw(Graphics2D g) 
	{
		bg.draw(g); //draw the background
		
		//set the style of the title of the page
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("CONTROLS", GamePanel.centerStringX("CONTROLS", 0, 600), GamePanel.HEIGHT/4);
		
		g.setColor(Color.WHITE);
		g.setFont(optionsFont);
		
		for (int i = 0; i < movementTypes.length; i++)
		{
			if (i == selection)
				g.setColor(new Color(255,150,0));
			else 
				g.setColor(Color.WHITE);
			
			//TODO Figure out how to apply the centerStringX() method to this?
			g.drawString(movementKeys[i],  GamePanel.WIDTH/2+20, 50+GamePanel.HEIGHT/4+(i*40));
			g.setColor(Color.WHITE);
			g.drawString(movementTypes[i], GamePanel.WIDTH/2-60, 50+GamePanel.HEIGHT/4+(i*40));
		}
		
		if (selection == movementTypes.length)
		{
			g.setColor(new Color(255,150,0));
		}
		else 
		{
			g.setColor(Color.WHITE);
		}
		
		g.setFont(subTextFont);
		g.drawString("Return to Menu", GamePanel.centerStringX("Return to Menu", 0, 600), GamePanel.HEIGHT-100);
	}
	
	//when you select something, it changes to press so that you can set the controls
	public void selection()
	{
		if (selection == movementTypes.length)
		{
			gsm.setState(GameStateManager.MENUSTATE);
		} 
		else 
		{
			for (int i = 0; i < movementTypes.length; i++)
			{
				if (selection == i && !movementKeys[i].equals(">>> Press <<<"))
				{
					movementKeys[i] = ">>> Press <<<";
					isListeningToKey = true;
				} 
			}
		}
	}
	
	//when the program is listening to the next keystroke, it will run this to set the key. 
	//Also ensures that you do not have a single key for multiple items
	public void setKey(int k){
		for (int i = 0; i < movementTypes.length; i++)
		{
			if (selection == i && movementKeys[i].equals(">>> Press <<<"))
			{
				isListeningToKey = false;
				for (int j = 0; j < movementKeys.length; j++)
				{
					if (KeyEvent.getKeyText(k).equals(movementKeys[j]) && selection != j)
					{
						selection = j;
						movementKeys[j] = ">>> Press <<<";
						isListeningToKey = true;
					}
				}
				movementKeys[i] = KeyEvent.getKeyText(k);
				if (i == 0) GameStateManager.up = k;
				else if (i == 1) GameStateManager.down = k;
				else if (i == 2) GameStateManager.right = k;
				else if (i == 3) GameStateManager.left = k;
				else if (i == 4) GameStateManager.glide = k;
				else if (i == 5) GameStateManager.select = k;
				else if (i == 6) GameStateManager.reset = k;
				break;
			} 
		}
	}

	public void keyPressed(int k) 
	{
		if(!isListeningToKey)
		{
			if(k == GameStateManager.select)
			{
				this.selection();
			} 
			else if (k == GameStateManager.up)
			{
				selection--;
				if (selection < 0)
				{
					selection = movementTypes.length;
				}
			} 
			else if (k == GameStateManager.down)
			{
				selection++;
				if (selection == movementTypes.length+1)
				{
					selection = 0;
				}
			}
		} 
		else 
		{
			this.setKey(k);
		}
	}

	public void keyReleased(int k) 
	{
	}

}
