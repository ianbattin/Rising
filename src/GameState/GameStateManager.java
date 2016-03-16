package GameState;

import Entities.Player;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Main.GamePanel;

public class GameStateManager 
{
	private static ArrayList<GameState> gameStates;
	private static int currentState;
	
	public static final int SPLASHSTATE = 0; //Splash screen
	public static final int MENUSTATE = 1; //Menu
	public static final int CONTROLSTATE = 2; //Lists controls
	public static final int CREDITSTATE = 3; //Show credits
	public static final int TRANSITION_INTROSTATE = 4; //Planes crash/background story
	public static final int LEVEL1STATE = 5; //Actually playing
	public static final int TRANSITION_OUTROSTATE = 6;
	public static final int BOSS1STATE = 7;
	//public static final int LEVEL2STATE = 8;
	//public static final int BOSS2STATE = 9;
	//public static final int LEVEL3STATE = 10;
	//public static final int BOSS3STATE = 11;
	public static final int OUTROSTATE = 8; //Outro state - must be greater than the last state (boss3state)
	
	
	//these are the controls. Need to be set up here so that they are accessible gamewide.
	//TODO: is public the right way to go? or do i need setter and getters for each?
	public static int up = KeyEvent.VK_W;
	public static int down = KeyEvent.VK_S;
	public static int right = KeyEvent.VK_D;
	public static int left = KeyEvent.VK_A;
	public static int glide = KeyEvent.VK_SPACE;
	public static int select = KeyEvent.VK_ENTER;
	public static int reset = KeyEvent.VK_BACK_SPACE;
	public static int action = KeyEvent.VK_E;
	public static int pause = KeyEvent.VK_ESCAPE;


	//Constructor, adds all the gamestates to this gamestate arraylist
	public GameStateManager()
	{
		gameStates = new ArrayList<GameState>();
		currentState = SPLASHSTATE;
		gameStates.add(new SplashState(this));		

	}
	
	//this method to be called by splashscreen state so that it can offload all tasks until after the splashscreen has displayed
	public void gameStateManagerLoad()
	{
		gameStates.add(new MenuState(this));
		gameStates.add(new ControlsState(this));
		gameStates.add(new CreditState(this));
		gameStates.add(new TransitionState(this, "Intro"));
		gameStates.add(new Level1State(this));
		gameStates.add(new TransitionState(this, "Outro"));
		gameStates.add(new Boss1State(this));
		gameStates.add(new OutroState(this, "Outro"));
	}
	
	public void setState(int state)
	{
		gameStates.get(state).init();
		gameStates.get(state).update();
		currentState = state;
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
	
	public GameState getState()
	{
		return gameStates.get(4);
	}
	
	public void setCurrentState(int state)
	{
		currentState = state;
	}
	
	//Resets a state by deleting and re adding a new version of a state (Decided it actually makes sense
	//to have this method in the GSM as the GSM is managing the states, which reseting is a part of
	public void resetState(int state)
	{
		GameState stateAtPos = gameStates.get(state);
		if(stateAtPos instanceof SplashState) gameStates.set(state, new SplashState(this));
		else if(stateAtPos instanceof MenuState) gameStates.set(state, new MenuState(this));
		else if(stateAtPos instanceof ControlsState) gameStates.set(state, new ControlsState(this));
		else if(stateAtPos instanceof CreditState) gameStates.set(state, new CreditState(this));
		else if(stateAtPos instanceof TransitionState && state == GameStateManager.TRANSITION_INTROSTATE) gameStates.set(state, new TransitionState(this, "Intro"));
		else if(stateAtPos instanceof TransitionState && state == GameStateManager.TRANSITION_OUTROSTATE) gameStates.set(state, new TransitionState(this, "Outro"));
		else if(stateAtPos instanceof Level1State) gameStates.set(state, new Level1State(this));
		else if(stateAtPos instanceof Boss1State) gameStates.set(state, new Boss1State(this));
		else if(stateAtPos instanceof OutroState) gameStates.set(state, new OutroState(this, "Outro"));
	}
	
	public void keyPressed(int k)
	{
		gameStates.get(currentState).keyPressed(k);
	}
	
	public void keyReleased(int k)
	{
		gameStates.get(currentState).keyReleased(k);
	}

	public void mouseClicked(MouseEvent e) 
	{
		gameStates.get(currentState).mouseClicked(e);
	}

	public void mouseEntered(MouseEvent e) 
	{
		gameStates.get(currentState).mouseEntered(e);
	}

	public void mouseExited(MouseEvent e) 
	{
		gameStates.get(currentState).mouseExited(e);
	}

	public void mousePressed(MouseEvent e) 
	{
		gameStates.get(currentState).mousePressed(e);
	}

	public void mouseReleased(MouseEvent e) 
	{
		gameStates.get(currentState).mouseReleased(e);
	}

	public void mouseDragged(MouseEvent e) 
	{
		gameStates.get(currentState).mouseDragged(e);
	}

	public void mouseMoved(MouseEvent e) 
	{
		gameStates.get(currentState).mouseMoved(e);
	}
}
