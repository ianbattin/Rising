package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import Main.GamePanel;

public class OutroState extends TransitionState
{
	private final String[] retryText = {"Continue?", "", "Yes", "No"};
	private static int outroFrame = 0;
	private int thisAlphaLevel = 0;
	private static int lastLevel;
	private byte selection = 0;
	private boolean willFadeOut = false;
	
	private Font optionsFont = new Font("Munro", Font.BOLD, 24);
	private Font scoreFont = new Font("Munro", Font.BOLD, 32);
	
	public OutroState(GameStateManager gsm, String path)
	{
		super(gsm, path);
	}
	
	public void init() 
	{
		super.isFadingIn = true;
		super.alphaLevel = 255;
		
		outroFrame = (outroFrame%4)+1;
		bg.setNewImage("/Outro/frame" + outroFrame + ".gif");
	}

	public void update() 
	{
		super.update();
		if(!super.isFadingIn && super.isFadingOut && thisAlphaLevel < 255)
		{
			thisAlphaLevel += 1;
		}
		
		if(super.isFadingOut && willFadeOut)
		{
			if(selection == 0)
				super.fadeOut(1000000000.0, Color.BLACK, 5, gsm, GameStateManager.OUTROSTATE, OutroState.lastLevel);
			else
				super.fadeOut(1000000000.0, Color.BLACK, 5, gsm, GameStateManager.OUTROSTATE, GameStateManager.MENUSTATE);
		}
	}

	public void draw(Graphics2D g) 
	{
		bg.draw(g);
		
		g.setColor(new Color(0,0,0,thisAlphaLevel));
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		
		g.setColor(new Color(175, 175, 175, thisAlphaLevel));
		
		g.setFont(scoreFont);
		g.drawString("Score: " + score, centerStringX("Score: " + score, 0, GamePanel.WIDTH, g), 500);
		g.setFont(optionsFont);
		for(int i = 0; i < retryText.length; i++)
		{
			if(selection == i-2) g.setColor(new Color(200,150,50,thisAlphaLevel));
			else g.setColor(new Color(175,175,175,thisAlphaLevel));
			g.drawString(retryText[i], super.centerStringX(retryText[i], 0, GamePanel.WIDTH, g), 200 + i*35);
		}
		
		super.drawFade(g);
	}

	public void keyPressed(int k) 
	{
		if(thisAlphaLevel > 75)
		{
			if(k == GameStateManager.up)
			{
				if(selection == 1)
					selection = 0;
				else
					selection++;
			}
			if(k == GameStateManager.down)
			{
				if(selection == 0)
					selection = 1;
				else
					selection--;
			}
			if(k == GameStateManager.select)
			{
				willFadeOut = true;
			}
		}
		
	}

	public void setLevel(int lastLevel)
	{
		OutroState.lastLevel = lastLevel;
	}
	
	public void keyReleased(int k) 
	{
		// TODO Auto-generated method stub
		
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
