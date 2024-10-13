package gamestates;

import main.Game;
import ui.MenuButton;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;


public class Menu extends State implements Statemethods{
	
	private MenuButton[] buttons = new MenuButton[3];
	private BufferedImage backgroundImg;
	private int menuX, menuY, menuWidth, menuHeight;
	
    /*
     * Khi khởi tạo Menu, ngoài việc khởi tạo đối tượng menu và playing qua Game.java
     * còn phải load các trạng thái của Button trên menu nữa
     */
	public Menu(Game game) {
        super(game);
        loadButtons();
        loadBackground();
    }
	
	/*
	 * Điểm (menuX, menuY) của MENU_BACKGROUND phải nằm ở vị trí sao cho
	 * cả ảnh menu phải nằm ở chính giữa màn hình
	 * điểm menuX, menuY được xác định qua công thức bên dưới
	 * 
	 * Vị trí thích hợp cho menuY là khoảng 25 pixel tính từ phần biên trên cùng
	 */
	private void loadBackground() {
		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
		menuWidth = (int)(backgroundImg.getWidth() * Game.SCALE);
		menuHeight = (int)(backgroundImg.getHeight() * Game.SCALE);
		menuX = Game.GAME_WIDTH/2 - menuWidth/2;
		menuY = (int)(25 * Game.SCALE);
	}
	/**
	 * nên tạo một lớp mới để giải thích công thức loadBackground này rõ ràng hơn
	 **/


	/* Tham số thứ 2: Đại diện cho vị trí cần in ra theo tọa độ y của nút trên menu
	 * Tham số thứ 3: 0 ứng với gamestate = playing 
	 */
    private void loadButtons() {
		buttons[0] = new MenuButton(Game.GAME_WIDTH/2, (int)(150 * Game.SCALE), 0, Gamestate.PLAYING);
		buttons[1] = new MenuButton(Game.GAME_WIDTH/2, (int)(220 * Game.SCALE), 1, Gamestate.OPTIONS);
		buttons[2] = new MenuButton(Game.GAME_WIDTH/2, (int)(290 * Game.SCALE), 2, Gamestate.QUIT);
	}


    /* Trong mỗi lần update hệ thống, duyệt qua toàn bộ các nút hiện đang có trên menu
     * và update chúng 
     */
	@Override
    public void update() {
        for (MenuButton mb: buttons) {
        	mb.update();
        }
    }

    @Override
    public void draw(Graphics g) {
    	g.drawImage(backgroundImg, menuX, menuY, menuWidth, menuHeight, null);
        for (MenuButton mb: buttons) {
        	mb.draw(g);
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
    	
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (MenuButton mb: buttons) {
        	if (isIn(e, mb)) {
        		mb.setMousePressed(true);
        	}
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (MenuButton mb: buttons) {
        	if (isIn(e, mb)) {
        		if(mb.isMousePressed()) {
        			mb.applyGamestate();
        		}
        	}
        	/*
        	 * Giả sử mình click vào nút xong lại kéo chuột ra bên ngoài nút
        	 * Thì hệ thống sẽ không tính là mình "đã click"
        	 * Chỉ coi là bấm nhầm và không thực hiện thêm thao tác
        	 */
        }
        resetButtons(); //Release xong thì phải trả lại trạng thái cho nút
    }

    private void resetButtons() {
    	for (MenuButton mb: buttons) {
    		mb.resetBools();
    	}
	}

    /*
     * Nếu bắt được sự kiện chuột di chuyển, check xem:
     * Nếu chuột ở trong một nút B nào đó thì nút B chuyển trạng thái mouseOver = true
     * Nếu cũng là di chuột mà chuột lại không ở trong nút nào cả thì các nút vẫn mouseOver = false
     */
	@Override
    public void mouseMoved(MouseEvent e) {
       	for (MenuButton mb: buttons) {
    		if (!isIn(e, mb)) {
    			mb.setMouseOver(false);
    		}
    		else mb.setMouseOver(true);
    	}
    }

    @Override
    public void keyPressed(KeyEvent e) {
//        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
//            Gamestate.state = Gamestate.PLAYING;
//        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
