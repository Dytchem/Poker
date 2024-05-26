
/**
 * 得分表
 */

import java.util.Arrays;
import java.time.LocalDateTime;

class Score implements Comparable<Score> { // 单个分数
	Player player;
	int score;

	Score(Player player) {
		this.player = player;
		score = player.handcard.score();
	}

	@Override
	public int compareTo(Score other) {
		return other.score - score;
	}
}

class Scores { // 集体打分并排序
	LocalDateTime now;
	Score[] scores;

	Scores(Players ps) {
		now = LocalDateTime.now();
		scores = new Score[ps.num];
		for (int i = 0; i < ps.num; ++i) {
			scores[i] = new Score(ps.get(i));
		}
		Arrays.sort(scores);
	}

	public String getShow() {
		String s = "[对局数据]\n对局时间：" + now + "\n排名\tID\t积分\t手牌\n";
		for (int i = 0; i < scores.length; ++i) {
			s = s + (i + 1) + "\t" + scores[i].player.getID() + "\t"
					+ (scores.length / 2 - i - (scores.length % 2 == 0 && 2 * i >= scores.length ? 1 : 0)) + "\t"
					+ scores[i].player.handcard.getShow() + "\n";
		}
		return s;
	}

	public void show() {
		System.out.print("[对局数据]\n对局时间：");
		System.out.println(now);
		System.out.println("排名\tID\t积分\t手牌");
		for (int i = 0; i < scores.length; ++i) {
			System.out.print((i + 1) + "\t" + scores[i].player.getID() + "\t"
					+ (scores.length / 2 - i - (scores.length % 2 == 0 && 2 * i >= scores.length ? 1 : 0)) + "\t");
			scores[i].player.handcard.show();
			System.out.println();
		}
//		System.out.println(getShow());
	}

	public Record[] getRecords() {
		Record[] re = new Record[scores.length];
		for (int i = 0; i < scores.length; ++i) {
			re[i] = new Record(this, i);
		}
		return re;
	}

	public void saveRecords() {
		Record[] records = getRecords();
		for (Record r : records) {
			r.write();
		}
	}
}