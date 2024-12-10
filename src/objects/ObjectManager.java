package objects;
	
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
	
import entities.Enemy;
import entities.Player;
import gamestates.Playing;
import levels.Level;
import main.Game;
import utilz.LoadSave;
import static utilz.Constants.ObjectConstants.*;
import static utilz.HelpMethods.*;
import static utilz.Constants.Projectiles.*;
	
public class ObjectManager {
	private Playing playing; // Tham chiếu đến trạng thái chơi game
	private BufferedImage[][] potionImgs, containerImgs; // Lưu trữ hình ảnh của các vật phẩm và thùng chứa
	private BufferedImage[] cannonImgs, grassImgs; // Lưu trữ hình ảnh của pháo và cỏ
	private BufferedImage[][] treeImgs; // Lưu trữ hình ảnh của cây
	private BufferedImage spikeImg, cannonBallImg; // Lưu trữ hình ảnh của bẫy gai và đạn pháo
	private ArrayList<Potion> potions; // Danh sách các vật phẩm (potion) trong trò chơi
	private ArrayList<GameContainer> containers; // Danh sách các thùng chứa (container) trong trò chơi
	private ArrayList<Projectile> projectiles = new ArrayList<>(); // Danh sách các đạn pháo đang hoạt động
	private BufferedImage[] shipImgs; //Hình ảnh cái thuyền
	
	private Level currentLevel; // Cấp độ hiện tại của trò chơi

	// Constructor khởi tạo ObjectManager với tham chiếu đến trạng thái chơi hiện tại
	public ObjectManager(Playing playing) {
		this.playing = playing;
		currentLevel = playing.getLevelManager().getCurrentLevel(); // Lấy cấp độ hiện tại từ LevelManager
		loadImgs(); // Tải tất cả hình ảnh cần thiết cho các đối tượng
	}

	// Kiểm tra xem người chơi có chạm vào bẫy gai không
	public void checkSpikesTouched(Player p) {
		for (Spike s : currentLevel.getSpikes()) // Duyệt qua danh sách các bẫy gai
			if (s.getHitbox().intersects(p.getHitbox())) // Nếu người chơi chạm vào hitbox của bẫy
				p.kill(); // Giết người chơi
	}

	// Kiểm tra xem kẻ thù có chạm vào bẫy gai không
	public void checkSpikesTouched(Enemy e) {
		for (Spike s : currentLevel.getSpikes()) // Duyệt qua danh sách các bẫy gai
			if (s.getHitbox().intersects(e.getHitbox())) // Nếu kẻ thù chạm vào hitbox của bẫy
				e.hurt(200); // Gây sát thương lên kẻ thù
	}

	// Kiểm tra xem vật phẩm có bị chạm vào bởi hitbox của đối tượng không (người chơi, kẻ thù)
	public void checkObjectTouched(Rectangle2D.Float hitbox) {
		for (Potion p : potions) // Duyệt qua danh sách các vật phẩm
			if (p.isActive()) { // Nếu vật phẩm đang hoạt động
				if (hitbox.intersects(p.getHitbox())) { // Nếu hitbox của đối tượng chạm vào hitbox của vật phẩm
					p.setActive(false); // Vô hiệu hóa vật phẩm
					applyEffectToPlayer(p); // Áp dụng hiệu ứng của vật phẩm lên người chơi
				}
			}
	}

	// Áp dụng hiệu ứng của vật phẩm lên người chơi dựa trên loại vật phẩm
	public void applyEffectToPlayer(Potion p) {
		if (p.getObjType() == RED_POTION) // Nếu là vật phẩm màu đỏ
			playing.getPlayer().changeHealth(RED_POTION_VALUE); // Tăng máu cho người chơi
		else
			playing.getPlayer().changePower(BLUE_POTION_VALUE); // Tăng sức mạnh cho người chơi
	}

	// Kiểm tra xem thùng chứa có bị tấn công không, nếu bị tấn công thì kích hoạt animation
	public void checkObjectHit(Rectangle2D.Float attackbox) {
		for (GameContainer gc : containers) // Duyệt qua danh sách thùng chứa
			if (gc.isActive() && !gc.doAnimation) { // Nếu thùng chứa còn hoạt động và chưa kích hoạt animation
				if (gc.getHitbox().intersects(attackbox)) { // Nếu hitbox của thùng chứa bị tấn công
					gc.setAnimation(true); // Bắt đầu animation mở thùng
					int type = 0;
					if (gc.getObjType() == BARREL) // Nếu là thùng (barrel)
						type = 1;
					potions.add(new Potion((int) (gc.getHitbox().x + gc.getHitbox().width / 2), 
					(int) (gc.getHitbox().y - gc.getHitbox().height / 2), type)); // Thêm vật phẩm vào danh sách
					return;
				}
			}
	}

