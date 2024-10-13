package entities;


import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import utilz.LoadSave;

public class Player extends Entity{
	private BufferedImage[][] animations;
	
	//HINH ANH
	private int aniTick, aniIndex;
	private int aniNum = 26;
	//HINH ANH
	
	//SUBIMAGE SIZE
	private int SUB_WIDTH = 64;
	private int SUB_HEIGHT = 40;
	//SUBIMAGE SIZE
	
	//PLAYER_ACTION
	private int playerAction = IDLE;
	//PLAYER_ACTION
	

	//PLAYER_STATE_MOVING
	private boolean moving = false;
	private boolean left, up, right, down, jump;
	//PLAYER_STATE_MOVING
	
	//PLAYER_STATE_ATTACKING
	private boolean attacking = false; 
	//PLAYER_STATE_ATTACKING
	
	//MOVING PARAMETERS
	private float playerSpeed = 1.2f * Game.SCALE;
	//MOVING PARAMETERS
	
	//CHECK COLLISION 
	private int[][] lvlData; 
	//CHECK COLLISION
	//(có vẻ) đây là cách dễ nhất để check collision giữa một nhân vật và map
	
	//HITBOX START POSITION
	private float xDrawOffset = 21 * Game.SCALE;
	private float yDrawOffset = 4 * Game.SCALE;
	//HITBOX START POSITION
	
	/*Giải thích:
	 * SUB_WIDTH = 64 và SUB_HEIGHT = 40
	 * giờ để đảm bảo các collision của nhân vật chỉ xảy trên biên hitbox của nhân vật
	 * thì mình cần tạo một hitbox có độ lớn khoảng 20*28, chứa đúng hoạt ảnh "hữu dụng" của nhân vật
	 * 
	 * Khi đó phần bị dư ra, nằm giữa biên bên trên của hitbox và biên bên trên của subimage nhân vật
	 * vào khoảng 4px
	 * Tương tự, phần bị dư ra nằm giữa bên bên trái của hitbox và biên trái của subimage nhân vật
	 * vào khoảng 21px
	 * 
	 * Phần bị dư ra này gọi là offset
	 */

	// Jumping / Gravity
	private float airSpeed = 0f;   // Van toc khi nhay hoac roi tu do
	private float gravity = 0.04f * Game.SCALE; // gia toc
	private float jumpSpeed = -2.25f * Game.SCALE; // van toc ban dau khi nhay
	private float fallSpeedAfterCollison = 0.5f * Game.SCALE;
	// van toc roi xuong trong truong hop nhan vat nhay den cham noc
	private boolean inAir = false;

	public Player(float x, float y, int width, int height) {
		super(x, y, width, height);
		loadAnimations();
		initHitbox(x, y, (int)(20 * Game.SCALE), (int)(27 * Game.SCALE)); 
		//cast int để không bị lỗi collision liên tục với mặt đất hoặc bờ tường
	}
	/*
	 * Tư tưởng bây giờ, đó là vị trí (x, y) của nhân vật 
	 * thực ra chính là vị trí ở góc(trái, trên) của hitbox nhân vật
	 * 
	 * Ta làm việc này để tiện thao tác với các biến của hitbox, giúp check collision thuận tiện hơn
	 */
	
	
	public void update() {
		updatePos();
		updateAnimationTick();
		setAnimation();
	}
	
	public void render(Graphics g, int xlvlOffset) {
		g.drawImage(animations[playerAction][aniIndex], (int)(hitBox.x - xDrawOffset) - xlvlOffset, (int)(hitBox.y - yDrawOffset), width, height, null);
		/*
		 Bước 1: vẽ hoạt ảnh nhân vật
		 Ta cần xác định vị trí bắt đầu vẽ hoạt ảnh từ trị ví của hitbox
		 
		 Như trên! 
		 hitbox.x - xdrawoffset là vị trí x đầu tiên để vẽ hoạt ảnh
		 hitbox.y - ydrawoffset là vị trí y đầu tiên để vẽ hoạt ảnh
		 độ lớn của hoạt ảnh này là (width, height)
		 đương nhiên, width và height ở đây tương ứng là SUB_WIDTH * SCALE và SUB_HEIGHT * SCALE được truyền vào thông qua constructor
		 */
		
//		System.out.println((int)(hitBox.x - xDrawOffset) + " " + (int)(hitBox.y - yDrawOffset) + " " + hitBox.x + " " + hitBox.y);
		
		drawHitbox(g);
		/*
		 * Bước 2: vẽ hitbox để debug
		 * (hitbox.x, hitbox.y) ở chỗ nào thì vẽ hitbox debug ở chỗ đó (easy) 
		 */
	}

