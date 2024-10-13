package gamestates;

import java.awt.event.MouseEvent;

import main.Game;
import ui.MenuButton;

public class State {
    protected Game game;

    public State(Game game) {
        this.game = game;
    }
    
    
    // Xét xem một điểm (e.getX(), e.getY()) có nằm trong Rectangle mb.getBounds hay không?
    public boolean isIn(MouseEvent e, MenuButton mb){
    	return mb.getBounds().contains(e.getX(), e.getY());
    }

    public Game getGame() {
        return game;
    }
    
    
}
