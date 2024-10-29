package ui;

import java.awt.Rectangle;

public class PauseButton {

    protected int x, y, width, height;  // Các thuộc tính xác định vị trí và kích thước của nút pause
    
    // Vùng (hình chữ nhật) để xác định kích thước và vùng bấm của nút
    protected Rectangle bounds;

    // Hàm khởi tạo cho lớp PauseButton, nhận vị trí (x, y) và kích thước (width, height)
    public PauseButton(int x, int y, int width, int height) {
        this.x = x;           // Gán tọa độ x của nút
        this.y = y;           // Gán tọa độ y của nút
        this.width = width;   // Gán chiều rộng của nút
        this.height = height; // Gán chiều cao của nút
        createBounds();       // Tạo đối tượng Rectangle để đại diện cho vùng của nút
    }

    // Tạo hình chữ nhật bao quanh nút để xác định vùng mà nút chiếm trên màn hình
    private void createBounds() {
        bounds = new Rectangle(x, y, width, height); 
	    // Vùng bấm tương ứng với tọa độ và kích thước của nút
    }

    // phương thức trả về tọa độ x của nút
    public int getX() {
        return x;
    }

    // phương thức thiết lập tọa độ x cho nút
    public void setX(int x) {
        this.x = x;
    }

    // phương thức trả về tọa độ y của nút
    public int getY() {
        return y;
    }

    // phương thức thiết lập tọa độ y cho nút
    public void setY(int y) {
        this.y = y;
    }

    // phương thức trả về chiều rộng của nút
    public int getWidth() {
        return width;
    }

    // phương thức thiết lập chiều rộng cho nút
    public void setWidth(int width) {
        this.width = width;
    }

    // phương thức trả về chiều cao của nút
    public int getHeight() {
        return height;
    }

    // phương thức thiết lập chiều cao cho nút
    public void setHeight(int height) {
        this.height = height;
    }

    // phương thức trả về vùng bấm của nút (dạng HCN)
    public Rectangle getBounds() {
        return bounds;
    }

    // phương thức thiết lập lại vùng bấm của nút
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

}
