
/**
 * 多线程
 */

import java.util.ArrayList;

class SingleThread extends Thread {
	Game game;
	private int duration;
	int t = 0;
	private Data data;
	static boolean output = false;

	SingleThread(Game g, int d, Data da) {
		game = g;
		duration = d;
		data = da;
	}

	@Override
	public void run() {
		while (true) {
			game.putCard();
			++t;
			if (output) {
				System.out.print(game.players.getShow());
				System.out.println("第" + t + "次");
			}
			data.add(game.scores.getRecords());
			(new OneGame(game.scores.getShow())).write();
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}

class MultiThread {
	private Data data;
	private ArrayList<SingleThread> threads;

	MultiThread() {
		data = new Data();
		data.start();
		threads = new ArrayList<SingleThread>();
	}

	public void show() {
		System.out.println("序号\t[玩家列表]\t次数");
		for (int i = 0; i < threads.size(); ++i) {
			System.out.print(i + "\t[");
			System.out.print(threads.get(i).game.players.getShow());
			System.out.println("]\t" + threads.get(i).t);
		}
	}

	public void add(Game g, int d) {
		SingleThread t = new SingleThread(g, d, data);
		threads.add(t);
		t.start();
	}

	public void del(int idx) {
		threads.get(idx).interrupt();
		threads.remove(idx);
	}

	public void kill() {
		for (SingleThread t : threads) {
			t.interrupt();
		}
		threads.clear();
		data.interrupt();
		// data.saveAll();
	}

	public void output(boolean b) {
		SingleThread.output = b;
	}
}
