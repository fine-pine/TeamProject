public class Piece {
	int x = 6;
	int y = 6;
	int skill;
	int neededMana;
	boolean isEnded = false;
	static int[] skillMana = new int[] {5, 2, 7, 10, 3, 2, 0};

	public static void main(String args[]) {}

	Piece(int skill) {
		this.skill = skill;
		this.neededMana = skillMana[skill];
	}

	public void move(int n) {
		int nextX, nextY;
		nextX = Game.shortX[y][x];
		nextY = Game.shortY[y][x];
		x = nextX;
		y = nextY;
		for (int i = 0; i < n - 1; i++) {
			if ((x == 6 && y == 6)) {
				isEnded = true;
				break;
			}
			nextX = Game.moveX[y][x];
			nextY = Game.moveY[y][x];
			x = nextX;
			y = nextY;
		}
		if ((x == 6 && y == 6))
			isEnded = true;
	}
}
