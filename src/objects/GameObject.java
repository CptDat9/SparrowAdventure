package objects;

import static utilz.Constants.ObjectConstants.*; // Import hằng số liên quan đến đối tượng từ lớp ObjectConstants
import static utilz.Constants.ANI_SPEED;
import java.awt.Color; 
import java.awt.Graphics;
import java.awt.geom.Rectangle2D; // Import cho hình chữ nhật 2D dùng cho hitbox

import main.Game; 
// Import lớp Game chính

public class GameObject {

	protected int x, y, objType; // Tọa độ x, y và loại đối tượng (ví dụ: thùng, hộp, súng)
	protected Rectangle2D.Float hitbox; // Hitbox (hình chữ nhật dùng để phát hiện va chạm)
	protected boolean doAnimation, active = true; // Cờ (flag) xác định đối tượng có hoạt ảnh hoặc đang hoạt động không
	protected int aniTick, aniIndex; // Biến để điều khiển tốc độ và trạng thái hoạt ảnh
	protected int xDrawOffset, yDrawOffset; // Offset cho việc vẽ đối tượng trên màn hình

	// Constructor để khởi tạo đối tượng với tọa độ và loại đối tượng
	public GameObject(int x, int y, int objType) {
		this.x = x;
		this.y = y;
		this.objType = objType;
	}

	// Cập nhật tiến trình hoạt ảnh dựa trên tốc độ hoạt ảnh
	protected void updateAnimationTick() {
		aniTick++; // Tăng giá trị tick hoạt ảnh
		if (aniTick >= ANI_SPEED) { // Nếu tick vượt quá tốc độ định nghĩa
			aniTick = 0; // Đặt lại tick
			aniIndex++; // Chuyển sang khung hoạt ảnh tiếp theo
			if (aniIndex >= GetSpriteAmount(objType)) { // Kiểm tra xem có vượt qua số lượng khung hình của loại đối tượng không
				aniIndex = 0; // Đặt lại khung hình đầu tiên
				// Nếu đối tượng là thùng hoặc hộp, ngừng hoạt ảnh và đặt đối tượng thành không hoạt động
				if (objType == BARREL || objType == BOX) {
					doAnimation = false;
					active = false;
				} 
				// Nếu là các loại súng, chỉ ngừng hoạt ảnh nhưng vẫn giữ đối tượng hoạt động
				else if (objType == CANNON_LEFT || objType == CANNON_RIGHT || objType == SHIP)
					doAnimation = false;
			}
		}
	}

	// Đặt lại trạng thái của đối tượng
	public void reset() {
		aniIndex = 0; // Đặt lại chỉ số khung hình hoạt ảnh
		aniTick = 0; // Đặt lại tick
		active = true; // Đặt đối tượng hoạt động trở lại

		// Với một số loại đối tượng cụ thể, ngừng hoạt ảnh
		if (objType == BARREL || objType == BOX || objType == CANNON_LEFT || objType == CANNON_RIGHT)
			doAnimation = false;
		else
			doAnimation = true; // Còn lại thì hoạt ảnh vẫn tiếp tục
	}

	// Khởi tạo hitbox với chiều rộng và chiều cao
	protected void initHitbox(int width, int height) {
		hitbox = new Rectangle2D.Float(x, y, (int) (width * Game.SCALE), (int) (height * Game.SCALE));
		// Hitbox được mở rộng dựa trên hệ số tỉ lệ của game
	}

	// Vẽ hitbox lên màn hình với offset cấp độ x
	public void drawHitbox(Graphics g, int xLvlOffset) {
		g.setColor(Color.PINK); // Đặt màu hitbox là màu hồng
//		 Vẽ hitbox với tọa độ đã trừ đi xLvlOffset (để cân nhắc vị trí của màn hình trong game)
		g.drawRect((int) hitbox.x - xLvlOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
	}

	// Các phương thức getter và setter:

	// Lấy loại đối tượng (ví dụ: thùng, hộp, súng)
	public int getObjType() {
		return objType;
	}

	// Lấy hitbox của đối tượng
	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}

	// Kiểm tra xem đối tượng có đang hoạt động không
	public boolean isActive() {
		return active;
	}

	// Đặt trạng thái hoạt động của đối tượng
	public void setActive(boolean active) {
		this.active = active;
	}

	// Đặt trạng thái hoạt ảnh của đối tượng
	public void setAnimation(boolean doAnimation) {
		this.doAnimation = doAnimation;
	}

	// Lấy giá trị offset theo trục x khi vẽ đối tượng
	public int getxDrawOffset() {
		return xDrawOffset;
	}

	// Lấy giá trị offset theo trục y khi vẽ đối tượng
	public int getyDrawOffset() {
		return yDrawOffset;
	}

	// Lấy chỉ số khung hình hiện tại của hoạt ảnh
	public int getAniIndex() {
		return aniIndex;
	}

	// Lấy giá trị tick hoạt ảnh hiện tại
	public int getAniTick() {
		return aniTick;
	}

}