	// Tải các đối tượng từ cấp độ mới
	public void loadObjects(Level newLevel) {
		currentLevel = newLevel; // Cập nhật cấp độ hiện tại
		potions = new ArrayList<>(newLevel.getPotions()); // Lấy danh sách vật phẩm từ cấp độ mới
		containers = new ArrayList<>(newLevel.getContainers()); // Lấy danh sách thùng chứa từ cấp độ mới
		projectiles.clear(); // Xóa danh sách đạn pháo để chuẩn bị cho cấp độ mới
	}

	// Tải tất cả hình ảnh cần thiết cho các đối tượng trong game
	private void loadImgs() {
		BufferedImage potionSprite = LoadSave.GetSpriteAtlas(LoadSave.POTION_ATLAS);
		potionImgs = new BufferedImage[2][7]; // Mảng lưu hình ảnh của 2 loại vật phẩm, mỗi loại có 7 hình ảnh

		for (int j = 0; j < potionImgs.length; j++)
			for (int i = 0; i < potionImgs[j].length; i++)
				potionImgs[j][i] = potionSprite.getSubimage(12 * i, 16 * j, 12, 16); // Cắt hình ảnh vật phẩm từ sprite

		BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_ATLAS);
		containerImgs = new BufferedImage[2][8]; // Mảng lưu hình ảnh của 2 loại thùng chứa, mỗi loại có 8 hình ảnh

		for (int j = 0; j < containerImgs.length; j++)
			for (int i = 0; i < containerImgs[j].length; i++)
				containerImgs[j][i] = containerSprite.getSubimage(40 * i, 30 * j, 40, 30); // Cắt hình ảnh thùng chứa từ sprite

		// Tải hình ảnh của bẫy gai, pháo và các đối tượng khác tương tự...		
		spikeImg = LoadSave.GetSpriteAtlas(LoadSave.TRAP_ATLAS);

