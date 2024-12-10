package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import gamestates.Playing;
import levels.Level;
import utilz.LoadSave;
import static utilz.Constants.EnemyConstants.*;

public class EnemyManager {

	private Playing playing;
	private BufferedImage[][] crabbyArr, sharkArr;
	private Level currentLevel;

	public EnemyManager(Playing playing) {
		this.playing = playing;
		loadEnemyImgs();
	}

	public void loadEnemies(Level level) {
		this.currentLevel = level;
	}

	//Update các tình trạng vật lý của Emeny
	//Nếu không có nhân vật nào Active, sẽ gán gamestate là Completed
	//Hàm update này cần phải detect được tình trạng hiện tại của map đang dùng, thì mới update thông tin vật lý một cách phù hợp
	
	/*
	 * Khi update về hình ảnh, cũng như khi check xem có bị Player hit hay không, các con quái vật đều phải đảm bảo là
	 * đang ở trong trạng thái active. Nếu không active, coi như con quái đó trong list<enemy> bị bỏ qua trong quá trình duyệt list
	 * 
	 * (Tóm lại: khi quái chết -> active = false -> coi như bị bỏ qua trong list)
	 */
	public void update(int[][] lvlData) {
		boolean isAnyActive = false;
		for (Crabby c : currentLevel.getCrabs())
			if (c.isActive()) {
				c.update(lvlData, playing);
				isAnyActive = true;
			}

		for (Shark s : currentLevel.getSharks())
			if (s.isActive()) {
				s.update(lvlData, playing);
				isAnyActive = true;
			}

		if (!isAnyActive)
			playing.setLevelCompleted(true);
	}

	// Vẽ đồ họa tương ứng của các Enemy trong game
	// Các nhân vật này cũng có tọa độ vẽ lấy xLvlOffset làm mốc chuẩn
	public void draw(Graphics g, int xLvlOffset) {
		drawCrabs(g, xLvlOffset);
		drawSharks(g, xLvlOffset);
	}

	private void drawSharks(Graphics g, int xLvlOffset) {
		for (Shark s : currentLevel.getSharks())
			if (s.isActive()) {
				g.drawImage(sharkArr[s.getState()][s.getAniIndex()], 
						(int) s.getHitbox().x - xLvlOffset - SHARK_DRAWOFFSET_X + s.flipX(),
						(int) s.getHitbox().y - SHARK_DRAWOFFSET_Y + (int) s.getPushDrawOffset(), 
						SHARK_WIDTH * s.flipW(), SHARK_HEIGHT, null);
//				s.drawHitbox(g, xLvlOffset);
//				s.drawAttackBox(g, xLvlOffset);
			}
	}

	/*
	 * Trường hợp 1:
	 * Walkdir = RIGHT
	 * -> Vẽ các hoạt ảnh Crabby NGƯỢC LẠI, bằng cách đặt flipX() = width của quái vật và flipW() = -1 để lật ngược ảnh vẽ của Crabby gốc
	 * 
	 * Trường hợp 2:
	 * Walkdir = LEFT
	 * -> Vẽ hoạt ảnh Crabby gốc là được
	 */
	private void drawCrabs(Graphics g, int xLvlOffset) {
		for (Crabby c : currentLevel.getCrabs())
			if (c.isActive()) {
				g.drawImage(crabbyArr[c.getState()][c.getAniIndex()], 
						(int) c.getHitbox().x - xLvlOffset - CRABBY_DRAWOFFSET_X + c.flipX(),
						(int) c.getHitbox().y - CRABBY_DRAWOFFSET_Y + (int) c.getPushDrawOffset(), 
						CRABBY_WIDTH * c.flipW(), CRABBY_HEIGHT, null);

//				c.drawHitbox(g, xLvlOffset);
//				c.drawAttackBox(g, xLvlOffset);
			}

	}

	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		for (Crabby c : currentLevel.getCrabs())
			if (c.isActive())
				if (c.getState() != DEAD && c.getState() != HIT)
					if (attackBox.intersects(c.getHitbox())) {
						c.hurt(20);
						return;
					}

		for (Shark s : currentLevel.getSharks())
			if (s.isActive()) {
				if (s.getState() != DEAD && s.getState() != HIT)
					if (attackBox.intersects(s.getHitbox())) {
						s.hurt(20);
						return;
					}
			}
	}

	//Nạp các đồ họa của Enemy tương ứng
	//Mảng gồm đồ họa của crabby, được nạp từ resource có tên là CRABBY_SPRITE qua hàm getSpriteAtlas vào trong hệ thống
	//Ta biết rằng mảng này có 9 đồ họa theo phía ngang, 5 đồ họa cho phía dọc, nên nạp tham số 9, 5 vào
	//Kích cỡ được nạp vào bảng crabbyArr[j][i] đúng bằng WIDTH_DEFAULT và HEIGHT_DEFAULT
	private void loadEnemyImgs() {
		crabbyArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE), 9, 5, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
		sharkArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.SHARK_ATLAS), 8, 5, SHARK_WIDTH_DEFAULT, SHARK_HEIGHT_DEFAULT);
	}

	private BufferedImage[][] getImgArr(BufferedImage atlas, int xSize, int ySize, int spriteW, int spriteH) {
		BufferedImage[][] tempArr = new BufferedImage[ySize][xSize];
		for (int j = 0; j < tempArr.length; j++)
			for (int i = 0; i < tempArr[j].length; i++)
				tempArr[j][i] = atlas.getSubimage(i * spriteW, j * spriteH, spriteW, spriteH);
		return tempArr;
	}

	public void resetAllEnemies() {
		for (Crabby c : currentLevel.getCrabs())
			c.resetEnemy();
		for (Shark s : currentLevel.getSharks())
			s.resetEnemy();
	}

}
