package main;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import inputs.KeyBoardInputs;
import inputs.MouseInputs;

import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;

public class GamePanel extends JPanel{ //hinh anh cua game ben trong Jframe
	//VERSION
	private static final long serialVersionUID = 1L;
	//VERSION
	
	//INPUT TU THIET BI NGOAI
	private MouseInputs mouseInputs;
	//INPUT TU THIET BI NGOAI
	
	//CAC TINH NANG LIEN QUAN DEN GAME
	private Game game;
	//CAC TINH NANG LIEN QUAN DEN GAME
	
	public GamePanel(Game game) {
		//WINDOW SETTINGS
		setPanelSize();
		//WINDOW SETTINGS
		
		//INIT GAME
		this.game = game;
		//INIT GAME
		
		//INPUT
		mouseInputs = new MouseInputs(this); 
		addKeyListener(new KeyBoardInputs(this)); //nhan dien cac trang thai cua phim bam ban phim
		addMouseListener(mouseInputs); //nhan dien cac trang thai click
		addMouseMotionListener(mouseInputs); //nhan dien cac trang thai move, drag cua con chuot
		//INPUT
	}
	

	private void setPanelSize() {
		Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
		setPreferredSize(size);
		// Đặt độ lớn của màn hình game đúng bằng kích cỡ size
		// Nếu không có hàm này thì window của trò chơi bị thu bé hết cỡ
		System.out.println("size: " + GAME_WIDTH + " : " + GAME_HEIGHT);
	}
		
	public void updateGame() {

	}

	
	public void paintComponent(Graphics g) { 
		super.paintComponent(g); 
		//Magic! Can co ham nay de ve duoc hoat anh trong game!
		game.render(g);
	}
	
	public Game getGame() {
		return game;
	}
}
