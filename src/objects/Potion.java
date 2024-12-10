package objects;

import main.Game;

// Lớp Potion đại diện cho các vật phẩm hồi máu trong game, thừa kế từ GameObject
public class Potion extends GameObject {

	// hoverOffset: Giá trị dùng để điều chỉnh vị trí lơ lửng của vật phẩm
	// maxHoverOffset: Độ lệch tối đa mà vật phẩm có thể di chuyển theo trục y
	// hoverDir: Hướng di chuyển hiện tại của vật phẩm (1: đi lên, -1: đi xuống)
	private float hoverOffset;
	private int maxHoverOffset, hoverDir = 1;

	// Constructor của Potion nhận tham số vị trí x, y và loại đối tượng objType
	public Potion(int x, int y, int objType) {
		// Gọi constructor của lớp cha (GameObject)
		super(x, y, objType);
		doAnimation = true; // Kích hoạt hoạt ảnh của vật phẩm

		// Khởi tạo kích thước hitbox của vật phẩm (7px chiều rộng, 14px chiều cao)
		initHitbox(7, 14);

		// Xác định độ lệch khi vẽ vật phẩm trên màn hình (theo tỷ lệ game)
		// Khoảng cách giữa biên bên trên với vị trí thật của hoạt ảnh là 2, giữa biên bên phải với vị trí thật là 3
		xDrawOffset = (int) (3 * Game.SCALE);
		yDrawOffset = (int) (2 * Game.SCALE);

		// Xác định độ lệch tối đa của vật phẩm khi lơ lửng (theo tỷ lệ game)
		maxHoverOffset = (int) (10 * Game.SCALE);
	}

	// Phương thức update() được gọi liên tục trong game loop để cập nhật trạng thái của vật phẩm
	public void update() {
		updateAnimationTick(); // Cập nhật hoạt ảnh của vật phẩm
		updateHover(); // Cập nhật vị trí lơ lửng của vật phẩm
	}

	// Phương thức updateHover() điều chỉnh vị trí lơ lửng của vật phẩm theo thời gian
	private void updateHover() {
		// Điều chỉnh hoverOffset dựa trên hướng di chuyển hiện tại và tỷ lệ game
		hoverOffset += (0.075f * Game.SCALE * hoverDir);

		// Nếu hoverOffset đạt đến giới hạn tối đa, đảo chiều di chuyển
		if (hoverOffset >= maxHoverOffset)
			hoverDir = -1; // Di chuyển xuống
		// Nếu hoverOffset nhỏ hơn 0, đảo chiều di chuyển
		else if (hoverOffset < 0)
			hoverDir = 1; // Di chuyển lên

		// Cập nhật vị trí y của hitbox theo vị trí lơ lửng hiện tại
		hitbox.y = y + hoverOffset;
	}
}
