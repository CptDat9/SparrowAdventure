package gamestates;

public enum Gamestate {
	PLAYING, MENU, OPTIONS, QUIT, CREDITS, PLAYER_SELECTION, PRE_CREDITS;

    public static Gamestate state = MENU;
}


/*
 * Tóm tắt tư tưởng hoạt động của Gamestate:
 * 
 * Mọi thứ bắt đầu từ Game.java:
 * Loop liên tục hai hoạt động: update() và render() của Game.java
 * 
 * - Tùy theo gamestate hiện tại:
 * 		+) Nếu gamestate đang ở menu thì update() sẽ chỉ liên quan đến
 * 		các loại update tương ứng với menu
 * 		+) Nếu đang ở playing thì update() sẽ chỉ liên quan đến playing
 * 
 * - Chả hạn, mỗi lần update ứng với state playing, 
 * player.update() sẽ được gọi theo -> update vị trí, hoạt ảnh nhân vật
 * 
 * - Khi đang ở trong một gamestate hiện tại, nếu muốn chuyển sang gamestate khác:
 * 		+) Nếu phát hiện nút CHUYỂN TRẠNG THÁI (ở đây là BackSpace đối với gamestate playing ở trong lớp Playing.java)
 * 		thì đổi giá trị gamestate sang cái khác -> update() và render() sẽ khác theo 
 * 
 * - Người ta phân biệt 2 gamestate qua lớp enum Gamestate.
 */
