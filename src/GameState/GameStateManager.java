package GameState;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Main.GamePanel;

public class GameStateManager 
{
	private static ArrayList<GameState> gameStates;
	private static int currentState;
	private String[] data;
	
	public static final int SPLASHSTATE = 0; //Splash screen
	public static final int MENUSTATE = 1; //Menu
	public static final int CONTROLSTATE = 2; //Lists controls
	public static final int CREDITSTATE = 3; //Show credits
	public static final int INTROSTATE = 4; //Planes crash/background story
	public static final int PLAYSTATE = 5; //Actually playing
	public static final int OUTROSTATE = 6; //Outro state
	
	
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
		gameStates.add(new IntroState(this));
		gameStates.add(new PlayState(this));
		gameStates.add(new OutroState(this));
		data = new String[gameStates.size()];
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
	
	public String getDataForState(int state)
	{
		return data[state];
	}
	
	//Resets a state by deleting and re adding a new version of a state (Decided it actually makes sense
	//to have this method in the GSM as the GSM is managing the states, which reseting is a part of
	public void resetState(int state)
	{
		setState(MENUSTATE);
		GameState stateAtPos = gameStates.get(state);
		gameStates.remove(state);
		if(stateAtPos instanceof SplashState) gameStates.add(state, new SplashState(this));
		else if(stateAtPos instanceof MenuState) gameStates.add(state, new MenuState(this));
		else if(stateAtPos instanceof ControlsState) gameStates.add(state, new ControlsState(this));
		else if(stateAtPos instanceof CreditState) gameStates.add(state, new CreditState(this));
		else if(stateAtPos instanceof IntroState) gameStates.add(state, new IntroState(this));
		else if(stateAtPos instanceof PlayState) gameStates.add(state, new PlayState(this));
		else if(stateAtPos instanceof OutroState) gameStates.add(state, new OutroState(this));
		data[state] = stateAtPos.data;
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
