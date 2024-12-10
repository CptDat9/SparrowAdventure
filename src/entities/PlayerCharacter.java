package entities;

import main.Game;
import utilz.LoadSave;

import static utilz.Constants.PlayerConstants.*;

public enum PlayerCharacter {

    PIRATE(5, 6, 3, 1, 3, 4, 8,
            0, 1, 2, 3, 4, 5, 6,
            LoadSave.PLAYER_PIRATE, 7, 8, 64, 40,
            20, 27, 21, 4),
//    ORC(6, 8, 8, 8, 6, 4, 4,
//            0, 1, 1, 1, 2, 4, 5,
//            LoadSave.PLAYER_ORC, 6, 8, 100, 100,
//            13, 15, 44, 42),
//    SOLDIER(6, 8, 8, 8, 6, 4, 4,
//            0, 1, 1, 1, 2, 5, 6,
//            LoadSave.PLAYER_SOLDIER, 7, 8, 100, 100,
//            12, 18, 44, 39),
   	LABUBU(5, 6, 3, 1, 3, 4, 8,
   			0, 1, 2, 3, 4, 5, 6,
            LoadSave.PLAYER_LABUBU, 7, 8, 64, 40,
            20, 27, 21, 4),
	CAPY(5, 6, 3, 1, 3, 4, 8,
            0, 1, 2, 3, 4, 5, 6,
            LoadSave.PLAYER_CAPY, 7, 8, 64, 40,
            20, 27, 21, 4);


    public int spriteA_IDLE, spriteA_RUNNING, spriteA_JUMP, spriteA_FALLING, spriteA_ATTACK, spriteA_HIT, spriteA_DEAD;
    public int rowIDLE, rowRUNNING, rowJUMP, rowFALLING, rowATTACK, rowHIT, rowDEAD;
    public String playerAtlas;
    public int rowA, colA;
    public int spriteW, spriteH;
    public int hitboxW, hitboxH;
    public int xDrawOffset, yDrawOffset;
    
    /*
     private float xDrawOffset = 21 * Game.SCALE;
    private float yDrawOffset = 4 * Game.SCALE;
     */

    PlayerCharacter(int spriteA_IDLE, int spriteA_RUNNING, int spriteA_JUMP, int spriteA_FALLING, int spriteA_ATTACK, int spriteA_HIT, int spriteA_DEAD,
                    int rowIDLE, int rowRUNNING, int rowJUMP, int rowFALLING, int rowATTACK, int rowHIT, int rowDEAD,
                    String playerAtlas, int rowA, int colA, int spriteW, int spriteH,
                    int hitboxW, int hitboxH,
                    int xDrawOffset, int yDrawOffset) {

        this.spriteA_IDLE = spriteA_IDLE;
        this.spriteA_RUNNING = spriteA_RUNNING;
        this.spriteA_JUMP = spriteA_JUMP;
        this.spriteA_FALLING = spriteA_FALLING;
        this.spriteA_ATTACK = spriteA_ATTACK;
        this.spriteA_HIT = spriteA_HIT;
        this.spriteA_DEAD = spriteA_DEAD;

        this.rowIDLE = rowIDLE;
        this.rowRUNNING = rowRUNNING;
        this.rowJUMP = rowJUMP;
        this.rowFALLING = rowFALLING;
        this.rowATTACK = rowATTACK;
        this.rowHIT = rowHIT;
        this.rowDEAD = rowDEAD;

        this.playerAtlas = playerAtlas;
        this.rowA = rowA;
        this.colA = colA;
        this.spriteW = spriteW;
        this.spriteH = spriteH;

        this.hitboxW = hitboxW;
        this.hitboxH = hitboxH;

        this.xDrawOffset = (int) (xDrawOffset * Game.SCALE);
        this.yDrawOffset = (int) (yDrawOffset * Game.SCALE);
    }

    public int getSpriteAmount(int player_action) {
        return switch (player_action) {
            case IDLE -> spriteA_IDLE;
            case RUNNING -> spriteA_RUNNING;
            case JUMP -> spriteA_JUMP;
            case FALLING -> spriteA_FALLING;
            case ATTACK -> spriteA_ATTACK;
            case HIT -> spriteA_HIT;
            case DEAD -> spriteA_DEAD;
            default -> 1;
        };
    }

    public int getRowIndex(int player_action) {
        return switch (player_action) {
            case IDLE -> rowIDLE;
            case RUNNING -> rowRUNNING;
            case JUMP -> rowJUMP;
            case FALLING -> rowFALLING;
            case ATTACK -> rowATTACK;
            case HIT -> rowHIT;
            case DEAD -> rowDEAD;
            default -> 1;
        };
    }

}
