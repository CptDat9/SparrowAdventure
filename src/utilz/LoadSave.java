package utilz;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import static utilz.Constants.EnemyConstants.CRABBY;

import javax.imageio.ImageIO;

import entities.Crabby;
import main.Game;

public class LoadSave {

	public static final String PLAYER_ATLAS = "player_sprites.png";
	public static final String LEVEL_ATLAS = "outside_sprites.png";
	public static final String LEVEL_ONE_DATA = "level_one_data_long.png";
	public static final String PLAYING_BG_IMG = "playing_bg_img.png";
	public static final String BIG_CLOUDS = "big_clouds.png";
	public static final String SMALL_CLOUDS = "small_clouds.png";
	public static final String CRABBY_SPRITE = "crabby_sprite.png";
	public static final String STATUS_BAR = "health_power_bar.png";

	public static BufferedImage GetSpriteAtlas(String fileName) {
		BufferedImage img = null;
		InputStream is = LoadSave.class.getResourceAsStream("/" + fileName) ;
		
		try { //Thu doc hinh anh tu resource
			img = ImageIO.read(is);
		} catch (IOException e) { //Khong doc duoc thi catch
			e.printStackTrace();
		} finally { //Du co doc duoc hay khong di chang nua
			try {
				is.close(); //Dong InputStream lai
			} catch (IOException e) { //Khong dong duoc thi bo tay
				e.printStackTrace();
			}
		}
		return img;
	}

	public static ArrayList<Crabby> GetCrabs() {
		BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
		ArrayList<Crabby> list = new ArrayList<>();

		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == CRABBY)
					list.add(new Crabby(i * Game.TILES_SIZE, j * Game.TILES_SIZE - Game.TILES_SIZE));
			}
		}

		return list;

	}
	
	public static int[][] GetLevelData(){

		BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
		int[][] lvlData = new int[img.getHeight()][img.getWidth()];

		
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getRed();
				if (value >= 48) value = 0; //có thể ảnh bitmap của mình bị thiết kế với kích cỡ lớn hơn 12*4
				lvlData[j][i] = value;
			}
		}
		return lvlData;
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
	
	ảnh này chính là lvl_one_data.png có kích cỡ 12*4 pixel !
	 */
}
