
/**
 * 游戏面板
 */

import java.io.*;

public class Panel {
	private BufferedReader input;
	private BufferedWriter output;
	MultiThread mt;

	Panel() {
		input = new BufferedReader(new InputStreamReader(System.in));
		output = new BufferedWriter(new OutputStreamWriter(System.err));
		mt = new MultiThread();
	}

	private String read() {
		try {
			output.write("\n");
			return input.readLine();
		} catch (IOException e) {
			return "";
		}
	}

	private void write(String s) {
		System.out.flush();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
		try {
			output.write(s);
			output.flush();
		} catch (IOException e) {
		}
	}

	public void test() {
		for (int i = 0; i < 3; ++i) {
			String s = read();
			System.out.println(s);
		}
	}

	public void launch() {
		Game.open();
		String s = "";
		loop: while (true) {
			write("1. 开始对局\n" + "2. 查询数据\n" + "3. 管理线程\n" + "4. 在线模式\n" + "5. 退出游戏\n");
			s = read();
			write("\n");
			switch (s) {
			case "1":
				game();
				break;
			case "2":
				data();
				break;
			case "3":
				controlThread();
				break;
			case "4":
				online();
				break;
			case "5":
				mt.kill();
				Game.close();
				break loop;
			}
		}
	}

	private void game() {
		Game g = new Game();
		String s = "";
		while (true) {
			write("`add [id1] [id2] [id3] ...` 添加玩家\n`del [id1] [id2] [id3] ...` 删除玩家\n" + "`show` 查看当前所有玩家\n"
					+ "`pk [n]` 开始n场对局\n" + "`thread [t]` 开启新线程让当前玩家们持续对战，间隔t毫秒\n`quit` 退出对局\n");
			s = read();
			if (s.startsWith("add ")) {
				String[] t = s.split(" ");
				for (int i = 1; i < t.length; ++i)
					if (!t[i].isEmpty())
						g.addPlayer(t[i]);
			} else if (s.startsWith("del ")) {
				String[] t = s.split(" ");
				for (int i = 1; i < t.length; ++i)
					if (!t[i].isEmpty())
						g.delPlayer(t[i]);
			} else if (s.startsWith("show")) {
				g.players.show();
			} else if (s.startsWith("pk")) {
				int t;
				try {
					t = Integer.parseInt(s.split(" ")[1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					t = 1;
				}
				for (int i = 0; i < t; ++i) {
					g.putCard();
					g.showRank();
				}
			} else if (s.startsWith("thread")) {
				int t;
				try {
					t = Integer.parseInt(s.split(" ")[1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					t = 1000;
				}
				mt.add(g, t);
				break;
			} else if (s.startsWith("quit")) {
				break;
			}
		}
	}

	private void data() {
		String s = "";
		while (true) {
			int num = Game.total_game_num;
			write("`showpk [l] [r]` 查询序号l~r对局（当前对局数：" + num + "）\n" + "`show [id]` 查询玩家信息\n" + "`showd [id]` 查询玩家详细信息\n"
					+ "`quit` 退出查询\n");
			s = read();
			if (s.startsWith("showpk")) {
				String[] t = s.split(" ");
				if (t.length == 1) {
					for (int i = 1; i <= num; ++i) {
						(new OneGame(i)).show();
					}
				} else if (t.length == 2) {
					int l = Integer.parseInt(t[1]);
					(new OneGame(l)).show();
				} else if (t.length == 3) {
					int l = Math.max(Integer.parseInt(t[1]), 1);
					int r = Math.min(Integer.parseInt(t[2]), num);
					for (int i = l; i <= r; ++i) {
						(new OneGame(i)).show();
					}
				}
			} else if (s.startsWith("showd ")) {
				try {
					(new PlayerInfo(s.split(" ")[1])).showDetail();
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			} else if (s.startsWith("show ")) {
				try {
					(new PlayerInfo(s.split(" ")[1])).show();
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			} else if (s.startsWith("quit")) {
				break;
			}
		}
	}

	private void controlThread() {
		String s = "";
		while (true) {
			write("`show` 查看所有线程\n`del [序号]` 删除线程\n`output [on/off]` 开启/取消 线程运行输出\n`quit` 退出\n");
			s = read();
			if (s.startsWith("show"))
				mt.show();
			else if (s.startsWith("del")) {
				try {
					int t = Integer.parseInt(s.split(" ")[1]);
					mt.del(t);
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			} else if (s.startsWith("output")) {
				String f;
				try {
					f = s.split(" ")[1];
				} catch (ArrayIndexOutOfBoundsException e) {
					f = "on";
				}
				if (f.equals("on"))
					mt.output(true);
				else if (f.equals("off"))
					mt.output(false);
			} else if (s.startsWith("quit"))
				break;
		}
	}

	private void online() {
		String s = "";
		loop: while (true) {
			write("1. 服务器模式\n" + "2. 客户端模式\n" + "3. 退出");
			s = read();
			write("\n");
			switch (s) {
			case "1":
				server();
				break;
			case "2":
				client();
				break;
			case "3":
				break loop;
			}
		}
	}

	private void server() {
		PokerServer server = new PokerServer();
		String s = "";
		while (true) {
			write("`add [port]` 开启端口\n`pk [n] [port]` 端口port玩家pk n次\n`show [port]` 查看端口port所有玩家\n`quit` 退出");
			s = read();
			String[] t = s.split(" ");

			try {
				if (s.startsWith("add ")) {
					server.addServer(Integer.parseInt(t[1]));
				} else if (s.startsWith("pk ")) {
					server.play(Integer.parseInt(t[2]), Integer.parseInt(t[1]));
				} else if (s.startsWith("show")) {
					server.show(Integer.parseInt(t[1]));
				} else if (s.startsWith("quit")) {
					server.kill();
					break;
				}
			} catch (Exception e) {
				System.out.println("输入错误！！！");
			}
		}
	}

	private void client() {
		String id = "";
		PokerClient c;
		while (true) {
			try {
				write("请输入主机host：");
				String host = read();
				write("请输入主机port：");
				int port = Integer.parseInt(read());
				write("请输入玩家ID：");
				id = read();
				c = new PokerClient(host, port, id);
				break;
			} catch (Exception e) {
				System.out.println("Error!!!");
			}
		}
		c.start();
		System.out.println("连接成功！！！（输入`quit`退出）");
		if (read().equals("quit")) {
			c.kill();
		}
	}
}
