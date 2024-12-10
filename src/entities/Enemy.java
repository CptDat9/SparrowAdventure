package entities;

import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.*;

import java.awt.geom.Rectangle2D;

import gamestates.Playing;

import static utilz.Constants.Directions.*;
import static utilz.Constants.*;

import main.Game;

public abstract class Enemy extends Entity {
	protected int enemyType;
	protected boolean firstUpdate = true;
	protected int walkDir = LEFT;
	protected int tileY;
	
    // Tam danh
    // player ma trong khoang nay cua enemy thi enemy se tan cong player
	protected float attackDistance = Game.TILES_SIZE;
	
	protected boolean active = true;
	protected boolean attackChecked;
	protected int attackBoxOffsetX;

	//Loại Enemy khởi tạo được sẽ dựa vào enemyType, có thể là Crabby, Shark, ...
	public Enemy(float x, float y, int width, int height, int enemyType) {
		super(x, y, width, height);
		this.enemyType = enemyType;

		maxHealth = GetMaxHealth(enemyType);
		currentHealth = maxHealth;
		walkSpeed = Game.SCALE * 0.5f;
	}

	
	 //Tọa độ x của AttackBox thực sự được vẽ ra bằng tọa độ x của hitbox nhân vật ban đầu, trừ đi phần offset
	protected void updateAttackBox() {
		attackBox.x = hitbox.x - attackBoxOffsetX;
		attackBox.y = hitbox.y;
	}

	protected void updateAttackBoxFlip() {
		if (walkDir == RIGHT)
			attackBox.x = hitbox.x + hitbox.width;
		else
			attackBox.x = hitbox.x - attackBoxOffsetX;

		attackBox.y = hitbox.y;
	}

	protected void initAttackBox(int w, int h, int attackBoxOffsetX) {
		attackBox = new Rectangle2D.Float(x, y, (int) (w * Game.SCALE), (int) (h * Game.SCALE));
		this.attackBoxOffsetX = (int) (Game.SCALE * attackBoxOffsetX);
	}

	//Check xem hitbox (của Enemy nói chung) có chạm vào "đất" của lvlData hay không
	//Chưa chạm đất thì inAir = true
	protected void firstUpdateCheck(int[][] lvlData) {
		if (!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;
		firstUpdate = false;
	}

	protected void inAirChecks(int[][] lvlData, Playing playing) {
		if (state != HIT && state != DEAD) {
			updateInAir(lvlData);
			playing.getObjectManager().checkSpikesTouched(this);
			if (IsEntityInWater(hitbox, lvlData))
				hurt(maxHealth);
		}
	}
	
	//Phải không chạm "đất" hoặc "tường" thì mới bay nhảy được tiếp
	protected void updateInAir(int[][] lvlData) {
	    //System.out.println(fallSpeed);
        // Truong hop ma enemy dang in air thi can de no tiep dat
		if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
			hitbox.y += airSpeed;
			airSpeed += GRAVITY;
		} else {
			inAir = false;
			
			//Tiếp đất rồi khi phải tìm cách để Enemy luôn giữ trên đất
			hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
			//Lấy thông tin về Tile hiện tại để check canSeePlayer
			tileY = (int) (hitbox.y / Game.TILES_SIZE);
		}
	}

	// Nói chung là move enemy theo tọa độ x của nó
	// Enemy sẽ chỉ đi sang trái, phải
	protected void move(int[][] lvlData) {
		float xSpeed = 0;

		if (walkDir == LEFT) xSpeed = -walkSpeed;
		else xSpeed = walkSpeed;

		// Check theo tương tác giữa hitbox của Enemy và các thành phần của một level
		// nếu Enemy còn chưa chạm tường và chưa rơi xuống đất, cứ đi tiếp
		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
			if (IsFloor(hitbox, xSpeed, lvlData)) {
				hitbox.x += xSpeed;
				return;
			}
		
		// Nếu chạm tường thì phải đảo hướng
		changeWalkDir();
	}

	//Chạy về phía nào của player?
	protected void turnTowardsPlayer(Player player) {
		if (player.hitbox.x > hitbox.x)
			walkDir = RIGHT;
		else
			walkDir = LEFT;
	}

	/* Giữa player và Enemy có hai loại tầm nhìn
	 * isSightClear: Không có vật cản ở giữa
	 * isInRange: Trong tầm nhìn khiến cho enemy bị "kích động" và tiến gần đến player thay vì đi xa ra
	 */
	protected boolean canSeePlayer(int[][] lvlData, Player player) {
		//Kiem tra xem enemy va player co cung hang khong
		int playerTileY = (int) (player.getHitbox().y / Game.TILES_SIZE);
		if (playerTileY == tileY)
			if (isPlayerInRange(player)) {
				// Kiem tra xem player co trong tam nhin khong
				if (IsSightClear(lvlData, hitbox, player.hitbox, tileY))
					// Khong co vat can giua enemy va player
					return true;
			}
		return false;
	}

	protected boolean isPlayerInRange(Player player) {
        // Kiem tra xem player co trong tam nhin cua enemy khong
        // Lay tam nhin bang 5 lan tam danh
		int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
		return absValue <= attackDistance * 5;
	}

	protected boolean isPlayerCloseForAttack(Player player) {
		int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
		switch (enemyType) {
		case CRABBY -> {
			return absValue <= attackDistance;
		}
		case SHARK -> {
			return absValue <= attackDistance * 2;
		}
		}
		return false;
	}

	/*
	 * Quái vật nhận sát thương
	 * Máu = 0 thì phát tín hiệu state hiện tại = DEAD
	 * Máu khác 0 thì phát tín hiệu state hiện tại = HIT
	 */
	public void hurt(int amount) {
		currentHealth -= amount;
		if (currentHealth <= 0) {
			newState(DEAD);
		}
		else {
			newState(HIT);
			if (walkDir == LEFT)
				pushBackDir = RIGHT;
			else
				pushBackDir = LEFT;
			pushBackOffsetDir = UP;
			pushDrawOffset = 0;
		}
	}

	protected void checkPlayerHit(Rectangle2D.Float attackBox, Player player) {
		if (attackBox.intersects(player.hitbox))
			player.changeHealth(-GetEnemyDmg(enemyType), this);
		else {
			if (enemyType == SHARK)
				return;
		}
		attackChecked = true;
	}

	//Lưu ý rằng updateAnimationTick của Player và của Enemy sẽ khác nhau
	//Các quái vật như Crabby, Shark, PinkStar sẽ Override hàm này, vì chúng có animation tick khác nhau
	protected abstract void updateAnimationTick();

	//Trường hợp kẻ địch đập vào tường thì phải quay ngược lại
	protected void changeWalkDir() {
		if (walkDir == LEFT)
			walkDir = RIGHT;
		else
			walkDir = LEFT;
	}

	public void resetEnemy() {
		hitbox.x = x;
		hitbox.y = y;
		firstUpdate = true;
		currentHealth = maxHealth;
		newState(IDLE);
		active = true;
		airSpeed = 0;

		pushDrawOffset = 0;
	}

	public int flipX() {
		if (walkDir == RIGHT)
			return width;
		else
			return 0;
	}

	public int flipW() {
		if (walkDir == RIGHT)
			return -1;
		else
			return 1;
	}

	public boolean isActive() {
		return active;
	}

	public float getPushDrawOffset() {
		return pushDrawOffset;
	}

}
