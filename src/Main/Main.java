package Main;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Main 
{
	public static JFrame window;
	
	public static void main(String[] args)
	{
		window = new JFrame("Meet Me In Paris"); //creates a new window with title Meet me in Paris
		window.setContentPane(new GamePanel()); //the content in the window is set to the GamePanel code
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exits the program when you hit the X
		window.setResizable(false); // they can't drag to resize - would screw up textures
		window.setLocation(new Point(Toolkit.getDefaultToolkit().getScreenSize().width/2 - GamePanel.WIDTHSCALED/2, 
				Toolkit.getDefaultToolkit().getScreenSize().height/2 - GamePanel.HEIGHTSCALED/2)); //creates this new window in the center of the users screen
		//window.setUndecorated(true); //get rids of the windows border
		window.pack(); //sets the size of the content to fit the window
		window.setVisible(true); //spawns the window on top of other windows
		window.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR), new Point(), "invisCursor"));
	}
}
