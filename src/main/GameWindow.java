package main;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

public class GameWindow {
	private JFrame jframe;
	
	public GameWindow(GamePanel gamePanel) {
		jframe = new JFrame();
		
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		// Nut X = dong window lai
		
		jframe.add(gamePanel);
		//jframe.setLocationRelativeTo(null); 
		/*dat vi tri tren cung ben trai cua window
		 ve chinh giua man hinh nguoi dung
		 */
		
		jframe.setResizable(false);
		/*
		 Không cho window thu nhỏ, phóng to
		 */
		
		jframe.pack();
		/*yêu cầu window hiệu chỉnh lại kích cỡ sao cho phù hợp với
		kích cỡ Preferred size và phần biên ngoài của các thành phần
		có bên trong window		 
		 */
		
		jframe.setLocationRelativeTo(null);
		/*
		 * Căn giữa màn hình phần gameplay
		 */
		
		jframe.setVisible(true); //Hien thi window ra man hinh
		
		jframe.addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				gamePanel.getGame().windowFocusLost();
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				// TODO Auto-generated method stub
			}
		} );
		/*
		 Giả sử mình nhấn giữ nút D cho nhân vật di chuyển ra ngoài
		 hoàn toàn màn hình window, sau đó thu nhỏ window lại
		 và nhả nút D ra
		 
		 Thì nhân vật sẽ đi mãi và không thể nào trờ lại được window 
		 ban đầu
		 
		 Có thể tạm hiểu là lúc này, window đã bị "lost focus"
		 
		 -> cần phải cài đặt: khi thu nhỏ window lại, cho nhân vật
		 không được di chuyển theo một hướng nào nữa
		 */
	}
	

}
