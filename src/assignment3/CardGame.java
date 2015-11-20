package assignment3;

public class CardGame {
	public static void main(String[] args) {
		int playerNum = 4;
		CardPile dealer = CardPile.makeFullDeck();
		CardPile[] players = new CardPile[playerNum];
		for(int i = 0; i < playerNum; i++) {
			players[i] = new CardPile();
		}
		// dealing cards
		for (int playerToDeal = 0;
				!dealer.isEmpty();
				playerToDeal = (playerToDeal+1)%playerNum) {
			players[playerToDeal].addToBottom(dealer.remove(0));
		}
		for(int i = 0; i < playerNum; i++) {
			if(players[i].find(Suit.SPADES, Value.ACE) != -1){
				System.out.println("Player "+i+" wins.");
			}
		}
	}
}
