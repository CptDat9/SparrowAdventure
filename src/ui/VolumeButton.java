package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import utilz.LoadSave;
import static utilz.Constants.UI.VolumeButtons.*;

public class VolumeButton extends PauseButton {
	private BufferedImage[] imgs;
	private BufferedImage slider;
	private int index = 0;
	private boolean mouseOver, mousePressed;
	private int buttonX, minX, maxX;
	private float floatValue = 0f;
	//Constructor của volume button.
	/*
	 * VOLUME_WIDTH là độ rộng của nút kéo thả
	 */
	public VolumeButton(int x, int y, int width, int height) {
		super(x + width / 2, y, VOLUME_WIDTH, height); 	// Gọi constructor của PauseButton để căn giữa nút trượt và khởi tạo bounds.x của nó
		bounds.x -= VOLUME_WIDTH / 2; // Setup vị trí bounds.x THỰC TẾ của nút trượt trên màn hình
		buttonX = x + width / 2; // Xác định vị trí X của nút trượt (theo vị trí chính giữa)
		/*
		 * Sau khi truyền tham số x + width/2 và Volume_Width, x và width thật sự đã bị thay đổi
		 * -> cần gán lại this.x = x và this.width = width
		 */
		this.x = x; // Lưu lại x ban đầu của thanh trượt
		this.width = width; // Lưu chiều rộng của thanh trượt
		
		//Thiết lập giới hạn có thể kéo của nút trượt
		minX = x + VOLUME_WIDTH / 2; 
		maxX = x + width - VOLUME_WIDTH / 2;
		
		loadImgs(); // Nạp tài nguyên
	}
// Phương thức tải hình ảnh của nút và thanh trượt (slider).
	private void loadImgs() {
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.VOLUME_BUTTONS);
		imgs = new BufferedImage[3]; // Mảng này chứa 3 trạng thái của nút gồm bình thường, hover, pressed khi di con trỏ chuột.
		for (int i = 0; i < imgs.length; i++)
			imgs[i] = temp.getSubimage(i * VOLUME_DEFAULT_WIDTH, 0, VOLUME_DEFAULT_WIDTH, VOLUME_DEFAULT_HEIGHT);
					// Cắt ảnh từ sprite atlas để tạo các trạng thái nút
		slider = temp.getSubimage(3 * VOLUME_DEFAULT_WIDTH, 0, SLIDER_DEFAULT_WIDTH, VOLUME_DEFAULT_HEIGHT);
	// Cắt ảnh cho slider.
	}
//Phương thức cập nhật dựa trên trạng thái của chuột.
	public void update() {
		index = 0; // Mặc định là bình thường
		if (mouseOver)
			index = 1; // Khi di chuyển nhưng không ấn
		if (mousePressed)
			index = 2; // Khi ấn chuột .

	}

      // Phương thức vẽ slider và nút âm lượng
	public void draw(Graphics g) {
		// Vẽ thanh trượt (slider)
		g.drawImage(slider, x, y, width, height, null);
		// Vẽ nút âm lượng dựa trên trạng thái hiện tại
		g.drawImage(imgs[index], bounds.x, y, VOLUME_WIDTH, height, null);
	}

	// Thay đổi vị trí X của nút trượt
	public void changeX(int x) {
		// Giới hạn giá trị X trong khoảng minX và maxX
		if (x < minX)
			buttonX = minX;
		else if (x > maxX)
			buttonX = maxX;		//Đạt giới hạn kéo thả rồi thì vẫn set vị trí là minX (hoặc maxX)
		else
			buttonX = x;
		// Cập nhật giá trị float đại diện cho mức âm lượng
		updateFloatValue();
		// Cập nhật vị trí biên bounds của nút
		bounds.x = buttonX - VOLUME_WIDTH / 2;
	}

	// Cập nhật giá trị floatValue dựa trên vị trí nút trượt
	private void updateFloatValue() {
		float range = maxX - minX; // Khoảng cách trượt tối đa
		float value = buttonX - minX; // Vị trí hiện tại của nút
		floatValue = value / range; // Tính toán giá trị float từ 0 đến 1
	}

	// Reset trạng thái của chuột
	public void resetBools() {
		mouseOver = false; // Reset trạng thái chuột di chuyển nhưng không ấn (hover)
		mousePressed = false; // Reset trạng thái ấn chuột
	}

	// Kiểm tra xem chuột có di chuyển qua nút không
	public boolean isMouseOver() {
		return mouseOver;
	}

	// Thiết lập trạng thái chuột di chuyển qua nút
	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}

	// Kiểm tra xem chuột có nhấn vào nút không
	public boolean isMousePressed() {
		return mousePressed;
	}

	// Thiết lập trạng thái chuột nhấn vào nút
	public void setMousePressed(boolean mousePressed) {
		this.mousePressed = mousePressed;
	}

	// Lấy giá trị floatValue đại diện cho mức âm lượng
	public float getFloatValue() {
		return floatValue;
	}
}
