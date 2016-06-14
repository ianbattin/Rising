package GameState;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import Main.GamePanel;
import Main.SoundPlayer;
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
			bg = new Background("/Backgrounds/splashscreen.JPG", 1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void init() 
	{
		SoundPlayer.playClip("InvoxLogo.wav");
	}


	public void update() 
	{
		elapsedTime += GamePanel.getElapsedTime();
		if(elapsedTime > 50000000.0) //time to wait before loading 
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
