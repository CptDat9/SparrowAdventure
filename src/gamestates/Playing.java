package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import entities.EnemyManager;
import entities.Player;
import entities.PlayerCharacter;
import levels.LevelManager;
import main.Game;
import objects.ObjectManager;
import ui.GameCompletedOverlay;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PauseOverlay;
import utilz.LoadSave;

import static utilz.Constants.Environment.*;

public class Playing extends State implements Statemethods {

    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private ObjectManager objectManager;
    private PauseOverlay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private GameCompletedOverlay gameCompletedOverlay;
    private LevelCompletedOverlay levelCompletedOverlay;
    
    // Y tuong cho background moving khi lam map rong:
    // Do thuc te screen chi hien thi tu (0,0) - (Width, Height)
    // Nen ta can tim cach sao cho khi nhan vat di chuyen thi se thay doi map theo
    // Tren screen ta se co mot vung hinh chu nhat sao cho khi nhan vat trong vung nay thi background khong di chuyen
    // noi cach khac la dung yen. Vung nay se o giua screen
    // Khi o ngoai vung nay, tuc la ben trai hoac ben phai thi nhan vat thuc te dung yen con cac canh vat se di chuyen
    // cho den khi cham bien cua map
    //  _______________________________________________
    //  |          |     vung background   |          |
    //  |   vung   |     khong di chuyen   |  vung    |
    //  |   trai   |     khi nhan vat dung |  phai    |
    //  |          |         trong day     |          |
    //  |          |                       |          |
    //  |__________|_______________________|__________|

    private boolean paused = false; //Luôn bắt đầu game ở trạng thái đang chơi

    private int xLvlOffset = 0;
    // khoang cach can thiet de tinh tien sao cho khung anh canh ve
    // thuc co toa do tu (x,y) se ve tu (0,0)
    // do screen chi hien thi tu (0,0)

    private int leftBorder = (int) (0.25 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.75 * Game.GAME_WIDTH);
    // Hai gia tri tren se la co dinh
    
    private int maxLvlOffsetX;

    private BufferedImage backgroundImg, bigCloud, smallCloud;

    private int[] smallCloudsPos;
    private Random rnd = new Random();
    private boolean gameOver = false;

    private boolean lvlCompleted;
    private boolean gameCompleted;
    private boolean playerDying;

    // Ship will be decided to drawn here. It's just a cool addition to the game
    // for the first level. Hinting on that the player arrived with the boat.

    // If you would like to have it on more levels, add a value for objects when
    // creating the level from lvlImgs. Just like any other object.

    // Then play around with position values so it looks correct depending on where
    // you want
    // it.

//    private boolean drawShip = true;
//    private int shipAni, shipTick, shipDir = 1;
//    private float shipHeightDelta, shipHeightChange = 0.05f * Game.SCALE;

    public Playing(Game game) {
        super(game);
        initClasses();
        
        //Tải các resource liên quan đến background
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
        
        //Pick random vị trí xuất hiện có thể theo phương y cho mây nhỏ
        //Đâu đấy khoảng 90 -> 190 theo phương y
        smallCloudsPos = new int[8];
        for (int i = 0; i < smallCloudsPos.length; i++)
            smallCloudsPos[i] = (int) (90 * Game.SCALE) + rnd.nextInt((int) (100 * Game.SCALE));

//        shipImgs = new BufferedImage[4];
//        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.SHIP_ATLAS);
//        for (int i = 0; i < shipImgs.length; i++)
//            shipImgs[i] = temp.getSubimage(i * 78, 0, 78, 72);

        calcLvlOffset();
        loadStartLevel();
    }

    public void loadNextLevel() {
        levelManager.setLevelIndex(levelManager.getLevelIndex() + 1);
        levelManager.loadNextLevel();
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
        resetAll();
    }

    private void loadStartLevel() {
        enemyManager.loadEnemies(levelManager.getCurrentLevel());
        objectManager.loadObjects(levelManager.getCurrentLevel());
    }

    private void calcLvlOffset() {
        maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        objectManager = new ObjectManager(this);

        pauseOverlay = new PauseOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
        levelCompletedOverlay = new LevelCompletedOverlay(this);
        gameCompletedOverlay = new GameCompletedOverlay(this);
    }

    public void setPlayerCharacter(PlayerCharacter pc) {

        player = new Player(pc, this);
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
    }

