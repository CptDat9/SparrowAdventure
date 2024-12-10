package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

/**
 * Lớp GameCompletedOverlay hiển thị giao diện khi trò chơi đã hoàn thành.
 * Nó bao gồm hình ảnh và các nút để người chơi có thể chọn quay lại menu hoặc xem thông tin tín dụng.
 */
public class GameCompletedOverlay {
    private Playing playing; 
	// Tham chiếu đến trạng thái trò chơi đang chơi
    private BufferedImage img;
	// Hình ảnh hiển thị khi trò chơi hoàn thành
    private MenuButton quit, credit; 
	// Các nút "Quay lại menu" và "Credit"
    private int imgX, imgY, imgW, imgH; 
	// Toạ độ và kích thước của hình ảnh

    /**
     * Hàm khởi tạo GameCompletedOverlay.
     *  playing Tham chiếu đến đối tượng Playing, cho phép truy cập các phương thức và thuộc tính của nó.
     */
    public GameCompletedOverlay(Playing playing) {
        this.playing = playing; // Gán tham chiếu đến trạng thái chơi
        createImg(); // Tạo hình ảnh hiển thị
        createButtons(); // Tạo các nút
    }

    /**
     * Tạo các nút cho giao diện hoàn thành trò chơi.
     * Bao gồm nút "Quay lại menu" và nút "Thông tin tín dụng".
     */
    private void createButtons() {
        // Khởi tạo nút "Quay lại menu" với vị trí và kích thước cụ thể
        quit = new MenuButton(Game.GAME_WIDTH / 2, (int) (270 * Game.SCALE), 2, Gamestate.MENU);
        // Khởi tạo nút "Thông tin tín dụng" với vị trí và kích thước cụ thể
        credit = new MenuButton(Game.GAME_WIDTH / 2, (int) (200 * Game.SCALE), 3, Gamestate.CREDITS);
    }

    /**
     * Tạo hình ảnh hiển thị cho giao diện hoàn thành trò chơi.
     * Lấy hình ảnh từ sprite sheet và tính toán kích thước, vị trí để hiển thị.
     */
    private void createImg() {
        img = LoadSave.GetSpriteAtlas(LoadSave.GAME_COMPLETED); // Lấy sprite sheet của hình ảnh hoàn thành
        imgW = (int) (img.getWidth() * Game.SCALE); // Tính chiều rộng hình ảnh
        imgH = (int) (img.getHeight() * Game.SCALE); // Tính chiều cao hình ảnh
        imgX = Game.GAME_WIDTH / 2 - imgW / 2; // Tính toạ độ x để căn giữa hình ảnh
        imgY = (int) (100 * Game.SCALE); // Tính toạ độ y để hiển thị hình ảnh
    }

    /**
     * Phương thức vẽ giao diện hoàn thành trò chơi lên màn hình.
     *   g Đối tượng Graphics để vẽ hình ảnh và các nút.
     */
    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200)); // Đặt màu nền với độ trong suốt
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT); // Vẽ hình chữ nhật che toàn màn hình

        g.drawImage(img, imgX, imgY, imgW, imgH, null); // Vẽ hình ảnh hoàn thành

        // Vẽ các nút "Quay lại menu" và "Thông tin tín dụng"
        credit.draw(g);
        quit.draw(g);
    }

    /**
     * Cập nhật trạng thái của các nút khi giao diện hoàn thành trò chơi được vẽ lại.
     */
    public void update() {
        credit.update(); // Cập nhật trạng thái nút "Thông tin tín dụng"
        quit.update(); // Cập nhật trạng thái nút "Quay lại menu"
    }

    /**
     * Kiểm tra xem con chuột có nằm trong phạm vi của nút không.
     *   b Nút cần kiểm tra.
     *   e Sự kiện chuột chứa thông tin vị trí chuột.
     * @return true nếu chuột nằm trong nút, ngược lại false.
     */
    private boolean isIn(MenuButton b, MouseEvent e) {
        return b.getBounds().contains(e.getX(), e.getY()); // Kiểm tra xem tọa độ chuột có nằm trong vùng của nút không
    }

    /**
     * Xử lý sự kiện di chuyển chuột.
     * Cập nhật trạng thái của các nút dựa trên vị trí của chuột.
     *   e Sự kiện chuột chứa thông tin vị trí chuột.
     */
    public void mouseMoved(MouseEvent e) {
        // Đặt trạng thái chuột không di vào cho tất cả các nút
        credit.setMouseOver(false);
        quit.setMouseOver(false);

        // Kiểm tra nếu chuột di vào nút nào thì cập nhật trạng thái tương ứng
        if (isIn(quit, e))
            quit.setMouseOver(true);
        else if (isIn(credit, e))
            credit.setMouseOver(true);
    }

    /**
     * Xử lý sự kiện khi chuột nhả nút.
     * Nếu chuột nhả nút trên một trong các nút, thực hiện hành động tương ứng.
     *   e Sự kiện chuột chứa thông tin vị trí chuột.
     */
    public void mouseReleased(MouseEvent e) {
        if (isIn(quit, e)) { // Nếu nhả chuột trên nút "Quay lại menu"
            if (quit.isMousePressed()) { // Nếu nút đã được nhấn
                playing.resetAll(); // Đặt lại trạng thái chơi
                playing.resetGameCompleted(); // Đặt lại trạng thái hoàn thành trò chơi
                playing.setGamestate(Gamestate.MENU); // Chuyển đến trạng thái menu
            }
        } else if (isIn(credit, e)) { // Nếu nhả chuột trên nút "Thông tin tín dụng"
            if (credit.isMousePressed()) { // Nếu nút đã được nhấn
                playing.resetAll(); // Đặt lại trạng thái chơi
                playing.resetGameCompleted(); // Đặt lại trạng thái hoàn thành trò chơi
                playing.setGamestate(Gamestate.CREDITS); // Chuyển đến trạng thái Credit
            }
        }

        // Đặt lại trạng thái của các nút
        quit.resetBools();
        credit.resetBools();
    }

    /**
     * Xử lý sự kiện khi chuột được nhấn.
     * Ghi nhận nút nào đã được nhấn để xử lý sau này.
     *   e Sự kiện chuột chứa thông tin vị trí chuột.
     */
    public void mousePressed(MouseEvent e) {
        if (isIn(quit, e))
		// Nếu nhấn vào nút "Quay lại menu"
            quit.setMousePressed(true);
        else if (isIn(credit, e))
		// Nếu nhấn vào nút "Thông tin tín dụng"
            credit.setMousePressed(true);
    }
}
