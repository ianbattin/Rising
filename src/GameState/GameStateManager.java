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
	
	private Player player;
	
	public static final int SPLASHSTATE = 0; //Splash screen
	public static final int MENUSTATE = 1; //Menu
	public static final int CONTROLSTATE = 2; //Lists controls
	public static final int CREDITSTATE = 3; //Show credits
	public static final int INTROSTATE = 4; //Planes crash/background story
	public static final int LEVEL1STATE = 5; //Actually playing
	public static final int TRANSITION1STATE = 6;
	public static final int BOSS1STATE = 7;
	//public static final int LEVEL2STATE = 7;
	//public static final int BOSS2STATE = 8;
	//public static final int LEVEL3STATE = 9;
	//public static final int BOSS3STATE = 10;
	public static final int OUTROSTATE = 8; //Outro state
	
	
	//these are the controls. Need to be set up here so that they are accessible gamewide.
	//TODO: is public the right way to go? or do i need setter and getters for each?
	public static int up = KeyEvent.VK_W;
	public static int down = KeyEvent.VK_S;
	public static int right = KeyEvent.VK_D;
	public static int left = KeyEvent.VK_A;
	public static int glide = KeyEvent.VK_SPACE;
	public static int select = KeyEvent.VK_ENTER;
	public static int reset = KeyEvent.VK_BACK_SPACE;
	public static int action = KeyEvent.VK_SHIFT;
	public static int pause = KeyEvent.VK_ESCAPE;
	public static int shootUp = KeyEvent.VK_UP;
	public static int shootDown = KeyEvent.VK_DOWN;
	public static int shootLeft = KeyEvent.VK_LEFT;
	public static int shootRight = KeyEvent.VK_RIGHT;
	

	//Constructor, adds all the gamestates to this gamestate arraylist
	public GameStateManager()
	{
		gameStates = new ArrayList<GameState>();
		currentState = SPLASHSTATE;
		gameStates.add(new SplashState(this));
		gameStates.add(new MenuState(this));
		gameStates.add(new ControlsState(this));
		gameStates.add(new CreditState(this));
		gameStates.add(new TransitionState(this, "Intro"));
		gameStates.add(new Level1State(this));
		gameStates.add(new TransitionState(this, "Outro"));
		gameStates.add(new Boss1State(this));
		gameStates.add(new OutroState(this));
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
	
	public void setCurrentState(int state)
	{
		currentState = state;
	}
	
	//Resets a state by deleting and re adding a new version of a state (Decided it actually makes sense
	//to have this method in the GSM as the GSM is managing the states, which reseting is a part of
	public void resetState(int state)
	{
		GameState stateAtPos = gameStates.get(state);
		gameStates.remove(state);
		if(stateAtPos instanceof SplashState) gameStates.add(state, new SplashState(this));
		else if(stateAtPos instanceof MenuState) gameStates.add(state, new MenuState(this));
		else if(stateAtPos instanceof ControlsState) gameStates.add(state, new ControlsState(this));
		else if(stateAtPos instanceof CreditState) gameStates.add(state, new CreditState(this));
		else if(stateAtPos instanceof TransitionState) gameStates.add(state, new TransitionState(this, "Intro"));
		else if(stateAtPos instanceof Level1State) gameStates.add(state, new Level1State(this));
		else if(stateAtPos instanceof Boss1State) gameStates.add(state, new Boss1State(this));
		else if(stateAtPos instanceof OutroState) gameStates.add(state, new OutroState(this));
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
