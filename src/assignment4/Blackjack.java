package assignment4;

import java.util.HashMap;
import java.util.Scanner;

import assignment3.Card;
import assignment3.Value;

public class Blackjack {
	public enum Results {
		DEALER_WINS, PLAYER_WINS, TIE, BLACKJACK
	};
	private static HashMap<Value, Integer> scoreMap;
	private static Scanner sc;

	private static void initialize() {
		initializeScoreMap();
		sc = new Scanner(System.in);
	}

	public static void main(String args[]) throws Exception {
		initialize();
		int chipPile = Integer.parseInt(args[0]);
		CardPile deck = CardPile.makeFullDeck(3);
		while (chipPile > 0 && deck.getNumCards() > 10) {
			System.out.println("Now you have $" + chipPile);
			System.out.print("How much to Bet? $");
			int chipBet = sc.nextInt();
			while (chipBet > chipPile) {
				System.out.println("We don't provide loan services.");
				System.out.print("How much to Bet? $");
				chipBet = sc.nextInt();
			}
			if (chipBet <= 0) {
				// leaving the game
				break;
			}
			Results result = playRound(deck);
			float factor = 0;
			switch (result) {
			case DEALER_WINS:
				factor = -1;
				break;
			case PLAYER_WINS:
				factor = 1;
				break;
			case TIE:
				factor = 0;
				break;
			case BLACKJACK:
				factor = 1.5f;
				break;
			default:
				throw new Exception("Unexpected round result: " + result.toString());
			}
			chipPile += (int) (factor * chipBet);
		}
		summerize(chipPile);
	}

	private static void summerize(int chipPile) {
		System.out.println("At the end of the Game, you have $" + chipPile);
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
		scoreMap.put(Value.TEN, 10);
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
				// Score of having each Ace counts as 1, which means 10 least
				// potentially
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

	public static Results playRound(CardPile deck) {
		CardPile player = new CardPile();
		CardPile dealer = new CardPile();
		// dealing initial two cards
		for (int i = 0; i < 2; i++) {
			player.addToBottom(deck.remove(0));
			dealer.addToBottom(deck.remove(0));
		}
		boolean playerStays = false, dealerStays = false;
		Results result = Results.TIE;
		// running out of cards is possible, which calls a tie
		while (!deck.isEmpty()) {
			int playerValue = countValue(player);
			int dealerValue = countValue(dealer);
			if (playerValue > 21) {
				// player busts
				System.out.println("You bust!");
				result = Results.DEALER_WINS;
				break;
			} else if (dealerValue > 21) {
				// dealer busts and player doesn't
				System.out.println("The dealer busts!");
				result = Results.PLAYER_WINS;
				break;
			} else if (isBlackJack(player)) {
				if (isBlackJack(dealer)) {
					// both black jack
					System.out.println("Both got black jack!");
					result = Results.TIE;
					break;
				} else {
					// player black jack
					System.out.println("You got black jack!");
					result = Results.PLAYER_WINS;
					break;
				}
			} else if (playerStays && dealerStays) {
				// no one busts
				if (playerValue < dealerValue) {// <= 21
					// dealer closer
					System.out.println("Dealer is closer!");
					result = Results.DEALER_WINS;
					break;
				} else if (playerValue == dealerValue) {
					// equal
					System.out.println("Both have the same score!");
					result = Results.TIE;
					break;
				} else {
					System.out.println("You are closer!");
					result = Results.PLAYER_WINS;
					break;
				}
			}
			boolean hit;
			displayInfo(player, dealer, true);
			while (true) {
				System.out.print("\"hit\" or \"stay\"? ");
				String decision = sc.next();
				if (decision.equals("hit")) {
					hit = true;
					break;
				} else if (decision.equals("stay")) {
					hit = false;
					break;
				} else
					continue;
			}
			if (hit) {
				System.out.println("You hit!");
				player.addToBottom(deck.remove(0));
				playerStays = false;
			} else {
				System.out.println("You stay.");
				playerStays = true;
			}
			if (countValue(dealer) < 18) {
				// dealer hits
				System.out.println("The dealer hits!");
				dealer.addToBottom(deck.remove(0));
				dealerStays = false;
			} else {
				System.out.println("The dealer stays.");
				dealerStays = true;
			}
		}
		displayInfo(player, dealer, false);
		return result;
	}

	private static boolean isBlackJack(CardPile cardPile) {
		return cardPile.getNumCards() == 2 && countValue(cardPile) == 21;
	}

	private static void displayInfo(CardPile player, CardPile dealer, boolean hideDealer) {
		System.out.println("You have:");
		System.out.println(player);
		System.out.println(countValue(player) + " points in total");
		System.out.println("Dealer has:");
		if (hideDealer)
			System.out.println(dealer.toStringHide1st());
		else {
			System.out.println(dealer);
			System.out.println(countValue(dealer) + " points in total");
		}
	}

}