	private void updateAnimationTick() {
		++aniTick;
		if (aniTick >= aniNum) {
			aniTick = 0;
			++aniIndex;
			if (aniIndex >= GetSpriteAmount(playerAction)) {
				aniIndex = 0;
				attacking = false;
				/*
				 sau khi chạy hàm setAnimation, cho playerAction = ATTACK_1;
				 hàm updatePos sẽ không có thao tác tương ứng cho ATTACK_1
				 nên khi tấn công, nhân vật sẽ đứng im
				 
				 sau khi chạy xong GetSpriteAmout(ATTACK_1), nhân vật ngừng
				 animation tấn công
				 */
			}
		}		
	}
	
	private void setAnimation() {
		int startAni = playerAction;
		
		if (moving) {
			playerAction = RUNNING;
		}
		else playerAction = IDLE;

		if(inAir) {
			if(airSpeed < 0)
				playerAction = JUMP;
			else
				playerAction = FALLING;
		}
		
		if (attacking) {
			playerAction = ATTACK_1;
		}
		
		if (startAni != playerAction){
			resetAniTick();
		}
	}
	
	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;
	}
	/*
	 chả hạn: khi mình đang ở playerAction = IDLE, mà chuyển sang tấn công
	 thì playerAction sẽ thành ATTACK_1
	 
	 Tuy nhiên rất có thể AniIndex của mình 
	 đang nằm ở một ví trí nào đó khác 0 tại thời điểm mình ATTACK
	 do đó, mỗi khi phát hiện playerAction bị thay đổi, 
	 cần chuyển AniIndex và AniTick về 0 ngay
	 */

	private void updatePos() { //di chuyen theo huong
		moving = false;

		if(jump)
			jump();
		
		//if (!left && !right && !inAir) return;

		if(!inAir)
			if((!left && !right) || (right && left))
				return;

		float xSpeed = 0;
		
		if (left)
			xSpeed -= playerSpeed;
		else if (right)
			xSpeed += playerSpeed;

		if(!inAir) {
			if(!IsEntityOnFloor(hitBox, lvlData)) {
				// Truong hop dang di het duong va roi xuong bac duoi
				inAir = true;
			}
		}

		if(inAir) {
			if(CanMoveHere(hitBox.x, hitBox.y + airSpeed, hitBox.width, hitBox.height, lvlData)) {
				hitBox.y += airSpeed;
				airSpeed += gravity;
				updateXPos(xSpeed);
			}
			else {
				// Cham noc hoac tren mat dat
				hitBox.y = GetEntityYPosUnderRoofOrAboveFloor(hitBox, airSpeed);
				if(airSpeed > 0) // Reset sau khi roi xong va cham dat
					resetInAir();
				else
					airSpeed = fallSpeedAfterCollison;
				updateXPos(xSpeed);
			}
		}
		else {
			// Neu khong inAir hay noi cach khac la dung tren mat dat thi chi can kiem tra collision voi XDirecrion
			updateXPos(xSpeed);
		}

		moving = true;

	}

	private void jump() {
		if(inAir) // Neu dang bay
			return;
		// Neu chua bay
		inAir = true;
		airSpeed = jumpSpeed; // van toc ban dau
	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;
	}

	private void updateXPos(float xSpeed) {
		// Ham nay de xem co di chuyen duoc sang phai / trai voi van toc XSpeed khong
		// Neu khong thi ta se di chuyen sao cho hitbox ke voi wall
		if (CanMoveHere(hitBox.x + xSpeed, hitBox.y, hitBox.width, hitBox.height, lvlData)) {
			hitBox.x += xSpeed;
		}
		else {
			// Truong hop van con khoang cach dx rat nho giua hitbox va wall
			// Ta mong muon bien cua hitbox ke voi wall
			hitBox.x = GetEntityXPosNextToWall(hitBox, xSpeed);
		}
	}

	private void loadAnimations() {
		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
		animations = new BufferedImage[9][6]; //cac hoat anh la ma tran 9*6 nhu trong anh o resource
		for (int j = 0; j < animations.length; j++) {
			for (int i = 0; i < animations[j].length; i++) {
				animations[j][i] = img.getSubimage(i*SUB_WIDTH, j*SUB_HEIGHT, SUB_WIDTH, SUB_HEIGHT);
				/* cai dat: subimage cua image ban dau duoc import vao bien animation[j][i]
					
				Lay subimage, vi tri toa do goc (i*64, j*40)
				Kich co cua subimage trong image la 64*40px
				 */
			}
		}
	}
	
	public void loadLvlData(int[][] lvlData) {
		this.lvlData = lvlData;
		if(!IsEntityOnFloor(hitBox, lvlData))
			inAir = true;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}
	
	public void resetDirBooleans() {
		left = right = up = down = false;
	}
	
	public void setAttack(boolean attacking) {
		this.attacking = attacking;		
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}
}
