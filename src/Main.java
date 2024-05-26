
/**
 * 程序入口
 */

class Tester {
	static void print(Object o) {
		System.out.print(o);
	}

	public void test1() {
		Cards cs = new Cards();
		Card c = cs.getOneCard();
		cs.show();
		print(c.getInfo() + "\n");
		c = cs.getOneCard();
		print(c.getInfo() + "\n");
	}

	public void test2() {
		Cards cs = new Cards();
		Player p = new Player("Dytchem");
		p.acquireHandCard(cs);
		print(p.getID() + "\n");
		p.handcard.show();
	}

	public void test3() {
		Game g = new Game();
		g.addPlayer("zjm");
		g.addPlayer("Dytchem");
		g.putCard();
		g.showRank();

		g.addPlayer("bbb");
		g.putCard();
		g.showRank();

		g.addPlayer("ccc");
		g.addPlayer("d");
		g.putCard();
		g.showRank();

		g.delPlayer("zjm");
		g.putCard();
		g.showRank();
	}

	public void test4() {
		print("\n");
		PlayerInfo p = new PlayerInfo("zjm");
		p.showDetail();
	}

	public void test5() {
		Panel p = new Panel();
		// p.test();
		p.launch();
	}
}

public class Main {
	static Tester t = new Tester();

	public static void main(String[] args) {
		t.test5();
	}
}
