
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
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
			}
		}
	}
}

class MultiThread {
	Data data;
	int flag = 0;
	ArrayList<SingleThread> threads;

	MultiThread(int d) {
		data = new Data(d);
		threads = new ArrayList<SingleThread>();
	}

	public void show() {
		System.out.println("序号\t[玩家]\t\t次数");
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
		if (flag == 0) {
			data.start();
			flag = 1;
		} else if (flag == 2) {
			data.resume();
			flag = 1;
		}
	}

	public void del(int idx) {
		threads.get(idx).stop();
		threads.remove(idx);
		if (threads.isEmpty()) {
			data.suspend();
			flag = 2;
		}
	}

	public void delAll() {
		for (SingleThread t : threads) {
			t.stop();
		}
		threads.clear();
		data.saveAll();
		data.suspend();
		flag = 2;
	}

	public void kill() {
		delAll();
		data.stop();
	}

	public void output(boolean b) {
		for (SingleThread t : threads) {
			t.output = b;
		}
	}
}