    @Override
    public void update() {
        if (paused) //Chỉ khi pause mới update pauseOverlay
            pauseOverlay.update();
        else if (lvlCompleted)
            levelCompletedOverlay.update();
        else if (gameCompleted)
            gameCompletedOverlay.update();
        else if (gameOver)
            gameOverOverlay.update();
        else if (playerDying)
            player.update();
        else { //Các trường hợp còn lại (khi game đang chạy), thì update các phần như levelManager, enemyManager
        	//Nếu update các lớp này thì có tác dụng gì?
        	//Sẽ gọi đến update các phần tương ứng trong một level: tình trạng vật lý của crabby, shark, ...
            
            levelManager.update();
            objectManager.update(levelManager.getCurrentLevel().getLevelData(), player);
            player.update();
            
            //Chúng ta cần có một cách gì đó để detect được info của map khi update các trạng thái vật lý của nhân vật trong game
            //Do đó, cần truyền tham số levelManager.currentLevelData vào hàm update
            enemyManager.update(levelManager.getCurrentLevel().getLevelData());
            checkCloseToBorder();
        }
    }

    /*
     Để làm được Bigger Level, có moving level, phải có một giá trị xLvlOffset
     đây chính là giá trị dùng để định vị vị trí (x, y) cần vẽ hoạt họa của lvl
     xLvlOffset được tính như thế nào?
     */
    private void checkCloseToBorder() {
    	//playerX là vị trí hiện tại của nhân vật, tính theo toàn bộ map
        int playerX = (int) player.getHitbox().x;
        
        /*
         xLvlOffset là giá trị tính từ vị trí biên bên trái (của map)
         đến vị trí biên bên trái (đang vẽ trên màn hình)
         
         diff là sự sai lệch giữa vị trí x của nhân vật và vị trí biên bên trái
         (đang vẽ trên màn hình)
        */
        int diff = playerX - xLvlOffset;
        
        if (diff > rightBorder)
        	 // player is beyond the right border -> we need to move the lvls to the right
            xLvlOffset += diff - rightBorder;
        else if (diff < leftBorder)
        	// opposite to the one above
            xLvlOffset += diff - leftBorder;
        
        //Không được quá nhỏ hay quá lớn đến nỗi tràn ra cả khả năng xLvlOffset có thể xuất hiện
        xLvlOffset = Math.max(Math.min(xLvlOffset, maxLvlOffsetX), 0);
        
        // Dieu nay de trong truong hop ma moi bat dau map hoac di den cuoi map
        // thi player se khong dung yen nua ma the te di chuyen
        // Noi cach khac coi nhu xoa bo vung trai vung phai
        // de nhan vat co the cham bien cua MAP cung nhu screen
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null); //Vẽ BackGround

        drawClouds(g);

        //xLvlOffset là giá trị tính từ vị trí biên bên trái (của map), đến vị trí biên bên trái (đang vẽ trên màn hình)
        //Đây chính là giá trị mà các hoạt ảnh liên quan đến lvl sẽ phải chạy lùi theo
        levelManager.draw(g, xLvlOffset);
        objectManager.draw(g, xLvlOffset);
        enemyManager.draw(g, xLvlOffset);
        player.render(g, xLvlOffset);
        objectManager.drawBackgroundTrees(g, xLvlOffset);
        
