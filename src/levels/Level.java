package levels;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Crabby;
import entities.Pinkstar;
import entities.Shark;
import main.Game;
import objects.BackgroundTree;
import objects.Cannon;
import objects.GameContainer;
import objects.Grass;
import objects.Potion;
import objects.Spike;

import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.ObjectConstants.*;

public class Level {

	private BufferedImage img;
	private int[][] lvlData;

	//Tập hợp các đối tượng có thể xuất hiện là một ArrayList có chung kiểu dữ liệu: có thể là Crabby, Shark,...
	private ArrayList<Crabby> crabs = new ArrayList<>();
	private ArrayList<Pinkstar> pinkstars = new ArrayList<>();
	private ArrayList<Shark> sharks = new ArrayList<>();
	private ArrayList<Potion> potions = new ArrayList<>();
	private ArrayList<Spike> spikes = new ArrayList<>();
	private ArrayList<GameContainer> containers = new ArrayList<>();
	private ArrayList<Cannon> cannons = new ArrayList<>();
	private ArrayList<BackgroundTree> trees = new ArrayList<>();
	private ArrayList<Grass> grass = new ArrayList<>();

	private int lvlTilesWide;
	private int maxTilesOffset;
	private int maxLvlOffsetX;
	private Point playerSpawn;

	public Level(BufferedImage img) {
		this.img = img;
		lvlData = new int[img.getHeight()][img.getWidth()];
		loadLevel();
		calcLvlOffsets();
	}

	/*
	Trên thực tế, người ta sẽ phải import map gồm các hoạt ảnh của outside từ outside.png, 
	tạo một mảng để lưu các subimage theo kích cỡ 32*32 từ map đó đưa vào lvlSprite[] trong lvlManager với 
	lvlSprite[] dùng để lưu subimage ở vị trí được đánh thứ tự thứ i trong ảnh outside ban đầu
	
	Sau đó, render map outside bằng một ma trận gồm một cặp các chỉ số cho trước
	ví dụ: public final int[][] lvlData = {{0, 2, 2, 2, 2, 3},
											 {2, 2, 2, 3, 4, 7}};
	trong đó, giá trị của ô (j, i) trong lvlData này là hoạt ảnh thứ (j*12 + i) trong ảnh outside ban đầu
	
	-> nhược điểm của phương pháp này là cực kì rối rắm để sửa chữa map nếu cần thiết!
	
	-> người ta nghĩ ra một cách khác: lvlData được nạp số từ một ảnh bitmap cũng có kích cỡ 12*4 từ bên ngoài
	trong đó, giá trị RGB của màu đỏ chính là chỉ số của hoạt ảnh 32*32 cần được render trong ảnh outside ban đầu
	
	ảnh này chính là lvl_one_data.png có kích cỡ 26*14 pixel !
	 */
	private void loadLevel() {
		// Looping through the image colors just once. Instead of one per
		// object/enemy/etc..
		// Removed many methods in HelpMethods class.
		for (int y = 0; y < img.getHeight(); y++)
			for (int x = 0; x < img.getWidth(); x++) {
				Color c = new Color(img.getRGB(x, y));
				int red = c.getRed();
				int green = c.getGreen();
				int blue = c.getBlue();

				//-> levelData là màu đỏ, các nhân vật là màu xanh lá, các vật thể trong game là màu xanh dương
				loadLevelData(red, x, y);
				loadEntities(green, x, y);
				loadObjects(blue, x, y);
			}
	}

	private void loadLevelData(int redValue, int x, int y) {
		if (redValue >= 50)
			lvlData[y][x] = 0;
		else
			lvlData[y][x] = redValue;
		switch (redValue) {
		case 0, 1, 2, 3, 30, 31, 33, 34, 35, 36, 37, 38, 39 -> 
		grass.add(new Grass((int) (x * Game.TILES_SIZE), (int) (y * Game.TILES_SIZE) - Game.TILES_SIZE, getRndGrassType(x)));
		}
	}

	private int getRndGrassType(int xPos) {
		return xPos % 2;
	}

	//Nạp các Entities vào level hiện tại
	//Dựa theo màu của greenValue trong lvlData, mình sẽ biết rằng Enemy mình đang nạp vào là Enemy nào
	//Nếu GreenValue == CRABBY(enum), thì vector ArrayList<Crabby> sẽ được add thêm một con crabby nữa 
	//Cụ thể là được add ở cái tile có tọa độ (x, y) trong game
	private void loadEntities(int greenValue, int x, int y) {
		switch (greenValue) {
			case CRABBY -> crabs.add(new Crabby(x * Game.TILES_SIZE, y * Game.TILES_SIZE));
			case PINKSTAR -> pinkstars.add(new Pinkstar(x * Game.TILES_SIZE, y * Game.TILES_SIZE));
			case SHARK -> sharks.add(new Shark(x * Game.TILES_SIZE, y * Game.TILES_SIZE));
			case 100 -> playerSpawn = new Point(x * Game.TILES_SIZE, y * Game.TILES_SIZE);
		}
	}

	
	private void loadObjects(int blueValue, int x, int y) {
		switch (blueValue) {
		case RED_POTION, BLUE_POTION -> potions.add(new Potion(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
		case BOX, BARREL -> containers.add(new GameContainer(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
		case SPIKE -> spikes.add(new Spike(x * Game.TILES_SIZE, y * Game.TILES_SIZE, SPIKE));
		case CANNON_LEFT, CANNON_RIGHT -> cannons.add(new Cannon(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
		case TREE_ONE, TREE_TWO, TREE_THREE -> trees.add(new BackgroundTree(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
		}
	}

	private void calcLvlOffsets() {
		lvlTilesWide = img.getWidth();
		
		/* |   |           |							|
		 * |   |           |     						|  <----giới hạn của map
		 * |   |           |     						|	
		 *     ^           ^  	               ^
		 *     |           |	               |
		 *     |           |                 offset
		 *     |           |   
		 *     +------- phần hiển thị hiện tại
		 */
		//Số lượng tile lớn nhất có thể bị dôi ra
		maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
		
		//Số pixel bị dư ra lớn nhất có thể xảy ra
		maxLvlOffsetX = Game.TILES_SIZE * maxTilesOffset;
	}

	public int getSpriteIndex(int x, int y) {
		return lvlData[y][x];
	}

	public int[][] getLevelData() {
		return lvlData;
	}

	public int getLvlOffset() {
		return maxLvlOffsetX;
	}

	public Point getPlayerSpawn() {
		return playerSpawn;
	}

	public ArrayList<Crabby> getCrabs() {
		return crabs;
	}

	public ArrayList<Shark> getSharks() {
		return sharks;
	}

	public ArrayList<Potion> getPotions() {
		return potions;
	}

	public ArrayList<GameContainer> getContainers() {
		return containers;
	}

	public ArrayList<Spike> getSpikes() {
		return spikes;
	}

	public ArrayList<Cannon> getCannons() {
		return cannons;
	}

	public ArrayList<Pinkstar> getPinkstars() {
		return pinkstars;
	}

	public ArrayList<BackgroundTree> getTrees() {
		return trees;
	}

	public ArrayList<Grass> getGrass() {
		return grass;
	}

}
