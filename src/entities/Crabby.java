package entities;

import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.EnemyConstants.GetSpriteAmount;
import static utilz.HelpMethods.IsFloor;
import static utilz.Constants.ANI_SPEED;

import gamestates.Playing;

public class Crabby extends Enemy {

	//(x, y) là vị trí mà Crabby spawn
	public Crabby(float x, float y) {
		super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
		
		//Hitbox của Crabby là 22x19
		initHitbox(22, 19);
		
		//AttackBox của Craaby là 30pixel về bên trái và bên phải -> tổng độ dài AttackBox = 22+30+30 = 82
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
					//Chỉ khi turnToward đủ gần rồi mới chiến
					if (isPlayerCloseForAttack(playing.getPlayer()))
						newState(ATTACK);
				}
				move(lvlData);
				break;
			case ATTACK:
				if (aniIndex == 0)
					attackChecked = false;
				/*
				 Kiem tra attack chi kiem tra 1 lan va ta chon
                 animation thu aniIndex de kiem tra 
                 
				 checkPlayerHit() chỉ xảy ra đúng 1 lần, do sau khi thực hiện hàm đó, attackChecked = true
				 aniIndex == 3 -> đúng thời điểm con cua búng càng
				 */
				if (aniIndex == 3 && !attackChecked)
					checkPlayerHit(attackBox, playing.getPlayer());
				break;
			case HIT:
				if (aniIndex <= GetSpriteAmount(enemyType, state) - 2)
					pushBack(pushBackDir, lvlData, 0.75f);
				updatePushBackDrawOffset();
				break;
			}
		}
	}
	
	@Override
	protected void updateAnimationTick() {
		aniTick++;
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(CRABBY, state)) {
				aniIndex = 0;
				switch (state) {
					case ATTACK, HIT -> newState(IDLE);
					case DEAD -> active = false;
				}
			}
		}
	}
}