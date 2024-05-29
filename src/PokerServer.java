
/**
 * 在线游戏模式
 */

import java.io.*;
import java.net.*;
import java.util.*;

class PokerClient extends Thread {
	String id;
	Socket s;

	PokerClient(String host, int port, String id) throws Exception {
		s = new Socket(host, port);
		s.getInputStream().read();
		this.id = id;
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		writer.write("add " + id);

	}

	@Override
	public void run() {
		while (true) {
			try {
				ObjectInputStream input = new ObjectInputStream(s.getInputStream());
				Record r = (Record) input.readObject();
				r.show();
				Thread.sleep(500);
				OneGame g = new OneGame((String) input.readObject());
				g.show();
				Thread.sleep(500);
				input.close();
			} catch (InterruptedException e) {
				break;
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
		}
	}

	public void kill() {
		try {
			s.shutdownInput();
			s.close();
			this.interrupt();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			writer.write("del " + id);
		} catch (IOException e) {
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
		synchronized (this) {
			game.putCard();
			game.save();
			String rank = game.scores.getShow();
			System.out.println(rank);
			Record[] rs = game.scores.getRecords();
			for (Record r : rs) {
				try {
					ObjectOutputStream output = new ObjectOutputStream(sockets.get(r.id).getOutputStream());
					output.writeObject(r);
					Thread.sleep(1000);
					output.writeObject(rank);
					Thread.sleep(1000);
				} catch (IOException | InterruptedException e) {
					delPlayer(r.id);
				}
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
				if (ss[0].equals("add"))
					addPlayer(ss[1], cs);
				else if (ss[0].equals("del"))
					delPlayer(ss[1]);
				Thread.sleep(1000);
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
