package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import Main.GamePanel;
import TileMap.Background;

public class OutroState extends GameState{

	private Background bg;
	private GameStateManager gsm;
	
	private String points;
	private float timer;
	
	public OutroState(GameStateManager gsm)
	{
		this.gsm = gsm;
		
		bg = new Background("/Outro/gameOver.gif", 1);
	}
	
	public void init() 
	{
		super.isFadingIn = true;
		super.alphaLevel = 255;
		
		timer = 0;
		points = gsm.getDataForState(GameStateManager.PLAYSTATE);
	}

	public void update() 
	{
		if(super.isFadingIn)
		{
			super.fadeIn(500000000.0);
		}
		else if (timer > 1000000000)
		{
			super.isFadingOut = true;
			super.fadeOut(1000000000.0, gsm, GameStateManager.OUTROSTATE, GameStateManager.MENUSTATE);
		}
		else
		{
			timer += GamePanel.getElapsedTime();
		}
	}

	public void draw(Graphics2D g) 
	{
		bg.draw(g);
		g.setColor(Color.WHITE);
		g.setFont(new Font("RusselSquare", Font.PLAIN, 24));
		g.drawString("Score: " + points, centerStringX("Score: " + points, 0, GamePanel.WIDTH, g), 480);
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
