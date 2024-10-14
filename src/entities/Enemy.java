package entities;
import main.Game;

import java.awt.geom.Rectangle2D;
import java.util.spi.AbstractResourceBundleProvider;

import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.*;
import static utilz.Constants.Directions.*;

public abstract class Enemy extends Entity{
    protected int aniIndex, enemyState, enemyType;
    protected int aniTick, aniSpeed = 25;
    protected boolean firstUpdate = true, inAir = false;
    protected float gravity = 0.04f * Game.SCALE;
    protected float fallSpeed = 0;

    protected float walkSpeed = 0.5f * Game.SCALE;
    protected int walkDir = LEFT;

    protected int tileY;

    protected float attackDistance = Game.TILES_SIZE;
    // Tam danh
    // player ma trong khoang nay cua enemy thi enemy se tan cong player

    protected int maxHealth;
    protected int currentHealth;
    protected boolean active = true;
    protected boolean attackChecked;

    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        initHitbox(x, y, width, height);
        this.enemyType = enemyType;
        maxHealth = GetMaxHealth(enemyType);
        currentHealth = maxHealth;
    }

    protected void firstUpdateCheck(int[][] lvldata) {
        if (!IsEntityOnFloor(hitBox, lvldata))
            inAir = true;
        firstUpdate = false;
    }

    protected void updateInAir(int[][] lvldata) {
        //System.out.println(fallSpeed);
        // Truong hop ma enemy dang in air thi can de no tiep dat
        if(CanMoveHere(hitBox.x, hitBox.y + fallSpeed, hitBox.width, hitBox.height, lvldata)) {
            hitBox.y += fallSpeed;
            fallSpeed += gravity;

        }
        else {
            inAir = false;
            hitBox.y = GetEntityYPosUnderRoofOrAboveFloor(hitBox, fallSpeed);
            tileY = (int)(hitBox.y / Game.TILES_SIZE);
        }
    }

    protected void move(int[][] lvldata) {
        float xSpeed = 0;
        if(walkDir == LEFT)
            xSpeed = -walkSpeed;
        else
            xSpeed = walkSpeed;
        if(CanMoveHere(hitBox.x + xSpeed, hitBox.y, hitBox.width, hitBox.height, lvldata)) {
            if(IsFloor(hitBox, xSpeed, lvldata)) {
                hitBox.x += xSpeed;
                return;
            }
        }
        changeWalkDir();
    }

    protected void turnTowardsPlayer(Player player) {
        if(player.hitBox.x > hitBox.x) {
            walkDir = RIGHT;
        }
        else
            walkDir = LEFT;
    }

    protected boolean canSeePlayer(int[][] lvldata, Player player) {
        //Kiem tra xem enemy va player co cung hang khong
        int playerTileY = (int)(player.getHitbox().y / Game.TILES_SIZE);
        if(playerTileY == tileY) {
            if(isPlayerInRange(player)) {
                // Kiem tra xem player co trong tam nhin khong
                if(IsSightClear(lvldata, hitBox, player.hitBox, tileY)) {
                    // Khong co vat can giua enemy va player
                    return true;
                }
            }
        }
        return false;
    }


    protected boolean isPlayerInRange(Player player) {
        int absValue = (int)Math.abs(player.hitBox.x - hitBox.x);
        return (absValue <= attackDistance * 5);
        // Kiem tra xem player co trong tam nhin cua enemy khong
        // Lay tam nhin bang 5 lan tam danh
    }

    protected boolean isPlayerCloseforAttack(Player player) {
        int absValue = (int)Math.abs(player.hitBox.x - hitBox.x);
        return (absValue <= attackDistance);
        // Kiem tra player co trong tam danh khong
    }

    protected void newState(int enemyState) {
        this.enemyState = enemyState;
        aniTick = 0;
        aniIndex = 0;
    }

    public void hurt(int amount) {
        currentHealth -= amount;
        if(currentHealth <= 0)
            newState(DEAD);
        else
            newState(HIT);
    }

    protected void checkEnemyHit(Rectangle2D.Float attackbox, Player player) {
        if(attackbox.intersects(player.hitBox))
            player.changeHealth(-GetEnemyDmg(enemyType));
        attackChecked = true;
    }


    protected void updateAnimationTick() {
        aniTick++;
        if(aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if(aniIndex >= GetSpriteAmount(enemyType, enemyState)) {
                aniIndex = 0;
                switch (enemyState) {
                    case ATTACK, HIT -> enemyState = IDLE;
                    case DEAD -> active = false;
                }
            }
        }
    }

    protected void changeWalkDir() {
        if(walkDir == LEFT)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }

    public int getAniIndex() {
        return aniIndex;
    }

    public int getEnemyState() {
        return enemyState;
    }

    public boolean isActive() {
        return active;
    }

    public void resetEnemy() {
        hitBox.x = x;
        hitBox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(IDLE);
        active = true;
        fallSpeed = 0;
    }
}
