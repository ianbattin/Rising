package Main;

import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class Main 
{
	public static void main(String[] args)
	{
		JFrame window = new JFrame("Rising"); //creates a new window with title Rising
		window.setContentPane(new GamePanel()); //the content in the window is set to the GamePanel code
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exits the program when you hit the X
		window.setResizable(false); // they can't drag to resize - would screw up textures
		window.setLocation(new Point(Toolkit.getDefaultToolkit().getScreenSize().width/2 - GamePanel.WIDTH/2, 
				Toolkit.getDefaultToolkit().getScreenSize().height/2 - GamePanel.HEIGHT/2)); //creates this new window in the center of the users screen
		window.pack(); //sets the size of the content to fit the window
		window.setVisible(true); //spawns the window on top of other windows
	}
}