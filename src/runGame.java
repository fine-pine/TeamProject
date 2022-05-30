import java.util.Scanner;
import java.util.Locale.Category;
import java.lang.Thread;
import java.util.concurrent.TimeUnit;
import javax.xml.catalog.CatalogException;
import java.io.*;
import java.util.HashMap;

public class runGame {

	static HashMap<String, String> data = new HashMap<String, String>();
	public static boolean isFinish = false;

	public static void main(String[] args) {

		// init game
		Game game = new Game();
		game.initObjects();

		// load data
		dataIO.readData(data);

		// login
		Scanner scan = new Scanner(System.in);
		String player1_ID, player1_PW;
		String player2_ID, player2_PW;

		do {
			System.out.println("Player1 login");
			System.out.println("nickname:");
			player1_ID = scan.next();
			System.out.println("PW:");
			player1_PW = scan.next();
		} while (!data.get(player1_ID).equals(player1_PW) && data.get(player1_ID) != null);

		do {
			System.out.println("Player2 login");
			System.out.println("nickname:");
			player2_ID = scan.next();
			System.out.println("PW:");
			player2_PW = scan.next();
		} while (!data.get(player2_ID).equals(player2_PW) && data.get(player2_ID) != null);

		// load data
		game.player1.loadData(data, player1_ID, player1_PW);
		game.player2.loadData(data, player2_ID, player2_PW);

		// set order
		int turn, oppose;
		int timeToSleep = 3;
		char player1_target = 's'; // player 1 click 's' button
		char player2_target = 'k'; // player 2 click 'k' button

		Counter T1 = new Counter(player1_target);
		T1.start();
		Counter T2 = new Counter(player2_target);
		T2.start();
		Timer Timer = new Timer(timeToSleep);
		Timer.start();

		try {
			TimeUnit.MILLISECONDS.sleep(timeToSleep * 1000 + 300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (T1.count >= T2.count) {
			turn = 1;
			oppose = 2;
		} else {
			turn = 2;
			oppose = 1;
		}

		// game start
		int choosePiece, diceResult;

		while (runGame.isFinish != true) {
			System.out.println("\n\nPlayer" + turn + " Turn");
			game.printBoard();
			game.getPlayer(turn).printPlayer();
			boolean catched = false;
			switch (game.chooseOpt(scan)) {
				case 1:
					do {
						diceResult = game.throwDice();
						boolean valid = false;
						System.out.println("choose Piece: ");
						choosePiece = scan.nextInt();
						valid = !game.getPlayer(turn).getPiece(choosePiece).isEnded;
						while (!valid && !runGame.isFinish) {
							System.out.println("invalid input");
							choosePiece = scan.nextInt();
							valid = !game.getPlayer(turn).getPiece(choosePiece).isEnded;
						}
						game.getPlayer(turn).movePiece(choosePiece, diceResult);
						catched = game.checkCatch(game.getPlayer(turn), game.getPlayer(oppose), choosePiece);
					} while (diceResult == 4 || diceResult == 5 || catched && !runGame.isFinish);
					break;
				case 2:
					boolean valid = false;
					System.out.println("choose Piece:");
					choosePiece = scan.nextInt();
					valid = !game.getPlayer(turn).getPiece(choosePiece).isEnded;
					while (!valid && !runGame.isFinish) {
						System.out.println("invalid input");
						choosePiece = scan.nextInt();
						valid = !game.getPlayer(turn).getPiece(choosePiece).isEnded;
					}
					game.useSkill(game, game.getPlayer(turn), game.getPlayer(oppose), choosePiece, scan);
					break;
				default:
					break;
			}
			if (game.getPlayer(turn).piece1.isEnded && game.getPlayer(turn).piece2.isEnded
					&& game.getPlayer(turn).piece3.isEnded == true) {
				System.out.println("Player" + turn + " Win!!");
				dataIO.saveData(data);
				runGame.isFinish = true;
			}

			if (turn == 1) {
				turn = 2;
				oppose = 1;
			} else if (turn == 2) {
				turn = 1;
				oppose = 2;
			} // handle unexcepted input
			if (game.player1.manaStack < 10)
				game.player1.manaStack++;
			if (game.player2.manaStack < 10)
				game.player2.manaStack++;
		}
	}
}



class Game {
	public static final char[][] board = new char[][] {{'o', 'o', 'o', ' ', 'o', 'o', 'o'},
			{'o', 'o', ' ', ' ', ' ', 'o', 'o'}, {'o', ' ', 'o', ' ', 'o', ' ', 'o'},
			{' ', ' ', ' ', 'o', ' ', ' ', ' '}, {'o', ' ', 'o', ' ', 'o', ' ', 'o'},
			{'o', 'o', ' ', ' ', ' ', 'o', 'o'}, {'o', 'o', 'o', ' ', 'o', 'o', 'o'}};
	public static final int[][] moveY = new int[][] {{1, 0, 0, 10, 0, 0, 0}, {2, 2, 10, 10, 10, 2, 0},
			{4, 10, 3, 10, 3, 10, 1}, {10, 10, 10, 4, 10, 10, 10}, {5, 10, 5, 10, 5, 10, 2},
			{6, 6, 10, 10, 10, 6, 4}, {6, 6, 6, 10, 6, 6, 5}};
	public static final int[][] moveX = new int[][] {{0, 0, 1, 10, 2, 4, 5}, {0, 2, 10, 10, 10, 4, 6},
			{0, 10, 3, 10, 3, 10, 6}, {10, 10, 10, 2, 10, 10, 10}, {0, 10, 1, 10, 5, 10, 6},
			{0, 0, 10, 10, 10, 6, 6}, {1, 2, 4, 10, 5, 6, 6}};
	public static final int[][] shortY = new int[][] {{1, 0, 0, 10, 0, 0, 1},
			{2, 2, 10, 10, 10, 2, 0}, {4, 10, 3, 10, 3, 10, 1}, {10, 10, 10, 4, 10, 10, 10},
			{5, 10, 5, 10, 5, 10, 2}, {6, 6, 10, 10, 10, 6, 4}, {6, 6, 6, 10, 6, 6, 5}};
	public static final int[][] shortX = new int[][] {{1, 0, 1, 10, 2, 4, 5},
			{0, 2, 10, 10, 10, 4, 6}, {0, 10, 3, 10, 3, 10, 6}, {10, 10, 10, 4, 10, 10, 10},
			{0, 10, 1, 10, 5, 10, 6}, {0, 0, 10, 10, 10, 6, 6}, {1, 2, 4, 10, 5, 6, 6}};

	Player player1, player2;

	public void initObjects() {
		player1 = new Player();
		player2 = new Player();
	}

	public Player getPlayer(int n) {
		switch (n) {
			case 1:
				return player1;
			case 2:
				return player2;
			default:
				return player1;
			// handle unexcepted input
		}
	}

	public void printBoard() {
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				if (i == player1.piece1.y && j == player1.piece1.x && player1.piece1.isEnded == false)
					System.out.printf("1-1");
				else if (i == player1.piece2.y && j == player1.piece2.x && player1.piece2.isEnded == false)
					System.out.printf("1-2");
				else if (i == player1.piece3.y && j == player1.piece3.x && player1.piece3.isEnded == false)
					System.out.printf("1-3");
				else if (i == player2.piece1.y && j == player2.piece1.x && player2.piece1.isEnded == false)
					System.out.printf("2-1");
				else if (i == player2.piece2.y && j == player2.piece2.x && player2.piece2.isEnded == false)
					System.out.printf("2-2");
				else if (i == player2.piece3.y && j == player2.piece3.x && player2.piece3.isEnded == false)
					System.out.printf("2-3");
				else
					System.out.printf("%c ", board[i][j]);
			}
			System.out.printf("\n");
		}
	}

