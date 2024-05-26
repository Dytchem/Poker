import java.io.*;
import java.nio.file.*;

/**
 * 游戏
 */

public class Game { // 游戏局
	static int total_game_num = 0;
	private Cards cards;
	Players players;
	Scores scores;

	Game() {
		cards = new Cards();
		cards.shuffle();
		players = new Players();
	}

	public void addPlayer(String s) {
		players.add(s);
	}

	public void delPlayer(String s) {
		players.del(s);
	}

	public void putCard() {
		for (int i = 0; i < players.num; ++i) {
			players.get(i).acquireHandCard(cards);
		}
		scores = new Scores(players);
		total_game_num++;
		save();
	}

	public void showRank() {
		scores.show();
		System.out.println();
	}

	public void save() {
		scores.saveRecords();
		(new OneGame(scores.getShow())).write();
	}

	public static void open() {
		Path root = Paths.get("data/games");
		try {
			if (!Files.exists(root))
				Files.createDirectories(root);
		} catch (Exception e) {
		}
		try {
			DataInputStream input = new DataInputStream(new FileInputStream("data/games/general"));
			total_game_num = input.readInt();
			input.close();
		} catch (IOException e) {
		}
	}

	public static void close() {
		try {
			DataOutputStream output = new DataOutputStream(new FileOutputStream("data/games/general"));
			output.writeInt(total_game_num);
			output.close();
		} catch (IOException e) {
		}
	}
}
