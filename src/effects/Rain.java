package effects;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import main.Game;
import utilz.LoadSave;

public class Rain {

    // Mảng các hạt mưa (vị trí của các giọt mưa trên màn hình)
	private Point2D.Float[] drops;
    // Đối tượng Random để tạo các giá trị ngẫu nhiên cho vị trí và di chuyển của giọt mưa, cái này thì giúp hình ảnh chân thực hơn và các hạt mưa không bị di chuyển theo quy luật.
	private Random rand;
    // Tốc độ di chuyển của hạt mưa
	private float rainSpeed = 1.25f;
    // Hình ảnh của hạt mưa (được nạp từ sprite atlas)
	private BufferedImage rainParticle;

    // Tạo hiệu ứng mưa 
	public Rain() {
		rand = new Random();
        // Khởi tạo 1000 giọt mưa
		drops = new Point2D.Float[1000];
        // Nạp hình ảnh của hạt mưa từ file sprite atlas
		rainParticle = LoadSave.GetSpriteAtlas(LoadSave.RAIN_PARTICLE);
        // Gọi phương thức khởi tạo vị trí cho các giọt mưa
		initDrops();
	}

    // Khởi tạo vị trí ngẫu nhiên cho tất cả các giọt mưa
	private void initDrops() {
		for (int i = 0; i < drops.length; i++)
			drops[i] = getRndPos();
	}

    // Tạo ra một vị trí ngẫu nhiên trong màn hình cho mỗi giọt mưa
	private Point2D.Float getRndPos() {
		return new Point2D.Float((int) getNewX(0), rand.nextInt(Game.GAME_HEIGHT));
	}

    // Cập nhật vị trí của các giọt mưa khi chúng rơi xuống
    // Mỗi giọt mưa sẽ rơi xuống với tốc độ cố định và được tái sinh ở phía trên cùng khi ra khỏi màn hình
	public void update(int xLvlOffset) {
		for (Point2D.Float p : drops) {
			p.y += rainSpeed;  // Di chuyển giọt mưa xuống dưới theo trục y
			if (p.y >= Game.GAME_HEIGHT) {
				// Khi giọt mưa đi quá chiều cao của màn hình, tái sinh ở trên cùng với vị trí x mới
				p.y = -20;
				p.x = getNewX(xLvlOffset);
			}
		}
	}

    // Tính toán vị trí x mới cho giọt mưa, với giá trị ngẫu nhiên từ ngoài màn hình vào trong màn hình
	private float getNewX(int xLvlOffset) {
		float value = (-Game.GAME_WIDTH) + rand.nextInt((int) (Game.GAME_WIDTH * 3f)) + xLvlOffset;
		return value;
	}

    // Vẽ các giọt mưa lên màn hình
	public void draw(Graphics g, int xLvlOffset) {
		for (Point2D.Float p : drops)
			g.drawImage(rainParticle, (int) p.getX() - xLvlOffset, (int) p.getY(), 3, 12, null); 
			// Vẽ giọt mưa với kích thước 3x12 pixel
	}

}
