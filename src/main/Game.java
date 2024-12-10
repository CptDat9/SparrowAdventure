package main;

import java.awt.Graphics;

import audio.AudioPlayer;
import gamestates.*;
import ui.AudioOptions;

public class Game implements Runnable {

    private GamePanel gamePanel;
    private Thread gameThread; // Luong trong game
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
   Nhanh	|		   logic tốt,			|		nhân vật chạy chậm	|		 
	 		|		   game mượt			|		game mượt			|
------------|-------------------------------|---------------------------|	   
   Thấp		|		nhân vật chạy nhanh		|		chạy thì chậm, 		|
	 		|		game hơi lag			|		game thì lag		|
*/
    

    private Playing playing;
    private Menu menu;
    private Credits credits;
    private Precredits precredits;
    private PlayerSelection playerSelection;
    private GameOptions gameOptions;
    private AudioOptions audioOptions;
    private AudioPlayer audioPlayer;
    
    
	/*
	 Ảnh của level được import gồm 26 ảnh theo chiều rộng, 14 ảnh theo chiều ngang
	 chúng đều cùng kích thước 32*32
	 */
    //CÁC HẰNG SỐ CHO CÁC TILES
    public final static int TILES_DEFAULT_SIZE = 32;
    public final static float SCALE = 1.5f;
    public final static int TILES_IN_WIDTH = 26;
    public final static int TILES_IN_HEIGHT = 14;
    public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;
    //CÁC HẰNG SỐ CHO CÁC TILES

    private final boolean SHOW_FPS_UPS = true;

    public Game() {
        System.out.println("size: " + GAME_WIDTH + " : " + GAME_HEIGHT);
        initClasses();
        gamePanel = new GamePanel(this);
        new GameWindow(gamePanel);
    	/*Tác dụng chính là để gamePanel tập trung vào việc 
		xử lý các sự kiện từ input
		Nếu bỏ hàm này đi thì chương trình 
		sẽ không nhận input (?)
		*/
        gamePanel.requestFocusInWindow();
        
        
        //Tác dụng: Để game nhận input từ mouse và keyboard ngay lập tức từ khi khởi tạo game
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();
        
        //gameloop should be the last after all!
        startGameLoop();
        
    }

    private void initClasses() {
        audioOptions = new AudioOptions(this);
        audioPlayer = new AudioPlayer();
        menu = new Menu(this);
        playing = new Playing(this);
        playerSelection = new PlayerSelection(this);
        credits = new Credits(this);
        precredits = new Precredits(this);
        gameOptions = new GameOptions(this);
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    @Override 
    //Sau khi implements Runnable, ta override hàm run để tạo luồng
    public void run() {
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while (true) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }
//            
            if (deltaF >= 1) {
            	gamePanel.repaint(); //repaint được do có extends từ JPanel
                frames++;
                deltaF--;
            }

            if (SHOW_FPS_UPS) {
                if (System.currentTimeMillis() - lastCheck >= 1000) {
                    lastCheck = System.currentTimeMillis();
                    System.out.println("FPS: " + frames + " | UPS: " + updates);
                    frames = 0;
                    updates = 0;
                }
            }
        }
    }
    //Cần tách riêng biệt luồng update logic và render frame -> tránh trường hợp sai số hệ thống làm FPS drop

    public void update() {
        switch (Gamestate.state) {
            case MENU -> menu.update();
            case PLAYER_SELECTION -> playerSelection.update();
            case PLAYING -> playing.update();
            case OPTIONS -> gameOptions.update();
            case CREDITS -> credits.update();
            case PRE_CREDITS -> precredits.update(); 
            case QUIT -> System.exit(0);
        }
    }

    @SuppressWarnings("incomplete-switch")
    public void render(Graphics g) {
        switch (Gamestate.state) {
            case MENU -> menu.draw(g);
            case PLAYER_SELECTION -> playerSelection.draw(g);
            case PLAYING -> playing.draw(g);
            case OPTIONS -> gameOptions.draw(g);
            case CREDITS -> credits.draw(g);
            case PRE_CREDITS -> precredits.draw(g); 
        }
    }

    public void windowFocusLost() {
        if (Gamestate.state == Gamestate.PLAYING)
            playing.getPlayer().resetDirBooleans();
    }
    /// Trong truong hop dang choi ma lo bam sang tab khac khien game chua dung han

    public Menu getMenu() {
        return menu;
    }

    public Playing getPlaying() {
        return playing;
    }

    public Credits getCredits() {
        return credits;
    }

    public PlayerSelection getPlayerSelection() {
        return playerSelection;
    }

    public GameOptions getGameOptions() {
        return gameOptions;
    }

    public AudioOptions getAudioOptions() {
        return audioOptions;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
}
