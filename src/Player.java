
/**
 * 玩家
 */

import java.util.ArrayList;

class Player implements Comparable<Player> { // 单个玩家
	private String id;
	HandCard handcard;

	Player(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

	public void acquireHandCard(Cards cs) {
		handcard = new HandCard(cs);
	}

	@Override
	public int compareTo(Player other) {
		return id.compareTo(other.id);
	}
}

class Players { // 多个玩家
	ArrayList<Player> players;
	int num;

	Players() {
		players = new ArrayList<Player>();
		num = 0;
	}

	public Player add(String id) {
		Player p = new Player(id);
		players.add(p);
		++num;
		return p;
	}

	public Player get(int idx) {
		return players.get(idx);
	}

	public Player del(String id) {
		for (int i = 0; i < num; ++i) {
			if (players.get(i).getID().equals(id)) {
				Player re = players.get(i);
				players.set(i, players.get(--num));
				players.remove(num);
				return re;
			}
		}
		return null;
	}

	public void show() {
		players.sort(null);
		System.out.println("共" + num + "名玩家：");
		for (Player p : players) {
			System.out.print(p.getID() + " ");
		}
		System.out.println();
	}

	public String getShow() {
		String s = "";
		for (Player p : players) {
			s = s + p.getID() + " ";
		}
		return s;
	}
}