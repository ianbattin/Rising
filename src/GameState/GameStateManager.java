package GameState;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GameStateManager 
{
	private static ArrayList<GameState> gameStates;
	private static int currentState;
	
	public static final int SPLASHSTATE = 0; //Splash screen
	public static final int MENUSTATE = 1; //Menu
	public static final int CONTROLSTATE = 2; //Lists controls
	public static final int CREDITSTATE = 3; //Show credits
	public static final int INTROSTATE = 4; //Planes crash/background story
	public static final int PLAYSTATE = 5; //Actually playing
	
	
	//these are the controls. Need to be set up here so that they are accessible gamewide.
	//TODO: is public the right way to go? or do i need setter and getters for each?
	public static int UP = KeyEvent.VK_W;
	public static int DOWN = KeyEvent.VK_S;
	public static int RIGHT = KeyEvent.VK_D;
	public static int LEFT = KeyEvent.VK_A;
	public static int GLIDE = KeyEvent.VK_SPACE;
	

	//Constructor, adds all the gamestates to this gamestate arraylist
	public GameStateManager()
	{
		gameStates = new ArrayList<GameState>();
		currentState = SPLASHSTATE;
		gameStates.add(new SplashState(this));
		gameStates.add(new MenuState(this));
		gameStates.add(new ControlsState(this));
		gameStates.add(new CreditState(this));
		gameStates.add(new IntroState(this));
		gameStates.add(new PlayState(this));

	}
	
	public void setState(int state)
	{
		currentState = state;
		gameStates.get(currentState).init();
	}
	
	public void update()
	{
		gameStates.get(currentState).update();
	}
	
	public void draw(Graphics2D g)
	{
		gameStates.get(currentState).draw(g);
	}
	
	public int getCurrentState()
	{
		return currentState;
	}
	
	public void setCurrentState(int state)
	{
		currentState = state;
	}
	
	public void keyPressed(int k)
	{
		gameStates.get(currentState).keyPressed(k);
	}
	
	public void keyReleased(int k)
	{
		gameStates.get(currentState).keyReleased(k);
	}
}
