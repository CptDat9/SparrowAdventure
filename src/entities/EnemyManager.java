package entities;

import gamestates.Playing;
import utilz.LoadSave;
import static utilz.Constants.EnemyConstants.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class EnemyManager {
    private Playing playing;
    private BufferedImage[][] crabbyArr;
    private ArrayList<Crabby> crabbies = new ArrayList<>();

    public EnemyManager(Playing playing) {
        this.playing = playing;
        loadEnemyImgs();
        addEnemies();
    }

    private void addEnemies() {
        crabbies = LoadSave.GetCrabs();
    }

    public void update(int[][] lvldata, Player player) {
        for(Crabby c : crabbies) if(c.isActive()) {
            c.update(lvldata, player);
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawCrabs(g, xLvlOffset);
    }

    private void drawCrabs(Graphics g, int xlvloffset) {
        for(Crabby c : crabbies) if(c.isActive()) {
            g.drawImage(crabbyArr[c.getEnemyState()][c.getAniIndex()], (int) c.getHitbox().x - xlvloffset - CRABBY_DRAWOFFSET_X + c.flipX(), (int) c.getHitbox().y - CRABBY_DRAWOFFSET_Y, CRABBY_WIDTH * c.flipW(), CRABBY_HEIGHT, null);
            //c.drawAttackBox(g, xlvloffset);
        }
    }

    public void checkEnemyHit(Rectangle2D.Float attackbox) {
        for(Crabby c : crabbies)
            if(c.isActive()) {
                if (attackbox.intersects(c.getHitbox())) {
                    c.hurt(10);
                    return;
                }
            }


    }

    private void loadEnemyImgs() {
        crabbyArr = new BufferedImage[5][9];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE);
        for(int j = 0; j < crabbyArr.length; j++)
            for(int i = 0; i < crabbyArr[j].length; i++)
                crabbyArr[j][i] = temp.getSubimage(i * CRABBY_WIDTH_DEFAULT, j * CRABBY_HEIGHT_DEFAULT, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
    }

    public void resetAllEnemies() {
        for(Crabby c : crabbies)
            c.resetEnemy();
    }
}
