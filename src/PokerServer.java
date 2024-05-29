
/**
 * 在线游戏模式
 */

import java.io.*;
import java.net.*;
import java.util.*;

class PokerClient extends Thread {
	String id;
	Socket s;
	String host;
	int port;

	PokerClient(String host, int port, String id) throws Exception {
		s = new Socket(this.host = host, this.port = port);
		s.getInputStream().read();
		this.id = id;
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		writer.write("add " + id + "\n");
		writer.flush();
	}

	@Override
	public void run() {
		while (true) {
			try {
				ObjectInputStream input = new ObjectInputStream(s.getInputStream());
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				Record r = (Record) input.readObject();
				System.out.println("***************************************");
				r.show();
				System.out.println();
				writer.write("OK\n");
				writer.flush();

				String S = (String) input.readObject();
				System.out.println(S);
				System.out.println("***************************************");
				OneGame g = new OneGame(S);
				g.write();
				writer.write("OK\n");
				writer.flush();
			} catch (IOException | ClassNotFoundException e) {
				break;
			}
		}
	}

	public void kill() {
		try {
			this.interrupt();
			s.close();
			s = new Socket(host, port);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			writer.write("del " + id + "\n");
			writer.flush();
			writer.close();
			s.close();
		} catch (Exception e) {
			System.out.println("退出失败");
		}
	}
}

class ServerThread extends Thread {
	Game game;
	private ServerSocket s;
	private HashMap<String, Socket> sockets;

	ServerThread(int port) {
		game = new Game();
		try {
			s = new ServerSocket(port);
		} catch (IOException e) {
		}
		sockets = new HashMap<>();
	}

	public void addPlayer(String id, Socket so) {
		synchronized (this) {
			game.addPlayer(id);
			sockets.put(id, so);
		}
	}

	public void delPlayer(String id) {
		synchronized (this) {
			game.delPlayer(id);
			sockets.remove(id);
		}
	}

	public void play() {
		String rank;
		Record[] rs;
		synchronized (this) {
			game.putCard();
			game.save();
			rank = game.scores.getShow();
			System.out.println(rank);
			rs = game.scores.getRecords();
		}
		for (Record r : rs) {
			try {
				ObjectOutputStream output = new ObjectOutputStream(sockets.get(r.id).getOutputStream());
				BufferedReader reader = new BufferedReader(new InputStreamReader(sockets.get(r.id).getInputStream()));
				output.writeObject(r);
				if (!reader.readLine().equals("OK"))
					throw new Exception("未收到客户端响应");
				output.writeObject(rank);
				// System.err.println("Debug Here...");
				if (!reader.readLine().equals("OK"))
					throw new Exception("未收到客户端响应");
			} catch (Exception e) {
				delPlayer(r.id);
				System.out.println("[" + r.id + " 莫名其妙退出了游戏]：" + e.toString() + "\n");
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			Socket cs;
			try {
				cs = s.accept();
				cs.getOutputStream().write(0);
				BufferedReader reader = new BufferedReader(new InputStreamReader(cs.getInputStream()));
				String[] ss = reader.readLine().split(" ");
				if (ss[0].equals("add")) {
					addPlayer(ss[1], cs);
					System.out.println("[" + ss[1] + " 加入了游戏]\n");
				} else if (ss[0].equals("del")) {
					delPlayer(ss[1]);
					System.out.println("[" + ss[1] + " 退出了游戏]\n");
				}
			} catch (Exception e) {
				break;
			}

		}
	}

	public void kill() {
		try {
			s.close();
			for (Map.Entry<String, Socket> k : sockets.entrySet()) {
				k.getValue().close();
			}
			this.interrupt();
		} catch (IOException e) {
		}
	}
}

class PokerServer {
	private ServerThread[] sts;

	PokerServer() {
		sts = new ServerThread[1 << 16];
	}

	public void addServer(int port) {
		sts[port] = new ServerThread(port);
		sts[port].start();
	}

	public void play(int port, int t) {
		for (int i = 0; i < t; ++i) {
			// System.err.println(i);
			sts[port].play();
		}
	}

	public void kill() {
		for (ServerThread s : sts) {
			if (s != null)
				s.kill();
		}
	}

	public void show(int port) {
		sts[port].game.players.show();
	}
}