        if (paused) { //Chỉ khi pause mới vẽ pauseOverlay
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g); //Vẽ Khung của trạng thái pause
        } else if (gameOver)
            gameOverOverlay.draw(g);
        else if (lvlCompleted)
            levelCompletedOverlay.draw(g);
        else if (gameCompleted)
            gameCompletedOverlay.draw(g);

    }

    /* Nhắc lại: xLvlOffset là giá trị tính từ vị trí biên bên trái (của map) đến vị trí biên bên trái (đang vẽ trên màn hình)
     Để mây nhỏ di chuyển nhanh hơn mây to, thực chất là
     Ta cho mây nhỏ phải bị "vẽ" lùi về phía sau với một vận tốc lớn hơn
     Ở đây ta chọn vận tốc bị "vẽ" lùi về sau cho mây to là 0.3*xLvlOffset, với mây nhỏ là 0.7*xLvlOffset
     Nếu không dùng đến tham số này, mây sẽ chỉ vẽ cố định tại một vị trí trong Panel (trông rất là dị)
    
     Tóm lại: ta lấy xLvlOffset làm hệ quy chiếu cho đám mây
    */
    private void drawClouds(Graphics g) {
        for (int i = 0; i < 4; i++)
            g.drawImage(bigCloud, i * BIG_CLOUD_WIDTH , (int) (204 * Game.SCALE), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);

        for (int i = 0; i < smallCloudsPos.length; i++)
            g.drawImage(smallCloud, SMALL_CLOUD_WIDTH * 4 * i , smallCloudsPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
    }

    public void setGameCompleted() {
        gameCompleted = true;
    }

    public void resetGameCompleted() {
        gameCompleted = false;
    }

    public void resetAll() {
        gameOver = false;
        paused = false;
        lvlCompleted = false;
        playerDying = false;

        player.resetAll();
        enemyManager.resetAllEnemies();
        objectManager.resetAllObjects();
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void checkObjectHit(Rectangle2D.Float attackBox) {
        objectManager.checkObjectHit(attackBox);
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        enemyManager.checkEnemyHit(attackBox);
    }

    public void checkPotionTouched(Rectangle2D.Float hitbox) {
        objectManager.checkObjectTouched(hitbox);
    }

    public void checkSpikesTouched(Player p) {
        objectManager.checkSpikesTouched(p);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameOver) {
            if (e.getButton() == MouseEvent.BUTTON1)
                player.setAttacking(true);
            else if (e.getButton() == MouseEvent.BUTTON3)
                player.powerAttack();
        }
		/*Nếu sự kiện bắt được là nút chuột trái thì
		 nhân vật bắt đầu tấn công!
		 */
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver && !gameCompleted && !lvlCompleted)
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(true);
                    break;
                case KeyEvent.VK_D:

                    player.setRight(true);
                    break;
                case KeyEvent.VK_SPACE:
                    player.addJump(1);
                    break;
                case KeyEvent.VK_ESCAPE: //Nhấn escape thì: đang dừng sẽ trở lại game
                    paused = !paused;
                //Nút này quan trọng, đây sẽ nơi duy nhất để chuyển từ trạng thái pause sang unpause trong game
            }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver && !gameCompleted && !lvlCompleted)
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(false);
                    break;
                case KeyEvent.VK_SPACE:
                    break;
            }
    }

    public void mouseDragged(MouseEvent e) {
        if (!gameOver && !gameCompleted && !lvlCompleted)
            if (paused)
                pauseOverlay.mouseDragged(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameOver)
            gameOverOverlay.mousePressed(e);
        else if (paused)
            pauseOverlay.mousePressed(e);
        else if (lvlCompleted)
            levelCompletedOverlay.mousePressed(e);
        else if (gameCompleted)
            gameCompletedOverlay.mousePressed(e);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (gameOver)
            gameOverOverlay.mouseReleased(e);
        else if (paused)
            pauseOverlay.mouseReleased(e);
        else if (lvlCompleted)
            levelCompletedOverlay.mouseReleased(e);
        else if (gameCompleted)
            gameCompletedOverlay.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (gameOver)
            gameOverOverlay.mouseMoved(e);
        else if (paused)
            pauseOverlay.mouseMoved(e);
        else if (lvlCompleted)
            levelCompletedOverlay.mouseMoved(e);
        else if (gameCompleted)
            gameCompletedOverlay.mouseMoved(e);
    }
    /* Tùy theo trạng thái của Playing: gameOver, levelCompleted, paused, 
     * thì mouseMoved, mouseReleased sẽ được hiểu theo các cách khác nhau
     * */

    public void setLevelCompleted(boolean levelCompleted) {
        game.getAudioPlayer().lvlCompleted();
        if (levelManager.getLevelIndex() + 1 >= levelManager.getAmountOfLevels()) {
            // No more levels
            gameCompleted = true;
            levelManager.setLevelIndex(0);
            levelManager.loadNextLevel();
            resetAll();
            return;
        }
        this.lvlCompleted = levelCompleted;
    }

    public void setMaxLvlOffset(int lvlOffset) {
        this.maxLvlOffsetX = lvlOffset;
    }

    public void unpauseGame() {
        paused = false;
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public Player getPlayer() {
        return player;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public ObjectManager getObjectManager() {
        return objectManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public void setPlayerDying(boolean playerDying) {
        this.playerDying = playerDying;
    }
}
