package entities;

import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.IsFloor;
import static utilz.Constants.Dialogue.*;

import gamestates.Playing;

public class Crabby extends Enemy {

	//(x, y) là vị trí mà Crabby spawn
	public Crabby(float x, float y) {
		super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
		
		//Hitbox của Crabby là 22x19
		initHitbox(22, 19);
		
		initAttackBox(82, 19, 30);
	}

	public void update(int[][] lvlData, Playing playing) {
		updateBehavior(lvlData, playing);
		updateAnimationTick();
		updateAttackBox();
	}

	private void updateBehavior(int[][] lvlData, Playing playing) {
		if (firstUpdate) firstUpdateCheck(lvlData);

		if (inAir) {
			inAirChecks(lvlData, playing);
		} else {
			switch (state) {
			case IDLE:
				if (IsFloor(hitbox, lvlData))
					newState(RUNNING);
				else
					inAir = true;
				break;
			case RUNNING:
				//Chỉ khi nhìn thấy player mới turn_towards
				//Không phải tự nhiên mà đang running lại turn_towards ngay
				if (canSeePlayer(lvlData, playing.getPlayer())) {
					turnTowardsPlayer(playing.getPlayer());
					//Nếu đủ gần thì chiến luôn
					if (isPlayerCloseForAttack(playing.getPlayer()))
						newState(ATTACK);
				}
				move(lvlData);

				if (inAir)
					playing.addDialogue((int) hitbox.x, (int) hitbox.y, EXCLAMATION);

				break;
			case ATTACK:
				if (aniIndex == 0)
					attackChecked = false;
				// Kiem tra attack chi kiem tra 1 lan va ta chon
                // animation thu aniIndex de kiem tra
				if (aniIndex == 3 && !attackChecked)
					checkPlayerHit(attackBox, playing.getPlayer());
				break;
			case HIT:
				if (aniIndex <= GetSpriteAmount(enemyType, state) - 2)
					pushBack(pushBackDir, lvlData, 2f);
				updatePushBackDrawOffset();
				break;
			}
		}
	}

}