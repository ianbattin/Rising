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
import Main.SoundPlayer;
import TileMap.Background;
import TileMap.TileMap;

public class MenuState extends GameState 
{
	private static Background bg;
	private static Background topBg;
	
	private String[] options = {"Play", "Controls", "Credits", "Quit" };
	private int currentChoice = 0;
	
	private Font optionsFont;
	private int secondaryFadingAlphaLevel;
	
	public MenuState(GameStateManager gsm)
	{
		super();
		this.gsm = gsm;

		super.isFadingOut = false;
		super.alphaLevel = 0;
		
		secondaryFadingAlphaLevel = 0;
		optionsFont = new Font("Munro", Font.PLAIN, 24);
		
		//This is going to try to set the background from a certain file path
		//the bg takes a long time to init; and since it doesnt change we dont need to re-init it every time 
		if(bg == null || topBg == null)
		{
			bg = new Background("/Backgrounds/MenuBackground.png", 0);
			topBg = new Background("/Backgrounds/MenuBackgroundTop.png", 0);
		}
		
		
	}

	public void init() 
	{
		music("La Foule.wav");
	}

	//Only thing being updated is the background for movement
	public void update()
	{
		bg.update();
		topBg.update();
		
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
			super.fadeOut(4000000000.0, Color.BLACK, 3, gsm, GameStateManager.MENUSTATE, GameStateManager.TRANSITION_INTROSTATE);
			//top part of image fading
			if (secondaryFadingAlphaLevel < 255)
			{
				secondaryFadingAlphaLevel += 1;
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
			g.drawString(options[i], GamePanel.WIDTH/4 + 30 + i*(120)-(int)(g.getFontMetrics().getStringBounds(options[i], g).getWidth())/2, GamePanel.HEIGHT/2 + 80); //uses the i variable from the for loop to correctly position options on top of eachother
		}
		super.drawFade(g);
		
		if(super.isFadingOut)
		{
			topBg.draw(g);
			g.setColor(new Color(0,0,0,secondaryFadingAlphaLevel));
			g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		}
		
	}

	//Selects the current game state
	private void select()
	{
		SoundPlayer.playClip("select.wav");
	    if(currentChoice == 0)
		{
			super.isFadingOut = true;
			SoundPlayer.animVolume(-40.0F);
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
			gsm.setState(GameStateManager.TRANSITION_INTROSTATE);
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
			if(k == GameStateManager.up || k == GameStateManager.left)
			{
				SoundPlayer.playClip("changeselection.wav");
				currentChoice--;
				//Unless you reach the top in which case it loops back to the bottom
				if(currentChoice == -1)
				{
					currentChoice = options.length - 1;
				}
			}
			
			//If you press the down key, the selected option goes down
			if(k == GameStateManager.down || k == GameStateManager.right)
			{
				SoundPlayer.playClip("changeselection.wav");
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
