package gamestates;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.Random;

import static utilz.Constants.Environment.*;


public class Playing extends State implements Statemethods {
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;

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


    private int xLvlOffset;
    // khoang cach can thiet de tinh tien sao cho khung anh canh ve
    // thuc co toa do tu (x,y) se ve tu (0,0)
    // do screen chi hien thi tu (0,0)


    private int leftBorder = (int)(0.2 * Game.GAME_WIDTH);// width cua vung trai
    private int rightBorder = (int)(0.8 * Game.GAME_WIDTH);// width cua vung phai
    // Hai gia tri tren se la co dinh

    private int lvlTileWide = LoadSave.GetLevelData()[0].length;
    private int maxTileOffset = lvlTileWide - Game.TILES_IN_WIDTH;//So luong tiles khong nhin thay
    private int maxLvlOffsetX = maxTileOffset * Game.TILES_SIZE;//chuyen cai tren thanh pixel

    private BufferedImage backgroundImg, bigCloud, smallCloud;
    private int[] smallCloudsPos;
    private Random rnd = new Random();
    private boolean gameOver = false;

    public Playing(Game game) {
        super(game);
        initClasses();
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
        smallCloudsPos = new int[8];
        for(int i = 0; i < smallCloudsPos.length; i++) {
            smallCloudsPos[i] = (int)(90 * Game.SCALE) + rnd.nextInt((int)(100 * Game.SCALE));
        }
    }

    private void initClasses(){
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        player = new Player(150, 150, (int)(64 * Game.SCALE), (int)(40 * Game.SCALE), this);
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void update() {
        levelManager.update();
        player.update();
        enemyManager.update(levelManager.getCurrentLevel().getLvlData(), player);
        checkCloseToBorder();
    }

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitbox().x;
        int diff = playerX - xLvlOffset;

        if(diff > rightBorder) {
            // player is beyond the right border -> we need to move the lvls to the right
            xLvlOffset += diff - rightBorder;
        }
        else if(diff < leftBorder) {
            // opposite to the one above
            xLvlOffset += diff - leftBorder;
        }

        if(xLvlOffset > maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if(xLvlOffset < 0)
            xLvlOffset = 0;


        // Dieu nay de trong truong hop ma moi bat dau map hoac di den cuoi map
        // thi player se khong dung yen nua ma the te di chuyen
        // Noi cach khac coi nhu xoa bo vung trai vung phai
        //de nhan vat co the cham bien cua MAP cung nhu screen
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        drawClouds(g);

        levelManager.draw(g, xLvlOffset);
        player.render(g, xLvlOffset);
        enemyManager.draw(g, xLvlOffset);

    }

    private void drawClouds(Graphics g) {
        for(int i = 0; i < 3; i++)
        g.drawImage(bigCloud, 0 + i * BIG_CLOUD_WIDTH - (int)(xLvlOffset * 0.3), (int)(204 * Game.SCALE), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);

        for(int i = 0; i < smallCloudsPos.length; i++)
        g.drawImage(smallCloud, SMALL_CLOUD_WIDTH * 4 * i - (int)(xLvlOffset * 0.7), smallCloudsPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
    }

    public void resetAll() {
        gameOver = false;
        //paused = false;
        player.resetAll();
        enemyManager.resetAllEnemies();
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        enemyManager.checkEnemyHit(attackBox);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(!gameOver)
            if (e.getButton() == MouseEvent.BUTTON1) {
            player.setAttack(true);
            }
		/*Nếu sự kiện bắt được là nút chuột trái thì
		 nhân vật bắt đầu tấn công!
		 */
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {

            case KeyEvent.VK_A:
                player.setLeft(true);
                break;
            case KeyEvent.VK_D:
                player.setRight(true);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(true);
                break;
            case KeyEvent.VK_BACK_SPACE:
                Gamestate.state = Gamestate.MENU;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(gameOver) return;
        switch(e.getKeyCode()) {
            case KeyEvent.VK_A:
                player.setLeft(false);
                break;
            case KeyEvent.VK_D:
                player.setRight(false);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(false);
                break;

        }
    }
}