	public int chooseOpt(Scanner scan) {
		System.out.println("1: throwDice / 2. useSkill");
		int input = scan.nextInt();
		return input;
	}

	public int throwDice() {
		int count = 0;

		for (int i = 0; i < 4; i++) {
			int k = (int) Math.round(Math.random());
			System.out.printf("%d ", k);
			if (k == 1)
				count++;
		}

		switch (count) {
			case 0:
				System.out.println("Yut");
				count = 4;
				break;
			case 1:
				System.out.println("Do");
				break;
			case 2:
				System.out.println("Gae");
				break;
			case 3:
				System.out.println("Geol");
				break;
			case 4:
				System.out.println("Mo");
				count = 5;
				break;
			default:
				break;
		}
		return count;
	}

	public boolean checkCatch(Player player, Player opposePlayer, int m) {
		boolean catched = false;
		for (int i = 1; i < 4; i++) {
			if (opposePlayer.getPiece(i).x == player.getPiece(m).x
					&& opposePlayer.getPiece(i).y == player.getPiece(m).y) {
				opposePlayer.getPiece(i).x = 6;
				opposePlayer.getPiece(i).y = 6;
				catched = true;
			}
		}
		return catched;
	}

	public void useSkill(Game game, Player player, Player opposePlayer, int m, Scanner scan) {
		int input;
		boolean valid = false;

		switch (player.getPiece(m).skill) {
			case 0:
				System.out.println(player.skillIntro[player.getPiece(m).skill]);
				input = scan.nextInt();
				player.movePiece(m, input);
				game.checkCatch(player, opposePlayer, m);
				player.manaStack -= Piece.skillMana[player.getPiece(m).skill];
				break;
			case 1:
				boolean catched = false;
				System.out.println(player.skillIntro[player.getPiece(m).skill]);
				do {
					input = game.throwDice();
					player.movePiece(m, 2 * input);
					catched = game.checkCatch(player, opposePlayer, m);
				} while (input == 4 || input == 5 || catched && !runGame.isFinish);
				player.manaStack -= Piece.skillMana[player.getPiece(m).skill];
				break;
			case 2:
				System.out.println(player.skillIntro[player.getPiece(m).skill]);
				System.out.println("choose piece:");
				input = scan.nextInt();
				while (!valid && !runGame.isFinish) {
					System.out.println("Invalid input");
					input = scan.nextInt();
					valid = !player.getPiece(input).isEnded;
				}
				player.getPiece(input).x = player.getPiece(m).x;
				player.getPiece(input).y = player.getPiece(m).y;
				player.manaStack -= Piece.skillMana[player.getPiece(m).skill];
				break;
			case 3:
				System.out.println(player.skillIntro[player.getPiece(m).skill]);
				System.out.println("choose piece:");
				input = scan.nextInt();
				while (!valid && !runGame.isFinish) {
					System.out.println("Invalid input");
					input = scan.nextInt();
					valid = !opposePlayer.getPiece(input).isEnded;
				}
				opposePlayer.getPiece(input).x = 6;
				opposePlayer.getPiece(input).y = 6;
				player.manaStack -= Piece.skillMana[player.getPiece(m).skill];
				break;
			case 4:
				System.out.println(player.skillIntro[player.getPiece(m).skill]);
				do {
					input = game.throwDice();
					int nextX, nextY;
					for (int i = 0; i < input; i++) {
						nextX = Game.shortX[player.getPiece(m).y][player.getPiece(m).x];
						nextY = Game.shortY[player.getPiece(m).y][player.getPiece(m).x];
						player.getPiece(m).x = nextX;
						player.getPiece(m).y = nextY;
					}
				} while ((input == 4 || input == 5) && !runGame.isFinish);
				player.manaStack -= Piece.skillMana[player.getPiece(m).skill];
				break;
		}
	}
}


