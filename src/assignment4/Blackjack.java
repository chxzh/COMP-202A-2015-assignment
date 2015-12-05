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
		CardPile deck = CardPile.makeFullDeck(1);
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
			Results result;
			try {
				result = playRound(deck);
			} catch (IndexOutOfBoundsException iobe) {
				// out of cards in the middle of a game
				System.out.println("Sorry we ran out of cards. Let's call a tie.");
				result = Results.TIE;
			}
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
			/*
			 * if any ace is in hand, here is to get the alternative score that
			 * is closest to 21. As ace was initially counted as 11, the options
			 * will be taking some number of ace and count them as 1, which
			 * means 10-multiplying-the-number lease. To find the closest one,
			 * we iterate from the largest to the smallest by -10 for each time
			 * counting one more ace, and first one not bigger than 21 (and
			 * biggest "<= 21") will be accepted. In case it doesn't have any
			 * alternative under 21, we take the smallest alternative, which is
			 * the smallest ">= 21". Either of them could be the closest but the
			 * below-21 got priority for possibility to win.			 * 
			 */
			
			int alterScore = score;
			for (int i = 1; i <= aceNum; i++) {
				// Score of having each Ace counts as 1, which means 10 least
				// potentially
				alterScore -= 10;
				// by going further score is reducing, further from 21
				if (alterScore <= scoreBar) {
					// therefore, the first off-bar alternative is optimal
					break;
				}
			}
			// at this point, the alterScore is either the smallest
			// bigger-than-21 or the biggest smaller-than-21
			score = alterScore;
		}
		return score;
	}

	public static Results playRound(CardPile deck) throws IndexOutOfBoundsException{
		// the run out of card exception will be handled outside
		CardPile player = new CardPile();
		CardPile dealer = new CardPile();
		// dealing initial two cards
		for (int i = 0; i < 2; i++) {
			player.addToBottom(deck.remove(0));
			dealer.addToBottom(deck.remove(0));
		}
		displayInfo(player, dealer, true);
		if (isBlackJack(player)) {// with only 2 cards currently
			if (isBlackJack(dealer)) {
				System.out.println("Both got black jack!");
				displayInfo(player, dealer, false);
				return Results.TIE;
			} else {
				System.out.println("You got black jack!");
				displayInfo(player, dealer, false);
				return Results.PLAYER_WINS;
			}
		} else {
			boolean playerHits = true;
			while(playerHits) {
				while (true) { // until player enters the recognizable choice
					System.out.print("\"hit\" or \"stay\"? ");
					String decision = sc.next();
					if ("hit".equals(decision.toLowerCase())) {
						playerHits = true;
						break;
					} else if (decision.equals("stay")) {
						playerHits = false;
						break;
					} else
						continue;
				} // end of while(true) the input reading loop
				if (playerHits) {
					System.out.println("You hit!");
					player.addToBottom(deck.remove(0));
					if (countValue(player) > 21) {
						// player busts
						displayInfo(player, dealer, false);
						System.out.println("You bust!");
						return Results.DEALER_WINS;
					} else {
						displayInfo(player, dealer, true);
					}
				} else { // not hitting
					System.out.println("You stay.");
					break;
				} // end of if(playerHits)
			} // end of while(playerHits)
			// by now player stays and it is dealer's turn
			while(countValue(dealer) < 18) {
				// dealer Hits
				displayInfo(player, dealer, true);
				System.out.println("The dealer hits");
				dealer.addToBottom(deck.remove(0));				
			} // end of while(dealer hits)
			// by now dealer is over 18, possibly busted already
			if(countValue(dealer) > 21) {
				// the dealer bursts while the player didn't
				displayInfo(player, dealer, false);
				System.out.println("The dealer busts!");
				return Results.PLAYER_WINS;
			} else {
				// neither of them burst
				int playerValue = countValue(player);
				int dealerValue = countValue(dealer);
				displayInfo(player, dealer, false);
				if (playerValue < dealerValue) {// <= 21
					// dealer closer
					System.out.println("Dealer is closer!");
					return Results.DEALER_WINS;
				} else if (playerValue == dealerValue) {
					// equal
					System.out.println("Both have the same score!");
					return Results.TIE;
				} else {
					System.out.println("You are closer!");
					return Results.PLAYER_WINS;
				}
			}
		} // end of else
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
