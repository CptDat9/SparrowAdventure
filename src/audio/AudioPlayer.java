package audio;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class AudioPlayer {

	// Các chỉ số cho các bài nhạc
	public static int MENU_1 = 0;
	public static int LEVEL_1 = 1;
	public static int LEVEL_2 = 2;

	// Các chỉ số cho các hiệu ứng âm thanh
	public static int DIE = 0; //Chết
	public static int JUMP = 1;// Nhảy
	public static int GAMEOVER = 2;// Kết thúc trò chơi
	public static int LVL_COMPLETED = 3;// hoàn thành level
	public static int ATTACK_ONE = 4;// Kĩ năng đánh 1
	public static int ATTACK_TWO = 5;// Kĩ năng đánh 2
	public static int ATTACK_THREE = 6;// Kĩ năng đánh 3

	private Clip[] songs, effects; // Mảng chứa các bài nhạc và hiệu ứng âm thanh
	private int currentSongId; // ID bài nhạc đang phát
	private float volume = 0.5f; // Mức âm lượng mặc định
	private boolean songMute, effectMute; // Trạng thái tắt tiếng của nhạc và hiệu ứng
	private Random rand = new Random(); // Để phát hiệu ứng âm thanh ngẫu nhiên

	// Constructor: Khởi tạo AudioPlayer và bắt đầu phát nhạc menu
	public AudioPlayer() {
		loadSongs(); // Nạp các bài nhạc
		loadEffects(); // Nạp các hiệu ứng âm thanh
		playSong(MENU_1); // Phát nhạc menu
	}

	// Nạp các file âm thanh cho nhạc nền từ thư mục /audio/
	private void loadSongs() {
		String[] names = { "menu", "level1", "level2" }; // Tên các file nhạc
		songs = new Clip[names.length]; // Tạo mảng chứa các bài nhạc
		for (int i = 0; i < songs.length; i++)
			songs[i] = getClip(names[i]); // Lấy từng bài nhạc từ file
	}

	// Nạp các file âm thanh cho hiệu ứng từ thư mục /audio/
	private void loadEffects() {
		String[] effectNames = { "die", "jump", "gameover", "lvlcompleted", "attack1", "attack2", "attack3" }; // Tên các file hiệu ứng
		effects = new Clip[effectNames.length]; // Tạo mảng chứa hiệu ứng âm thanh
		for (int i = 0; i < effects.length; i++)
			effects[i] = getClip(effectNames[i]); // Lấy từng hiệu ứng từ file

		updateEffectsVolume(); // Cập nhật âm lượng cho hiệu ứng âm thanh
	}

	// Lấy file âm thanh từ đường dẫn và tạo đối tượng Clip
	private Clip getClip(String name) {
		URL url = getClass().getResource("/audio/" + name + ".wav"); // Đường dẫn tới file âm thanh
		AudioInputStream audio;

		try {
			audio = AudioSystem.getAudioInputStream(url); // Lấy dữ liệu âm thanh từ file
			Clip c = AudioSystem.getClip(); // Tạo đối tượng Clip
			c.open(audio); // Mở file âm thanh
			return c; // Trả về đối tượng Clip

		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace(); // Bắt và in lỗi nếu có
		}

		return null; // Trả về null nếu xảy ra lỗi
	}

	// Đặt âm lượng mới cho cả nhạc nền và hiệu ứng âm thanh
	public void setVolume(float volume) {
		this.volume = volume;
		updateSongVolume(); // Cập nhật âm lượng của nhạc
		updateEffectsVolume(); // Cập nhật âm lượng của hiệu ứng
	}

	// Dừng phát nhạc hiện tại
	public void stopSong() {
		if (songs[currentSongId].isActive())
			songs[currentSongId].stop(); // Dừng bài nhạc nếu nó đang phát
	}

	// Đặt nhạc nền theo cấp độ (level) trò chơi
	public void setLevelSong(int lvlIndex) {
		if (lvlIndex % 2 == 0)
			playSong(LEVEL_1); // Phát nhạc của level 1 nếu chỉ số là chẵn
		else
			playSong(LEVEL_2); // Phát nhạc của level 2 nếu chỉ số là lẻ
	}

	// Khi hoàn thành màn chơi, dừng nhạc và phát hiệu ứng hoàn thành
	public void lvlCompleted() {
		stopSong();
		playEffect(LVL_COMPLETED); // Phát hiệu ứng âm thanh hoàn thành level
	}

	// Phát hiệu ứng âm thanh tấn công ngẫu nhiên
	public void playAttackSound() {
		int start = 4; // Các hiệu ứng tấn công bắt đầu từ chỉ số 4
		start += rand.nextInt(3); // Chọn ngẫu nhiên giữa các hiệu ứng tấn công
		playEffect(start); // Phát hiệu ứng tấn công
	}

	// Phát hiệu ứng âm thanh
	public void playEffect(int effect) {
		if (effects[effect].getMicrosecondPosition() > 0)
			effects[effect].setMicrosecondPosition(0); // Reset thời gian nếu hiệu ứng đã phát
		effects[effect].start(); // Bắt đầu phát hiệu ứng
	}

	// Phát nhạc nền
	public void playSong(int song) {
		stopSong(); 
    // Dừng bài nhạc hiện tại

		currentSongId = song; // Cập nhật ID bài nhạc mới
		updateSongVolume(); // Cập nhật âm lượng
		songs[currentSongId].setMicrosecondPosition(0); // Đặt lại vị trí phát nhạc
		songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY); // Phát nhạc liên tục
	}

	// Chuyển đổi trạng thái tắt/bật tiếng của nhạc nền
	public void toggleSongMute() {
		this.songMute = !songMute;
		for (Clip c : songs) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(songMute); // Tắt hoặc bật tiếng cho nhạc
		}
	}

	// Chuyển đổi trạng thái tắt/bật tiếng của hiệu ứng âm thanh
	public void toggleEffectMute() {
		this.effectMute = !effectMute;
		for (Clip c : effects) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(effectMute); // Tắt hoặc bật tiếng cho hiệu ứng
		}
		if (!effectMute)
			playEffect(JUMP); // Nếu bật lại tiếng, phát hiệu ứng nhảy
	}

	// Cập nhật âm lượng cho nhạc nền
	private void updateSongVolume() {
		FloatControl gainControl = (FloatControl) songs[currentSongId].getControl(FloatControl.Type.MASTER_GAIN);
		float range = gainControl.getMaximum() - gainControl.getMinimum(); // Tính khoảng điều chỉnh âm lượng
		float gain = (range * volume) + gainControl.getMinimum(); // Tính giá trị âm lượng mới
		gainControl.setValue(gain); // Đặt âm lượng mới cho nhạc nền
	}

	// Cập nhật âm lượng cho hiệu ứng âm thanh
	private void updateEffectsVolume() {
		for (Clip c : effects) {
			FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
			float range = gainControl.getMaximum() - gainControl.getMinimum(); // Tính khoảng điều chỉnh âm lượng
			float gain = (range * volume) + gainControl.getMinimum(); // Tính giá trị âm lượng mới
			gainControl.setValue(gain); // Đặt âm lượng mới cho hiệu ứng âm thanh
		}
	}
}
