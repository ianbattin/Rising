package GameState;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import Main.GamePanel;
import TileMap.Background;

public class ControlsState extends GameState {
	
	private Background bgUpperHalf, bgLowerHalf;	
	private GameStateManager gsm;

	private Color titleColor;
	private Font titleFont, optionsFont, subTextFont, backupFont;
	
	private int selection;
	private int resSelection;
	private boolean isListeningToKey; //true if next key press will set controls
	private boolean isChangingResolution;
	private double newScale;
	private double[] resOption = {0.7, 0.8, 0.9, 1.0};
	
	private String[] movementTypes = {"Jump:", "Drop:", "Right:", "Left:", "Glide:", "Select:", "Reset:", "Action:"};
	private String[] movementKeys = {KeyEvent.getKeyText(GameStateManager.up), KeyEvent.getKeyText(GameStateManager.down), KeyEvent.getKeyText(GameStateManager.right), 
			KeyEvent.getKeyText(GameStateManager.left), KeyEvent.getKeyText(GameStateManager.glide), KeyEvent.getKeyText(GameStateManager.select), KeyEvent.getKeyText(GameStateManager.reset), KeyEvent.getKeyText(GameStateManager.action)};

	
	public ControlsState(GameStateManager gsm)
	{
		super();
		this.gsm = gsm;
		
		bgLowerHalf = new Background("/Backgrounds/MenuBackgroundTop.png", 1);
		bgUpperHalf = new Background("/Backgrounds/LowerBackground.png", 1);	
		
		selection = 0;
		resSelection = 0;
		newScale = GamePanel.scaleHeight;
		isListeningToKey = false;
		isChangingResolution = false;
		
		titleColor = new Color(255, 60 ,0);
		backupFont = new Font("Times", Font.PLAIN, 24);
		titleFont = new Font("Munro", Font.BOLD, 40);
		optionsFont = new Font("Munro", Font.PLAIN, 24);
		subTextFont = new Font("Munro", Font.PLAIN, 20);
	}
	
	//nothing to init
	public void init() 
	{
	}

	//nothing to update
	public void update() 
	{
		bgLowerHalf.update();
		bgUpperHalf.update();
	}

	//draw the controls
	public void draw(Graphics2D g) 
	{
		bgUpperHalf.draw(g); //draw the background
		bgLowerHalf.draw(g);
		
		//set the style of the title of the page
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("CONTROLS", centerStringX("CONTROLS", 0, GamePanel.WIDTH, g), GamePanel.HEIGHT/4);
		
		g.setColor(Color.WHITE);
		g.setFont(optionsFont);
		
		for (int i = 0; i < movementTypes.length; i++)
		{
			if (i == selection)
				g.setColor(new Color(255,150,0));
			else 
				g.setColor(Color.WHITE);
			
			//TODO Figure out how to apply the centerStringX() method to this?
			if(movementKeys[i].length() == 1 && !titleFont.canDisplay(movementKeys[i].toCharArray()[0]))
			{
				g.setFont(backupFont);
			}
			g.drawString(movementKeys[i],  GamePanel.WIDTH/2+30, 50+GamePanel.HEIGHT/4+(i*40));
			g.setFont(optionsFont);
			g.setColor(Color.WHITE);
			g.drawString(movementTypes[i], GamePanel.WIDTH/2-70, 50+GamePanel.HEIGHT/4+(i*40));
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
		if (isChangingResolution)
		{
			g.setColor(Color.WHITE);
			g.drawString("Change Display Settings: ", centerStringX("Change Display Settings: " + (int)(GamePanel.WIDTH*newScale) + "x" + (int)(GamePanel.WIDTH*newScale), 0, GamePanel.WIDTH, g), GamePanel.HEIGHT-200);
			g.setColor(new Color(255,150,0));
			
			for(int i = 0; i < resOption.length; i++)
			{
				if(resSelection == i)
					g.setColor(new Color(255,150,0));
				else if (resOption[i] == GamePanel.scaleHeight)
					g.setColor(Color.ORANGE);
				else
					g.setColor(Color.WHITE);
				g.drawString((int)(GamePanel.WIDTH*resOption[i]) + "x" + (int)(GamePanel.WIDTH*resOption[i]), centerStringX("Change Display Settings: 800x800", 0, GamePanel.WIDTH, g) + (int)g.getFontMetrics().getStringBounds("Change Display Settings:  ", g).getWidth(), GamePanel.HEIGHT-230+(30*i));
			}
		}
		else
		{
			g.drawString("Change Display Settings: " + (int)(GamePanel.WIDTH*newScale) + "x" + (int)(GamePanel.WIDTH*newScale), centerStringX("Change Display Settings: " + (int)(GamePanel.WIDTH*newScale) + "x" + (int)(GamePanel.WIDTH*newScale), 0, GamePanel.WIDTH, g), GamePanel.HEIGHT-200);
		}
		if (selection == movementTypes.length + 1)
		{
			g.setColor(new Color(255,150,0));
		}
		else 
		{
			g.setColor(Color.WHITE);
		}
		
		g.drawString("Return to Menu", centerStringX("Return to Menu", 0, GamePanel.WIDTH, g), GamePanel.HEIGHT-100);
	}
	
	//when you select something, it changes to press so that you can set the controls
	public void selection()
	{
		if (selection == movementTypes.length)
		{
			
			if(isChangingResolution && resOption[resSelection] != GamePanel.scaleHeight)
			{
				GamePanel.setNewSize(resOption[resSelection]);
			}
			else
			{
				isChangingResolution = !isChangingResolution;
			}
		} 
		else if (selection == movementTypes.length + 1)
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
		if(!isListeningToKey && !isChangingResolution)
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
					selection = movementTypes.length+1;
				}
			} 
			else if (k == GameStateManager.down)
			{
				selection++;
				if (selection == movementTypes.length+2)
				{
					selection = 0;
				}
			}
		} 
		else if(isListeningToKey)
		{
			this.setKey(k);
		}
		else if(isChangingResolution)
		{
			if(k == GameStateManager.select)
			{
				this.selection();
			} 
			else if (k == GameStateManager.up)
			{
				resSelection--;
				if (resSelection < 0)
				{
					resSelection = resOption.length-1;
				}
			} 
			else if (k == GameStateManager.down)
			{
				resSelection++;
				if (resSelection == resOption.length)
				{
					resSelection = 0;
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
