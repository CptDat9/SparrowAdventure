package ui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;
import static utilz.Constants.UI.URMButtons.*;

public class PauseOverlay {    // Lớp này đại diện cho giao diện tạm dừng trong trò chơi
    // biến lưu trạng thái trò chơi hiện tại (đang chơi)
    private Playing playing;
    
    // hình nền của giao diện tạm dừng
    private BufferedImage backgroundImg;
    
    // các biến để xác định vị trí và kích thước của hình nền
    private int bgX, bgY, bgW, bgH;
    
    // tùy chọn âm thanh trong giao diện tạm dừng
    private AudioOptions audioOptions;
    
    // Các nút trên giao diện tạm dừng: menu, chơi lại và tiếp tục
    private UrmButton menuB, replayB, unpauseB;

    // Hàm khởi tạo cho lớp, nhận đối tượng Playing để biết trạng thái trò chơi
    public PauseOverlay(Playing playing) {
        this.playing = playing;
        loadBackground(); // Tải hình nền của giao diện tạm dừng
        audioOptions = playing.getGame().getAudioOptions(); // Lấy các tùy chọn âm thanh
        createUrmButtons(); // Tạo các nút cho giao diện tạm dừng
    }

    // Phương thức tạo các nút "menu", "replay" và "unpause"
    private void createUrmButtons() {
        // Tính toán vị trí của các nút dựa trên tỉ lệ màn hình của game
        int menuX = (int) (313 * Game.SCALE);
        int replayX = (int) (387 * Game.SCALE);
        int unpauseX = (int) (462 * Game.SCALE);
        int bY = (int) (325 * Game.SCALE);

        // Khởi tạo các nút với vị trí và kích thước
        menuB = new UrmButton(menuX, bY, URM_SIZE, URM_SIZE, 2);
        replayB = new UrmButton(replayX, bY, URM_SIZE, URM_SIZE, 1);
        unpauseB = new UrmButton(unpauseX, bY, URM_SIZE, URM_SIZE, 0);
        //Các nút menu, replay hay unpause có rowIndex để vẽ khác nhau là 2,1,0
    }

    // Phương thức tải hình nền từ tài nguyên
    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND); //Tải qua hàm GetSpriteAtals, tên file là PAUSE_BACKGROUND được định nghĩa trong lớp LoadSave
        
        //Tính chiều cao, chiều rộng của hình cần vẽ ra màn hình
        bgW = (int) (backgroundImg.getWidth() * Game.SCALE); 
        bgH = (int) (backgroundImg.getHeight() * Game.SCALE); 
        
        bgX = Game.GAME_WIDTH / 2 - bgW / 2; // Căn giữa hình nền theo chiều ngang
        bgY = (int) (25 * Game.SCALE); // Đặt vị trí y của hình nền
    }

    // Phương thức cập nhật trạng thái của các nút và âm thanh
    public void update() {
        menuB.update();
        replayB.update();
        unpauseB.update();
        audioOptions.update(); // Cập nhật các tùy chọn âm thanh
    }

    // Phương thức vẽ giao diện tạm dừng lên màn hình
    public void draw(Graphics g) {
        // Vẽ hình nền
        g.drawImage(backgroundImg, bgX, bgY, bgW, bgH, null);

        // Vẽ các nút "menu", "replay" và "unpause"
        menuB.draw(g);
        replayB.draw(g);
        unpauseB.draw(g);

        // Vẽ các tùy chọn âm thanh
        audioOptions.draw(g);
    }

    // xử lý khi người dùng kéo chuột (liên quan đến tùy chọn âm thanh)
    public void mouseDragged(MouseEvent e) {
        audioOptions.mouseDragged(e);
    }

    // xử lý sự kiện khi người dùng nhấn chuột
    public void mousePressed(MouseEvent e) {
        if (isIn(e, menuB))
            menuB.setMousePressed(true); // Nếu nhấn vào nút menu
        else if (isIn(e, replayB))
            replayB.setMousePressed(true); // Nếu nhấn vào nút replay
        else if (isIn(e, unpauseB))
            unpauseB.setMousePressed(true); // Nếu nhấn vào nút unpause
        else
            audioOptions.mousePressed(e); // Nếu nhấn vào vùng tùy chọn âm thanh
    }

    // xử lý khi người dùng thả chuột
    public void mouseReleased(MouseEvent e) {
        if (isIn(e, menuB)) {
            if (menuB.isMousePressed()) {
                playing.resetAll(); // Reset lại tất cả trạng thái chơi
                playing.setGamestate(Gamestate.MENU); // Chuyển về màn hình menu
                playing.unpauseGame(); // Thoát khỏi trạng thái tạm dừng
            }
        } else if (isIn(e, replayB)) {
            if (replayB.isMousePressed()) {
                playing.resetAll(); // Reset lại trò chơi
                playing.unpauseGame(); // Thoát khỏi trạng thái tạm dừng
            }
        } else if (isIn(e, unpauseB)) {
            if (unpauseB.isMousePressed())
                playing.unpauseGame(); // Nếu nhấn vào nút unpause, tiếp tục chơi
        } else
            audioOptions.mouseReleased(e); // Nếu liên quan đến tùy chọn âm thanh

        // reset trạng thái của các nút
        menuB.resetBools();
        replayB.resetBools();
        unpauseB.resetBools();
    }

    // xử lý khi di chuyển chuột qua các nút
    public void mouseMoved(MouseEvent e) {
        menuB.setMouseOver(false);
        replayB.setMouseOver(false);
        unpauseB.setMouseOver(false);

        if (isIn(e, menuB))
            menuB.setMouseOver(true); // Nếu di chuyển chuột qua nút menu
        else if (isIn(e, replayB))
            replayB.setMouseOver(true); // Nếu di chuyển chuột qua nút replay
        else if (isIn(e, unpauseB))
            unpauseB.setMouseOver(true); // Nếu di chuyển chuột qua nút unpause
        else
            audioOptions.mouseMoved(e); // Nếu liên quan đến tùy chọn âm thanh
    }

    // Phương thức kiểm tra chuột có nằm trong vùng của một nút không
    private boolean isIn(MouseEvent e, PauseButton b) {
        return b.getBounds().contains(e.getX(), e.getY());
    }

}
