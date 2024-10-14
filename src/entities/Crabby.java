package entities;
import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.stream.LongStream;

import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.Directions.RIGHT;
import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.*;
import static utilz.Constants.Directions.*;

public class Crabby extends Enemy{
    //AttackBox
    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;

    public Crabby(float x, float y) {
        super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
        initHitbox(x, y, (int)(22 * Game.SCALE), (int)(19 * Game.SCALE));
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(82 * Game.SCALE), (int)(19 * Game.SCALE));
        attackBoxOffsetX = (int)(Game.SCALE * 30);
    }

    public void update(int[][] lvldata, Player player) {
        updateBehavior(lvldata, player);
        updateAnimationTick();
        updateAttackBox();
    }

    private void updateAttackBox() {
        attackBox.x = hitBox.x - attackBoxOffsetX;
        attackBox.y = hitBox.y;
    }

    private void updateBehavior(int[][] lvldata, Player player) {// Giong voi player
        if(firstUpdate)
            firstUpdateCheck(lvldata);

        if(inAir) {
            updateInAir(lvldata);
        }
        else {
            switch (enemyState) {
                case IDLE:
                    newState(RUNNING);
                    break;
                case RUNNING:
                    if(canSeePlayer(lvldata, player))
                        turnTowardsPlayer(player);
                    if(isPlayerCloseforAttack(player))
                        newState(ATTACK);

                    move(lvldata);
                    break;
                case ATTACK:
                    if(aniIndex == 0)
                        attackChecked = false;
                    // Kiem tra attack chi kiem tra 1 lan va ta chon
                    // animation thu aniIndex de kiem tra
                    if(aniIndex == 3 && !attackChecked) {
                        checkEnemyHit(attackBox, player);
                    }
                    break;
                case HIT:
                    break;
            }
        }

    }

    public void drawAttackBox(Graphics g, int xlvloffset) {
        g.setColor(Color.red);
        g.drawRect((int)attackBox.x - xlvloffset, (int)attackBox.y, (int)attackBox.width, (int)attackBox.height);
    }

    public int flipX() {
        if(walkDir == RIGHT)
            return width;
        return 0;
    }
    public int flipW() {
        if(walkDir == RIGHT) return -1;
        return 1;
    }

}
