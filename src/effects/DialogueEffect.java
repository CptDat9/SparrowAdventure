package effects;

import static utilz.Constants.ANI_SPEED; // Tốc độ hoạt ảnh (animation speed)
import static utilz.Constants.Dialogue.*; // Các hằng số liên quan đến loại đối thoại

public class DialogueEffect {

	private int x, y, type; // Vị trí (x, y) và loại đối thoại
	private int aniIndex, aniTick; // Chỉ số hoạt ảnh và số lần tick (đếm thời gian cho hoạt ảnh)
	private boolean active = true; // Trạng thái kích hoạt, hiệu ứng có đang hoạt động không

	// Constructor: Khởi tạo hiệu ứng đối thoại với vị trí (x, y) và loại đối thoại
	public DialogueEffect(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}

	// Cập nhật hiệu ứng đối thoại theo thời gian (tick), xử lý hoạt ảnh
	public void update() {
		aniTick++; // Tăng số lần tick
		if (aniTick >= ANI_SPEED) { // Khi số tick đạt đến tốc độ hoạt ảnh quy định
			aniTick = 0; // Reset số tick
			aniIndex++; // Tăng chỉ số hoạt ảnh để chuyển sang khung hình tiếp theo
			if (aniIndex >= GetSpriteAmount(type)) { // Nếu hoạt ảnh đã hết các khung hình
				active = false; // Hiệu ứng kết thúc, đặt trạng thái không hoạt động
				aniIndex = 0; // Reset chỉ số hoạt ảnh về 0
			}
		}
	}

	// Vô hiệu hóa (deactivate) hiệu ứng đối thoại
	public void deactive() {
		active = false;
	}

	// Reset lại hiệu ứng với vị trí mới và kích hoạt lại
	public void reset(int x, int y) {
		this.x = x;
		this.y = y;
		active = true; // Đặt hiệu ứng hoạt động trở lại
	}

	// Lấy chỉ số hoạt ảnh hiện tại
	public int getAniIndex() {
		return aniIndex;
	}

	// Lấy vị trí x của hiệu ứng
	public int getX() {
		return x;
	}

	// Lấy vị trí y của hiệu ứng
	public int getY() {
		return y;
	}

	// Lấy loại đối thoại của hiệu ứng
	public int getType() {
		return type;
	}

	// Kiểm tra hiệu ứng có đang hoạt động không
	public boolean isActive() {
		return active;
	}
}