		cannonImgs = new BufferedImage[7];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CANNON_ATLAS);

		for (int i = 0; i < cannonImgs.length; i++)
			cannonImgs[i] = temp.getSubimage(i * 40, 0, 40, 26);

		cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL);
		treeImgs = new BufferedImage[2][4];
		BufferedImage treeOneImg = LoadSave.GetSpriteAtlas(LoadSave.TREE_ONE_ATLAS);
		for (int i = 0; i < 4; i++)
			treeImgs[0][i] = treeOneImg.getSubimage(i * 39, 0, 39, 92);

		BufferedImage treeTwoImg = LoadSave.GetSpriteAtlas(LoadSave.TREE_TWO_ATLAS);
		for (int i = 0; i < 4; i++)
			treeImgs[1][i] = treeTwoImg.getSubimage(i * 62, 0, 62, 54);

		BufferedImage grassTemp = LoadSave.GetSpriteAtlas(LoadSave.GRASS_ATLAS);
		grassImgs = new BufferedImage[2];
		for (int i = 0; i < grassImgs.length; i++)
			grassImgs[i] = grassTemp.getSubimage(32 * i, 0, 32, 32);
		
		BufferedImage shipTemp = LoadSave.GetSpriteAtlas(LoadSave.SHIP_ATLAS);
		shipImgs = new BufferedImage[4];
		for (int i = 0; i < shipImgs.length; i++)
			shipImgs[i] = shipTemp.getSubimage(78 * i, 0, 78, 72);
		
	}
	
	// Cập nhật trạng thái của các đối tượng trong game
	public void update(int[][] lvlData, Player player) {
		updateBackgroundTrees(); // Cập nhật trạng thái cây nền
		for (Potion p : potions) // Cập nhật trạng thái vật phẩm
			if (p.isActive())
				p.update();
	
		for (GameContainer gc : containers) // Cập nhật trạng thái thùng chứa
			if (gc.isActive())
				gc.update();

		updateCannons(lvlData, player); // Cập nhật trạng thái pháo
		updateProjectiles(lvlData, player); // Cập nhật trạng thái đạn pháo
		updateShips();
	}

	// Cập nhật trạng thái cây nền
	private void updateBackgroundTrees() {
		for (BackgroundTree bt : currentLevel.getTrees())
			bt.update();
	}

	// Cập nhật trạng thái của đạn pháo
	private void updateProjectiles(int[][] lvlData, Player player) {
		for (Projectile p : projectiles)
			if (p.isActive()) { // Nếu đạn pháo đang hoạt động
				p.updatePos(); // Cập nhật vị trí đạn
				if (p.getHitbox().intersects(player.getHitbox())) { // Nếu đạn chạm vào người chơi
					player.changeHealth(PROJECTILES_DAMAGE); // Gây sát thương cho người chơi
					p.setActive(false); // Vô hiệu hóa đạn
				} else if (IsProjectileHittingLevel(p, lvlData)) // Nếu đạn chạm vào địa hình
					p.setActive(false); // Vô hiệu hóa đạn
			}
	}
	
	private void updateShips() {
		for (Ship s : currentLevel.getShips())
			s.update();
	}

	// Kiểm tra xem người chơi có trong phạm vi của pháo không
	private boolean isPlayerInRange(Cannon c, Player player) {
		int absValue = (int) Math.abs(player.getHitbox().x - c.getHitbox().x); // Tính khoảng cách giữa pháo và người chơi
		return absValue <= Game.TILES_SIZE * 5; // Nếu khoảng cách <= 5 ô gạch thì trả về true
	}
	
	// Cần kiểm tra các điều kiện cơ bản để xem, quả pháo khi nào thì bắn? khi nào thì không bắn?
	private void updateCannons(int[][] lvlData, Player player) {
		for (Cannon c : currentLevel.getCannons()) {
			if (!c.doAnimation)
				if (c.getTileY() == player.getTileY())
					if (isPlayerInRange(c, player))
						if (isPlayerInfrontOfCannon(c, player))
							if (CanCannonSeePlayer(lvlData, player.getHitbox(), c.getHitbox(), c.getTileY())) {
								c.setAnimation(true);
							}
								
			c.update();
			if (c.getAniIndex() == 4 && c.getAniTick() == 0) {
				shootCannon(c);
			}
		}
	}

	// Kiểm tra xem người chơi có đang ở phía trước pháo không
	private boolean isPlayerInfrontOfCannon(Cannon c, Player player) {
		if (c.getObjType() == CANNON_LEFT) {
			if (c.getHitbox().x > player.getHitbox().x)
				return true;

		} else if (c.getHitbox().x < player.getHitbox().x)
			return true;

		return false;
	}
	
	private void shootCannon(Cannon c) {
		int dir = 1;
		if (c.getObjType() == CANNON_LEFT)
			dir = -1;
			projectiles.add(new Projectile((int) c.getHitbox().x, (int) c.getHitbox().y, dir));
	}

	// Vẽ các đối tượng trong game ra màn hình
	public void draw(Graphics g, int xLvlOffset) {
		drawPotions(g, xLvlOffset); // Vẽ các vật phẩm
		drawContainers(g, xLvlOffset); // Vẽ các thùng chứa
		drawTraps(g, xLvlOffset); // Vẽ bẫy gai
		drawCannons(g, xLvlOffset); // Vẽ pháo
		drawProjectiles(g, xLvlOffset); // Vẽ đạn pháo
		drawGrass(g, xLvlOffset); //Vẽ cỏ
		drawShips(g, xLvlOffset); //Vẽ thuyền
	}
	private void drawGrass(Graphics g, int xLvlOffset) {
    // Vẽ cỏ trên màn hình, với mỗi loại cỏ được xác định bởi type và vị trí được tính toán dựa trên offset
    for (Grass grass : currentLevel.getGrass())
        g.drawImage(grassImgs[grass.getType()], grass.getX() - xLvlOffset, grass.getY(), (int) (32 * Game.SCALE), (int) (32 * Game.SCALE), null);
}

	public void drawBackgroundTrees(Graphics g, int xLvlOffset) {
	// Vẽ các cây nền với animation dựa trên type của cây
		for (BackgroundTree bt : currentLevel.getTrees()) {
			int type = bt.getType();
			if (type == 9) // Nếu type là 9, điều chỉnh lại để vẽ như type 8
				type = 8;
				// Vẽ cây với vị trí và kích thước phù hợp dựa trên type của cây
				g.drawImage(treeImgs[type - 7][bt.getAniIndex()], bt.getX() - xLvlOffset + GetTreeOffsetX(bt.getType()), (int) (bt.getY() + GetTreeOffsetY(bt.getType())), GetTreeWidth(bt.getType()),
	                GetTreeHeight(bt.getType()), null);
		}
	}
	
	private void drawProjectiles(Graphics g, int xLvlOffset) {
	    // Vẽ các đạn pháo (projectile) trên màn hình, chỉ vẽ nếu đạn pháo đang hoạt động
	    for (Projectile p : projectiles)
	        if (p.isActive())
	            g.drawImage(cannonBallImg, (int) (p.getHitbox().x - xLvlOffset), (int) (p.getHitbox().y), CANNON_BALL_WIDTH, CANNON_BALL_HEIGHT, null);
	}
	
	private void drawCannons(Graphics g, int xLvlOffset) {
	    // Vẽ pháo (cannons) trên màn hình
	    for (Cannon c : currentLevel.getCannons()) {
	        int x = (int) (c.getHitbox().x - xLvlOffset); // Tính toán vị trí X của pháo dựa trên offset
	        int width = CANNON_WIDTH;
	
	        if (c.getObjType() == CANNON_RIGHT) { // Nếu là pháo bên phải, điều chỉnh vị trí và chiều rộng để vẽ đối xứng
	            x += width;
	            width *= -1;
	        }
	        // Vẽ pháo với animation tương ứng
	        g.drawImage(cannonImgs[c.getAniIndex()], x, (int) (c.getHitbox().y), width, CANNON_HEIGHT, null);
	    }
	}
	
	private void drawTraps(Graphics g, int xLvlOffset) {
	    // Vẽ các bẫy gai (spikes) trên màn hình
	    for (Spike s : currentLevel.getSpikes())
	        g.drawImage(spikeImg, (int) (s.getHitbox().x - xLvlOffset), (int) (s.getHitbox().y - s.getyDrawOffset()), SPIKE_WIDTH, SPIKE_HEIGHT, null);
	}
	
	private void drawContainers(Graphics g, int xLvlOffset) {
	    // Vẽ các thùng chứa (containers) trên màn hình
	    for (GameContainer gc : containers)
	        if (gc.isActive()) { // Chỉ vẽ nếu thùng chứa còn hoạt động
	            int type = 0;
	            if (gc.getObjType() == BARREL) // Nếu thùng chứa là barrel thì type = 1
	                type = 1;
	            // Vẽ thùng chứa với animation tương ứng
	            g.drawImage(containerImgs[type][gc.getAniIndex()], 
	            		(int) (gc.getHitbox().x - gc.getxDrawOffset() - xLvlOffset), 
	            		(int) (gc.getHitbox().y - gc.getyDrawOffset()), 
	            		CONTAINER_WIDTH, CONTAINER_HEIGHT, null);
	        }
	}
	
	private void drawPotions(Graphics g, int xLvlOffset) {
	    // Vẽ các vật phẩm (potions) trên màn hình
	    for (Potion p : potions)
	        if (p.isActive()) { // Chỉ vẽ nếu vật phẩm còn hoạt động
	            int type = 0;
	            if (p.getObjType() == RED_POTION) // Nếu vật phẩm là lọ đỏ thì type = 1
	                type = 1;
	            // Vẽ vật phẩm với animation tương ứng
	            g.drawImage(potionImgs[type][p.getAniIndex()], (int) (p.getHitbox().x - p.getxDrawOffset() - xLvlOffset), (int) (p.getHitbox().y - p.getyDrawOffset()), POTION_WIDTH, POTION_HEIGHT,
	                    null);
	        }
	}
	
	private void drawShips(Graphics g, int xLvlOffset) {
	    // Vẽ các con thuyền (ships) trên màn hình
	    for (Ship s : currentLevel.getShips())
	        g.drawImage(shipImgs[s.getAniIndex()], 
	        		(int) (s.getHitbox().x - xLvlOffset), 
	        		(int) (s.getHitbox().y - s.getyDrawOffset() + s.getShipHeightDelta()), 
	        		SHIP_WIDTH, SHIP_HEIGHT, null);
	    
	    
	}

	
	public void resetAllObjects() {
	    // Đặt lại trạng thái của tất cả các đối tượng (vật phẩm, thùng chứa, pháo) về trạng thái ban đầu khi người chơi bắt đầu lại
		loadObjects(playing.getLevelManager().getCurrentLevel()); // Tải lại các đối tượng từ cấp độ hiện tại
		for (Potion p : potions)
			p.reset(); // Đặt lại trạng thái của từng vật phẩm
	    for (GameContainer gc : containers)
	    	gc.reset(); // Đặt lại trạng thái của từng thùng chứa
	    for (Cannon c : currentLevel.getCannons())
	    	c.reset(); // Đặt lại trạng thái của từng pháo
	}
}
