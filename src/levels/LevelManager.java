package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import utilz.LoadSave;

public class LevelManager {
//	private Game game;
	private BufferedImage[] levelSprite;
	private Level levelOne; //Sau này, biến này sẽ trở thành một mảng chứa các level;
	
	public LevelManager(Game game) {
//		this.game = game;
		importOutsideSprites();
		levelOne = new Level(LoadSave.GetLevelData());
	}
	
	private void importOutsideSprites() {
		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
		levelSprite = new BufferedImage[48];
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 12; i++) {
				int index = j*12 + i;
				levelSprite[index] = img.getSubimage(i*32, j*32, 32, 32);
			}
		}
	}
	/*
	Nạp thông tin về Outside vào mảng levelSprite
	phần thử thứ index = j*12 + i đại diện cho subImage trong ảnh tại vị trí
	pixel (i*32, j*32) với kích cỡ lấy subImage là 32*32 px 
	 */

	public void draw(Graphics g, int lvlOffset) {
		for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
			for (int i = 0; i < levelOne.getLvlData()[0].length; i++) {
				int index = levelOne.getSpriteIndex(i, j);
				g.drawImage(levelSprite[index], Game.TILES_SIZE*i - lvlOffset, Game.TILES_SIZE*j, Game.TILES_SIZE, Game.TILES_SIZE, null);
			}
		}
	}
	
	public void update() {
		
	}
	
	public Level getCurrentLevel() {
		return levelOne;
	}
}
