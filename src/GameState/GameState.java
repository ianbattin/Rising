package GameState;

import java.awt.Graphics2D;

import Main.GamePanel;

//Every game state will have each of these things. 
//Abstraction makes it more organized
public abstract class GameState
{
	protected GameStateManager gsm;
	public abstract void init();
	public abstract void update();
	public abstract void draw(Graphics2D g);
	public abstract void keyPressed(int k);
	public abstract void keyReleased(int k);
	
	//Centers string between the xPos and endPos x coordinates
	public static int centerStringX(String s, int xPos, int endPos, Graphics2D g)
	{
        int stringLen = (int)g.getFontMetrics().getStringBounds(s, g).getWidth();
        int width = endPos - xPos;
        int start = width/2 - stringLen/2;
        return start + xPos;
	}
}
