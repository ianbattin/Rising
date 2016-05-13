package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import Main.GamePanel;
import TileMap.Background;

public class OutroState extends TransitionState
{
	private float timer;
	
	private int outroFrames;
	
	public OutroState(GameStateManager gsm, String path)
	{
		super(gsm, path);
	}
	
	public void init() 
	{
		super.isFadingIn = true;
		super.alphaLevel = 255;
		
		outroFrames+=1;
		if(outroFrames > 4)
			outroFrames = 1;
		System.out.println(outroFrames);
		super.setTotalFrames(outroFrames);
		
		timer = 0;
	}

	public void update() 
	{
		super.update();
		/*
		if(super.isFadingIn)
		{
			super.fadeIn(500000000.0, Color.BLACK, 5);
		}
		else if (timer > 2000000000 * (outroFrames+1))
		{
			super.isFadingOut = true;
			super.fadeOut(1000000000.0, Color.BLACK, 5, gsm, GameStateManager.OUTROSTATE, GameStateManager.MENUSTATE);
		}
		else
		{
			timer += GamePanel.getElapsedTime();
		}
		*/
	}

	public void draw(Graphics2D g) 
	{
		bg.draw(g);
		g.setColor(Color.LIGHT_GRAY);
		g.setFont(new Font("Munro", Font.BOLD, 30));
		g.drawString("Score: " + score, centerStringX("Score: " + score, 0, GamePanel.WIDTH, g), 500);
		super.drawFade(g);
	}

	public void keyPressed(int k) 
	{
		// TODO Auto-generated method stub
		
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
