package objects;

import static utilz.Constants.ObjectConstants.*; // Sử dụng các hằng số từ lớp ObjectConstants

import main.Game;

// Lớp GameContainer đại diện cho các vật phẩm có thể chứa đồ (như hộp, thùng) trong game, kế thừa từ GameObject
public class GameContainer extends GameObject {

	// Constructor nhận tọa độ x, y và loại đối tượng objType
	public GameContainer(int x, int y, int objType) {
		// Gọi constructor của lớp cha (GameObject) để khởi tạo vị trí và loại đối tượng
		super(x, y, objType);
		// Tạo hitbox cho đối tượng dựa trên loại đối tượng được truyền vào
		createHitbox();
	}

	// Phương thức createHitbox() tạo hitbox dựa trên loại đối tượng
	private void createHitbox() {
		// Nếu đối tượng là hộp (BOX), khởi tạo hitbox với kích thước 25x18 pixels
		if (objType == BOX) {
			initHitbox(25, 18);

			// Điều chỉnh độ lệch khi vẽ đối tượng trên màn hình theo tỷ lệ game
			xDrawOffset = (int) (7 * Game.SCALE);
			yDrawOffset = (int) (12 * Game.SCALE);

		} else { // Nếu không phải hộp, khởi tạo hitbox với kích thước 23x25 pixels
			initHitbox(23, 25);
			xDrawOffset = (int) (8 * Game.SCALE);
			yDrawOffset = (int) (5 * Game.SCALE);
		}

		// Điều chỉnh vị trí hitbox theo trục y và trục x dựa trên các giá trị lệch
		// Cộng thêm 2 cho y thì mới chạm đất
		// Cộng thêm offset/2 cho x thì mới căn giữa tile được
		hitbox.y += yDrawOffset + (int) (Game.SCALE * 2); // Dịch chuyển hitbox theo trục y
		hitbox.x += xDrawOffset / 2; // Dịch chuyển hitbox theo trục x
	}

	// Phương thức update() được gọi liên tục trong game loop để cập nhật trạng thái đối tượng
	public void update() {
		// Nếu đối tượng có hoạt ảnh, cập nhật tick để tạo hoạt ảnh
		if (doAnimation)
			updateAnimationTick();
	}
}
