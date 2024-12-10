package entities;

import static utilz.Constants.Directions.DOWN;
import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.Directions.UP;
import static utilz.HelpMethods.CanMoveHere;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import main.Game;

public abstract class Entity {
	//Vị trí, kích thước vẽ trên màn hình
	protected float x, y;
	protected int width, height;
	
	//Rectangle là một class đặc biệt để tạo hitbox cho nhân vật trong game
	protected Rectangle2D.Float hitbox;  
	
	
	protected int aniTick, aniIndex;
	
	//Trạng thái hiện tại của một Entity
	protected int state;
	
	
	protected float airSpeed;
	protected boolean inAir = false;
	protected int maxHealth;
	protected int currentHealth;
	protected Rectangle2D.Float attackBox;
	protected float walkSpeed;

	protected int pushBackDir;
	protected float pushDrawOffset;
	protected int pushBackOffsetDir = UP;
	
	/*
	 protected: các biến này sẽ được sử dụng ở trong 
	 cả những lớp kế thừa từ lớp này
	 */

	public Entity(float x, float y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/*
	 * Default: speed = 0.95f
	 * limit = -30f
	 */
	protected void updatePushBackDrawOffset() {
		float speed = 0.5f;
		float limit = -15f;

		if (pushBackOffsetDir == UP) {
			pushDrawOffset -= speed;
			if (pushDrawOffset <= limit)
				pushBackOffsetDir = DOWN;
		} else {
			pushDrawOffset += speed;
			if (pushDrawOffset >= 0)
				pushDrawOffset = 0;
		}
	}

	protected void pushBack(int pushBackDir, int[][] lvlData, float speedMulti) {
		float xSpeed = 0;
		if (pushBackDir == LEFT)
			xSpeed = -walkSpeed;
		else
			xSpeed = walkSpeed;

		if (CanMoveHere(hitbox.x + xSpeed * speedMulti, hitbox.y, hitbox.width, hitbox.height, lvlData))
			hitbox.x += xSpeed * speedMulti;
	}

	//AttackHitbox cũng phải bị vẽ lùi với tham số xLvlOffset
	protected void drawAttackBox(Graphics g, int xLvlOffset) {
		g.setColor(Color.red);
		g.drawRect((int) (attackBox.x - xLvlOffset), (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
	}

	//Hitbox cũng phải bị vẽ lùi với tham số xLvlOffset
	protected void drawHitbox(Graphics g, int xLvlOffset) {
		g.setColor(Color.BLUE);
		g.drawRect((int) hitbox.x - xLvlOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
	}

	protected void initHitbox(int width, int height) {
		hitbox = new Rectangle2D.Float(x, y, (int) (width * Game.SCALE), (int) (height * Game.SCALE));
	}

	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}

	public int getState() {
		return state;
	}

	public int getAniIndex() {
		return aniIndex;
	}

	//Nếu trạng thái của nhân vật thay đổi (cả Enemy và Player nói chung), chả hạn: từ RUNNING sang ATTACKING
	//Thì thay đổi luôn biến trạng thái hiện tại.
	//Bắt đầu vẽ lại kiểu hoạt ảnh mới tương ứng với state hiện tại, và gán biến đếm cho số ani đã chạy được về 0
	protected void newState(int state) {
		this.state = state;
		aniTick = 0;
		aniIndex = 0;
	}
}

/*
abstract class để làm gì ??
để sau này một số lớp khác như Player, Enemy,... sẽ được kế thừa
từ lớp này ra với tư cách là một dàn ý bắt buộc 
 */
