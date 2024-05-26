
/**
 * 数据存储与读取
 */

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDateTime;

class PlayerInfo { // 一条玩家数据（可独立用于存储）
	String id;
	int points;
	int game_num;

	PlayerInfo(String id) {
		this.id = id;
		read();
	}

	public void read() {
		Path root = Paths.get("data/players/" + id + "/records");
		try {
			if (!Files.exists(root))
				Files.createDirectories(root);
			DataInputStream input = new DataInputStream(new FileInputStream("data/players/" + id + "/" + "info"));
			points = input.readInt();
			game_num = input.readInt();
			input.close();
		} catch (Exception e) {
			points = 0;
			game_num = 0;
			write();
		}
	}

	public void write() {
		try {
			DataOutputStream output = new DataOutputStream(new FileOutputStream("data/players/" + id + "/" + "info"));
			output.writeInt(points);
			output.writeInt(game_num);
			output.close();
		} catch (IOException e) {
		}
	}

	public void show() {
		System.out.println("[玩家信息]");
		System.out.println("ID:\t" + id);
		System.out.println("总积分：\t" + points);
		System.out.println("总局数：\t" + game_num);
		System.out.println();
	}

	public void showDetail() {
		show();
		(new Records(id)).show();
	}
}

class Record implements Serializable { // 一条玩家对局数据（可独立用于存储）
	private static final long serialVersionUID = 1L;
	String id;
	LocalDateTime time;
	int player_num;
	int rank;
	int points;
	HandCard handcard;

	Record(Scores ss, int idx) {
		id = ss.scores[idx].player.getID();
		time = ss.now;
		player_num = ss.scores.length;
		rank = idx + 1;
		points = player_num / 2 - idx - (player_num % 2 == 0 && 2 * idx >= player_num ? 1 : 0);
		handcard = ss.scores[idx].player.handcard;
	}

	public void write() {
		PlayerInfo p = new PlayerInfo(id);
		try {
			ObjectOutputStream output = new ObjectOutputStream(
					new FileOutputStream("data/players/" + id + "/" + "records/" + p.game_num));
			output.writeObject(this);
			output.close();
			p.game_num++;
			p.points += points;
			p.write();
		} catch (IOException e) {
		}
	}

	public void show() {
		System.out.println("玩家：\t" + id);
		System.out.println("时间：\t" + time);
		System.out.println("排名：\t" + rank + " / " + player_num);
		System.out.println("积分：\t" + points);
		System.out.print("手牌：\t");
		handcard.show();
		System.out.println();
	}
}

class Records { // 玩家的所有对局数据（用于查询）
	String id;
	Record[] records;

	Records(String id) {
		this.id = id;
		read();
	}

	public void read() {
		PlayerInfo p = new PlayerInfo(id);
		records = new Record[p.game_num];
		try {
			for (int i = 0; i < p.game_num; ++i) {
				ObjectInputStream input = new ObjectInputStream(
						new FileInputStream("data/players/" + p.id + "/" + "records/" + i));
				records[i] = (Record) input.readObject();
				input.close();
			}
		} catch (Exception e) {
		}

	}

	public void show() {
		for (int i = 0; i < records.length; ++i) {
			System.out.println((i + 1) + "：[玩家对局数据]");
			records[i].show();
			System.out.println();
		}
		System.out.println();
	}
}

class OneGame {
	private String s = "null";
	int idx;

	OneGame(String s) {
		this.s = s;
	}

	OneGame(int idx) {
		this.idx=idx;
		if (idx < 1 || idx > Game.total_game_num)
			return;
		try {
			ObjectInputStream input = new ObjectInputStream(new FileInputStream("data/games/" + idx));
			s = (String) input.readObject();
			input.close();
		} catch (Exception e) {
			write();
		}
	}

	public void show() {
		System.out.print(idx + "：");
		System.out.println(s);
	}

	public void write() {
		Path root = Paths.get("data/games");
		try {
			if (!Files.exists(root))
				Files.createDirectories(root);
		} catch (Exception e) {
		}
		try {
			ObjectOutputStream output = new ObjectOutputStream(
					new FileOutputStream("data/games/" + Game.total_game_num));
			output.writeObject(s);
			output.close();
		} catch (IOException e) {
		}
	}
}

class Data extends Thread { // 待保存的数据队列（用于多线程中）
	private Queue<Record> q;
	int duration;

	Data(int d) {
		q = new LinkedList<>();
		duration = d;
	}

	public void add(Record r) {
		q.add(r);
	}

	public void add(Record[] rs) {
		for (Record r : rs) {
			q.add(r);
		}
	}

	public void saveAll() {
		int t = q.size();
		try {
			for (int i = 0; i < t; ++i) {
				q.poll().write();
			}
		} catch (NullPointerException e) {
		}
	}

	@Override
	public void run() {
		while (true) {
			while (true) {
				saveAll();
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
