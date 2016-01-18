package TileMapEditor.TileMapEditor;

import java.io.*;
import javax.swing.*;

import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;


public class MyPanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	
	public static final int WIDTH = 1601;
	public static final int HEIGHT = 801;
	public static final double SCALE = Math.min(((Toolkit.getDefaultToolkit().getScreenSize().height-50.0)/HEIGHT), 1);
	public static int TILESIZE = 25;
	public static int numRows;
	public static int numCols;
	
	private String path = "Resources/Sprites/Tiles/";
	private BufferedImage tileset;
	private BufferedImage[][] tiles;
	private int numTiles;
	
	private Thread thread;
	private int FPS = 30;
	private long targetTime = 1000 / FPS;
	
	private BufferedImage image;
	private Graphics2D g;
	
	private Block[] blocks;
	private BufferedImage currentBlockImage;
	private int currentBlock;
	
	private String name = "forest1";
	private int[][] map;
	private int mapWidth;
	private int mapHeight;
	
	private int xmap;
	private int ymap;
	private boolean shiftDown;
	private int mmx;
	private int mmy;
	private int xblock;
	private boolean ctrlDown;
	private boolean altDown;
	
	private int mousex;
	private int mousey;
	private int tilex;
	private int tiley;
	
	public MyPanel() {
		super();
		setPreferredSize(new Dimension((int)(WIDTH * SCALE), (int)(HEIGHT * SCALE)));
		setFocusable(true);
		requestFocus();
	}
	
	public void addNotify() {
		super.addNotify();
		if(thread == null) {
			thread = new Thread(this);
			thread.start();
		}
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	
	public void run() {
		
		init();
		
		long start;
		long elapsed;
		long wait;
		
		while(true) {
			
			start = System.nanoTime();
			
			update();
			render();
			draw();
			
			elapsed = (System.nanoTime() - start) / 1000000;
			wait = targetTime - elapsed;
			if(wait < 0) wait = 10;
			try {
				Thread.sleep(wait);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void init() {
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		
		mapWidth = 32;
		mapHeight = 32;
		map = new int[mapHeight][mapWidth];
		String str = JOptionPane.showInputDialog(null, "Input name of Tileset File (Include file extension)", "Tile Set", 1);
		path += str;
		
		try {
			tileset = ImageIO.read(new File(path));
			numRows = tileset.getHeight() / TILESIZE;
			numCols = tileset.getWidth() / TILESIZE;
			numTiles = numCols * numRows;
			tiles = new BufferedImage[numRows][numCols];
			for(int i = 0; i < numCols; i++) {
				for(int j = 0; j < numRows; j++) {
					tiles[j][i] = tileset.getSubimage(
							TILESIZE * i,
							TILESIZE * j,
							TILESIZE,
							TILESIZE
							);
				}
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		// create clickable blocks
		blocks = new Block[numTiles];
		int width = numTiles / numRows;
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < numRows; j++) {
				blocks[i + width*j] = new Block(tiles[j][i]);
				blocks[i + width*j].setPosition(i * TILESIZE, (j) * TILESIZE);
			}
		}
	}
	
	private void loadTileSet(String s) {
		
		System.out.println("LOADING " + s);
		
		try {
			
			tileset = ImageIO.read(new File(s));
			TILESIZE = tileset.getHeight() / numRows;
			int width = tileset.getWidth() / TILESIZE;
			numTiles = width * numRows;
			tiles = new BufferedImage[numRows][width];
			for(int i = 0; i < width; i++) {
				for(int j = 0; j < numRows; j++) {
					tiles[j][i] = tileset.getSubimage(
							TILESIZE * i,
							TILESIZE * j,
							TILESIZE,
							TILESIZE
							);
				}
			}
			
			blocks = new Block[numTiles];
			for(int i = 0; i < width; i++) {
				for(int j = 0; j < numRows; j++) {
					blocks[i + width*j] = new Block(tiles[j][i]);
					blocks[i + width*j].setPosition(i * TILESIZE, HEIGHT - (numRows - j) * TILESIZE);
				}
			}
			
		}
		catch(Exception e) {
			System.out.println("Couldn't load " + s);
			e.printStackTrace();
		}
		
	}
	
	private void update() {
	}
	
	private void render() {
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		// draw map
		for(int row = 0; row < mapHeight; row++) {
			for(int col = 0; col < mapWidth; col++) {
				try {
					g.drawImage(
						blocks[map[row][col]].getImage(),
						col * TILESIZE + xmap,
						row * TILESIZE + ymap,
						null
					);
				}
				catch(Exception e) {}
			}
		}
		
		// draw map dimensions
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(5));
		g.drawRect(xmap, ymap, mapWidth * TILESIZE, mapHeight * TILESIZE);
		g.setStroke(new BasicStroke(1));

		// draw map border
		//g.setColor(Color.GREEN);
		//g.drawRect(xmap + 10 * TILESIZE, ymap + 7 * TILESIZE, (mapWidth - 20) * TILESIZE, (mapHeight - 14) * TILESIZE);
		
		// draw clickable blocks
		int bh = 0;
		int bw = WIDTH/2;
		g.setColor(Color.WHITE);
		g.fillRect(bw, bh, WIDTH/2, HEIGHT);
		for(int i = 0; i < numTiles; i++) {
			blocks[i].draw(g);
		}
		
		// draw current block
		g.setColor(Color.RED);
		int width = numTiles / numRows;
		try {
			g.drawRect((currentBlock % width) * TILESIZE + xblock + bw, bh + TILESIZE * (currentBlock / width), TILESIZE, TILESIZE);
		} catch(Exception e) {}
		
		// draw current block number
		g.drawString("Current Tile: " + currentBlock, WIDTH/2 - 100, 80);
		
		// draw position
		g.setColor(Color.RED);
		g.drawString("Pixel: " + mousex + ", " + mousey, WIDTH/2 - 100, 20);
		g.drawString("Tile: " + tilex + ", " + tiley, WIDTH/2 - 100, 35);
		
		// draw map size in tiles
		g.setColor(Color.RED);
		g.drawString("Map Width: " + mapWidth, WIDTH/2 - 100, 50);
		g.drawString("Map Height: " + mapHeight, WIDTH/2 - 100, 65);
		
		//draw tileset border
		g.setColor(Color.RED);
		g.drawRect(WIDTH/2, 0, WIDTH/2, tileset.getHeight());
	}
	
	private void draw() {
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, (int)(WIDTH * SCALE), (int)(HEIGHT * SCALE), null);
		g2.dispose();
	}
	
	private void fillRecursive(int tx, int ty, int cb) {
		if(tx == -1 || tx == mapWidth || ty == -1 || ty == mapHeight) return;
		if(map[ty][tx] != cb) return;
		map[ty][tx] = currentBlock;
		fillRecursive(tx - 1, ty, cb);
		fillRecursive(tx + 1, ty, cb);
		fillRecursive(tx, ty - 1, cb);
		fillRecursive(tx, ty + 1, cb);
	}
	
	/////////////////////////////////////////////////////////////////////
	
	public void keyTyped(KeyEvent key) {}
	public void keyPressed(KeyEvent key) {
		int k = key.getKeyCode();
		if(k == KeyEvent.VK_SHIFT) {
			shiftDown = true;
		}
		if(k == KeyEvent.VK_CONTROL) {
			ctrlDown = true;
		}
		if(k == KeyEvent.VK_ALT) {
			altDown = true;
		}
		if(k == KeyEvent.VK_N) {
			if(ctrlDown) {
				mapWidth = 10;
				mapHeight = 8;
				map = new int[mapHeight][mapWidth];
			}
		}
		if(k == KeyEvent.VK_T) {
			String str = JOptionPane.showInputDialog(null, "Set tile size:", "Tile Size", 1);
			int i = Integer.parseInt(str);
			TILESIZE = i;
		}
		if(k == KeyEvent.VK_EQUALS) {
			String str = JOptionPane.showInputDialog(null, "Amount increased:", "Shifted", 1);
			int amount = Integer.parseInt(str);
			int nt = numTiles - (amount * 3);
			for(int row = 0; row < mapHeight; row++) {
				for(int col = 0; col < mapWidth; col++) {
					if(map[row][col] >= nt / 3) {
						if(map[row][col] < 2 * nt / 3) {
							map[row][col] += amount;
						}
						else {
							map[row][col] += 2 * amount;
						}
					}
				}
			}
		}
		if(k == KeyEvent.VK_MINUS) {
			String str = JOptionPane.showInputDialog(null, "Amount decreased:", "Shifted", 1);
			int amount = Integer.parseInt(str);
			int nt = numTiles + (amount * 3);
			for(int row = 0; row < mapHeight; row++) {
				for(int col = 0; col < mapWidth; col++) {
					if(map[row][col] >= nt / 3) {
						if(map[row][col] < 2 * nt / 3) {
							map[row][col] -= amount;
						}
						else {
							map[row][col] -= amount * 2;
						}
					}
				}
			}
		}
		if(k == KeyEvent.VK_F) {
			fillRecursive(tilex, tiley, map[tiley][tilex]);
		}
		if(k == KeyEvent.VK_S) {
			if(ctrlDown) {
				try {
					String str = JOptionPane.showInputDialog(null, "Save file name", "Save Map", 1);
					if(str == null) return;
					BufferedWriter bw = new BufferedWriter(new FileWriter("Resources/Maps/" + str + ".txt"));
					bw.write(TILESIZE + "\n");
					bw.write(mapWidth + "\n");
					bw.write(mapHeight + "\n");
					for(int row = 0; row < mapHeight; row++) {
						for(int col = 0; col < mapWidth; col++) {
							bw.write(map[row][col] + " ");
						}
						bw.write("\n");
					}
					bw.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(k == KeyEvent.VK_O) {
			if(ctrlDown) {
				String str = null;
				try {
					str = JOptionPane.showInputDialog(null, "Open file name", "Open Map", 1);
					if(str == null) return;
					BufferedReader br = new BufferedReader(new FileReader("Resources/Maps/" + str + ".txt"));
					TILESIZE = Integer.parseInt(br.readLine());
					mapWidth = Integer.parseInt(br.readLine());
					mapHeight = Integer.parseInt(br.readLine());;
					map = new int[mapHeight][mapWidth];
					String delim = "\\s+";
					for(int row = 0; row < mapHeight; row++) {
						String line = br.readLine();
						String[] tokens = line.split(delim);
						for(int col = 0; col < mapWidth; col++) {
							map[row][col] = Integer.parseInt(tokens[col]);
						}
					}
				}
				catch(Exception e) {
					System.out.println("Couldn't load maps/" + str);
					e.printStackTrace();
				}
				ctrlDown = false;
			}
		}
		if(k == KeyEvent.VK_B) {
			String str = null;
			try {
				str = JOptionPane.showInputDialog(null, "Open tileset", "Open tileset", 1);
				if(str == null) return;
				loadTileSet(str);
			}
			catch(Exception e) {
				System.out.println("Couldn't load graphics/tiles/" + str);
				e.printStackTrace();
			}
		}
		if(k == KeyEvent.VK_L) {
			if(shiftDown) {
				boolean ok = true;
				for(int row = 0; row < mapHeight; row++) {
					for(int col = 0; col < mapWidth; col++) {
						if(map[row][col] == blocks.length - 1) {
							ok = false;
							break;
						}
					}
					if(!ok) {
						break;
					}
				}
				if(!ok) return;
				for(int row = 0; row < mapHeight; row++) {
					for(int col = 0; col < mapWidth; col++) {
						map[row][col]++;
					}
				}
			}
		}
		if(k == KeyEvent.VK_K) {
			if(shiftDown) {
				boolean ok = true;
				for(int row = 0; row < mapHeight; row++) {
					for(int col = 0; col < mapWidth; col++) {
						if(map[row][col] == 0) {
							ok = false;
							break;
						}
					}
					if(!ok) {
						break;
					}
				}
				if(!ok) return;
				for(int row = 0; row < mapHeight; row++) {
					for(int col = 0; col < mapWidth; col++) {
						map[row][col]--;
					}
				}
			}
		}
		// replace
		if(k == KeyEvent.VK_V) {
			try {
				int source = Integer.parseInt(JOptionPane.showInputDialog(null, "Source", "Replace", 1));
				int dest = Integer.parseInt(JOptionPane.showInputDialog(null, "Destination", "Replace", 1));
				for(int row = 0; row < mapHeight; row++) {
					for(int col = 0; col < mapWidth; col++) {
						if(map[row][col] == source) {
							map[row][col] = dest;
						}
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		// shift everything above num
		if(k == KeyEvent.VK_C) {
			try {
				int num = Integer.parseInt(JOptionPane.showInputDialog(null, "Increment 1 from: ", "Shift", 1));
				for(int row = 0; row < mapHeight; row++) {
					for(int col = 0; col < mapWidth; col++) {
						if(map[row][col] >= numTiles / 3) continue;
						if(map[row][col] >= num) {
							map[row][col]++;
							if(map[row][col] == numTiles) {
								map[row][col]--;
							}
						}
					}
				}
			}
			catch(Exception e) {}
		}
		if(k == KeyEvent.VK_RIGHT) {
			if(shiftDown) {
				mapWidth++;
				int[][] temp = new int[mapHeight][mapWidth];
				for(int row = 0; row < mapHeight; row++) {
					for(int col = 0; col < mapWidth - 1; col++) {
						temp[row][col] = map[row][col];
					}
				}
				map = temp;
			}
			else if(ctrlDown) {
				xblock += TILESIZE;
				int width = numTiles / numRows;
				for(int i = 0; i < width; i++) {
					blocks[i].setPosition(i * TILESIZE + xblock, HEIGHT - 2 * TILESIZE);
					blocks[i + width].setPosition(i * TILESIZE + xblock, HEIGHT - TILESIZE);
				}
			}
			else if(altDown) {
				for(int row = 0; row < mapHeight; row++) {
					for(int col = mapWidth - 1; col > 0; col--) {
						map[row][col] = map[row][col - 1];
					}
					map[row][0] = 0;
				}
			}
			else {
				xmap -= TILESIZE;
			}
		}
		if(k == KeyEvent.VK_LEFT) {
			if(shiftDown) {
				mapWidth--;
				int[][] temp = new int[mapHeight][mapWidth];
				for(int row = 0; row < mapHeight; row++) {
					for(int col = 0; col < mapWidth; col++) {
						temp[row][col] = map[row][col];
					}
				}
				map = temp;
			}
			else if(ctrlDown) {
				xblock -= TILESIZE;
				int width = numTiles / numRows;
				for(int i = 0; i < width; i++) {
					blocks[i].setPosition(i * TILESIZE + xblock, HEIGHT - 2 * TILESIZE);
					blocks[i + width].setPosition(i * TILESIZE + xblock, HEIGHT - TILESIZE);
				}
			}
			else if(altDown) {
				for(int row = 0; row < mapHeight; row++) {
					for(int col = 0; col < mapWidth - 1; col++) {
						map[row][col] = map[row][col + 1];
					}
					map[row][mapWidth - 1] = 0;
				}
			}
			else {
				xmap += TILESIZE;
			}
		}
		if(k == KeyEvent.VK_UP) {
			if(shiftDown) {
				mapHeight++;
				int[][] temp = new int[mapHeight][mapWidth];
				for(int row = 0; row < mapHeight - 1; row++) {
					for(int col = 0; col < mapWidth; col++) {
						temp[row+1][col] = map[row][col];
					}
				}
				map = temp;
				ymap -= TILESIZE;
			}
			else if(altDown) {
				for(int col = 0; col < mapWidth; col++) {
					for(int row = 0; row < mapHeight - 1; row++) {
						map[row][col] = map[row + 1][col];
					}
					map[mapHeight - 1][col] = 0;
				}
			}
			else {
				ymap += TILESIZE;
			}
		}
		if(k == KeyEvent.VK_DOWN) {
			if(shiftDown) {
				mapHeight--;
				int[][] temp = new int[mapHeight][mapWidth];
				for(int row = 0; row < mapHeight; row++) {
					for(int col = 0; col < mapWidth; col++) {
						try {
							temp[row-1][col] = map[row][col];
						} catch(Exception e) {
							 }
					}
				}
				map = temp;
			}
			else if(altDown) {
				for(int col = 0; col < mapWidth; col++) {
					for(int row = mapHeight - 1; row > 0; row--) {
						map[row][col] = map[row - 1][col];
					}
					map[0][col] = 0;
				}
			}
			else {
				ymap -= TILESIZE;
			}
		}
	}
	public void keyReleased(KeyEvent key) {
		int k = key.getKeyCode();
		if(k == KeyEvent.VK_SHIFT) {
			shiftDown = false;
		}
		if(k == KeyEvent.VK_CONTROL) {
			ctrlDown = false;
		}
		if(k == KeyEvent.VK_ALT) {
			altDown = false;
		}
	}
	
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mouseClicked(MouseEvent me) {}
	public void mousePressed(MouseEvent me) {
		if(SwingUtilities.isLeftMouseButton(me)) {
			int y = (int) (me.getY() / SCALE);
			int x = (int) (me.getX() / SCALE - xblock);
			// clicked a block
			int b = 0;
			if(y >= 0 && x >= WIDTH/2) {
				b = x / TILESIZE - numCols + (y / TILESIZE)*numCols;
				if(b >= 0) {
					currentBlockImage = blocks[b].getImage();
					currentBlock = b;
				}
			}
			else {
				y = (int) (me.getY() / SCALE - ymap);
				x = (int) (me.getX() / SCALE - xmap);
				if(x < WIDTH/2 && x < mapWidth * TILESIZE &&
						y > 0 && y < mapHeight * TILESIZE) {
					map[y / TILESIZE][x / TILESIZE] = currentBlock;
				}
			}
		}
		else if(SwingUtilities.isRightMouseButton(me)) {
			int y = (int) (me.getY() / SCALE - ymap);
			int x = (int) (me.getX() / SCALE - xmap);
			if(x > 0 && x < mapWidth * TILESIZE &&
					y > 0 && y < mapHeight * TILESIZE) {
				map[y / TILESIZE][x / TILESIZE] = 0;
			}
		}
		else if(SwingUtilities.isMiddleMouseButton(me)) {
			mmx = me.getX();
			mmy = me.getY();
		}
	}
	public void mouseReleased(MouseEvent me) {}

	public void mouseMoved(MouseEvent me) {
		mousex = me.getX() - xmap;
		mousey = me.getY() - ymap;
		tilex = mousex / TILESIZE;
		tiley = mousey / TILESIZE;
	}
	public void mouseDragged(MouseEvent me) {
		if(SwingUtilities.isLeftMouseButton(me)) {
			int y = (int) (me.getY() / SCALE);
			if(y >= HEIGHT) {
			}
			else {
				y = (int) (me.getY() / SCALE - ymap);
				int x = (int) (me.getX() / SCALE - xmap);
				if(x > 0 && x < mapWidth * TILESIZE &&
					y > 0 && y < mapHeight * TILESIZE) {
					map[y / TILESIZE][x / TILESIZE] = currentBlock;
				}
			}
		}
		else if(SwingUtilities.isRightMouseButton(me)) {
			int y = (int) (me.getY() / SCALE - ymap);
			int x = (int) (me.getX() / SCALE - xmap);
			if(x > 0 && x < mapWidth * TILESIZE &&
				y > 0 && y < mapHeight * TILESIZE) {
				map[y / TILESIZE][x / TILESIZE] = 0;
			}
		}
		else if(SwingUtilities.isMiddleMouseButton(me)) {
			int y = me.getY();
			int x = me.getX();
			int dx = (x - mmx) / TILESIZE;
			int dy = (y - mmy) / TILESIZE;
			if(dx != 0 || dy != 0) {
				mmx = me.getX();
				mmy = me.getY();
				xmap += dx * TILESIZE;
				ymap += dy * TILESIZE;
			}
		}
	}
	
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		int notches = mwe.getWheelRotation();
		if(notches < 0) {
			currentBlock--;
		}
		else {
			currentBlock++;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}