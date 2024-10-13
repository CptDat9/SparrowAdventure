package main;

import java.awt.Graphics;

import gamestates.Gamestate;
import gamestates.Menu;
import gamestates.Playing;

public class Game implements Runnable{
	private GameWindow gameWindow;
	private GamePanel gamePanel;
	
	public Thread gameThread; // Luong trong game
	
	private final int FPS_SET = 120;
	private final int UPS_SET = 200;
	/*
	 UPS: tần suất update logic về vật lý và trạng thái của nhân vật trên một giây
	 UPS càng nhanh thì nhân vật thay đổi trạng thái càng nhanh, và di chuyển, tương tác
	 với các vật khác với tốc độ càng nhanh
	 
	 FPS: tần suất render frame về hình ảnh và hoạt họa của nhân vật trên một giây
	 FPS càng nhanh thì game càng mượt
	 
	  
  \UPS		|		      Nhanh            	|   	    Thấp			|
FPS\--------|-------------------------------|---------------------------|
Nhanh		|		   logic tốt,			|		nhân vật chạy chậm	|		 
	 		|		   game mượt			|		game mượt			|
------------|-------------------------------|---------------------------|	   
Thấp		|		nhân vật chạy nhanh		|		chạy thì chậm, 		|
	 		|		game hơi lag			|		game thì lag		|
*/

	////////////////////////

	private Playing playing;
	private Menu menu;

	//CÁC HẰNG SỐ CHO CÁC TILES
	public final static int TILES_DEFAULT_SIZE = 32; 
	public final static float SCALE = 1.5f;
	public final static int TILES_IN_WIDTH = 26;
	public final static int TILES_IN_HEIGHT = 14;
	public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
	public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
	public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;
	//CÁC HẰNG SỐ CHO CÁC TILES
	

	/*
	 Ảnh của level được import gồm 26 ảnh theo chiều rộng, 14 ảnh theo chiều ngang
	 chúng đều cùng kích thước 32*32
	 */
		
	
	public Game() {
		initClasses();
		gamePanel = new GamePanel(this);
		//Tại sao lại phải có this ở đây :(
		
		gameWindow = new GameWindow(gamePanel);
		
		gamePanel.requestFocus();
		/*Tác dụng chính là để gamePanel tập trung vào việc 
		xử lý các sự kiện từ input
		Nếu bỏ hàm này đi thì chương trình 
		sẽ không nhận input (?)
		*/
		
		startGameLoop(); //gameloop should be the last after all!
	}

	private void initClasses(){
		menu = new Menu(this);
		playing = new Playing(this);
	}

	private void startGameLoop() {
		gameThread = new Thread(this);
		gameThread.start();		
	}
	
	public void update() {
		switch (Gamestate.state) {
		case MENU:
			menu.update();
			break;
		case PLAYING:
			playing.update();
			break;
		case OPTIONS:
		case QUIT:
		default:
			System.exit(0);
			break;
		}
	}
	
	public void render(Graphics g) {
		switch (Gamestate.state) {
			case MENU:
				menu.draw(g);
				break;
			case PLAYING:
				playing.draw(g);
				break;
		}

	} //Vẽ background trước, vẽ người sau

	@Override
	public void run() {
		double timePerFrame = 1000000000.0/FPS_SET; //10^9/FPS
		double timePerUpdate = 1000000000.0/UPS_SET; //10^9/UPS
		
		long previousTime = System.nanoTime();
		int frames = 0;
		int updates = 0;
		long lastCheck = System.currentTimeMillis();
		
		double deltaU = 0;
		double deltaF = 0;
		
		while (true) {
			long currentTime = System.nanoTime();
			
			deltaU += (currentTime - previousTime)/timePerUpdate;
			deltaF += (currentTime - previousTime)/timePerFrame;
			previousTime = currentTime;
			
			if (deltaU >= 1) {
				update();
				++updates;
				--deltaU;
			} //updating
			
			if (deltaF >= 1) {
				gamePanel.repaint(); //repaint được do có extends từ JPanel
				--deltaF;
				++frames;
			} //rendering

			if (System.currentTimeMillis() - lastCheck >= 1000) {
				lastCheck = System.currentTimeMillis();
				//System.out.println("FPS: " + frames + " | UPS: " + updates);
				frames = 0;
				updates = 0;
			}
		}
	}
	
	public void windowFocusLost() {
		if(Gamestate.state == Gamestate.PLAYING) {
			playing.getPlayer().resetDirBooleans();
		}

		/// Trong truong hop dang choi ma lo bam sang tab khac khien game chua dung han
	}

	public Menu getMenu() {
		return menu;
	}

	public Playing getPlaying() {
		return playing;
	}
}