/* classes for setOrder */


class Counter implements Runnable {
	public static boolean isFinish;
	private Thread t;
	char target;
	Scanner scan = new Scanner(System.in);
	int count = 0;

	Counter(char target) {
		this.target = target;
	}

	public void run() {
		while (!isFinish) {
			char input = scan.next().charAt(0);
			if (input == target)
				this.count++;
		}
	}

	public void start() {
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}
}


class Timer implements Runnable {

	private Thread t;
	int timeToSleep;

	Timer(int timeToSleep) {
		this.timeToSleep = timeToSleep;
	}

	public void run() {
		try {
			for (int i = timeToSleep; i > 0; i--) {
				System.out.println("End in " + i + "sec..");
				Thread.sleep(1000);
			}
			System.out.println("End");
			Counter.isFinish = true;
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
		return;
	}

	public void start() {
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}
}


/* methods for dataIO */


class dataIO {

	public static void readData(HashMap<String, String> hashMap) {
		try {
			// read data
			InputStream fis = new FileInputStream(".\\save.txt");
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			String data = br.readLine();
			String[] pairs = data.substring(1, data.length() - 1).split(", ");
			for (int i = 0; i < pairs.length; i++) {
				String pair = pairs[i];
				String[] keyValue = pair.split("=");
				hashMap.put(keyValue[0], String.valueOf(keyValue[1]));
			}

			System.out.println("current Data");
			System.out.println(hashMap.toString());

			br.close();
			isr.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveData(HashMap<String, String> hashMap) {
		try {
			// save data
			FileOutputStream fos = new FileOutputStream(".\\save.txt");
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter bw = new BufferedWriter(osw);

			bw.write(hashMap.toString());

			bw.flush(); // Create file if not exist & write buffer to file
			bw.close();
			fos.close();
			osw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
