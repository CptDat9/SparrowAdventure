package objects;

import main.Game;

public class Ship extends GameObject { 
    private int shipAni, shipTick, shipDir = 1;
    private float shipHeightDelta, shipHeightChange = 0.05f * Game.SCALE;
    public Ship(int x, int y, int objType) {
        super(x, y, objType);      
        initHitbox(78, 72);  
        
        xDrawOffset = 0;             
        yDrawOffset = (int)(Game.SCALE * 16);
        
        hitbox.y += yDrawOffset;
    }
        
    public void update() {
    	shipTick++;
    	if (shipTick >= 35) {
    		shipTick = 0;
    		shipAni++;
    		if (shipAni >= 4)
    			shipAni = 0;
    	}

    	shipHeightDelta += shipHeightChange * shipDir;
    	shipHeightDelta = Math.max(Math.min(10 * Game.SCALE, shipHeightDelta), 0);

    	if (shipHeightDelta == 0)
    		shipDir = 1;
    	else if (shipHeightDelta == 10 * Game.SCALE)
    		shipDir = -1;
    }

    public float getShipHeightDelta() {
    	return shipHeightDelta;
    }
}