package utilz;

import java.awt.geom.Rectangle2D;

import main.Game;
import objects.Projectile;

public class HelpMethods {

	public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {
		/*Bắt đầu check xem:
		 nhân vật có vị trí (x, y) với độ lớn (của hitbox) là hình chữ nhật w*h, có thể di chuyển
		 đến một vị trí đã có trong lvlData[][] hay không?
		*/
		if (!IsSolid(x, y, lvlData))
			if (!IsSolid(x + width, y + height, lvlData))
				if (!IsSolid(x + width, y, lvlData))
					if (!IsSolid(x, y + height, lvlData))
						return true;
		return false;
	}

	private static boolean IsSolid(float x, float y, int[][] lvlData) {
		//Dựa theo lvlData đã được nạp, nhân lên với TILES_SIZE, ta có kích thước của toàn bộ lvl về mặt độ dài
		int maxWidth = lvlData[0].length * Game.TILES_SIZE; 
		if (x < 0 || x >= maxWidth)
			return true;
		if (y < 0 || y >= Game.GAME_HEIGHT)
			return true;
		float xIndex = x / Game.TILES_SIZE;
		float yIndex = y / Game.TILES_SIZE;
		//in fact: mỗi tọa độ (x, y) của mình, đều tương đương với một cái tile nào đấy trong game

		return IsTileSolid((int) xIndex, (int) yIndex, lvlData);
	}

	public static boolean IsProjectileHittingLevel(Projectile p, int[][] lvlData) {
		return IsSolid(p.getHitbox().x + p.getHitbox().width / 2, p.getHitbox().y + p.getHitbox().height / 2, lvlData);
	}

	public static boolean IsEntityInWater(Rectangle2D.Float hitbox, int[][] lvlData) {
		// Will only check if entity touch top water. Can't reach bottom water if not
		// touched top water.
		if (GetTileValue(hitbox.x, hitbox.y + hitbox.height, lvlData) != 48)
			if (GetTileValue(hitbox.x + hitbox.width, hitbox.y + hitbox.height, lvlData) != 48)
				return false;
		return true;
	}

	private static int GetTileValue(float xPos, float yPos, int[][] lvlData) {
		int xCord = (int) (xPos / Game.TILES_SIZE);
		int yCord = (int) (yPos / Game.TILES_SIZE);
		return lvlData[yCord][xCord];
	}

