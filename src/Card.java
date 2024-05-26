
/**
 * 卡牌
 */

import java.io.Serializable;
import java.util.Random;

class Card implements Serializable { // 单张牌
	private static final long serialVersionUID = 1L;
	private static final String[] NUM = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
	private static final String[] TYPE = { "黑桃", "红桃", "方块", "梅花" };
	int num;
	private int type;

	Card(int num, int type) {
		this.num = num;
		this.type = type;
	}

	Card(Card c) {
		this.num = c.num;
		this.type = c.type;
	}

	public String getInfo() {
		return TYPE[type] + NUM[num];
	}
}

class Cards { // 牌库
	private Card[] cards;
	private int cur;

	Cards() {
		cards = new Card[52];
		for (int i = 0; i < 52; ++i)
			cards[i] = new Card(i / 4, i % 4);
		cur = 52;
	}

	public void show() {
		for (Card c : cards) {
			System.err.println(c.getInfo());
		}
	}

	public void shuffle() {
		Random r = new Random();
		for (int i = 51; i >= 0; --i) {
			int idx = r.nextInt(i + 1);
			Card c = cards[idx];
			cards[idx] = cards[i];
			cards[i] = c;
		}
		cur = 0;
	}

	public Card getOneCard() {
		if (cur >= 52) {
			shuffle();
		}
		return cards[cur++];
	}
}

class HandCard implements Serializable { // 手牌
	private static final long serialVersionUID = 1L;
	private Card[] cards;

	HandCard(Cards cs) { // 从牌库取牌
		cards = new Card[3];
		for (int i = 0; i < 3; ++i) {
			cards[i] = cs.getOneCard();
		}
	}

	HandCard(HandCard h) {
		cards = new Card[3];
		for (int i = 0; i < 3; ++i) {
			cards[i] = new Card(h.cards[i]);
		}
	}

	public String getShow() {
		String s = "";
		for (Card c : cards) {
			s += c.getInfo() + " ";
		}
		return s;
	}

	public void show() {
		for (Card c : cards) {
			System.out.print(c.getInfo() + " ");
		}
	}

	public int score() { // 打分函数
		int re = 0;
		int[] cnt = new int[13];
		for (Card c : cards) {
			cnt[c.num]++;
			switch (cnt[c.num]) {
			case 1:
				re += c.num;
				break;
			case 2:
				re += c.num * 100;
				break;
			case 3:
				re += c.num * 10000;
				break;
			}
		}
		return re;
	}
}
