package TileMapEditor.TileMapEditor;

import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class App {
	
	public static void main(String[] args) {
		
		JFrame window = new JFrame("Tile Map Editor");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(new MyPanel());
		window.setResizable(false);
		window.setLocation(new Point(Toolkit.getDefaultToolkit().getScreenSize().width/2 - (int)(MyPanel.WIDTH * MyPanel.SCALE)/2, 
				Toolkit.getDefaultToolkit().getScreenSize().height/2 - (int)(MyPanel.HEIGHT * MyPanel.SCALE)/2));
		window.pack();
		window.setVisible(true);
		
	}
	
}