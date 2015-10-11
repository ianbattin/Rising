package GameState;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class GameStateManager 
{
	private static ArrayList<GameState> gameStates;
	private static int currentState;
	
	private static final int MENUSTATE = 0; //Menu
	private static final int CONTROLSSTATE = 1; //Lists controls
	private static final int INTROSTATE = 2; //Planes crash/background story
	private static final int PLAYSTATE = 3; //Actually playing

	//Constructor, adds all the gamestates to this gamestate arraylist
	public GameStateManager()
	{
		gameStates = new ArrayList<GameState>();
		currentState = MENUSTATE;
		gameStates.add(new MenuState(this));
		//gameStates.add(new ControlsState(this));
		//gameStates.add(new IntroState(this));
		//gameStates.add(new PlayState(this));
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
