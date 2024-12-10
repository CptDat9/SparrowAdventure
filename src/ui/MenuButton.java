package ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import utilz.LoadSave;
import static utilz.Constants.UI.Buttons.*;

public class MenuButton {
	private int xPos, yPos, rowIndex, index;
	private int xOffsetCenter = B_WIDTH / 2; //pixel x tại vị trí chính giữa màn hình
	private Gamestate state;
	private BufferedImage[] imgs;
	private boolean mouseOver, mousePressed;
	private Rectangle bounds;

	/* 
	Constructor này sẽ tạo một nút trong menu với các thông tin như vị trí (xPos, yPos), 
	trạng thái hàng của nút (rowIndex), và trạng thái game tương ứng (state).
	Nó sẽ gọi các phương thức để tải hình ảnh của nút và khởi tạo vùng bao nút 
	(Rectangle bounds) dùng để kiểm tra va chạm với chuột sau này.
	*/
	public MenuButton(int xPos, int yPos, int rowIndex, Gamestate state) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.rowIndex = rowIndex;
		this.state = state;
		loadImgs(); // Tải các ảnh cho nút từ ảnh trong tệp res
		initBounds(); // Khởi tạo vùng bao quanh nút
	}

	/* 
	initBounds() khởi tạo một hình chữ nhật để bao quanh nút, với kích thước và vị trí 
	dựa trên thông tin nút. Phần xOffsetCenter giúp căn giữa nút bằng cách điều chỉnh 
	vị trí theo chiều ngang.
	*/
	private void initBounds() {
		bounds = new Rectangle(xPos - xOffsetCenter, yPos, B_WIDTH, B_HEIGHT);
	}

	/* 
	Phương thức loadImgs() sẽ tải hình ảnh của nút từ ảnh trong tệp res, chia ra thành 3 trạng thái: 
	bình thường (index = 0), hover (index = 1) khi chuột di qua nút, và nhấn (index = 2) 
	khi chuột bấm vào nút. 
	Hình ảnh của mỗi trạng thái được cắt từ ảnh trong tệp res.
	*/
	private void loadImgs() {
		imgs = new BufferedImage[3];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.MENU_BUTTONS);
		for (int i = 0; i < imgs.length; i++)
			imgs[i] = temp.getSubimage(i * B_WIDTH_DEFAULT, rowIndex * B_HEIGHT_DEFAULT, B_WIDTH_DEFAULT, B_HEIGHT_DEFAULT);
	}

	/* 
	Phương thức draw(Graphics g) vẽ nút lên màn hình, sử dụng hình ảnh tương ứng với trạng thái 
	(index) hiện tại của nút (bình thường, hover, nhấn). Nó vẽ nút tại vị trí xPos và yPos 
	được căn giữa dựa vào xOffsetCenter.
	*/
	public void draw(Graphics g) {
		g.drawImage(imgs[index], xPos - xOffsetCenter, yPos, B_WIDTH, B_HEIGHT, null);
	}

	/* 
	Phương thức update() cập nhật trạng thái của nút. Nó sẽ kiểm tra xem chuột có đang hover 
	hoặc nhấn nút hay không để cập nhật giá trị của biến index, từ đó thay đổi hình ảnh 
	hiển thị của nút.
	*/
	public void update() {
		index = 0; // Trạng thái bình thường
		if (mouseOver)
			index = 1; // Khi hover (tức là chuột di chuyển nhưng không nhấn)
		if (mousePressed)
			index = 2; // Khi nhấn chuột
	}

	public boolean isMouseOver() {
		return mouseOver; // Trả về trạng thái hover của nút (đã giải thích trạng thái Hover ở trên)
	}

	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver; // Cập nhật trạng thái hover của nút
	}

	public boolean isMousePressed() {
		return mousePressed; // Trả về trạng thái nhấn của nút
	}

	public void setMousePressed(boolean mousePressed) {
		this.mousePressed = mousePressed; // Cập nhật trạng thái nhấn của nút
	}

	/* 
	Phương thức getBounds() trả về vùng bao quanh của nút, 
	dùng để kiểm tra xem chuột có bấm vào nút hay không (dùng cho việc xử lý va chạm).
	*/
	public Rectangle getBounds() {
		return bounds;
	}
 
	/* 
	Phương thức applyGamestate() thay đổi trạng thái của game khi người dùng nhấn vào nút này. 
	Khi nhấn vào nút, trạng thái game sẽ chuyển sang trạng thái được gán cho nút này.
	*/
	public void applyGamestate() {
		Gamestate.state = state;
	}

	// Phương thức resetBools() sẽ reset trạng thái của nút về mặc định, tức là không hover và không nhấn chuột.
	public void resetBools() {
		mouseOver = false;
		mousePressed = false;
	}

	
	// Phương thức getState() trả về trạng thái game mà nút này đại diện.
	public Gamestate getState() {
		return state;
	}

}