	public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData) {
		int value = lvlData[yTile][xTile];
		/*
		 * Quy ước:
		 * Gốc tọa độ O của window nằm ở góc trên cùng bên trái màn hình
		 * tia Ox đi trái sang phải
		 * tia Oy đi từ TRÊN xuống DƯỚI
		 * thành ra, yIndex là chỉ số hàng, xIndex là chỉ số cột của ma trận lvlData[][]
		 */

		switch (value) {
		case 11, 48, 49:
			return false;
		default:
			return true;
		}
		/*
		 (value >= 48 || value < 0) -> nếu mình di chuyển ra ngoài biên, thì solid -> không đi đâu được nữa
		 value != 11 -> nếu mình không đi vào ô không gian trống -> chạm vào vật thể -> solid
		 */


	}
	

	public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
		// Ham nay se tra ve vi tri cua hitbox.x ke canh voi wall nhat co the
		
		int currentTile = (int) (hitbox.x / Game.TILES_SIZE);
		// cot ma nhan vat dang dung
		
		if (xSpeed > 0) {
			// RIGHT
			// Nhan vat dang di ve ben phai
			
			int tileXPos = currentTile * Game.TILES_SIZE;
			// Vi tri cua nhan vat theo x cua tile hien tai
			
			int xOffset = (int) (Game.TILES_SIZE - hitbox.width);
			// Khoang cach giua canh ben trai cua hitbox voi canh ben trai cua currentTile
			// khi hitbox da sat vao canh ben phai cua currentTile;
			//  |        |
			//  |_____|  |
			//  | kc  |  |
			
			return tileXPos + xOffset - 1;
			// Tru 1 de hitbox khong overlap voi wall
			// tuc la luon ton tai khoang cach dx = 1 pixel giua bien hitbox voi wall
			
		} else {
			// Left
			// Nhan vat dang di ve ben trai
			return currentTile * Game.TILES_SIZE;
		}
		//xSpeed != 0 vi neu xSpeed == 0 se khong co collision
	}

	public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
		//Ham nay tuong tu ham ben tren nhung doi voi y
		int currentTile = (int) (hitbox.y / Game.TILES_SIZE);
		if (airSpeed > 0) {
			// Falling - touching floor
			int tileYPos = currentTile * Game.TILES_SIZE;
			int yOffset = (int) (Game.TILES_SIZE - hitbox.height);
			return tileYPos + yOffset - 1;
		} else
			// Jumping
			return currentTile * Game.TILES_SIZE;

	}

	public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
		// Check the pixel below bottom-left and bottom-right
		if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
			if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
				return false;
		return true;
	}

	public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
		// Check diem duoi phai
		if (xSpeed > 0)
			return IsSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
		else
			return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
	}

	/*
	 * Check xem như thế nào là IsFloor ?
	 * Khi điểm dưới cùng bên trái và điểm dưới cùng bên phải đều cùng là Solid
	 */
	public static boolean IsFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
		if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
			if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
				return false;
		return true;
	}

	public static boolean CanCannonSeePlayer(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile) {
		int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);
		int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

		if (firstXTile > secondXTile)
			return IsAllTilesClear(secondXTile, firstXTile, yTile, lvlData);
		else
			return IsAllTilesClear(firstXTile, secondXTile, yTile, lvlData);
	}

	//Giữa hai tiles không có vật cản cứng
	public static boolean IsAllTilesClear(int xStart, int xEnd, int y, int[][] lvlData) {
		for (int i = 0; i < xEnd - xStart; i++)
			if (IsTileSolid(xStart + i, y, lvlData))
				return false;
		return true;
	}

	//Walkable được hết tức là ở giữa hai tile xStart và xEnd thỏa mãn hai điều kiện
	public static boolean IsAllTilesWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
		//Điều kiện 1: Giữa chúng không có vật cản
		if (IsAllTilesClear(xStart, xEnd, y, lvlData))
			for (int i = 0; i < xEnd - xStart; i++) {
				
				// Điều kiện 2: Giữa chúng không có cái hố nào
				if (!IsTileSolid(xStart + i, y + 1, lvlData))
					return false;
			}
		return true;
	}

	// Player can sometimes be on an edge and in sight of enemy.
	// The old method would return false because the player x is not on edge.
	// This method checks both player x and player x + width.
	// If tile under playerBox.x is not solid, we switch to playerBox.x +
	// playerBox.width;
	// One of them will be true, because of prior checks.

	//Check xem có vật cản ở giữa hitbox của player và của enemy hay không
	//Người ta phải chia các pixel thành các tiles nhỏ hơn để dễ check việc này (gameloop sẽ mượt hơn)
	//firstXTile là tile hiện tại dành cho đối tượng thứ nhất, second là cho đối tượng thứ 2
	public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float enemyBox, Rectangle2D.Float playerBox, int yTile) {
		int firstXTile = (int) (enemyBox.x / Game.TILES_SIZE);

		int secondXTile;
		if (IsSolid(playerBox.x, playerBox.y + playerBox.height + 1, lvlData))
			secondXTile = (int) (playerBox.x / Game.TILES_SIZE);
		else
			secondXTile = (int) ((playerBox.x + playerBox.width) / Game.TILES_SIZE);

		//Check cả hai trường hợp: đối tượng thứ nhất đứng trước (hoặc đứng sau) đối tượng thứ hai xem có vật cản nào không
		if (firstXTile > secondXTile)
			return IsAllTilesWalkable(secondXTile, firstXTile, yTile, lvlData);
		else
			return IsAllTilesWalkable(firstXTile, secondXTile, yTile, lvlData);
	}

	public static boolean IsSightClear_OLD(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile) {
		int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);
		int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

		if (firstXTile > secondXTile)
			return IsAllTilesWalkable(secondXTile, firstXTile, yTile, lvlData);
		else
			return IsAllTilesWalkable(firstXTile, secondXTile, yTile, lvlData);
	}
}
