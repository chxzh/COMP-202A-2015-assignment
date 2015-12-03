package assignment4;

import java.util.HashMap;
import java.util.Scanner;

import assignment3.Card;
import assignment3.Value;

public class Blackjack {
	private static HashMap<Value, Integer> scoreMap;

	public static void main(String args[]) {
		initializeScoreMap();
	}

	private static void initializeScoreMap() {
		scoreMap = new HashMap<Value, Integer>(13);
		scoreMap.put(Value.ACE, 11);
		scoreMap.put(Value.TWO, 2);
		scoreMap.put(Value.THREE, 3);
		scoreMap.put(Value.FOUR, 4);
		scoreMap.put(Value.FIVE, 5);
		scoreMap.put(Value.SIX, 6);
		scoreMap.put(Value.SEVEN, 7);
		scoreMap.put(Value.EIGHT, 8);
		scoreMap.put(Value.NINE, 9);
		scoreMap.put(Value.JACK, 10);
		scoreMap.put(Value.QUEEN, 10);
		scoreMap.put(Value.KING, 10);
	}

	public static int getScore(Card card) {
		return scoreMap.get(card.getValue());
	}

	public static int countValue(CardPile carePile) {
		int aceNum = 0;
		int score = 0;
		int cardNum = carePile.getNumCards();
		int scoreBar = 21; // inclusive bar
		for (int i = 0; i < cardNum; i++) {
			Card card = carePile.get(i);
			if (card.getValue() == Value.ACE)
				aceNum++;
			score += getScore(card);
		}
		if (score > scoreBar && aceNum > 0) {
			for (int i = 1; i <= aceNum; i++) {
				// Score of having i Ace count as 1, which means 10 lease
				int alterScore = score - 10 * i;
				// by going further score is reducing, further from 21
				if (alterScore <= scoreBar) {
					// therefore, the first off-bar alternative is optimal
					return alterScore;
				}
			}
		}
		return score;
	}

	public static void playRound(CardPile carePile) {
		Scanner sc = new Scanner(System.in);
	}
}
