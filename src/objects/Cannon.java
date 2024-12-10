package objects;

import static utilz.Constants.ObjectConstants.ANI_SPEED_CANNON;
import static utilz.Constants.ObjectConstants.GetSpriteAmount;

import main.Game;

// Lớp Cannon đại diện cho đối tượng súng thần công trong game, kế thừa từ GameObject
public class Cannon extends GameObject {

	// tileY: Chứa giá trị tọa độ Y trên lưới (grid) của game
	private int tileY;

	// Constructor của Cannon nhận tham số vị trí x, y và loại đối tượng objType
	public Cannon(int x, int y, int objType) {
		// Gọi constructor của lớp cha (GameObject) để khởi tạo vị trí và loại đối tượng
		super(x, y, objType);

		// Tính toán tileY bằng cách chia tọa độ y của đối tượng cho kích thước một ô lưới (TILES_SIZE)
		tileY = y / Game.TILES_SIZE;

		// Khởi tạo hitbox cho súng thần công với kích thước 40x26 pixels
		initHitbox(40, 26);

		// Điều chỉnh vị trí của hitbox theo trục y (lùi xuống một chút để căn chỉnh với hình ảnh)
		hitbox.y += (int) (6 * Game.SCALE);
	      // hitbox.x -= (int) (1 * Game.SCALE); nó sẽ điều chỉnh hitbox theo trục x nếu được mở lại.
	}

	// Phương thức update() được gọi trong vòng lặp game để cập nhật trạng thái của đối tượng
	public void update() {
		// Nếu đối tượng có hoạt ảnh, cập nhật tick để tạo hoạt ảnh
		if (doAnimation)
			updateAnimationTick();
	}
	
	@Override
	protected void updateAnimationTick() {
		aniTick++; // Tăng giá trị tick hoạt ảnh
		if (aniTick >= ANI_SPEED_CANNON) { // Nếu tick vượt quá tốc độ định nghĩa
			aniTick = 0; // Đặt lại tick
			aniIndex++; // Chuyển sang khung hoạt ảnh tiếp theo
			if (aniIndex >= GetSpriteAmount(objType)) { // Kiểm tra xem có vượt qua số lượng khung hình của loại đối tượng không
				aniIndex = 0; // Đặt lại khung hình đầu tiên
				doAnimation = false;
			}
		}
	}

	// Getter để lấy giá trị tileY (tọa độ Y trên lưới) của đối tượng
	public int getTileY() {
		return tileY;
	}
}
