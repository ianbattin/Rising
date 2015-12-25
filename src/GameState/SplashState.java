package GameState;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import Main.GamePanel;
import TileMap.Background;

public class SplashState extends GameState 
{
	private Background bg;
	long elapsedTime;
	
	public SplashState(GameStateManager gsm)
	{
		super();
		init();
		this.gsm = gsm;
		
		elapsedTime = 0;
		try
		{
			bg = new Background("/Backgrounds/splashscreen.jpg", 1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void init() 
	{
		music("Modero.wav");
	}


	public void update() 
	{
		elapsedTime += GamePanel.getElapsedTime();
		if(elapsedTime < 200000000.0) 
		{
			bg.update(); 
		}
		else
		{	
			gsm.gameStateManagerLoad();
			gsm.setState(GameStateManager.MENUSTATE);
		}
	}

	public void draw(Graphics2D g) 
	{

		bg.draw(g); 
		
	}
	public void keyPressed(int k) 
	{
		
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
